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
public class ControlAforos {
    private InterfazCafeteria gui;
    
    // Semáforos de capacidad
    private Semaphore semCocina = new Semaphore(100);
    private Semaphore semAreaConsumicion = new Semaphore(30);
    
    // Listas para visualización
    private ArrayList<String> listParque = new ArrayList<>();
    private ArrayList<String> listCocina = new ArrayList<>();
    private ArrayList<String> listSalaDescansoC = new ArrayList<>();
    private ArrayList<String> listSalaDescansoV = new ArrayList<>();
    private ArrayList<String> listConsumicion = new ArrayList<>();
    private ArrayList<String> listEsperandoConsumir = new ArrayList<>();

    // Cerrojo para proteger las listas
    private Lock cerrojoListas = new ReentrantLock();

    public ControlAforos(InterfazCafeteria gui) {
        this.gui = gui;
    }

    // Métodos para manejar las listas
    public void entrarParque(String id) {
        cerrojoListas.lock();
        try {
            listParque.add(id);
            gui.setParque(listParque.toString());
        } finally {
            cerrojoListas.unlock();
        }
    }
    
    public void salirParque(String id) {
        cerrojoListas.lock();
        try {
            listParque.remove(id);
            gui.setParque(listParque.toString());
        } finally {
            cerrojoListas.unlock();
        }
    }

    // Cocina, aforo máximo = 100
    public void entrarCocina(String id) throws InterruptedException {
        semCocina.acquire();
        cerrojoListas.lock();
        try {
            listCocina.add(id);
            gui.setCocina(listCocina.toString());
        } finally {
            cerrojoListas.unlock();
        }
    }

    public void salirCocina(String id) {
        cerrojoListas.lock();
        try {
            listCocina.remove(id);
            gui.setCocina(listCocina.toString());
        } finally {
            cerrojoListas.unlock();
        }
        semCocina.release();
    }
    
    // Sala de descanso, sin límite de aforo
    public void entrarDescansoCocinero(String id) {
        cerrojoListas.lock();
        try {
            listSalaDescansoC.add(id);
            gui.setSalaDescansoCocineros(listSalaDescansoC.toString());
        } finally {
            cerrojoListas.unlock();
        }
    }
    public void salirDescansoCocinero(String id) {
        cerrojoListas.lock();
        try {
            listSalaDescansoC.remove(id);
            gui.setSalaDescansoCocineros(listSalaDescansoC.toString());
        } finally {
            cerrojoListas.unlock();
        }
    }
    
    public void entrarDescansoVendedor(String id) {
        cerrojoListas.lock();
        try {
            listSalaDescansoV.add(id);
            gui.setSalaDescansoVendedores(listSalaDescansoV.toString());
        } finally {
            cerrojoListas.unlock();
        }
    }
    public void salirDescansoVendedor(String id) {
        cerrojoListas.lock();
        try {
            listSalaDescansoV.remove(id);
            gui.setSalaDescansoVendedores(listSalaDescansoV.toString());
        } finally {
            cerrojoListas.unlock();
        }
    }

    // Área de consumición, aforo máximo = 30
    public void esperarMesa(String id){
        cerrojoListas.lock();
        try {
            listEsperandoConsumir.add(id);
            gui.setEsperandoConsumir(listEsperandoConsumir.toString());
        } finally {
            cerrojoListas.unlock();
        }
    }

    public void entrarConsumir(String id) throws InterruptedException {
        semAreaConsumicion.acquire(); // Espera hueco (30 max)
        cerrojoListas.lock();
        try {
            listEsperandoConsumir.remove(id);
            gui.setEsperandoConsumir(listEsperandoConsumir.toString());
            listConsumicion.add(id);
            gui.setAreaConsumicion(listConsumicion.toString());
        } finally {
            cerrojoListas.unlock();
        }
    }

    public void salirConsumir(String id) {
        cerrojoListas.lock();
        try {
            listConsumicion.remove(id);
            gui.setAreaConsumicion(listConsumicion.toString());
        } finally {
            cerrojoListas.unlock();
        }
        semAreaConsumicion.release();
    }
    
    // Métodos para RMI
    public int getCantidadParque() {
        cerrojoListas.lock();
        try {
            return listParque.size();
        } finally {
            cerrojoListas.unlock();
        }
    }
    public int getCantidadCocina() {
        cerrojoListas.lock();
        try {
            return listCocina.size();
        } finally {
            cerrojoListas.unlock();
        }
    }
    public int getCantidadDescanso() { // Suma cocineros y vendedores
        cerrojoListas.lock();
        try {
            return listSalaDescansoC.size() + listSalaDescansoV.size();
        } finally {
            cerrojoListas.unlock();
        }
    }
    public int getCantidadAreaConsumicion() {
        cerrojoListas.lock();
        try {
            return listConsumicion.size();
        } finally {
            cerrojoListas.unlock();
        }
    }
}
