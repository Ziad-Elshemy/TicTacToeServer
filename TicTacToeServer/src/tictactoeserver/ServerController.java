/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tictactoeserver;

import com.google.gson.Gson;
import java.io.DataInputStream;
import java.io.DataOutputStream;
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
    public Socket playerSocket;
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
                            //receive theclient requests
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
                                //send the result of database query to the client
                                System.out.println("request Data :"+requestData);

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
                                        new Thread(new GameHandler(playerSocket, player.playerSocket));
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
 class GameHandler implements Runnable
{

    Socket player1;
    Socket player2;
    DataOutputStream toPlayer1;
    DataInputStream fromPlayer1;
    DataInputStream fromPlayer2;
    DataOutputStream toPlayer2;
    Gson gsonData;
    
    private final int YOUR_TURN = 1;
     private final int OPPONINT_TURN = 0;
    
    private char [][] board = new char[3][3];//X = 1 , O= -1
    
    public GameHandler(Socket player1 , Socket player2)
    {
        this.player1 = player1;
        this.player2 = player2;
        initializeBoard(board);
    }

    @Override
    public void run() 
    {
        try 
        {
            fromPlayer1 = new DataInputStream(player1.getInputStream());
            toPlayer1 = new DataOutputStream(player1.getOutputStream());
            fromPlayer2 = new DataInputStream(player2.getInputStream());
            toPlayer2 = new DataOutputStream(player2.getOutputStream());
            //the server notify player1 that it is his turn 
            toPlayer1.writeInt(YOUR_TURN);
            
            while(true)
            {
                //recieve move from player1
                String data1 = fromPlayer1.readLine();
                ArrayList dataFromUser1 = gsonData.fromJson(data1, ArrayList.class);
                int row = (int) dataFromUser1.get(0);
                int coloumn = (int) dataFromUser1.get(1);
                board[row][coloumn]='X';
                //notify player 1 its player 2 turn
                toPlayer1.writeInt(OPPONINT_TURN);
                
                if(isWon('X'))
                {
                    
                    break;
                }
                else if(isFull())
                {
                    
                    break;
                }
                else
                {
                    //notify that its player2 turn
                    toPlayer2.writeInt(YOUR_TURN);
                    //send player1 move
                    sendMove(toPlayer2, row, coloumn);
                }
                //receive move from player 2
                 String data2 = fromPlayer1.readLine();
                 ArrayList dataFromUser2 = gsonData.fromJson(data2, ArrayList.class);
                 row = (int) dataFromUser2.get(0);
                 coloumn = (int) dataFromUser2.get(1);
                 board[row][coloumn]='O';
                 
                 if(isWon('O'))
                {
                    
                    break;
                }
                else if(isFull())
                {
                    
                    break;
                }
                else
                {
                    //notify that its player1 turn
                    toPlayer1.writeInt(YOUR_TURN);
                    //send player1 move
                    sendMove(toPlayer1, row, coloumn);
                }
            }
        } 
        catch (IOException ex) 
        {
            Logger.getLogger(GameHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }
    
    private void initializeBoard ( char [][] board)
    {
        
    }

    private boolean isWon(char token) 
    {
        //check row for win
        //check coloumn for win
        //check diagonal for win
        return true;
    }

    private boolean isFull() {
        //check that there is no cell reminded
        return false;
    }
    private void sendMove (DataOutputStream out , int row , int col)
    {
        try 
        {
            out.writeInt(row);
            out.writeInt(col);
        } 
        catch (IOException ex) 
        {
            Logger.getLogger(GameHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
}