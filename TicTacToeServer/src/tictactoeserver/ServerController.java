/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tictactoeserver;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Ziad-Elshemy
 */
public class ServerController {
    
    DataInputStream dis;
    PrintStream ps;
    Socket playerSocket;
    static Vector<ServerController> playersList = new Vector<>();
    
    public ServerController(Socket socket){
        try {
            playerSocket = socket;
            dis = new DataInputStream(socket.getInputStream());
            ps = new PrintStream(socket.getOutputStream());
            playersList.add(this);
        } catch (IOException ex) {
            Logger.getLogger(ServerController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
