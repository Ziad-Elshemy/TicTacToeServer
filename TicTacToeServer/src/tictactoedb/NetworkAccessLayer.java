package tictactoedb;

import java.sql.SQLException;
import java.util.ArrayList;


public class NetworkAccessLayer {
    

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
            player.setGender(DatabaseDaoImpl.playerResult.getString(7));
 
        }else{

            player=null;

        }
        
        
        return player;
         
    }
    
    

//    public static PlayerDto register(PlayerDto player) throws SQLException{
//        
//        result=DatabaseDaoImpl.insert(player);
//        if(result){
//   
//          DatabaseDaoImpl.selectUser(player.getUserName() , player.getPassword());
//          if(DatabaseDaoImpl.playerResult.next()){            
//            player=new PlayerDto();
//            player.setUserName(DatabaseDaoImpl.playerResult.getString(1)); 
//            player.setName(DatabaseDaoImpl.playerResult.getString(2));
//            player.setPassword(DatabaseDaoImpl.playerResult.getString(3));
//            player.setScore(DatabaseDaoImpl.playerResult.getInt(4));
//            player.setIsOnline(DatabaseDaoImpl.playerResult.getBoolean(5)); 
//            player.setIsPlaying(DatabaseDaoImpl.playerResult.getBoolean(6)); 
//
//        }else{
//
//            player=null;
//
//        }
//     }
//        
//         return player;
//
//    }

    
    
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
            player.setGender(DatabaseDaoImpl.playerResult.getString(7));


        }else{

            player=null;

        }
     }
        
         return player;

    }

    
    
    public static boolean logout(PlayerDto player) throws SQLException{
        
         result=DatabaseDaoImpl.updateUserState(player); 
        
         return result;
    }
    
    public static boolean updateUserState(PlayerDto player) throws SQLException{
        
         result=DatabaseDaoImpl.updateUserState(player); 
         return result;
    }
    
    
    public static ArrayList<PlayerDto> getOnlinePlayers() throws SQLException{
        
        
        ArrayList<PlayerDto> onlinePlayersArray = new ArrayList<>();
        
        DatabaseDaoImpl.selectOnlineUsers(); 
        
        while(DatabaseDaoImpl.playerResult.next()){
            player=new PlayerDto();
            player.setUserName(DatabaseDaoImpl.playerResult.getString(1)); 
            player.setName(DatabaseDaoImpl.playerResult.getString(2));
            player.setPassword(DatabaseDaoImpl.playerResult.getString(3));
            player.setScore(DatabaseDaoImpl.playerResult.getInt(4));
            player.setIsOnline(DatabaseDaoImpl.playerResult.getBoolean(5)); 
            player.setIsPlaying(DatabaseDaoImpl.playerResult.getBoolean(6));
            player.setGender(DatabaseDaoImpl.playerResult.getString(7));

            
            onlinePlayersArray.add(player);

        }

        return onlinePlayersArray;

    } 
    
}
 