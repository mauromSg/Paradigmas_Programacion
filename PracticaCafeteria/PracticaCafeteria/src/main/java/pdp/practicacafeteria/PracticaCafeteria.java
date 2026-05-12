/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package pdp.practicacafeteria;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

/**
 *
 * @author sergiopavon
 */
public class PracticaCafeteria {

    public static void main(String[] args) {
        // Inicializar Logger y GUI
        Log log = new Log();
        InterfazCafeteria gui = new InterfazCafeteria();
        gui.setVisible(true);

        // Inicializar Recursos Compartidos
        ControlAforos aforos = new ControlAforos(gui);
        Despensa despensa = new Despensa(log, gui);
        Mostrador mostrador = new Mostrador(log, gui);
        Caja caja = new Caja(log, gui);
        
        // Iniciar servidor RMI
        try {
            CafeteriaRemotaImpl objetoRemoto = new CafeteriaRemotaImpl(aforos, despensa, mostrador, caja, gui);
            LocateRegistry.createRegistry(1099);    // Para RMI, puerto 1099
            Naming.rebind("//localhost/Cafeteria", objetoRemoto);
            
            System.out.println("Servidor RMI Activo y esperando clientes...");
            
        } catch (Exception e) {
            System.err.println("Error al iniciar RMI: " + e.getMessage());
        }

        // Creación de hilos cocineros
        new Thread(() -> {
            for (int i = 1; i <= 500; i++) {
                String id = String.format("B-%04d", i);
                Cocinero c = new Cocinero(id, aforos, despensa, gui);
                c.start();
                try {
                    // Intervalo 1 a 2 segundos
                    Thread.sleep((int)(1000 + 1000 * Math.random()));
                } catch (InterruptedException e) {}
            }
        }).start();

        // Creación de hilos vendedores
        new Thread(() -> {
            for (int i = 1; i <= 500; i++) {
                String id = String.format("V-%04d", i);
                Vendedor v = new Vendedor(id, aforos, despensa, mostrador, gui);
                v.start();
                try {
                    // Intervalo 0.5 a 2.5 segundos
                    Thread.sleep((int)(500 + 2000 * Math.random()));
                } catch (InterruptedException e) {}
            }
        }).start();

        

        // Creación de hilos clientes
        new Thread(() -> {
            for (int i = 1; i <= 8000; i++) {
                String id = String.format("C-%04d", i);
                Cliente c = new Cliente(id, aforos, mostrador, caja, gui);
                c.start();
                try {
                    // Intervalo 1 a 3 segundos
                    Thread.sleep((int)(1000 + 2000 * Math.random()));
                } catch (InterruptedException e) {}
            }
        }).start();

    }
}