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
import javafx.application.Platform;
import javafx.scene.control.TextArea;
import tictactoedb.NetworkAccessLayer;
import tictactoedb.DatabaseDao;
import tictactoedb.PlayerDto;
import tictactoedb.DatabaseDaoImpl;
import tictactoedb.PlayerDto;
import utilities.Codes;

/**
 *
 * @author Ziad-Elshemy
 */
public class ServerController {
    
    private DataInputStream dataInputStream;
    private PrintStream outputStream;
    Socket playerSocket;
    static Vector<ServerController> playersList = new Vector<>();
    static int i =1;
    Thread thread;
    String userName;
    String playSympol;
    ArrayList requestData;
    Gson gson = new Gson();
    DatabaseDao myDatabase = new DatabaseDaoImpl();
    private PlayerDto databaseResult;
    private PlayerDto currentPlayer;
    private String jsonPlayerData;
    double operationCode;
    private ArrayList onlinePlayers;
    private OnboardStatisticController onboardStatisticController;
    private TextArea receivedDataArea;


    double code ;
    
    
    public ServerController(Socket socket ,OnboardStatisticController onboardStatisticController , TextArea receivedDataArea){
        this.onboardStatisticController = onboardStatisticController;
        this.receivedDataArea = receivedDataArea;
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
                           
                            
                            if(requestData!=null){
                                
                                code=(double) requestData.get(0);
                            
                            
                            
                            }else{
                                
                                break;
                            
                            }

                            if(code == Codes.REGESTER_CODE){
                                
                                String jsonPlayerData = (String)requestData.get(1);
                                System.out.println("the Player data in server: "+jsonPlayerData);
                                int databaseResult = myDatabase.register(jsonPlayerData);
                                requestData.clear();
                                requestData.add(Codes.REGESTER_CODE);
                                requestData.add(databaseResult);
                                outputStream.println(requestData);
                                currentPlayer = gson.fromJson(jsonPlayerData, PlayerDto.class);  
                                if (onboardStatisticController != null) {
                                    Platform.runLater(() -> {
                                        onboardStatisticController.updatePlayerStatusChart();
                                        onboardStatisticController.appendTextToArea(receivedDataArea, "A new player  " + currentPlayer.getUserName() + " has registered.\n");  
                                    });
                                    }
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
                                
                                if(currentPlayer!=null){
                                
                                System.out.println(currentPlayer.getName());
                                currentPlayer.setIsOnline(true); 
                                userName = currentPlayer.getUserName();                                                                        
                                NetworkAccessLayer.updateUserState(currentPlayer);
                                sendMessageToAllPlayers();
                                 // Notify OnboardStatisticController to update available players
                                 if (onboardStatisticController != null) {
                                    Platform.runLater(() -> {
                                        onboardStatisticController.updateAvailablePlayers();
                                        onboardStatisticController.updatePlayerStatusChart();
                                        onboardStatisticController.appendTextToArea(receivedDataArea, "Player " + currentPlayer.getUserName() + " has logged in.\n");                                    });
                                    }
                                
                                } 
                                
                            }else if(code == Codes.CHANGE_PASSWORD_CODE){
                                 String jsonPlayerData = (String)requestData.get(1);
                                 System.out.println("Edit Data in Server: "+jsonPlayerData);
                                 int dataDaseResult = myDatabase.editProfile(jsonPlayerData);
                                 requestData.clear();
                                 requestData.add(Codes.CHANGE_PASSWORD_CODE);
                                 requestData.add(dataDaseResult);
                                 outputStream.println(requestData);
                                 if (onboardStatisticController != null) {
                                    Platform.runLater(() -> {
                                        onboardStatisticController.appendTextToArea(receivedDataArea, "Player " + currentPlayer.getUserName() + " has changed password .\n");                                    });
                                    }
                            }else if(code == Codes.LOGOUT_CODE ){
                                 
                                 if (onboardStatisticController != null) {
                                    Platform.runLater(() -> {
                                        onboardStatisticController.updateAvailablePlayers();
                                        onboardStatisticController.updatePlayerStatusChart();
                                        onboardStatisticController.appendTextToArea(receivedDataArea, "Player " + currentPlayer.getUserName() + " has logged out.\n");
                                    });
                                 }
                                if(currentPlayer!=null){
                                 
                                 currentPlayer.setIsOnline(false); 
                                 currentPlayer.setIsPlaying(false); 
                                 
                                 NetworkAccessLayer.logout(currentPlayer);
                                 playersList.remove(this);
                                 currentPlayer.setIsOnline(false); 
                                 currentPlayer.setIsPlaying(false);
                                 NetworkAccessLayer.updateUserState(currentPlayer);
                                 sendMessageToAllPlayers();
                                 currentPlayer=null;
                                 
                                  // Notify OnboardStatisticController to update available players
                                if (onboardStatisticController != null) {
                                    Platform.runLater(() -> {
                                        onboardStatisticController.updateAvailablePlayers();
                                        onboardStatisticController.updatePlayerStatusChart();
                                    });
                                };

                                 break;
                                }
                            }else if(code == Codes.SEND_INVITATION_CODE)
                            {

                                 String recieverUsername = (String)requestData.get(1);
                                 System.out.println("object of reciever player in Server: "+recieverUsername);
                                 
                                 PlayerDto player_data = gson.fromJson(recieverUsername, PlayerDto.class);
                                 
                                 System.out.println("user name of reciever player in Server: "+player_data.getUserName());
                                 
                                 for(ServerController player : playersList){
                                 
                                    if(player.userName.equals(player_data.getUserName())){
                                        requestData.clear();
                                        requestData.add(Codes.SEND_INVITATION_CODE);
                                        requestData.add(currentPlayer);
                                        player.outputStream.println(gson.toJson(requestData));
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
                                        player.outputStream.println(gson.toJson(requestData));
                                        if(isAccepted==1.0){
                                            
                                             currentPlayer.setIsPlaying(true);  
                                             NetworkAccessLayer.updateUserState(currentPlayer);
                                             
                                             player_data.setIsPlaying(true);  
                                             NetworkAccessLayer.updateUserState(player_data);
                                             if (onboardStatisticController != null) {
                                                Platform.runLater(() -> {
                                                    onboardStatisticController.updateAvailablePlayers();
                                                    onboardStatisticController.updatePlayerStatusChart();
                                                    onboardStatisticController.appendTextToArea(receivedDataArea, "Player " + currentPlayer.getUserName() + " has started game \n");
                                                });
                                            }
                                        }
                                        System.out.println(isAccepted+"==========================================");
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
                                 outputStream.println(requestData);
                                 if (onboardStatisticController != null) {
                                                Platform.runLater(() -> {
                                                    onboardStatisticController.appendTextToArea(receivedDataArea, "Player " + currentPlayer.getUserName() + " etied profile \n");
                                                });
                                            }
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
                                        player.outputStream.println(gson.toJson(requestData));
                                        if (onboardStatisticController != null) {
                                                Platform.runLater(() -> {
                                                    onboardStatisticController.updateAvailablePlayers();
                                                    onboardStatisticController.updatePlayerStatusChart();
                                                    onboardStatisticController.appendTextToArea(receivedDataArea, "Player " + currentPlayer.getUserName() + " send invite \n");
                                                });
                                            }
                                    }
                                }
                                
                            }
                            else if(code == Codes.PLAY_AGAIN_CODE)
                            {
                                // System.out.println("Request fron EDITPROFILE in server: "+json);
                                 String revieverUsername = (String)requestData.get(1);
                                 System.out.println("object of reciever player in Server: "+revieverUsername);
                                 
                                 //PlayerDto player_data = gson.fromJson(revieverUsername, PlayerDto.class);
                                 
                                 //System.out.println("user name of reciever player in Server: "+player_data.getUserName());
                                 
                                 for(ServerController player : playersList){
                                    //System.out.println(""+player.userName);
                                    //System.out.println(""+player.playerSocket.getLocalPort());
                                    if(player.userName.equals(revieverUsername)){
                                        requestData.clear();
                                        requestData.add(Codes.PLAY_AGAIN_CODE);
                                        requestData.add(userName);
                                        player.outputStream.println(gson.toJson(requestData));
                                        if (onboardStatisticController != null) {
                                                Platform.runLater(() -> {
                                                    onboardStatisticController.updateAvailablePlayers();
                                                    onboardStatisticController.updatePlayerStatusChart();
                                                    onboardStatisticController.appendTextToArea(receivedDataArea, "Player " + currentPlayer.getUserName() + " has started game \n");
                                                });
                                            }
                                    }
                                }
                                
                            }
                            
                        } catch (IOException ex) {
                            Logger.getLogger(ServerController.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (SQLException ex) {
                            
                            requestData.clear();
                            requestData.add(operationCode);
                            databaseResult=null;
                            String jsonDatabaseResult = gson.toJson(databaseResult);
                            requestData.add(jsonDatabaseResult);
                            outputStream.println(requestData);
                            System.out.println(ex.toString());
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
    
public void handleGameResult(String winnerUsername, String loserUsername) {
    try {

        if (!DatabaseDaoImpl.playerExists(winnerUsername)) {
            throw new SQLException("Winner username not found: " + winnerUsername);
        }
        if (!DatabaseDaoImpl.playerExists(loserUsername)) {
            throw new SQLException("Loser username not found: " + loserUsername);
        }


        DatabaseDaoImpl.updatePlayerScore(winnerUsername, 1); 
        DatabaseDaoImpl.updatePlayerScore(loserUsername, 0); 

        // Notify OnboardStatisticController to update top players
        if (onboardStatisticController != null) {
            Platform.runLater(() -> {
                onboardStatisticController.updateTopPlayers();
                onboardStatisticController.appendTextToArea(receivedDataArea, "Updated scores for " + winnerUsername + " and " + loserUsername + ".\n");
            });
        } else {
            System.err.println("OnboardStatisticController is not initialized.");
        }
    } catch (SQLException e) {
        e.printStackTrace();
        if (onboardStatisticController != null) {
            Platform.runLater(() -> {
                onboardStatisticController.appendTextToArea(receivedDataArea, "Error updating scores: " + e.getMessage() + "\n");
            });
        }
    }
}
    
    public void updateAvailablePlayers() {
        Platform.runLater(() -> {
            onboardStatisticController.updateAvailablePlayers();
        });
    }

}
