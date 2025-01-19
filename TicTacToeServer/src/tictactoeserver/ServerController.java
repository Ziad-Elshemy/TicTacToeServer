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
            //userName = "player"+i;
            userName = "ziad"+i;
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
                                
                                
                            }else if(code == Codes.CHANGE_PASSWORD_CODE)
                            {
                                // System.out.println("Request fron EDITPROFILE in server: "+json);
                                 String jsonPlayerData = (String)requestData.get(1);
                                 System.out.println("Edit Data in Server: "+jsonPlayerData);
                                 int dataDaseResult = myDatabase.editProfile(jsonPlayerData);
                                 requestData.clear();
                                 requestData.add(Codes.CHANGE_PASSWORD_CODE);
                                 requestData.add(dataDaseResult);
                                 mouth.println(requestData);
                            }else if(code == Codes.SEND_INVITATION_CODE)
                            {
                                // System.out.println("Request fron EDITPROFILE in server: "+json);
                                 String revieverUsername = (String)requestData.get(1);
                                 System.out.println("object of reciever player in Server: "+revieverUsername);
                                 
                                 PlayerDto player_data = gson.fromJson(revieverUsername, PlayerDto.class);
                                 
                                 System.out.println("user name of reciever player in Server: "+player_data.getUserName());
                                 
                                 for(ServerController player : playersList){
                                    //System.out.println(""+player.userName);
                                    //System.out.println(""+player.playerSocket.getLocalPort());
                                    if(player.userName.equals(player_data.getUserName().toString())){
                                        requestData.clear();
                                        requestData.add(Codes.SEND_INVITATION_CODE);
                                        requestData.add(userName);
                                        player.mouth.println(gson.toJson(requestData));
                                    }
                                }
                                
                            }
                            //send accepted or rejected
                            else if(code == Codes.INVITATION_REPLY_CODE)
                            {
                                // System.out.println("Request fron EDITPROFILE in server: "+json);
                                 double isAccepted = (double)requestData.get(1);
                                 String senderData = (String)requestData.get(2);
                                 
                                 System.out.println("object of sender player in Server: "+senderData);
                                 PlayerDto player_data = gson.fromJson(senderData, PlayerDto.class);
                                 System.out.println("user name of sender player in Server: "+player_data.getUserName());
                                 
                                 for(ServerController player : playersList){
                                    //System.out.println(""+player.userName);
                                    //System.out.println(""+player.playerSocket.getLocalPort());
                                    
                                    if(player.userName.equals(player_data.getUserName().toString())){
                                        //don't forget to handle the reject you need if condition here
                                        requestData.clear();
                                        requestData.add(Codes.INVITATION_REPLY_CODE);
                                        requestData.add(isAccepted);
                                        requestData.add(userName);
                                        player.mouth.println(gson.toJson(requestData));
                                    }
                                }
                                
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
