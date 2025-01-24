/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tictactoedb;

import com.google.gson.Gson;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.derby.jdbc.ClientDriver;

/**
 *
 * @author Ziad-Elshemy
 */
public class DatabaseDaoImpl implements DatabaseDao{
    
    Gson gson = new Gson();
    private static Connection con;
    private static ResultSet result;
    static ResultSet playerResult;
    private static PreparedStatement statement; 
    private static Statement selectStatement;
    
   static{ 
        try {
            DriverManager.registerDriver(new ClientDriver());
            con= DriverManager.getConnection("jdbc:derby://localhost:1527/tictactoe_db","root","root");
            result=selectAll();
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseDao.class.getName()).log(Level.SEVERE, null, ex);
        }
  
    }

    @Override
    public int register(String json){
        int result = 0;
        try {
//            PlayerDto player = new PlayerDto();
//            
//            player.setUserName("raed1998");
//            player.setName("Raed");
//            player.setPassword("raed123");

            PlayerDto player = gson.fromJson(json, PlayerDto.class);
            PreparedStatement pst = con.prepareStatement("insert into Players (username , name , password , gender ) values (? , ? , ? , ?)");
            pst.setString(1, player.getUserName());
            pst.setString(2, player.getName());
            pst.setString(3, player.getPassword());
            pst.setString(4, player.getGender()); 
            
            result = pst.executeUpdate();
            
        } catch (SQLException ex) {
           // Logger.getLogger(DatabaseDao.class.getName()).log(Level.SEVERE, null, ex);
            return 0;
        }
        return result;
    }

    @Override
    public int editProfile(String gsonrequest){
        int result = 0;
        try {
            PlayerDto player = this.gson.fromJson(gsonrequest, PlayerDto.class);
            //System.out.println(" IN editProfile UserNAme: "+player.getUserName()+" Password: "+player.getPassword());
            statement = con.prepareStatement("UPDATE PLAYERS SET PASSWORD = ? WHERE USERNAME = ?");
            statement.setString(1, player.getPassword());
            statement.setString(2, player.getUserName());
            result = statement.executeUpdate();
            
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseDao.class.getName()).log(Level.SEVERE, null, ex);
            result =  0;
        }
        return result;
    }
    
    @Override
  public String selectInfoForEdidProfilePage(String usename)
    {
        PlayerDto player = new PlayerDto();
        String playerJson=null;
        ResultSet selectResult;
        try {
            
            System.out.println("DTO selectInfoForEdidProfilePage");
          //  Connection con = getConnection();
            statement = con.prepareStatement("SELECT USERNAME , NAME , SCORE FROM PLAYERS WHERE USERNAME = ?",ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
            statement.setString(1, usename);
            selectResult = statement.executeQuery();
            selectResult.first();
            System.out.println("SeclectForEdit :"+selectResult.getString("NAME"));
            player.setUserName(selectResult.getString("USERNAME"));
            player.setName(selectResult.getString("NAME"));
            player.setScore(selectResult.getInt("SCORE"));
            playerJson = gson.toJson(player);
            System.out.println("jeson sent from DataDataBase: "+playerJson);
            selectResult.close();
//            pst.close();
//            con.close();
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return playerJson;
    }
   public static boolean insert(PlayerDto player) throws SQLException {
        
         statement = con.prepareStatement("INSERT INTO PLAYERS (username, name, password, score, gender,is_online, is_playing) VALUES (?, ?, ?,?,?,?,?)");
         statement.setString(1, player.getUserName());
         statement.setString(2, player.getName());
         statement.setString(3, player.getPassword());
         statement.setInt(4, player.getScore());
         statement.setString(5,player.getGender()); 
         statement.setBoolean(6,player.getIsOnline());
         statement.setBoolean(7, player.getIsPlaying());
         int resultInt = statement.executeUpdate();
         result=selectAll();
         
         return resultInt != 0;
    }
    
    public static boolean delete(String username) throws SQLException {
        
        statement = con.prepareStatement("DELETE FROM PLAYERS WHERE username=?");
        statement.setString(1, username);
        int resultInt = statement.executeUpdate();
        result=selectAll();
        return resultInt != 0;  
    }
     
    public static boolean updateUserState(PlayerDto player) throws SQLException {
        
         statement = con.prepareStatement("UPDATE PLAYERS SET score = ? , is_online = ? , is_playing = ? WHERE username=?");
         statement.setString(4, player.getUserName());
         statement.setInt(1, player.getScore());
         statement.setBoolean(2, player.getIsOnline());
         statement.setBoolean(3, player.getIsPlaying());
         int resultInt = statement.executeUpdate();
         
         result=selectAll();
         return resultInt != 0;  
    }
    
    public static ResultSet  selectAll() throws SQLException {
            selectStatement = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE) ;
            String queryString = "select * from PLAYERS";
            ResultSet selectResult = selectStatement.executeQuery(queryString) ;
            return selectResult;

    }
    
    public static ResultSet  selectUser(String username , String password) throws SQLException {
            String queryString = "SELECT * FROM PLAYERS WHERE username = ? AND password = ?";   
            statement = con.prepareStatement(queryString , ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
            statement.setString(1, username);
            statement.setString(2, password);
            playerResult = statement.executeQuery();
            return playerResult;

    }
    
    public static ResultSet  selectOnlineUsers() throws SQLException {
            String queryString = "SELECT * FROM PLAYERS WHERE is_online = ?";   
            statement = con.prepareStatement(queryString , ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
            statement.setBoolean(1, true);
            playerResult = statement.executeQuery();
            return playerResult;

    }
        
    
}

    
    