/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package db;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import javax.ejb.Remote;

/**
 * @author Håkon Gilje
 * @author Arild Høyland
 * @author Viktor Setervang
 * @author Peter Hagane
 */
@Remote
public interface DBQuerierRemote {
    public Connection dbConnection();
    
    public HashMap<String, String> login(String userName, String securePassword);
    
    public ArrayList<String> multiQuery(ArrayList<String> columns, ArrayList<String> 
            tables, ArrayList<String> where);
   
    public ArrayList<LinkedHashMap> multiQueryHash(ArrayList<String> columns, ArrayList<String> 
            tables, ArrayList<String> where);
    
    public ArrayList<HashMap> getUserNotifications(String queryPart2, String userName);
    
    public byte[] getDeliveryFile(String userName, int idModul);

    public String getDeliveryFilename(String userName, int idModul);
    
    public ArrayList<HashMap> getResources();
    
    public byte[] getResourceFile(int idResources);

    public String getStoredSalt(String userName);
}
