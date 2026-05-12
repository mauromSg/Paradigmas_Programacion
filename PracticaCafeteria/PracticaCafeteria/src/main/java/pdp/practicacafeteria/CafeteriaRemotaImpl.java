/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pdp.practicacafeteria;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 *
 * @author sergiopavon
 */
public class CafeteriaRemotaImpl extends UnicastRemoteObject implements ICafeteriaRemota {
    
    private ControlAforos aforos;
    private Despensa despensa;
    private Mostrador mostrador;
    private Caja caja;
    private InterfazCafeteria guiServer;

    public CafeteriaRemotaImpl(ControlAforos aforos, Despensa despensa, Mostrador mostrador, Caja caja, InterfazCafeteria guiServer) throws RemoteException {
        super();
        this.aforos = aforos;
        this.despensa = despensa;
        this.mostrador = mostrador;
        this.caja = caja;
        this.guiServer = guiServer;
    }

    public int obtenerClientesParque() throws RemoteException {
        return aforos.getCantidadParque();
    }

    public int obtenerClientesMostrador() throws RemoteException {
        return mostrador.getCantidadClientes();
    }

    public int obtenerClientesCaja() throws RemoteException {
        return caja.getCantidadClientes();
    }

    public int obtenerClientesConsumicion() throws RemoteException {
        return aforos.getCantidadAreaConsumicion();
    }

    public int obtenerCocinerosCocina() throws RemoteException {
        return aforos.getCantidadCocina();
    }

    public int obtenerCocinerosDespensa() throws RemoteException {
        return despensa.getCantidadCocineros();
    }

    public int obtenerVendedoresDespensa() throws RemoteException {
        return despensa.getCantidadVendedores();
    }

    public int obtenerVendedoresMostrador() throws RemoteException {
        return mostrador.getCantidadVendedores();
    }

    public int obtenerPersonalDescanso() throws RemoteException {
        return aforos.getCantidadDescanso();
    }

    public int obtenerStockDespensaCafe() throws RemoteException {
        return despensa.getStockCafes();
    }

    public int obtenerStockDespensaRosquillas() throws RemoteException {
        return despensa.getStockRosquillas();
    }

    public int obtenerStockMostradorCafe() throws RemoteException {
        return mostrador.getStockCafes();
    }

    public int obtenerStockMostradorRosquillas() throws RemoteException {
        return mostrador.getStockRosquillas();
    }

    public double obtenerRecaudacion() throws RemoteException {
        return caja.getRecaudacion();
    }

    public void pausarOReanudarServidor() throws RemoteException {
        // Llamamos al método de la GUI del servidor que ya gestiona la pausa y el botón
        guiServer.cambiarEstadoPausa();
    }
}
