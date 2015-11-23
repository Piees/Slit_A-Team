/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;

/**
 * @author Yngve Ranestad
 * @author Viktor Setervang 
 * @author Håkon Gilje
 * @author Arild Høyland
 */
@Stateless
public class dbConnector implements dbConnectorRemote {

    // JDBC driver name and database URL
    //static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
    private static final String DB_URL = "jdbc:mysql://peterhagane.net:3306/a_team";
    private static final String USERNAME = "yngve";
    private static final String PASSWORD = "a_team";
    //private String queryResult;
    private static Connection DBConnection;
    private ArrayList<String> updateUsersArrayList;
    private Map<String, String> userMap;
    public static HashMap<String, Map> allUsersHashMap;

    @Override
    public Connection dbConnection() {
        // Connection
        if (DBConnection == null) {
            try {
                DBConnection = DriverManager.getConnection(
                        DB_URL, USERNAME, PASSWORD);
                System.out.println("New connection established!");
            } catch (SQLException ex) {
                Logger.getLogger(dbConnector.class.getName()).log(Level.SEVERE, null, ex);
                //return null;
            }
            return DBConnection;
        } else {
            System.out.println("Old connection reused");
            return DBConnection;
        }
    }

}
