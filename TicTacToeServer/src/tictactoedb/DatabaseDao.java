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
public interface DatabaseDao {
    

    
    public int register(String json);
    public int editProfile(String gsonrequest);
    
    
    public int selectInfoForEdidProfilePage(String usename);
    
    
}
