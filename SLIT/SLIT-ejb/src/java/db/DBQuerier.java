/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package db;

import com.google.common.io.ByteStreams;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;

/**
 * This EJB handles database queries.
 *
 * @author Håkon Gilje
 * @author Arild Høyland
 * @author Viktor Setervang
 * @author Peter Hagane
 */
@Stateless
public class DBQuerier implements DBQuerierRemote {

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
        } else {
            System.out.println("Old connection reused");
            return DBConnection;
        }
    }

    /**
     * This method is used by the Login class to check if the user has supplied
     * a correct userName and password combination.
     *
     * This method should later on implement some kind of encryption mechanism
     *
     * @param userName username provided by the user
     * @param pwd password provided by the user
     * @return the result of the login query, if the HashMap is empty then the
     * login was unsuccessful
     */
    @Override
    public HashMap<String, String> login(String userName, String securePassword) {

        String loginQuery = "SELECT * FROM User WHERE userName=? and pwd=?";
        Connection dbConnection = dbConnection();
        HashMap<String, String> userHashMap = new HashMap<>();
        try {
            // PreparedStatement prevents SQL Injections by users.
            PreparedStatement ps = dbConnection.prepareStatement(loginQuery);
            ps.setString(1, userName);
            ps.setString(2, securePassword);
            ResultSet rs = ps.executeQuery();
            // If true then the username + password was a match
            if (rs.next()) {
                userHashMap.put("userName", userName);
                userHashMap.put("userType", rs.getString("userType"));
                userHashMap.put("fName", rs.getString("fName"));
                userHashMap.put("lName", rs.getString("lName"));
                userHashMap.put("mail", rs.getString("mail"));
            } else {
                userHashMap.put("error1", "Username Password combination invalid");
            }
        } catch (SQLException ex) {
            Logger.getLogger(dbConnector.class.getName()).log(Level.SEVERE, null, ex);
        }
        return userHashMap;
    }

    public String getStoredSalt(String userName) {
        System.out.println("Putting together query to retrieve salt from defined user");
        String fetchSaltQuery = "SELECT salt FROM User WHERE userName=?";
        Connection dbConnection = dbConnection();
        String fetchedSalt = null;
        try {
            PreparedStatement statement = dbConnection.prepareStatement(fetchSaltQuery);
            statement.setString(1, userName);
            System.out.println("Executing query");
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                System.out.println("Extracting salt from fetched row");
                //følgende linje sjekker om brukeren har en salt-verdi lagret
                if(rs.getString("salt") != null){
                //hvis løkken finner en verdi 'salt' i raden, så knyttes denne verdien til variablen
                fetchedSalt = rs.getString("salt");
                }else{
                //hvis ikke, så settes variabelen til å være en tom string(som sjekkes i login.)
                fetchedSalt = "";}
            }

        } catch (SQLException ex) {
            Logger.getLogger(dbConnector.class.getName()).log(Level.SEVERE, null, ex);

        }
        System.out.println("The salt for the user " + userName + " is " + fetchedSalt);
        return fetchedSalt;
    }

    @Deprecated
    @Override
    public ArrayList multiQuery(ArrayList<String> columns, ArrayList<String> tables, ArrayList<String> where) {
        String query = "SELECT ";
        ArrayList<String> queryResults = new ArrayList<>();

        int countColumns = 0;
        while (columns.size() > (countColumns + 1)) {
            query += columns.get(countColumns) + ", ";
            countColumns++;
        }
        query += columns.get(countColumns) + " FROM ";

        int countTables = 0;
        while (tables.size() > (countTables + 1)) {
            query += tables.get(countTables) + ", ";
            countTables++;
        }
        query += tables.get(countTables);
        if (where != null) {
            int countWhere = 0;
            query += " WHERE ";
            while (where.size() > (countWhere + 1)) {
                query += where.get(countWhere) + ", ";
                countWhere++;
            }
            query += where.get(countWhere) + ";";
        } else {
            query += ";";
        }
        DBConnection = dbConnection();
        try {
            System.out.println("Deprecated multi-query method called");
            PreparedStatement ps = DBConnection.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();
            while (rs.next()) {
                int i = 1;
                while (columnCount >= i) {
                    queryResults.add(rs.getString(i));
                    i++;
                }
            }
        } catch (SQLException e) {
            System.out.println("SQL-SYNTAX-ERROR I DEPRECATED-MULTI-QUERY-METODE");
            System.out.println(e);
        }
        return queryResults;
    }

    /**
     * Method for making queries to the DB.
     *
     * @param columns arraylist with name of columns to be queried (can be *)
     * @param tables arraylist with name of tables to be queried from
     * @param where arraylist with name of where-conditions for this query
     * @return arraylist with hashmaps containing query-result. First hashmap
     * contains the results of the first row in the DB, 2nd hashmap contains
     * result from 2nd row in the DB and so on...
     */
    @Override
    public ArrayList<LinkedHashMap> multiQueryHash(ArrayList<String> columns, ArrayList<String> tables, ArrayList<String> where) {
        String query = "SELECT ";
        ArrayList<LinkedHashMap> queryResults = new ArrayList();
        //the following block adds all columns to the query-string
        int countColumns = 0;
        //as long as countColumns is one larger than columns.size(), 
        //we have more columns to add
        while (columns.size() > (countColumns + 1)) {
            query += columns.get(countColumns) + ", ";
            countColumns++;
        }
        //if we don't have more columns to add, end the columns-part of query-string
        query += columns.get(countColumns) + " FROM ";

        //same princicple as for columns
        int countTables = 0;
        while (tables.size() > (countTables + 1)) {
            query += tables.get(countTables) + ", ";
            countTables++;
        }
        query += tables.get(countTables);

        //we don't need a where-condition, so it could be null, and we need to check
        if (where != null) {
            //if we have strings in the where-list, same principle as for columns/tables
            int countWhere = 0;
            query += " WHERE ";
            while (where.size() > (countWhere + 1)) {
                query += where.get(countWhere) + ", ";
                countWhere++;
            }
            query += where.get(countWhere) + ";";
        } //if we don't have any where-conditions, the we finish the query-string
        else {
            query += ";";
        }
        DBConnection = dbConnection();
        try {
            PreparedStatement ps = DBConnection.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            //we need metadata about the resultSet, which we get using ResultSetMetaData
            ResultSetMetaData rsmd = rs.getMetaData();
            //get the number of columns in this ResultSet
            int columnCount = rsmd.getColumnCount();
            //as long as there are more rows in the ResultSet, go to the next one
            while (rs.next()) {
                //create HashMap storing results for this row in the ResultSet
                LinkedHashMap<String, Object> resultMap = new LinkedHashMap<>();
                //counter for which column we wish to get value from
                int resultSetIndex = 1;
                while (columnCount >= resultSetIndex) {
                    //add to HashMap, getting the name of current column from rsmd 
                    //and the value of current column from ResultSet(rs)
                    resultMap.put(rsmd.getColumnName(resultSetIndex), rs.getObject(resultSetIndex));
                    resultSetIndex++;
                }
                //add this map to the return-list containing list of all maps
                queryResults.add(resultMap);
            }
        } catch (SQLException e) {
            System.out.println("SQL-SYNTAX-ERROR I MULTI-QUERY-METODE");
            System.out.println(e);
        }
        return queryResults;
    }

    /**
     * Gets the unseen notifications of user specified by parameter userName.
     *
     * @param queryPart2 extra query logic, typically regarding before or after
     * current_timestamp()
     * @param userName the username used by the query.
     * @return list containing all the queried notifications.
     */
    @Override
    public ArrayList<HashMap> getUserNotifications(String queryPart2, String userName) {
        String query = "SELECT * "
                + "FROM Notification "
                + "WHERE userName=? "
                + "AND Notification.seen=? ";

        query += queryPart2;
        // "AND Notification.notificationTime <= CURRENT_TIMESTAMP()";

        Connection dbConnection = dbConnection();
        ArrayList<HashMap> notifications = new ArrayList<>();
        try {
            // PreparedStatement prevents SQL Injections by users.
            PreparedStatement ps = dbConnection.prepareStatement(query);
            ps.setString(1, userName);
            ps.setBoolean(2, false);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                HashMap<String, Object> notification = new HashMap<>();
                int idNotification = rs.getInt("idNotification");
                Timestamp timestamp = rs.getTimestamp("notificationTime");
                String notificationText = rs.getString("notificationText");

                notification.put("idNotification", idNotification);
                notification.put("timestamp", timestamp);
                notification.put("notificationText", notificationText);
                notifications.add(notification);
                //String notificationTime = timestamp.toString();
                //notifications.add(notificationTime + ":\n" + notificationText);           
            }
        } catch (SQLException ex) {
            Logger.getLogger(dbConnector.class.getName()).log(Level.SEVERE, null, ex);
        }
        return notifications;
    }

    /**
     * Gets a delivered "module assignment" file.
     *
     * @param userName the username of the user that delivered the assignment
     * @param idModul the id (PK) of the module that the assignment was for.
     * @return the assignment file
     */
    @Override
    public byte[] getDeliveryFile(String userName, int idModul) {
        String query = "SELECT deliveryFile, fileName FROM Delivery WHERE deliveredBy =? AND idModul=?";

        Connection dbConnection = dbConnection();

        try {
            // PreparedStatement prevents SQL Injections by users.
            PreparedStatement ps = dbConnection.prepareStatement(query);
            ps.setString(1, userName);
            ps.setInt(2, idModul);
            ResultSet rs = ps.executeQuery();

            // If true then the username + password was a match
            if (rs.next()) {
                InputStream InputStream = rs.getBinaryStream("deliveryFile");
                //System.out.println("deliveryFile seems to be converted to inputStream");

                byte[] byteData;
                try {
                    byteData = ByteStreams.toByteArray(InputStream);

                    return byteData;
                } catch (IOException ex) {
                    Logger.getLogger(dbConnector.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(dbConnector.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * Gets the filename of a delivered assignment
     * @param userName the username of the user that delivered the assignment
     * @param idModul the id (PK) of the module that the assignment was for.
     * @return the assignment's filename
     */
    @Override
    public String getDeliveryFilename(String userName, int idModul) {
        String query = "SELECT fileName FROM Delivery WHERE deliveredBy =? AND idModul=?";

        Connection dbConnection = dbConnection();

        try {
            // PreparedStatement prevents SQL Injections by users.
            PreparedStatement ps = dbConnection.prepareStatement(query);
            ps.setString(1, userName);
            ps.setInt(2, idModul);
            ResultSet rs = ps.executeQuery();

            // If true then the username + password was a match
            if (rs.next()) {
                String fileName = rs.getString("fileName");
                //System.out.println("deliveryFile seems to be converted to inputStream");
                return fileName;
            }
        } catch (SQLException ex) {
            Logger.getLogger(dbConnector.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * @return all resources from the Resource table.
     */
    @Override
    public ArrayList<HashMap> getResources() {
        String query = "SELECT * FROM Resources";
        Connection dbConnection = dbConnection();
        ArrayList<HashMap> resources = new ArrayList<>();
        try {
            // PreparedStatement prevents SQL Injections by users.
            PreparedStatement ps = dbConnection.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            // If true then the username + password was a match
            while (rs.next()) {
                HashMap<String, Object> resourceMap = new HashMap<>();
                resourceMap.put("userName", rs.getString("userName"));
                resourceMap.put("isMessage", rs.getBoolean("isMessage"));
                resourceMap.put("idResource", rs.getInt("idResource"));
                resourceMap.put("title", rs.getString("title"));
                resourceMap.put("resourceText", rs.getString("resourceText"));
                resourceMap.put("url", rs.getString("url"));
                resourceMap.put("resourceDate", rs.getTimestamp("resourceDate"));
                resourceMap.put("fileName", rs.getString("fileName"));
                resources.add(resourceMap);
            }

        } catch (SQLException ex) {
            Logger.getLogger(dbConnector.class.getName()).log(Level.SEVERE, null, ex);
        }
        return resources;
    }

    /**
     * Gets the specified resource file
     *
     * @param idResources the id (PK) of the resource that will be returned.
     * @return the resource file
     */
    @Override
    public byte[] getResourceFile(int idResources) {
        String query = "SELECT resourceFile FROM Resources WHERE idResource=?";
        Connection dbConnection = dbConnection();
        try {
            // PreparedStatement prevents SQL Injections by users.
            PreparedStatement ps = dbConnection.prepareStatement(query);
            ps.setInt(1, idResources);
            ResultSet rs = ps.executeQuery();
            // If true then the username + password was a match
            if (rs.next()) {
                InputStream InputStream = rs.getBinaryStream("resourceFile");
                byte[] byteData;
                try {
                    byteData = ByteStreams.toByteArray(InputStream);

                    return byteData;
                } catch (IOException ex) {
                    Logger.getLogger(dbConnector.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(dbConnector.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

}
