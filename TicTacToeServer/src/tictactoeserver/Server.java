/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tictactoeserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Ziad-Elshemy
 */
public class Server extends Thread {
    ServerSocket serverSocket;
    ServerController serverController;
    
    public Server(){
    
        try {
            serverSocket = new ServerSocket(5005);
            start();
            System.out.println("server is online");
        } catch (IOException ex) {
            System.out.println("server Constructor error");
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void stopServer(){
        try {
            serverSocket.close();
            stop();
            System.out.println("server is offline");
        } catch (IOException ex) {
            System.out.println("stopServer Function error");
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void run() {
        while (true) {            
            try {
                Socket socket;
                socket = serverSocket.accept();
                serverController = new ServerController(socket);
            } catch (IOException ex) {
                System.out.println("new player added");
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    
    
}
