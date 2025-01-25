package tictactoeserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server extends Thread {
    ServerSocket serverSocket;
    ServerController serverController;
    static int counter = 0;
    
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
            // you need to close all connections if you have players in the playersrList.
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
                Socket playerSocket;
                playerSocket = serverSocket.accept();
                serverController = new ServerController(playerSocket);
                System.out.println("new player added");
                
                /*
                String str = serverController.dis.readLine();
                counter++;
                String msg = "";
                for(ServerController player : ServerController.playersList){
                    msg = ""+str+counter;
                    player.ps.println(msg);
                }
                if(msg.equals("Player No.5")){
                    myDatabase.register();
                }
                */
                
                
                
            } catch (IOException ex) {
                System.out.println("can not add player!");
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    
    
}
