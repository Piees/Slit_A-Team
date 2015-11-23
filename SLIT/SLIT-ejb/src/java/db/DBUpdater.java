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
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.swing.JTextArea;
import slitcommon.DeliveryStatus;

/**
 *
 * @author Viktor Setervang
 */
@Stateless
public class DBUpdater implements DBUpdaterRemote {
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
     * Marks notifications as seen.
     * @param idNotification a list of all notifications that will be marked 
     * as seen
     */
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
}
