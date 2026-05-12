/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pdp.practicacafeteria;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @author sergiopavon
 */
public class Despensa {
    private Log log;
    private InterfazCafeteria gui;

    // Stock
    private int cafes = 0;
    private int rosquillas = 0;

    // Semáforos de aforo (en cocina: 50 cocineros y 50 vendedores)
    private Semaphore aforoCocineros = new Semaphore(50);
    private Semaphore aforoVendedores = new Semaphore(50);
    
    private Lock cerrojo = new ReentrantLock();
    private Condition hayComida = cerrojo.newCondition();
    
    // Listas de cocineros y vendedores
    private ArrayList<String> listCocineros = new ArrayList<>();
    private ArrayList<String> listVendedores = new ArrayList<>();

    public Despensa(Log log, InterfazCafeteria gui) {
        this.log = log;
        this.gui = gui;
    }

    // Operaciones de cocinero:
    public void entrarCocinero(String id) throws InterruptedException {
        aforoCocineros.acquire();
        cerrojo.lock();
        try {
            listCocineros.add(id);
            gui.setDespensaCocineros(listCocineros.toString());
        } finally {
            cerrojo.unlock();
        }
    }

    public void dejarProductos(String id, int nCafes, int nRosquillas) {
        cerrojo.lock();
        try {
            this.cafes += nCafes;
            this.rosquillas += nRosquillas;
            
            log.escribirLog("Cocinero " + id + " deja " + nCafes + " cafés y " + nRosquillas + " rosquillas en Despensa.");
            gui.setDespensaCafes(this.cafes);
            gui.setDespensaRosquillas(this.rosquillas);
            
            // Despertar a los vendedores que están esperando
            hayComida.signalAll();
            
        } finally {
            cerrojo.unlock();
        }
    }

    public void salirCocinero(String id) {
        cerrojo.lock();
        try {
            listCocineros.remove(id);
            gui.setDespensaCocineros(listCocineros.toString());
        } finally {
            cerrojo.unlock();
        }
        aforoCocineros.release();
    }

    // Operaciones de vendedor
    public void entrarVendedor(String id) throws InterruptedException {
        aforoVendedores.acquire();
        cerrojo.lock();
        try {
            listVendedores.add(id);
            gui.setDespensaVendedores(listVendedores.toString());
        } finally {
            cerrojo.unlock();
        }
    }

    // El vendedor intenta coger una determinada cantidad de productos
    public int[] cogerProductos(String id, int objCafe, int objRosquilla) throws InterruptedException {
        cerrojo.lock();
        try {
            // Si no hay nada, el vendedor espera
            while (this.cafes == 0 && this.rosquillas == 0) {
                log.escribirLog("Vendedor " + id + " esperando stock en Despensa...");
                hayComida.await();
            }

            int cCogidos = 0;
            int rCogidos = 0;
            
            // Coger cafés
            if (this.cafes >= objCafe) {
                cCogidos = objCafe;
                this.cafes -= objCafe;  // Hay más cafes de lo que se piden
            } else {
                cCogidos = this.cafes;  // Se cogen todos los cafés que quedan y Despensa vacía
                this.cafes = 0;
            }
            
            // Coger rosquillas
            if (this.rosquillas >= objRosquilla) {
                rCogidos = objRosquilla;
                this.rosquillas -= objRosquilla;
            } else {
                rCogidos = this.rosquillas;
                this.rosquillas = 0;
            }
            
            log.escribirLog("Vendedor " + id + " recoge " + cCogidos + " cafés y " + rCogidos + " rosquillas de Despensa.");
            gui.setDespensaCafes(this.cafes);
            gui.setDespensaRosquillas(this.rosquillas);
            
            // Array con la cantidad de cafés y rosquillas cogidos
            return new int[]{cCogidos, rCogidos};
        } finally {
            cerrojo.unlock();
        }
    }

    public void salirVendedor(String id) {
        cerrojo.lock();
        try {
            listVendedores.remove(id);
            gui.setDespensaVendedores(listVendedores.toString());
        } finally {
            cerrojo.unlock();
        }
        aforoVendedores.release();
    }
    
    // Métodos para RMI
    public int getCantidadCocineros() {
        cerrojo.lock();
        try {
            return listCocineros.size();
        } finally {
            cerrojo.unlock();
        }
    }
    public int getCantidadVendedores() {
        cerrojo.lock();
        try {
            return listVendedores.size();
        } finally {
            cerrojo.unlock();
        }
    }
    public int getStockCafes() {
        cerrojo.lock();
        try {
            return this.cafes;
        } finally {
            cerrojo.unlock();
        }
    }
    public int getStockRosquillas() {
        cerrojo.lock();
        try {
            return this.rosquillas;
        } finally {
            cerrojo.unlock();
        }
    }
}
