/*
 * test
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package db;

import java.io.InputStream;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.ejb.Remote;
import javax.swing.JTextArea;
import slitcommon.DeliveryStatus;

/**
 * @author Yngve Ranestad
 * @author Viktor Setervang 
 * @author Håkon Gilje
 * @author Arild Høyland
 */
@Remote
public interface dbConnectorRemote {
    
   
    public Connection dbConnection();
    
    public HashMap<String, String> login(String userName, String pwd);
            
    public ArrayList<String> multiQuery(ArrayList<String> columns, ArrayList<String> 
            tables, ArrayList<String> where);
   
    public ArrayList<LinkedHashMap> multiQueryHash(ArrayList<String> columns, ArrayList<String> 
            tables, ArrayList<String> where);
    
    public int countRows(String column, String tableName);     
    
    public ArrayList<HashMap> getUserNotifications(String queryPart2, String userName);
    
    public Map<String, String> eachUserMap(int fromIndex);
    
    public void updateUsersHashMap();
    
    public HashMap<String, Map> getAllUsersHashMap();
    
    public byte[] getDeliveryFile(String userName, int idModul);
    //public JPanel makeContactPanel();
    public String getDeliveryFilename(String userName, int idModul);
    
    public ArrayList<HashMap> getResources();
    
    public byte[] getResourceFile(int idResources);
    
}
