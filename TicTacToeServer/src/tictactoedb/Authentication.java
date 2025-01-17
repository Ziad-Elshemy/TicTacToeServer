package tictactoedb;

import java.sql.ResultSet;
import java.sql.SQLException;


public class Authentication {
    
    
    
    static PlayerDto player;
    static boolean result;
    

    
    public static PlayerDto login(String username , String password) throws SQLException{
        
        DatabaseDao.selectUser(username , password); 
        
        if(DatabaseDao.playerResult.next()){
            player=new PlayerDto();
            player.setUserName(DatabaseDao.playerResult.getString(1)); 
            player.setName(DatabaseDao.playerResult.getString(2));
            player.setPassword(DatabaseDao.playerResult.getString(3));
            player.setScore(DatabaseDao.playerResult.getInt(4));
            player.setIsOnline(DatabaseDao.playerResult.getBoolean(5)); 
            player.setIsPlaying(DatabaseDao.playerResult.getBoolean(6)); 

        }else{

            player=null;

        }
        
        
        return player;
         
    }
    
    
    public static PlayerDto register(PlayerDto player) throws SQLException{
               
        result=DatabaseDao.insert(player);
        
        if(result){
            
            DatabaseDao.selectUser(player.getUserName() , player.getPassword());
          if(DatabaseDao.playerResult.next()){
              
            player=new PlayerDto();
            player.setUserName(DatabaseDao.playerResult.getString(1)); 
            player.setName(DatabaseDao.playerResult.getString(2));
            player.setPassword(DatabaseDao.playerResult.getString(3));
            player.setScore(DatabaseDao.playerResult.getInt(4));
            player.setIsOnline(DatabaseDao.playerResult.getBoolean(5)); 
            player.setIsPlaying(DatabaseDao.playerResult.getBoolean(6)); 

        }else{

            player=null;

        }

        }
        
         return player;

    }
    
    
    public void logout(PlayerDto player){
    
    
    
    }
    
}
 