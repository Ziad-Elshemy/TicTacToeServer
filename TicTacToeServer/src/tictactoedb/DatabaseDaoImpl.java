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
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.derby.jdbc.ClientDriver;

/**
 *
 * @author Ziad-Elshemy
 */
public class DatabaseDaoImpl implements DatabaseDao{
    
    Gson gson = new Gson();
    
    public DatabaseDaoImpl(){
        try {
            DriverManager.registerDriver(new ClientDriver());
            //Connection con = getConnection();
            System.out.println("hello from database");
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

    @Override
    public int editProfile(String gsonrequest){
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
    
    @Override
    public String selectInfoForEdidProfilePage(String usename)
    {
        PlayerDto player = new PlayerDto();
        String playerJson=null;
        ResultSet selectResult;
        try {
            
            System.out.println("DTO selectInfoForEdidProfilePage");
            Connection con = getConnection();
            PreparedStatement pst = con.prepareStatement("SELECT USERNAME , NAME , SCORE FROM PLAYERS WHERE USERNAME = ?",ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
            pst.setString(1, usename);
            selectResult = pst.executeQuery();
            selectResult.first();
            System.out.println("SeclectForEdit :"+selectResult.getString("NAME"));
            player.setUserName(selectResult.getString("USERNAME"));
            player.setName(selectResult.getString("NAME"));
            player.setScore(selectResult.getInt("SCORE"));
            playerJson = gson.toJson(player);
            System.out.println("jeson sent from DataDataBase: "+playerJson);
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return playerJson;
    }
    
    public static Connection getConnection() throws SQLException{
        return  DriverManager.getConnection("jdbc:derby://localhost:1527/tictactoe_db", "root", "root");
    };
    
}

    
    