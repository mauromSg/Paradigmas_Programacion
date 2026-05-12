/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pdp.practicacafeteria;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author sergiopavon
 */
public class Log {
    private PrintWriter writer;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public Log() {
        try {
            writer = new PrintWriter(new FileWriter("evolucion_cafeteria.txt"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }    
    
    // Synchronized para que solo un hilo pueda escribir a la vez
    public synchronized void escribirLog(String mensaje) {
        String tiempo = LocalDateTime.now().format(formatter);
        String linea = "[" + tiempo + "] " + mensaje;
        writer.println(linea);
        writer.flush(); 
    }
    
}
