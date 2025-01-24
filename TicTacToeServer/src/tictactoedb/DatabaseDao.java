
package tictactoedb;


public interface DatabaseDao {
    

    
    public int register(String json);
    public int editProfile(String gsonrequest);
    public String selectInfoForEdidProfilePage(String usename);
    
    
}
