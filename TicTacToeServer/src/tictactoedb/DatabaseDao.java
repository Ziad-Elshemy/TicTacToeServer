package tictactoedb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.derby.jdbc.ClientDriver;



public class DatabaseDao {
    
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
    
    public static boolean insert(PlayerDto player) throws SQLException {
        
         statement = con.prepareStatement("INSERT INTO PLAYERS (username, name, password, score, is_online, is_playing) VALUES (?, ?, ?,?,?,?)");
         statement.setString(1, player.getUserName());
         statement.setString(2, player.getName());
         statement.setString(3, player.getPassword());
         statement.setInt(4, player.getScore());
         statement.setBoolean(5,player.getIsOnline());
         statement.setBoolean(6, player.getIsPlaying());
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
     
    public static boolean update(PlayerDto player) throws SQLException {
        
         statement = con.prepareStatement("UPDATE PLAYERS SET name = ? , password = ? , score = ? , is_online = ? , is_playing = ? WHERE username=?");
         statement.setString(6, player.getUserName());
         statement.setString(1, player.getName());
         statement.setString(2, player.getPassword());
         statement.setInt(3, player.getScore());
         statement.setBoolean(4, player.getIsOnline());
         statement.setBoolean(5, player.getIsPlaying());
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
     
}
