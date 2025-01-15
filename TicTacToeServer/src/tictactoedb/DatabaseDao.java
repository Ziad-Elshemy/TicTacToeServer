/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tictactoedb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.derby.jdbc.ClientDriver;

/**
 *
 * @author Ziad-Elshemy
 */
public class DatabaseDao {
    
    public DatabaseDao(){
        try {
            DriverManager.registerDriver(new ClientDriver());
            Connection con = DriverManager.getConnection("jdbc:derby://localhost:1527/tictactoe_db", "root", "root");
            System.out.println("hello from database");
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseDao.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
