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
public class Mostrador {
    private Log log;
    private InterfazCafeteria gui;

    // Stock
    private int cafes = 0;
    private int rosquillas = 0;

    // Aforos y colas
    private Semaphore aforoVendedores = new Semaphore(20);
    
    // ColaEsperaMostrador (máximo 20 esperando dentro de la cafetería). 
    // Los que no quepan aquí, se quedarán en Entrada Cafeteria
    private Semaphore colaEsperaMostrador = new Semaphore(20);
    
    // Max 5 clientes siendo atendidos
    private Semaphore aforoAtencionClientes = new Semaphore(5);

    private Lock cerrojo = new ReentrantLock();
    private Condition hayStock = cerrojo.newCondition(); 
    
    // Listas
    private ArrayList<String> listVendedores = new ArrayList<>();
    private ArrayList<String> listEntrada = new ArrayList<>();      // Nuevos: Gente esperando entrar al local
    private ArrayList<String> listClientesCola = new ArrayList<>(); // Gente dentro (máximo 20)
    private ArrayList<String> listClientesAtencion = new ArrayList<>(); // En mostrador (máximo 5)

    public Mostrador(Log log, InterfazCafeteria gui) {
        this.log = log;
        this.gui = gui;
    }

    // VENDEDOR
    public void entrarVendedor(String id) throws InterruptedException {
        aforoVendedores.acquire();
        cerrojo.lock();
        try {
            listVendedores.add(id);
            gui.setMostradorVendedores(listVendedores.toString());
        } finally {
            cerrojo.unlock();
        }
    }

    public void reponer(String id, int nCafes, int nRosquillas) {
        cerrojo.lock();
        try {
            this.cafes += nCafes;
            this.rosquillas += nRosquillas;
            
            log.escribirLog("Vendedor " + id + " repone " + nCafes + " cafés y " + nRosquillas + " rosquillas en Mostrador.");
            gui.setMostradorCafes(this.cafes);
            gui.setMostradorRosquillas(this.rosquillas);
            
            // Despertar a clientes esperando stock
            hayStock.signalAll();
        } finally {
            cerrojo.unlock();
        }
    }

    public void salirVendedor(String id) {
        cerrojo.lock();
        try {
            listVendedores.remove(id);
            gui.setMostradorVendedores(listVendedores.toString());
        } finally {
            cerrojo.unlock();
        }
        aforoVendedores.release();
    }

    // CLIENTE
    
    // 1. Cliente nuevo, llega y entra en Entrada Cafeteria
    public void entrarColaEspera(String id) throws InterruptedException {
        cerrojo.lock();
        try {
             listEntrada.add(id);
             gui.setEntradaCafeteria(listEntrada.toString());
        } finally {
            cerrojo.unlock();
        }
        
        // Intenta entrar a la colaEsperaMostrador (Semáforo de 20 huecos)
        // Si no hay hueco, se queda bloqueado aquí
        colaEsperaMostrador.acquire(); 
        
        // Consigue entrar: Pasa de la entrada a Esperando Mostrador
        cerrojo.lock();
        try {
            listEntrada.remove(id); // Sale de la puerta
            gui.setEntradaCafeteria(listEntrada.toString());
            
            listClientesCola.add(id); // Entra a la cola
            gui.setEsperandoMostrador(listClientesCola.toString());
        } finally {
            cerrojo.unlock();
        }
    }

    // 2. Pasar de la cola al mostrador (máximo 5)
    public void accederMostrador(String id) throws InterruptedException {
        aforoAtencionClientes.acquire(); // Espera ser uno de los 5 atendidos
        
        cerrojo.lock();
        try {
            // Sale de la cola
            listClientesCola.remove(id);
            gui.setEsperandoMostrador(listClientesCola.toString());
            colaEsperaMostrador.release(); // Libera un hueco en la cola de 20 para el siguiente de la entrada
            
            // Entra al mostrador (lista ClientesAtencion)
            listClientesAtencion.add(id);
            gui.setEnMostradorClientes(listClientesAtencion.toString());
        } finally {
            cerrojo.unlock();
        }
    }

    // 3. Pedir y esperar stock
    public void pedirYRecoger(String id, int demandaCafe, int demandaRosquilla) throws InterruptedException {
        cerrojo.lock();
        try {
            while (this.cafes < demandaCafe || this.rosquillas < demandaRosquilla) {
                hayStock.await();  // El cliente espera a que haya stock disponible
            }
            
            // Preparar el pedido
            this.cafes -= demandaCafe;
            this.rosquillas -= demandaRosquilla;
            
            log.escribirLog("Cliente " + id + " toma su pedido. Quedan C:" + this.cafes + " R:" + this.rosquillas);
            gui.setMostradorCafes(this.cafes);
            gui.setMostradorRosquillas(this.rosquillas);
            
        } finally {
            cerrojo.unlock();
        }
    }

    public void salirMostrador(String id) {
        cerrojo.lock();
        try {
            listClientesAtencion.remove(id);
            gui.setEnMostradorClientes(listClientesAtencion.toString());
        } finally {
            cerrojo.unlock();
        }
        // El cliente sale del mostrador y libera un hueco para que entre el siguiente
        aforoAtencionClientes.release(); 
    }
    
    // Métodos para RMI
    public int getCantidadVendedores() {
        cerrojo.lock();
        try {
            return listVendedores.size();
        } finally {
            cerrojo.unlock();
        }
    }
    public int getCantidadClientes() { // Suma de clientes en: cola  + atendiendo
        cerrojo.lock();
        try {
            return listClientesCola.size() + listClientesAtencion.size();
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