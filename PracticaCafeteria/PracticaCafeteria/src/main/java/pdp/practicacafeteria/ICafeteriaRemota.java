/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package pdp.practicacafeteria;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author sergiopavon
 */
public interface ICafeteriaRemota extends Remote {
    
    public int obtenerClientesParque() throws RemoteException;
    public int obtenerClientesMostrador() throws RemoteException;
    public int obtenerClientesCaja() throws RemoteException;
    public int obtenerClientesConsumicion() throws RemoteException;
    
    public int obtenerCocinerosCocina() throws RemoteException;
    public int obtenerCocinerosDespensa() throws RemoteException;
    
    public int obtenerVendedoresDespensa() throws RemoteException;
    public int obtenerVendedoresMostrador() throws RemoteException;
    public int obtenerPersonalDescanso() throws RemoteException; // Suma de ambos
    
    public int obtenerStockDespensaCafe() throws RemoteException;
    public int obtenerStockDespensaRosquillas() throws RemoteException;
    public int obtenerStockMostradorCafe() throws RemoteException;
    public int obtenerStockMostradorRosquillas() throws RemoteException;
    
    public double obtenerRecaudacion() throws RemoteException;
    
    // Operación de control para pausar o reanudar el servidor
    public void pausarOReanudarServidor() throws RemoteException;
}
