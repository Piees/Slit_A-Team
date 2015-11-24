/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;

/**
 * This EJB handles deletion from database.
 * 
 * @author Arild Høyland
 * @author Viktor Setervang
 */
@Stateless
public class DBDeleter implements DBDeleterRemote {
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
    
    @Override
        public String deleteUser(String userName) {
        Connection dbConnection = dbConnection();
        String delete = "DELETE FROM User WHERE userName = " + userName + ";";
        try{
            PreparedStatement us = dbConnection.prepareStatement(delete);
            us.executeUpdate(delete);
            return "Brukeren er slettet.";
        }
        catch (SQLException e) {
            System.out.println(e);
            return "Feil! Brukeren ble ikke slettet.";      
        }
    }
}
