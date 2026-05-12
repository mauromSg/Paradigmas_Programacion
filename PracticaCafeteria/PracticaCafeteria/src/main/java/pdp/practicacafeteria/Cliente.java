/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pdp.practicacafeteria;

import java.util.Random;

/**
 *
 * @author sergiopavon
 */
public class Cliente extends Thread {
    private String id;
    private ControlAforos aforos;
    private Mostrador mostrador;
    private Caja caja;
    private InterfazCafeteria gui;
    private Random rand = new Random();

    public Cliente(String id, ControlAforos aforos, Mostrador mostrador, Caja caja, InterfazCafeteria gui) {
        this.id = id;
        this.aforos = aforos;
        this.mostrador = mostrador;
        this.caja = caja;
        this.gui = gui;
    }

    private void esperar(int min, int max) {
        try {
            gui.comprobarPausa(); // Verificar pausa antes de esperar
            Thread.sleep(rand.nextInt((max - min) * 1000 + 1) + min * 1000);
        } catch (InterruptedException e) { e.printStackTrace(); }
    }

    public void run() {
        try {
            gui.comprobarPausa();

            // 1. Entra al parque y lo contempla
            aforos.entrarParque(id);
            esperar(5, 10);
            
            // 2. Trayecto a cafetería
            aforos.salirParque(id);
            esperar(3, 9);
            
            // 3. Cola de espera mostrador
            mostrador.entrarColaEspera(id); // Se bloquea si hay 20 esperando
            
            // 4. Acceder al mostrador
            mostrador.accederMostrador(id); // Se bloquea si hay 5 en mostrador
            
            // Seleccionar pedido
            int nCafes = rand.nextInt(3) + 1;
            int nRosquillas = rand.nextInt(5);
            
            // Pedir y esperar stock
            mostrador.pedirYRecoger(id, nCafes, nRosquillas);
            
            // Salir del mostrador
            mostrador.salirMostrador(id);
            
            // 5. Caja
            caja.llegarColaCaja(id);
            
            caja.usarMaquinaPago(id); // Entra a la máquina
            esperar(2, 5); // Tiempo de pago
            
            double total = (nCafes * 1.50) + (nRosquillas * 2.50);
            caja.confirmarPago(id, total);
            
            // 6. Área de consumición
            aforos.esperarMesa(id);
            aforos.entrarConsumir(id); // Espera aforo 30
            
            esperar(10, 15); // Consumir
            
            aforos.salirConsumir(id);
            
            
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
