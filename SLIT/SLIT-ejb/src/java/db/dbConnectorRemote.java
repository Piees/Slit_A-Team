/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package db;

import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import javax.ejb.Remote;

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
    
    /**
     *
     * @return
     */
    public Connection dbConnection();
    
    public HashMap<String, String> login(String userName, String pwd);
    
    public ArrayList<String> multiQuery(String query);

    public String insertIntoDB(String table, ArrayList<String> columns, ArrayList<Object> values);
    
    public ArrayList multiQuery(ArrayList<String> columns, ArrayList<String> 
            tables, ArrayList<String> where);
    
    public int countRows(String column, String tableName);     
    
    public ArrayList<ArrayList> getUserNotifications(String query, String userName);
    
    public void markNotificationsAsSeen(ArrayList<Integer> idNotification);

     ///TEST_PASSORDHASH
    public boolean addNewUser() throws SQLException, NoSuchAlgorithmException;




}
