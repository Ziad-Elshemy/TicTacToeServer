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
    public int insert(String json) {
        int result = 0;
        try {

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
    
    public static Connection getConnection() throws SQLException{
        return  DriverManager.getConnection("jdbc:derby://localhost:1527/tictactoe_db", "root", "root");
    };
    
}
