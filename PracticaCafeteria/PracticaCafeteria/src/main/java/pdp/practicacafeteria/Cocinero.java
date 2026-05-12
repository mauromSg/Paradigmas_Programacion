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
public class Cocinero extends Thread {
    private String id;
    private ControlAforos aforos;
    private Despensa despensa;
    private InterfazCafeteria gui;
    private Random rand = new Random();

    public Cocinero(String id, ControlAforos aforos, Despensa despensa, InterfazCafeteria gui) {
        this.id = id;
        this.aforos = aforos;
        this.despensa = despensa;
        this.gui = gui;
    }

    private void esperar(int min, int max) {
        try {
            gui.comprobarPausa();
            Thread.sleep(rand.nextInt((max - min) * 1000 + 1) + min * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        while (true) {
            try {
                gui.comprobarPausa();

                // 1. Sala descanso
                aforos.entrarDescansoCocinero(id);
                esperar(5, 10);
                aforos.salirDescansoCocinero(id);

                // 2. Trayecto cocina
                esperar(1, 3);

                // 3. Cocina
                aforos.entrarCocina(id); // Aforo máximo = 100
                
                // Preparar productos
                esperar(5, 10);
                
                boolean esCafe = rand.nextBoolean();
                int producidosC = 0;
                int producidosR = 0;
                
                if (esCafe) {
                    producidosC = rand.nextInt(4) + 2; // entre 2 y 5 cafés
                } else {
                    producidosR = rand.nextInt(5) + 4; // entre 4 y 8 rosquillas
                }
                
                // Salir de la cocina y dirigirse hacia la despensa
                aforos.salirCocina(id);
                esperar(2, 5);

                // 4. Despensa
                despensa.entrarCocinero(id); // Aforo máximo = 50 cocineros
                
                despensa.dejarProductos(id, producidosC, producidosR);
                
                despensa.salirCocinero(id);

                // 5. Trayecto a sala de descanso
                esperar(2, 5);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
