/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package notification;

import db.dbConnectorRemote;
import java.sql.Timestamp;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import slitclient.EJBConnector;

/**
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
     * @param time in format yyyy-[m]m-[d]d hh:mm:ss[.f...]. 
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
            dbConnectorRemote dbConnector = ejbConnector.getEjbRemote();
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
            uploadNotificationStatus = dbConnector.insertIntoDB("Notification", columns, values);
        }
        return uploadNotificationStatus;
    }
  
}
