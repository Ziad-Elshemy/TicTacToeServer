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
                                //mouth.println(requestData);
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
                                mouth.println(requestData);
                                
                                
                            }else if(code == Codes.CHANGE_PASSWORD_CODE)
                            {
                                // System.out.println("Request fronm SELECT FOR EDITPROFILE in server: "+json);
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
                            else if(code == Codes.SELECT_DATA_FOR_EDIT_PROFILE_CODE)
                            {
                                 System.out.println("SERVER CONTROLLER EDITPROFILE: "+json);
                                 String jsonPlayerData = (String)requestData.get(1);
                                 System.out.println("Edit Data in Server: "+jsonPlayerData);
                                 String dataDaseResult = myDatabase.selectInfoForEdidProfilePage(jsonPlayerData);
                                 requestData.clear();
                                 gson.toJson(dataDaseResult);
                                 
                                 //System.out.println("Player in Server Contoller : "+dataDaseResult.getName()+","+dataDaseResult.getUserName()+","+dataDaseResult.getScore());
                                 requestData.add(Codes.SELECT_DATA_FOR_EDIT_PROFILE_CODE);
                                 requestData.add(dataDaseResult);
                                 System.out.println("Jeson Request Data: "+requestData.getClass());
                                 mouth.println(requestData);
                            }
                            else if(code == Codes.SEND_PLAY_ON_BOARD_CODE)
                            {
                                // System.out.println("Request fron EDITPROFILE in server: "+json);
                                String gameData = (String)requestData.get(1);
                                 
                                 System.out.println("object of sender game_data in Server: "+gameData);
                                 ArrayList<String> game_data = gson.fromJson(gameData, ArrayList.class);
                                 System.out.println("user name of enemy player in Server: "+game_data.get(0));
                                 String enemyUsername = (String)game_data.get(0);
                                 String enemySympol = (String)game_data.get(1);
                                 String clicked_btn_id = (String)game_data.get(2);
                                 System.out.println("user name of reciever player in Server: "+enemyUsername);
                                 System.out.println("object of reciever player in Server: "+enemySympol);
                                 
                                 //PlayerDto player_data = gson.fromJson(revieverUsername, PlayerDto.class);
                                 
                                 
                                 
                                 for(ServerController player : playersList){
                                    //System.out.println(""+player.userName);
                                    //System.out.println(""+player.playerSocket.getLocalPort());
                                     System.out.println("players list: "+player.userName);
                                    if(player.userName.equals(enemyUsername)){
                                        System.out.println("test SEND_PLAY_ON_BOARD_CODE");
                                        requestData.clear();
                                        requestData.add(Codes.SEND_PLAY_ON_BOARD_CODE);
                                        requestData.add(userName);
                                        requestData.add(enemySympol);
                                        requestData.add(clicked_btn_id);
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
