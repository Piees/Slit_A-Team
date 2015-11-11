/*
 * test
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package db;

<<<<<<< HEAD
import java.security.NoSuchAlgorithmException;
=======
import java.io.InputStream;
>>>>>>> refs/remotes/origin/master
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.ejb.Remote;
import slitcommon.DeliveryStatus;

/**
 *
 * @author piees
 */
@Remote
public interface dbConnectorRemote {
    
    /**
     * UNUSED METHOD - REMOVE??
     * @param sql
     * @return single query string
     */
    public String singleQuery(String sql, String colName);
    
    /**
     *
     * @return
     */
    public Connection dbConnection();
    
    public HashMap<String, String> login(String userName, String pwd);
    
    public String insertIntoDB(String table, ArrayList<String> columns, ArrayList<Object> values);
    
    public String addDeliveryEvaluation(String evaluationValue, String evaluatedByValue, 
            int whereValue1, String whereValue2, DeliveryStatus evaluationStatus);   
            
    public ArrayList multiQuery(ArrayList<String> columns, ArrayList<String> 
            tables, ArrayList<String> where);
   
    public HashMap multiQueryHash(ArrayList<String> columns, ArrayList<String> 
            tables, ArrayList<String> where);
    
    public int countRows(String column, String tableName);     
    
    public ArrayList<HashMap> getUserNotifications(String queryPart2, String userName);
    
    public void markNotificationsAsSeen(ArrayList<Integer> idNotification);
<<<<<<< HEAD

     ///TEST_PASSORDHASH
    public boolean addNewUser() throws SQLException, NoSuchAlgorithmException;




=======
    
    public Map<String, String> eachUserMap(int fromIndex);
    
    public void updateUsersHashMap();
    
    public HashMap<String, Map> getAllUsersHashMap();
    
    public byte[] getDeliveryFile(String userName, int idModul);
    //public JPanel makeContactPanel();
    public String getDeliveryFilename(String userName, int idModul);
    
    public ArrayList<HashMap> getResources();
    
    public byte[] getResourceFile(int idResources);
    
>>>>>>> refs/remotes/origin/master
}
