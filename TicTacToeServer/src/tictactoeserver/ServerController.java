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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import tictactoedb.Authentication;
import tictactoedb.DatabaseDao;
import tictactoedb.PlayerDto;
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
    Thread thread;
    String userName;
    String playSympol;
    ArrayList requestData;
    Gson gson = new Gson();
    PlayerDto databaseResult;
    PlayerDto currentPlayer;
    String jsonPlayerData;
    
    
    public ServerController(Socket socket){
        try {
            playerSocket = socket;
            ear = new DataInputStream(socket.getInputStream());
            mouth = new PrintStream(socket.getOutputStream());
            playersList.add(this);
            System.out.println("Test Controller");
            thread = new Thread(){
                @Override
                public void run() {
                    while (true) {                        
                        try {
                            String json = ear.readLine();
                            System.out.println("the sendRequest data in server: "+json);
                            requestData = gson.fromJson(json, ArrayList.class);
                            System.out.println(requestData.get(0).getClass().getName());
                            double code = (double) requestData.get(0);
                            
                            
                            if(code==Codes.REGESTER_CODE){  
                                
                            
                                
                                jsonPlayerData = (String)requestData.get(1);
                                System.out.println("the Player data in server: "+jsonPlayerData);
                                currentPlayer = gson.fromJson(jsonPlayerData, PlayerDto.class);                                
                                databaseResult = Authentication.register(currentPlayer);
                                requestData.clear();
                                requestData.add(Codes.REGESTER_CODE);
                                String jsonDatabaseResult = gson.toJson(databaseResult); // Serialize PlayerDto to JSON
                                requestData.add(jsonDatabaseResult);
                                mouth.println(requestData); 
                              
                            }else if(code == Codes.LOGIN_CODE){
                                
                            
  
                                jsonPlayerData = (String)requestData.get(1);
                                System.out.println("the Player data in server: "+jsonPlayerData);
                                currentPlayer = gson.fromJson(jsonPlayerData, PlayerDto.class);                                
                                databaseResult = Authentication.login(currentPlayer.getUserName(),currentPlayer.getPassword());
                                requestData.clear();
                                requestData.add(Codes.LOGIN_CODE);
                                String jsonDatabaseResult = gson.toJson(databaseResult); // Serialize PlayerDto to JSON
                                requestData.add(jsonDatabaseResult);
                                mouth.println(requestData); 
                                
                                                              
                                
                                
                            
                    }
                            
                           
                          
                        
                        } catch (IOException ex) {
                            Logger.getLogger(ServerController.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (SQLException ex) {
                           // Logger.getLogger(ServerController.class.getName()).log(Level.SEVERE, null, ex);
                           databaseResult=null;
                            requestData.clear();
                            requestData.add(Codes.REGESTER_CODE);
                            String jsonDatabaseResult = gson.toJson(databaseResult); // Serialize PlayerDto to JSON
                            requestData.add(jsonDatabaseResult);
                            mouth.println(requestData);
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
