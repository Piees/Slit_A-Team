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
import java.util.Arrays;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import java.sql.Timestamp;
import java.util.HashMap;
import slitcommon.DeliveryStatus;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.ByteStreams;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import javax.swing.JTextArea;

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
     * This method is used by the Login class to check if the user has supplied
     * a correct userName and password combination.
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
    public HashMap<String, String> login(String userName, String pwd) {
        String loginQuery = "SELECT * FROM User WHERE userName=? and pwd=?";
        Connection dbConnection = dbConnection();
        HashMap<String, String> userHashMap = new HashMap<>();
        try {
            // PreparedStatement prevents SQL Injections by users.
            PreparedStatement ps = dbConnection.prepareStatement(loginQuery);
            ps.setString(1, userName);
            ps.setString(2, pwd);
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

    /**
     * Method for inserting new rows into tables in the DB. Data to be inserted
     * is checked using instanceof, and set by the correct method
     *
     * @param table name of table to be inserted into
     * @param columns name of columns to be inserted into
     * @param values value to be inserted into given columns
     * @return string with confirmation on wether operation was successfull
     */
    @Override
    public String insertIntoDB(String table, ArrayList<String> columns, ArrayList<Object> values) {
        //create the beginning of the insert-string
        String insert = "INSERT INTO " + table + "(";

        int countColumns = 0;
        //while we have more columns than the current count +1, add 
        //column.get(countColumns) +", "; to the insert-string
        while (columns.size() > (countColumns + 1)) {
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
        while (values.size() > (countValues + 1)) {
            insert += "?, ";
            countValues++;
        }
        //same principle as for columns, we don't have any more values, therefore 
        //we make this the end of the insert-string
        insert += "?);";
        DBConnection = dbConnection();
        try {
            PreparedStatement ps = DBConnection.prepareStatement(insert);
            int i = 1;
            int index = 0;
            //this sets the "?" in our insert-string as the corresponding -1
            //index in the arraylist
            //meaning our first "?" will have the first element (index 0) of our arraylist
            //because arraylist index starts at 0, and the index for counting "?" in our
            //insert-string starts at 1, int i must always be one larger than the arraylist-index
            //we also check datatype using instanceof, so we can use the corresponding ps.set-method
            while (values.size() >= i) {
                if (values.get(index) instanceof String) {
                    ps.setString(i, values.get(index).toString());
                } else if (values.get(index) instanceof Integer) {
                    ps.setInt(i, (int) values.get(index));
                } else if (values.get(index) instanceof Boolean) {
                    ps.setBoolean(i, (Boolean) values.get(index));
                } else if (values.get(index) instanceof File) {
                    File file = (File) values.get(index);
                    FileInputStream fileInput = null;
                    try {
                        fileInput = new FileInputStream(file);
                        ps.setBinaryStream(i, (FileInputStream) fileInput);

                    } catch (FileNotFoundException ex) {
                        Logger.getLogger(dbConnector.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else if (values.get(index) instanceof Timestamp) {
                    ps.setTimestamp(i, (Timestamp) values.get(index));
                } else if (values.get(index) instanceof DeliveryStatus) {
                    ps.setString(i, DeliveryStatus.IKKESETT.toString());
                } else {
                    System.out.println("INVALID OBJECT TYPE!");
                }
                index++;
                i++;
            }
            ps.executeUpdate();
            return "Opplastning vellykket!";
        } catch (SQLException ex) {
            System.out.println("CATCH I INSERT-METODE");
            System.out.println(ex);
            return "Opplastning feilet!";
        }
    }

    /**
     * Add evaluation to the correct row in the Delivery table in the DB
     *
     * @param evaluationValue the evaluation comment
     * @param evaluatedByValue the userName of the teacher-user evaluating this
     * delivery
     * @param whereValue1 the idModul of the delivery being evaluated
     * @param whereValue2 the userName of the student-user that made this
     * delivery
     * @param evaluationStatus the result of the evaluation, in either enum
     * GODKJENT or IKKEGODKJENT
     * @return confirmation string describing result of statement
     */
    @Override
    public String addDeliveryEvaluation(String evaluationValue, String evaluatedByValue,
            int whereValue1, String whereValue2, DeliveryStatus evaluationStatus) {
        //the statement for this update
        String update = "UPDATE Delivery SET evaluation =? "
                + ", evaluatedBy =?, evaluationDate = now(), deliveryStatus = '" + evaluationStatus + "'"
                + " WHERE idModul =? AND deliveredBy =?;";
        DBConnection = dbConnection();
        try {
            PreparedStatement ps = DBConnection.prepareStatement(update);
            //in order to ensure injection-proofing, we set the given values here
            ps.setString(1, evaluationValue);
            ps.setString(2, evaluatedByValue);
            ps.setInt(3, whereValue1);
            ps.setString(4, whereValue2);
            ps.executeUpdate();
            return "Lagret i database.";
        } catch (SQLException e) {
            System.out.println(e);
            return "Feil! Ble ikke lagret i database.";
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
    public ArrayList multiQueryHash(ArrayList<String> columns, ArrayList<String> tables, ArrayList<String> where) {
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
                LinkedHashMap<String, String> resultMap = new LinkedHashMap<>();
                //counter for which column we wish to get value from
                int resultSetIndex = 1;
                while (columnCount >= resultSetIndex) {
                    //add to HashMap, getting the name of current column from rsmd 
                    //and the value of current column from ResultSet(rs)
                    resultMap.put(rsmd.getColumnName(resultSetIndex), rs.getString(resultSetIndex));
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

    @Override
    public ArrayList<HashMap> getUserNotifications(String queryPart2, String userName) {
        String query = "SELECT * "
                + "FROM Notification "
                + "WHERE userName=? "
                + "AND Notification.seen=? ";

        query += queryPart2;
        //               "AND Notification.notificationTime <= CURRENT_TIMESTAMP()";

        // TRENGER 2 forskjellige resultat sett, ett med før og ett mer etter CURRENT_TIMESTAMP()
        // DET ETTER CURRENT_TIMESTAMP() SKAL PUTTES INN I ASYNC NOTIFICATION METODEN.
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
    public void updateUsersHashMap() {
        ArrayList<String> select = new ArrayList<>(Arrays.asList("userType,"
                + "mail, fname, lname, userName"));
        ArrayList<String> from = new ArrayList<>(Arrays.asList("User"));
        ArrayList<String> where = new ArrayList<>(Arrays.asList("userName != 'null'"));
        updateUsersArrayList = multiQuery(select, from, where);
        allUsersHashMap = new HashMap<>();
        for (int i = 0; i < updateUsersArrayList.size(); i += 5) {
            Map<String, String> updateUserHashMapHelper = eachUserMap(i);
            allUsersHashMap.put(updateUserHashMapHelper.get("userName"),
                    updateUserHashMapHelper);
        }
    }

    //@Override
    public HashMap<String, Map> getAllUsersHashMap() {
        return allUsersHashMap;
    }

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

    /**
     * Deletes a delivery from the Delivery-table in the DB, if it fits the
     * given idModul and userName
     *
     * @param idModul the idModul attribute of this delivery
     * @param userName the deliveredBy attribute of this delivery
     * @return confirmation string telling whether operation was successful
     */
    @Override
    public String deleteDelivery(int idModul, String userName) {
        Connection dbConnection = dbConnection();
        String delete = "DELETE FROM Delivery WHERE idModul=" + idModul + " AND deliveredBy='" + userName + "';";
        try {
            PreparedStatement ps = dbConnection.prepareStatement(delete);
            //this should be used to make it inejction-proof, but I can't get it to work
            //stuck with SQL syntax-error even after triple-checking delete-statement
//            ps.setInt(1, idModul);
//            ps.setString(2, userName);
            System.out.print(ps);
            ps.executeUpdate(delete);
            return "Innlevering slettet.";
        } catch (SQLException e) {
            System.out.println(e);
            return "Feil! Innlevering ble ikke slettet.";
        }
    }

    @Override
    public String updateModul(ArrayList<JTextArea> listOfEdits, int idModul) {
        Connection dbConnection = dbConnection();
        String update = "UPDATE Modul SET title =?, description=?, learningObj=?,"
                + "resources=?, excercise=?, evalForm=? WHERE idModul = " + idModul + ";";
        try {
            System.out.println("TRYYYYYYYYYYYYY HER");
            PreparedStatement ps = dbConnection.prepareStatement(update);
            int i = 1;
            for (JTextArea textArea : listOfEdits) {
                ps.setString(i, textArea.getText());
                i++;
            }
            System.out.println(ps);
            ps.executeUpdate();
            return "Modul ble endret.";
        } catch (SQLException e) {
            System.out.println(e);
            return "Feil! Modul ble ikke endret.";
        }
    }
    
    /**
     * Method for deleting a modul from the DB. Shows a confirmation string telling
     * user whether operation was successful
     * @param idModul id of the module to be deleted
     * @return confirmation string with result of operation
     */
    @Override
    public String deleteModul(int idModul)  {
        Connection dbConnection = dbConnection();
        String delete = "DELETE FROM Modul WHERE idModul = " + idModul + ";";
        try {
            PreparedStatement ps = dbConnection.prepareStatement(delete);
            ps.executeUpdate(delete);
            return "Modul slettet.";
        }
        catch (SQLException e)  {
            System.out.println(e);
            return "Feil! Modul ble ikke slettet.";
        }
    }
}
