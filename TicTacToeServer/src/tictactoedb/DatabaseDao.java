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


public interface DatabaseDao {
    

    
    public int register(String json);
    public int editProfile(String gsonrequest);
    public int selectInfoForEdidProfilePage(String usename);
    
    
}
