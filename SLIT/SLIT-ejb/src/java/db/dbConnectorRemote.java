/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package db;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.ejb.Remote;
import javax.swing.JPanel;

/**
 *
 * @author piees
 */
@Remote
public interface dbConnectorRemote {
    
    /**
     * 
     * @param sql
     * @return single query string
     */
    public String singleQuery(String sql, String colName);
    
    public Connection dbConnection();
    
    public HashMap<String, String> login(String userName, String pwd);
    
    public ArrayList<String> multiQuery(String query);

    public String insertIntoDB(String table, ArrayList<String> columns, ArrayList<Object> values);
    
    public ArrayList multiQuery(ArrayList<String> columns, ArrayList<String> 
            tables, ArrayList<String> where);
    
    public int countRows(String column, String tableName);     
    
    public ArrayList<HashMap> getUserNotifications(String queryPart2, String userName);
    
    public void markNotificationsAsSeen(ArrayList<Integer> idNotification);
    
    public Map<String, String> eachUserMap(int fromIndex);
    
    public void updateUsersHashMap();
    
    public HashMap<String, Map> getAllUsersHashMap();
    
    //public JPanel makeContactPanel();
}
