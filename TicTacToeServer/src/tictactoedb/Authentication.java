package tictactoedb;

import java.sql.ResultSet;
import java.sql.SQLException;


public class Authentication {
    

    static PlayerDto player;
    static boolean result;
    

    
    public static PlayerDto login(String username , String password) throws SQLException{
        
        DatabaseDaoImpl.selectUser(username , password); 
        
        if(DatabaseDaoImpl.playerResult.next()){
            player=new PlayerDto();
            player.setUserName(DatabaseDaoImpl.playerResult.getString(1)); 
            player.setName(DatabaseDaoImpl.playerResult.getString(2));
            player.setPassword(DatabaseDaoImpl.playerResult.getString(3));
            player.setScore(DatabaseDaoImpl.playerResult.getInt(4));
            player.setIsOnline(DatabaseDaoImpl.playerResult.getBoolean(5)); 
            player.setIsPlaying(DatabaseDaoImpl.playerResult.getBoolean(6)); 

        }else{

            player=null;

        }
        
        
        return player;
         
    }
    
    
    public static PlayerDto register(PlayerDto player) throws SQLException{
        
        result=DatabaseDaoImpl.insert(player);
        if(result){
   
          DatabaseDaoImpl.selectUser(player.getUserName() , player.getPassword());
          if(DatabaseDaoImpl.playerResult.next()){            
            player=new PlayerDto();
            player.setUserName(DatabaseDaoImpl.playerResult.getString(1)); 
            player.setName(DatabaseDaoImpl.playerResult.getString(2));
            player.setPassword(DatabaseDaoImpl.playerResult.getString(3));
            player.setScore(DatabaseDaoImpl.playerResult.getInt(4));
            player.setIsOnline(DatabaseDaoImpl.playerResult.getBoolean(5)); 
            player.setIsPlaying(DatabaseDaoImpl.playerResult.getBoolean(6)); 

        }else{

            player=null;

        }
        }
        
         return player;

    }
    
    
    public void logout(PlayerDto player){
    
    
    
    }
    
}
 