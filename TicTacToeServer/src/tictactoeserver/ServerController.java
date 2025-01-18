/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tictactoeserver;

import com.google.gson.Gson;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import tictactoedb.DatabaseDao;
import tictactoedb.DatabaseDaoImpl;
import utilities.Codes;

/**
 *
 * @author Ziad-Elshemy
 */
public class ServerController {
    
    DataInputStream ear;
    PrintStream mouth;
    Socket playerSocket;
    static Vector<ServerController> playersList = new Vector<>();
    static int i =1;
    Thread thread;
    String userName;
    String playSympol;
    ArrayList requestData;
    Gson gson = new Gson();
    DatabaseDao myDatabase = new DatabaseDaoImpl();
    
    
    public ServerController(Socket socket){
        try {
            playerSocket = socket;
            ear = new DataInputStream(socket.getInputStream());
            mouth = new PrintStream(socket.getOutputStream());
            userName = "player"+i;
            i++;
            playersList.add(this);
            //System.out.println("Test Controller");
            System.out.println("==========================");
            for(ServerController player : playersList){
                System.out.println(""+player.userName);
                
            }
            System.out.println("==========================");
            thread = new Thread(){
                @Override
                public void run() {
                    while (true) {                        
                        try {
                            String json = ear.readLine();
                            System.out.println("the sendRequest data in server: "+json);
                            requestData = gson.fromJson(json, ArrayList.class);
                            double code = (double)requestData.get(0);
                            if(code == Codes.REGESTER_CODE){
                                String jsonPlayerData = (String)requestData.get(1);
                                System.out.println("the Player data in server: "+jsonPlayerData);
                                int databaseResult = myDatabase.register(jsonPlayerData);
                                requestData.clear();
                                requestData.add(Codes.REGESTER_CODE);
                                requestData.add(databaseResult);
                                //[1,1]
                                mouth.println(requestData);
                                for(ServerController player : playersList){
                                    System.out.println(""+player.userName);
                                    System.out.println(""+player.playerSocket.getLocalPort());
                                    if(player.userName.equals("player2")){
                                        ArrayList test = new ArrayList();
                                        test.add("hi player 2 this message is from "+userName);
                                        player.mouth.println(test);
                                    }
                                    if(player.userName.equals("player1")){
                                        ArrayList test = new ArrayList();
                                        test.add(100);
                                        player.mouth.println(test);
                                    }
                                }
                                
                            }
                            if(code == Codes.CHANGE_PASSWORD_CODE)
                            {
                                // System.out.println("Request fron EDITPROFILE in server: "+json);
                                 String jsonPlayerData = (String)requestData.get(1);
                                 System.out.println("Edit Data in Server: "+jsonPlayerData);
                                 int dataDaseResult = myDatabase.editProfile(jsonPlayerData);
                                 requestData.clear();
                                 requestData.add(Codes.CHANGE_PASSWORD_CODE);
                                 requestData.add(dataDaseResult);
                                 mouth.println(requestData);
                            }
                            
                        } catch (IOException ex) {
                            Logger.getLogger(ServerController.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
                
            };
            thread.start();
            
        } catch (IOException ex) {
            Logger.getLogger(ServerController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
