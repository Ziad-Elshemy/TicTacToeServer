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
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.derby.jdbc.ClientDriver;

/**
 *
 * @author Ziad-Elshemy
 */
public class DatabaseDao {
    
    Gson gson = new Gson();
    
    public DatabaseDao(){
        try {
            DriverManager.registerDriver(new ClientDriver());
            //Connection con = getConnection();
            System.out.println("hello from database");
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseDao.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
//    public int register(){
//        int result = 0;
//        try {
//            PlayerDto player = new PlayerDto();
//            
//            player.setUserName("raed1998");
//            player.setName("Raed");
//            player.setPassword("raed123");
//            
//            Connection con = getConnection();
//            PreparedStatement pst = con.prepareStatement("insert into Players (username , name , password ) values (? , ? , ?)");
//            pst.setString(1, player.getUserName());
//            pst.setString(2, player.getName());
//            pst.setString(3, player.getPassword());
//            
//            result = pst.executeUpdate();
//            
//        } catch (SQLException ex) {
//            Logger.getLogger(DatabaseDao.class.getName()).log(Level.SEVERE, null, ex);
//            return 0;
//        }
//        return result;
//    }
    
    public int register(String json){
        int result = 0;
        try {
//            PlayerDto player = new PlayerDto();
//            
//            player.setUserName("raed1998");
//            player.setName("Raed");
//            player.setPassword("raed123");

            PlayerDto player = gson.fromJson(json, PlayerDto.class);
            
            Connection con = getConnection();
            PreparedStatement pst = con.prepareStatement("insert into Players (username , name , password ) values (? , ? , ?)");
            pst.setString(1, player.getUserName());
            pst.setString(2, player.getName());
            pst.setString(3, player.getPassword());
            
            result = pst.executeUpdate();
            
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseDao.class.getName()).log(Level.SEVERE, null, ex);
            return 0;
        }
        return result;
    }
     public int editProfile(String gsonrequest)
    {
        int result = 0;
        try {
            PlayerDto player = this.gson.fromJson(gsonrequest, PlayerDto.class);
            //System.out.println(" IN editProfile UserNAme: "+player.getUserName()+" Password: "+player.getPassword());
            Connection con = getConnection();
            PreparedStatement pst = con.prepareStatement("UPDATE PLAYERS SET PASSWORD = ? WHERE USERNAME = ?");
            pst.setString(1, player.getPassword());
            pst.setString(2, player.getUserName());
            result = pst.executeUpdate();
            
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseDao.class.getName()).log(Level.SEVERE, null, ex);
            result =  0;
        }
        return result;
    }
    
    public int selectInfoForEdidProfilePage(String usename)
    {
        int result = 0 ;
        try {
            
            Connection con = getConnection();
            PreparedStatement pst = con.prepareStatement("");
            
            return result;
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }
    
    
    public static Connection getConnection() throws SQLException{
        return  DriverManager.getConnection("jdbc:derby://localhost:1527/tictactoe_db", "root", "root");
    };
    
}
