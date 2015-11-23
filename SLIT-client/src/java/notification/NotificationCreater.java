/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package notification;

import db.DBInserterRemote;
import db.DBQuerierRemote;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import slitclient.EJBConnector;

/**
 * This class contains all the logic needed on client-side to create 
 * new notifications.
 * 
 * @author Viktor Setervang
 */
public class NotificationCreater {
    
    /**
     * This method creates a new notification using the current time as timestamp
     * 
     * @param userName userName of the receiver of the notification. 
     * @param text what the notification is about.
     * @return a string regarding upload status of the notification
     */
    public String createNewNotification(String userName, String text) { 
        DateHandler dh = new DateHandler();
        return createNewNotification(userName, dh.getCurrentTimestamp().toString(), text);
    }
    
    /**
     * Creates new notifications
     * 
     * @param userName userName of the receiver of the notification. 
     * @param time when the user will receive the notification. 
     *   The format should be in yyyy-[m]m-[d]d hh:mm:ss[.f...]. 
     *   The fractional seconds may be omitted. The leading zero 
     *   for mm and dd may also be omitted. 
     * @param text what the notification is about.
     * @return a string regarding upload status of the notification
     */
    public String createNewNotification(String userName, String time, String text) { 

        Timestamp timestamp = null;
        try {
            timestamp = Timestamp.valueOf(time);
        } catch (Exception ex) {
//            JOptionPane.showMessageDialog(null, "Oppgi en gyldig "
//                    + "varsel dato og tid.");         
            return "Oppgi en gyldig varsel dato/tid.";
        }
        String uploadNotificationStatus = "";
        if (timestamp != null) {

            EJBConnector ejbConnector = EJBConnector.getInstance();
            DBInserterRemote dbInserter = ejbConnector.getDBInserter();
            ArrayList<String> columns = new ArrayList<>();
            ArrayList<Object> values = new ArrayList<>();
            columns.add("userName");
            columns.add("seen");
            columns.add("notificationTime");
            columns.add("notificationText");

            values.add(userName);
            values.add(false);
            values.add(time);
            values.add(text);
            uploadNotificationStatus = dbInserter.insertIntoDB("Notification", columns, values);
        }
        return uploadNotificationStatus;
    }
    
    /**
     * Creates a notification to all users of the specified userType 
     * using the current time as timestamp
     * 
     * @param userType userType of the receivers of the notification. 
     * @param text what the notification is about.
     * @return a string regarding upload status of the notification
     */
    public String notificationToUserType(String userType, String text) {
        DateHandler dh = new DateHandler();
        return notificationToUserType(userType, dh.getCurrentTimestamp().toString(), text);
    }
    
    /**
     * Creates a notification to all users of the specified userType 
     * 
     * @param userType userType of the receivers of the notification. 
     * @param time when the user will receive the notification. 
     *   The format should be in yyyy-[m]m-[d]d hh:mm:ss[.f...]. 
     *   The fractional seconds may be omitted. The leading zero 
     *   for mm and dd may also be omitted. 
     * @param text what the notification is about.
     * 
     * @return a string regarding upload status of the notification
     */
    public String notificationToUserType(String userType, String time, String text) {
        ArrayList<String> columns = new ArrayList<>();
        ArrayList<String> tables = new ArrayList<>();
        ArrayList<String> where = new ArrayList<>();
        columns.add("userName");
        tables.add("User");
        where.add("userType = " + "\"" + userType + "\"");
        EJBConnector ejbConnector = EJBConnector.getInstance();
        DBQuerierRemote dbQuerier = ejbConnector.getDBQuerier();
        ArrayList<LinkedHashMap> allUsersOfUserType = dbQuerier.multiQueryHash(columns, tables, where);
        System.out.println("allUsersOfUserType.size() " + allUsersOfUserType.size());
        String notifySuccess = "Alle brukere av typen " + userType + " ble varslet";
        for (LinkedHashMap user: allUsersOfUserType) {
            System.out.println(user.get("userName").toString() + " " + time + " " + text);
            String status = createNewNotification(user.get("userName").toString(), time, text);
            if (!status.equals("Opplastning vellykket!")) {
                notifySuccess = "Ikke alle " + userType + " ble varselet";
            }
        }
        System.out.println(notifySuccess);
        return notifySuccess;
    }
}
