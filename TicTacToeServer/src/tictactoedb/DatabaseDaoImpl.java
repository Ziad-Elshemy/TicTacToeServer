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
import java.util.ArrayList;
import java.util.List;
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
            con.setAutoCommit(false);
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

    @Override
    public int deleteAccount(String jsonPlayer) {
        int excutionStatus = 0;
        try {
            PlayerDto player = gson.fromJson(jsonPlayer, PlayerDto.class);
            PreparedStatement stmnt = con.prepareStatement("DELETE FROM PLAYERS WHERE USERNAME = ?");
            stmnt.setString(1,player.getUserName() );
            int result = stmnt.executeUpdate();
            if(result>0)
            {
                excutionStatus=1;
                playerResult = stmnt.executeQuery();
            }
            stmnt.close();
            con.close();
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseDaoImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return excutionStatus;
    }
    
        
    
public static List<String> getAvailablePlayers() {
    List<String> availablePlayers = new ArrayList<>();
    try {
        ResultSet resultSet = selectOnlineUsers(); // Assuming this fetches online users from the database
        while (resultSet.next()) {
            String username = resultSet.getString("USERNAME");
            // Add the username to the list
            availablePlayers.add(username);
        }
        resultSet.close();
    } catch (SQLException ex) {
        Logger.getLogger(DatabaseDao.class.getName()).log(Level.SEVERE, null, ex);
    }
    return availablePlayers;
}

    public static List<String> getTopPlayers() {
        List<String> topPlayers = new ArrayList<>();
        try {
            // Query to select players ordered by score descending
            String queryString = "SELECT * FROM PLAYERS ORDER BY SCORE DESC";
            statement = con.prepareStatement(queryString);
            ResultSet resultSet = statement.executeQuery();

            // Retrieve top players
            while (resultSet.next()) {
                String name = resultSet.getString("NAME");
                int score = resultSet.getInt("SCORE");
                topPlayers.add(name + " : " + score);
            }
            resultSet.close();
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return topPlayers;
    }

    public static void updatePlayerScore(String username, int scoreChange) throws SQLException {

        if (scoreChange == 0) {
            return; 
        }

        String query = "UPDATE players SET score = score + ? WHERE username = ?";
        try (PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setInt(1, scoreChange);
            pstmt.setString(2, username);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("No player found with username: " + username);
            }
        }
    }


    public static boolean playerExists(String username) throws SQLException {
    String query = "SELECT COUNT(*) FROM players WHERE username = ?";
    try (PreparedStatement pstmt = con.prepareStatement(query)) {
        pstmt.setString(1, username);
        ResultSet rs = pstmt.executeQuery();
        if (rs.next()) {
            return rs.getInt(1) > 0;
        }
    }
    return false;
    }

    public static int[] getPlayerStatusCounts() throws SQLException {
        int[] counts = new int[3]; // [online, offline, inGame]
        String query = "SELECT is_online, is_playing FROM players";

        try (PreparedStatement pstmt = con.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) { 

            while (rs.next()) {
                boolean isOnline = rs.getBoolean("is_online");
                boolean isPlaying = rs.getBoolean("is_playing");
                if (isOnline) {
                    if (isPlaying) {
                        counts[2]++; // inGame
                    } else {
                        counts[0]++; // online
                    }
                } else {
                    counts[1]++; // offline
                }
            }
        }
        return counts;
    }
}

    
    