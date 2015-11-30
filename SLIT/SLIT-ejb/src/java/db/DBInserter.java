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
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import slitcommon.DeliveryStatus;

/**
 * This EJB handles database insertion.
 * 
 * @author Arild Høyland
 * @author Håkon Gilje
 * @author Viktor Setervang
 */
@Stateless
public class DBInserter implements DBInserterRemote {
    // JDBC driver name and database URL
    //static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
//    private static final String DB_URL = "jdbc:mysql://peterhagane.net:3306/a_team";
//    private static final String USERNAME = "yngve";
//    private static final String PASSWORD = "a_team";
    
    
    private static final String DB_URL = "jdbc:mysql://localhost:3306/a_team";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "root";
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

}
