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
import tictactoedb.NetworkAccessLayer;
import tictactoedb.DatabaseDao;
import tictactoedb.PlayerDto;
import tictactoedb.DatabaseDaoImpl;
import utilities.Codes;

/**
 *
 * @author Ziad-Elshemy
 */
public class ServerController {
    
    private DataInputStream dataInputStream;
    private PrintStream outputStream;
    private Socket playerSocket;
    private static Vector<ServerController> playersList = new Vector<>();
    private Thread thread;
    private String userName;
    private String playSympol;
    private ArrayList requestData;
    private Gson gson = new Gson();
    private PlayerDto databaseResult;
    private PlayerDto currentPlayer;
    private String jsonPlayerData;
    private DatabaseDao myDatabase = new DatabaseDaoImpl();
    double operationCode;
    private ArrayList onlinePlayers;


    
    
    public ServerController(Socket socket){
        
        try {
            playerSocket = socket;
            dataInputStream = new DataInputStream(socket.getInputStream());
            outputStream = new PrintStream(socket.getOutputStream());
            playersList.add(this);
     
        
            thread = new Thread(){
                @Override
                public void run() {
                    while (true) {                        
                        try {
                            String json = dataInputStream.readLine();
                            System.out.println("the sendRequest data in server: "+json);
                            requestData = gson.fromJson(json, ArrayList.class);   
                            double code = (double) requestData.get(0);
                            
                            if(code == Codes.REGESTER_CODE){ 
                                
                                
                                operationCode=code;
                                jsonPlayerData = (String)requestData.get(1);
                                currentPlayer = gson.fromJson(jsonPlayerData, PlayerDto.class);                                
                                databaseResult = NetworkAccessLayer.register(currentPlayer);
                                requestData.clear();
                                requestData.add(Codes.REGESTER_CODE);
                                String jsonDatabaseResult = gson.toJson(databaseResult); 
                                requestData.add(jsonDatabaseResult);
                                outputStream.println(requestData); 
                                currentPlayer=databaseResult;
                                currentPlayer.setIsOnline(true); 
                                currentPlayer.setIsPlaying(true); 
                                 
                                NetworkAccessLayer.makePlayerOnline(currentPlayer);
                                
                                sendMessageToAllPlayers(); 

                               
                              
                            }else if(code == Codes.LOGIN_CODE){
                                

                                operationCode=code;
                                jsonPlayerData = (String)requestData.get(1);
                                currentPlayer = gson.fromJson(jsonPlayerData, PlayerDto.class);                                
                                databaseResult = NetworkAccessLayer.login(currentPlayer.getUserName(),currentPlayer.getPassword());
                                requestData.clear();
                                requestData.add(Codes.LOGIN_CODE);
                                String jsonDatabaseResult = gson.toJson(databaseResult); 
                                requestData.add(jsonDatabaseResult);
                                outputStream.println(requestData);
                                currentPlayer=databaseResult;
                                System.out.println(currentPlayer.getName());
                                currentPlayer.setIsOnline(true); 
                                currentPlayer.setIsPlaying(true);
                                 
                                NetworkAccessLayer.makePlayerOnline(currentPlayer);
                                
                                sendMessageToAllPlayers(); 
                              
    
                             }else if(code == Codes.CHANGE_PASSWORD_CODE){
                                 operationCode=code;
                                 String jsonPlayerData = (String)requestData.get(1);
                                 System.out.println("Edit Data in Server: "+jsonPlayerData);
                                 int dataDaseResult = myDatabase.editProfile(jsonPlayerData);
                                 requestData.clear();
                                 requestData.add(Codes.CHANGE_PASSWORD_CODE);
                                 requestData.add(dataDaseResult);
                                 outputStream.println(requestData);
                                 
                             }else if(code == Codes.LOGOUT_CODE ){
                                 
                                 
                                 currentPlayer.setIsOnline(false); 
                                 
                                 NetworkAccessLayer.logout(currentPlayer);
                                 
                                 playersList.remove(this);
                                 currentPlayer.setIsOnline(false); 
                                 currentPlayer.setIsPlaying(false);
                                 sendMessageToAllPlayers();
                                 break;
                            
                            
                            }
                            
                        } catch (IOException ex) {
                            
                           
                        } catch (SQLException ex) {
                            
                            
                            requestData.clear();
                            requestData.add(operationCode);
                            databaseResult=null;
                            String jsonDatabaseResult = gson.toJson(databaseResult);
                            requestData.add(jsonDatabaseResult);
                            outputStream.println(requestData);
                            
                        }
                    }
                }
                
            };
            thread.start();
            
            
   
            
        } catch (IOException ex) {
            Logger.getLogger(ServerController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void sendMessageToAllPlayers(){
        
        try {
            ArrayList result=new ArrayList<>();
            result.add(Codes.GET_ONLINE_PLAYERS); 
            onlinePlayers=NetworkAccessLayer.getOnlinePlayers();
            String jsonDatabaseResult = gson.toJson(onlinePlayers);
            result.add(jsonDatabaseResult);
            
            for(ServerController player : playersList){
                
                player.outputStream.println(result);
                
            }
        } catch (SQLException ex) {
            Logger.getLogger(ServerController.class.getName()).log(Level.SEVERE, null, ex);
        }
    

    }
    

    
}
