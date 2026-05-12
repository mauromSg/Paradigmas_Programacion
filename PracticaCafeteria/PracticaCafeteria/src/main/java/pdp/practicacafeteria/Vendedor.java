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
public class Vendedor extends Thread {
    private String id;
    private ControlAforos aforos; // Solo para usar sala descanso visualmente si queremos
    private Despensa despensa;
    private Mostrador mostrador;
    private InterfazCafeteria gui;
    private Random rand = new Random();

    public Vendedor(String id, ControlAforos aforos, Despensa despensa, Mostrador mostrador, InterfazCafeteria gui) {
        this.id = id;
        this.aforos = aforos;
        this.despensa = despensa;
        this.mostrador = mostrador;
        this.gui = gui;
    }

    private void esperar(int min, int max) {
        try {
            gui.comprobarPausa();
            Thread.sleep(rand.nextInt((max - min) * 1000 + 1) + min * 1000);
        } catch (InterruptedException e) { e.printStackTrace(); }
    }

    @Override
    public void run() {
        while (true) {
            try {
                gui.comprobarPausa();

                // 1. Sala Descanso
                aforos.entrarDescansoVendedor(id);
                esperar(5, 10);
                aforos.salirDescansoVendedor(id);

                // 2. Trayecto Despensa
                esperar(1, 3);

                // 3. Despensa
                despensa.entrarVendedor(id); // Max 50 vendedores
                
                int objCafe = rand.nextInt(4) + 3; // 3-6
                int objRosq = rand.nextInt(6) + 5; // 5-10
                
                // Coger productos (parcial o total)
                int[] cogidos = despensa.cogerProductos(id, objCafe, objRosq);
                
                // Preparar (1-3s) dentro o fuera? El PDF dice "Una vez recopilados... se prepara en 1-3s"
                // Asumimos que esto ocurre antes de ir al mostrador, quizás aún en despensa o saliendo.
                esperar(1, 3);
                
                despensa.salirVendedor(id);

                // 4. Trayecto Mostrador
                esperar(2, 5);

                // 5. Mostrador
                mostrador.entrarVendedor(id); // Max 20
                
                // Colocar productos (1-3s)
                esperar(1, 3);
                mostrador.reponer(id, cogidos[0], cogidos[1]);
                
                mostrador.salirVendedor(id);

                // 6. Trayecto Sala Descanso
                esperar(2, 5);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
