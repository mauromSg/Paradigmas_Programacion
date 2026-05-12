/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pdp.practicacafeteria;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @author sergiopavon
 */
public class Caja {
    private Log log;
    private InterfazCafeteria gui;

    private double recaudacionTotal = 0.0;
    
    // Aforo caja: 10 Clientes
    private Semaphore aforoCaja = new Semaphore(10);
    
    // Cerrojo para proteger variable compartida y listas
    private Lock cerrojo = new ReentrantLock();
    
    private ArrayList<String> listEsperandoCaja = new ArrayList<>(); // cola  antes de coger máquina autopago
    private ArrayList<String> listEnCaja = new ArrayList<>(); // usando la máquina autopago

    public Caja(Log log, InterfazCafeteria gui) {
        this.log = log;
        this.gui = gui;
    }
    
    public void llegarColaCaja(String id) {
        cerrojo.lock();
        try {
            listEsperandoCaja.add(id);
            gui.setEsperandoCaja(listEsperandoCaja.toString());
        } finally {
            cerrojo.unlock();
        }
    }

    public void usarMaquinaPago(String id) throws InterruptedException {
        aforoCaja.acquire(); // Espera libre una de las 10 máquinas
        
        cerrojo.lock();
        try {
            listEsperandoCaja.remove(id);
            gui.setEsperandoCaja(listEsperandoCaja.toString());
            
            listEnCaja.add(id);
            gui.setEnCaja(listEnCaja.toString());
        } finally {
            cerrojo.unlock();
        }
    }
    
    public void confirmarPago(String id, double importe) {
        cerrojo.lock();
        try {
            recaudacionTotal += importe;
            gui.setRecaudacion(String.format("%.2f €", recaudacionTotal));
            log.escribirLog("Cliente " + id + " paga " + importe + "€. Total: " + recaudacionTotal);
            
            // Sale de la caja
            listEnCaja.remove(id);
            gui.setEnCaja(listEnCaja.toString());
        } finally {
            cerrojo.unlock();
        }
        
        // Deja la máquina libre
        aforoCaja.release();
    }
    
    // --- MÉTODOS PARA RMI ---
    public int getCantidadClientes() { // Cola + Pagando
        cerrojo.lock();
        try {
            return listEsperandoCaja.size() + listEnCaja.size();
        } finally {
            cerrojo.unlock();
        }
    }
    public double getRecaudacion() {
        cerrojo.lock();
        try {
            return this.recaudacionTotal;
        } finally {
            cerrojo.unlock();
        }
    }
}
