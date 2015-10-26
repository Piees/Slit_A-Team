/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package db;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import java.sql.Timestamp;

/**
 *
 * @author piees
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
        }
        else {
            System.out.println("Old connection reused");
            return DBConnection;
        }
    }
    
    @Override
    public String singleQuery(String query, String colName) {
        String queryResult = null;
        Connection dbConnection = dbConnection();
        // Query
        try {
            PreparedStatement ps = dbConnection.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                if(rs.isLast()) {
                    queryResult = rs.getString(colName);
                    System.out.println(queryResult);
                } else {
                    System.out.println("There are more than one result");
                    break;
                }
            }
            System.out.println("Statement Successful");
        } catch (SQLException e) {
           System.out.println(e);
           return("error: " + e);
        }
        return queryResult;
    }
    
    // This method is not sql injection proof.
    // What if a crooked admin creates a new user where userName is 
    // is an injection?
    @Override
    public ArrayList<String> multiQuery(String query)   {
        Connection dbConnection = dbConnection();
        ArrayList<String> queryResults = new ArrayList<>();
        try {
            PreparedStatement ps = dbConnection.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            int i = 0;
            while(rs.next())    {
                queryResults.add(rs.getString(i));
                i++;
            }
        }
        catch (SQLException e)  {
            System.out.println(e);
        }
        return queryResults;
    }
    /**
     * This method is used by the Login class to check if the user
     * has supplied a correct userName and password combination.
     * 
     * This method should later on contain some kind of encryption mechanism
     * like salt?
     * 
     * @param userName
     * @param pwd 
     * @return the result of the login query
     * 
     */
    @Override
    public ArrayList<String> login(String userName, String pwd) {
        String loginQuery = "SELECT * FROM User WHERE userName=? and pwd=?";
        Connection dbConnection = dbConnection();
        ArrayList<String> loginResults = new ArrayList<>();
        try {
            // PreparedStatement prevents SQL Injections by users.
            PreparedStatement ps = dbConnection.prepareStatement(loginQuery);
            ps.setString(1, userName);
            ps.setString(2, pwd);
            ResultSet rs = ps.executeQuery();
            // If true then the username + password was a match
            if (rs.next()) {
                loginResults.add(userName);
                loginResults.add(rs.getString("userType"));
                loginResults.add(rs.getString("fname"));
                loginResults.add(rs.getString("lname"));
            } 
            else {
                loginResults.add("Username Password combination invalid");              
            }   
        } 
        catch (SQLException ex) {
            Logger.getLogger(dbConnector.class.getName()).log(Level.SEVERE, null, ex);
            loginResults.add(ex.getMessage());
        }
        return loginResults;
    }
    
    @Override
    public String insertIntoDB(String table, ArrayList<String> columns, ArrayList<Object> values) {
        //create the beginning of the insert-string
        String insert = "INSERT INTO " + table + "(";
      
        int countColumns = 0;
        //while we have more columns than the current count +1, add 
        //column.get(countColumns) +", "; to the insert-string
        while(columns.size() > (countColumns +1))   {
            insert += columns.get(countColumns) + ", ";
            countColumns++;
        }
        //if columns.size() is smaller than countColumns+1, this means that
        //this is the last column. Therefore we can't add a comma at the end,
        //but instead we can close the paranthesis and continue to values
        insert += columns.get(countColumns) + ") VALUES(";
        
        int countValues = 0;
        //same principle as for columns here
        //we insert ? here instead of values, as we're going to add
        //the values using the setString() method of PreparedStatement
        while(values.size() > (countValues + 1))   {
            insert += "?, ";
            countValues++;
        }
        //same principle as for columns, we don't have any more values, therefore 
        //we make this the end of the insert-string
        insert +=  "?);";
        System.out.println("metode insert kalt");
        System.out.print(insert);
        DBConnection = dbConnection();
        try {
            System.out.println("try i insert-metode");
            PreparedStatement ps = DBConnection.prepareStatement(insert);
            int i = 1;
            int index = 0;
            //this sets the "?" in our insert-string as the corresponding -1
            //index in the arraylist
            //meaning our first "?" will have the first element (index 0) of our arraylist
            //because arraylist index starts at 0, and the index for counting "?" in our
            //insert-string starts at 1, this must always be one larger than the arraylist-index
            while (values.size() >= i) {
                if(values.get(index) instanceof String) {
                    ps.setString(i, values.get(index).toString());
                }           
                else if (values.get(index) instanceof Integer) {
                    ps.setInt(i, (int) values.get(index));
                }           
                else if (values.get(index) instanceof Boolean) {
                    ps.setBoolean(i,(Boolean) values.get(index));
                }           
                else if (values.get(index) instanceof File) {
                    File file = (File) values.get(index);
                    FileInputStream fileInput = null;
                    try {
                        fileInput = new FileInputStream(file);
                    } catch (FileNotFoundException ex) {
                        Logger.getLogger(dbConnector.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    ps.setBinaryStream(i,(FileInputStream) fileInput);
                }           
                else if (values.get(index) instanceof Timestamp) {
                    ps.setTimestamp(i,(Timestamp) values.get(index)); 
                }
                else {
                    System.out.println("INVALID OBJECT TYPE!");
                }
                index++;
                i++;
            }
            System.out.println(ps);
            ps.executeUpdate();
            return "Opplasting vellykket!";
        }
        catch (SQLException ex)  {
            System.out.println("CATCH I INSERT-METODE");
            System.out.println(ex);
            return "Opplasting feilet!";
        }
    }
    public int countRows(String column, String tableName)    {
        String count = "SELECT COUNT(" + column + ") FROM " +  tableName + ";";
        String numberOfRows = "";
        DBConnection = dbConnection();
        try {
            PreparedStatement ps = DBConnection.prepareStatement(count);
            ResultSet rs = ps.executeQuery();
            rs.next();
            numberOfRows = rs.getString(1);
            System.out.println("ANTALL REKKER I MODUL:" + numberOfRows);
        }
        catch (SQLException e)  {
            System.out.println(e);
        }
        int returnInt = Integer.parseInt(numberOfRows);
        return returnInt;
    }
    
    @Override
    public ArrayList multiQuery(ArrayList<String> columns, ArrayList<String> 
            tables, ArrayList<String> where)    {
        String query = "SELECT ";
        ArrayList<String> queryResults = new ArrayList<>();
        
        int countColumns = 0;
        while(columns.size() > (countColumns +1))   {
            query += columns.get(countColumns) + ", ";
            countColumns++;
        }
        query += columns.get(countColumns) + " FROM ";
        
        int countTables = 0;
        while(tables.size() > (countTables +1)) {
            query += tables.get(countTables) + ", ";
            countTables++;
        }
        query += tables.get(countTables);
        if(where != null)    {
            int countWhere = 0;
            query += " WHERE ";
                while(where.size() > (countWhere +1))   {
                query += where.get(countWhere) + ", ";
                countWhere ++;
                }
            query += where.get(countWhere) + ";";
        }
        else {
            query += ";";
        }
        DBConnection = dbConnection();
        try {
            System.out.println("try i multi-query metode");
            PreparedStatement ps = DBConnection.prepareStatement(query);
            System.out.println(ps);
            ResultSet rs = ps.executeQuery();
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();
            while (rs.next())   {
                int i = 1;
                while (columnCount >= i)    {
                    queryResults.add(rs.getString(i));
                    i++;
                }
            }
            System.out.println("QueryResults-liste HER: " + queryResults.size());
        }
        catch (SQLException e)  {
            System.out.println("SQL-SYNTAX-ERROR I MULTI-QUERY-METODE");
            System.out.println(e);
        }
        return queryResults;
    } 
    
    @Override
    public ArrayList<ArrayList> getUserNotifications(String query, String userName) {
        Connection dbConnection = dbConnection();
        ArrayList<ArrayList> notifications = new ArrayList<>();
        try {
            // PreparedStatement prevents SQL Injections by users.
            PreparedStatement ps = dbConnection.prepareStatement(query);
            ps.setString(1, userName);
            ps.setBoolean(2, false);
            ResultSet rs = ps.executeQuery();
            while (rs.next())   {
                ArrayList<Object> notification = new ArrayList<>();
                int idNotification = rs.getInt("idNotification");
                Timestamp timestamp =  rs.getTimestamp("notificationTime");
                String notificationText = rs.getString("notificationText");
                
                notification.add(idNotification);
                notification.add(timestamp);
                notification.add(notificationText);
                notifications.add(notification);
                //String notificationTime = timestamp.toString();
                //notifications.add(notificationTime + ":\n" + notificationText);           
            }   
        } 
        catch (SQLException ex) {
            Logger.getLogger(dbConnector.class.getName()).log(Level.SEVERE, null, ex);
        }
        return notifications;
    }
    
    @Override
    public void markNotificationsAsSeen(ArrayList<Integer> idNotification) {
        String updateNotification = "UPDATE Notification set seen=? WHERE idNotification=?";
        Connection dbConnection = dbConnection();
        PreparedStatement ps;
        try {
            for (Integer id : idNotification) {
                System.out.print(id);
                ps = dbConnection.prepareStatement(updateNotification);
                ps.setBoolean(1, true);
                ps.setInt(2, id);
                ps.executeUpdate();   
            }
        } catch (SQLException ex) {
            Logger.getLogger(dbConnector.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
