
package tictactoedb;


public interface DatabaseDao {
    

    
    public int register(String json);
    public int editProfile(String gsonrequest);   
    public int deleteAccount(String userName);
    
}
