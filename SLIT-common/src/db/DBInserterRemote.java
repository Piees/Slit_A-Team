/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package db;

import java.sql.Connection;
import java.util.ArrayList;
import javax.ejb.Remote;

/**
 * @author Arild Høyland
 * @author Håkon Gilje
 * @author Viktor Setervang
 */
@Remote
public interface DBInserterRemote {
    
    public Connection dbConnection();
    
    public String insertIntoDB(String table, ArrayList<String> columns, ArrayList<Object> values);
}
