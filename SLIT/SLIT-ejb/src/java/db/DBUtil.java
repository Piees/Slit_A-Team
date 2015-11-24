/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package db;

import com.google.common.collect.ImmutableMap;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateful;

/**
 * @author Yngve Ranestad
 * @author Arild Høyland
 * @author Viktor Setervang
 */
@Stateful
public class DBUtil implements DBUtilRemote {

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
    /**
     * Counts the number of rows in a given table
     *
     * @param column the name of column to be counted (can be all, expressed
     * with *)
     * @param tableName the name of the DB-table to count rows in
     * @return the number of rows found in the given table
     */
    @Override
    public int countRows(String column, String tableName) {
        String count = "SELECT COUNT(" + column + ") FROM " + tableName + ";";
        int returnInt = 0;
        DBConnection = dbConnection();
        try {
            PreparedStatement ps = DBConnection.prepareStatement(count);
            ResultSet rs = ps.executeQuery();
            rs.next();
            returnInt = rs.getInt(1);
        } catch (SQLException e) {
            System.out.println(e);
        }
        return returnInt;
    }

    @Override
    public void updateUsersHashMap() {
        ArrayList<String> select = new ArrayList<>(Arrays.asList("userType,"
                + "mail, fname, lname, userName"));
        ArrayList<String> from = new ArrayList<>(Arrays.asList("User"));
        ArrayList<String> where = new ArrayList<>(Arrays.asList("userName != 'null'"));
        DBQuerier dbQuerier = new DBQuerier();
        updateUsersArrayList = dbQuerier.multiQuery(select, from, where);
        allUsersHashMap = new HashMap<>();
        for (int i = 0; i < updateUsersArrayList.size(); i += 5) {
            Map<String, String> updateUserHashMapHelper = eachUserMap(i);
            allUsersHashMap.put(updateUserHashMapHelper.get("userName"),
                    updateUserHashMapHelper);
        }
    }
    
    @Override
    public Map<String, String> eachUserMap(int fromIndex) {
        userMap = ImmutableMap.of(
                "userType", updateUsersArrayList.get(fromIndex), //fromIndex (+1,2,3,4)
                "mail", updateUsersArrayList.get(fromIndex + 1),
                "fname", updateUsersArrayList.get(fromIndex + 2),
                "lname", updateUsersArrayList.get(fromIndex + 3),
                "userName", updateUsersArrayList.get(fromIndex + 4)
        );
        return userMap;
    }

    @Override
    public HashMap<String, Map> getAllUsersHashMap() {
        return allUsersHashMap;
    }    
}
