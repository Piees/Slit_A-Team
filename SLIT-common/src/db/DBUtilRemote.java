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

/**
 * @author Yngve Ranestad
 * @author Arild Høyland
 * @author Viktor Setervang
 */
@Remote
public interface DBUtilRemote {
        
    public int countRows(String column, String tableName);
    
    public Map<String, String> eachUserMap(int fromIndex);
    
    public void updateUsersHashMap();
    
    public HashMap<String, Map> getAllUsersHashMap();
    
    public Connection dbConnection();
}
