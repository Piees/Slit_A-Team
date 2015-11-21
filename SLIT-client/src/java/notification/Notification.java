/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package notification;

import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;
import db.dbConnectorRemote;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JFrame;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Set;
import slitclient.EJBConnector;

/**
 *
 * @author Viktor Setervang
 */
public class Notification {
    // This is a button from the UserGUI class
    private JButton userGUINotificationButton;
    
    private String userName;
    private final EJBConnector ejbConnector = EJBConnector.getInstance();
    private final dbConnectorRemote connector = ejbConnector.getEjbRemote();    
   
    private JFrame frame;
    private JButton notificationGUIButton;   
    private HashMap<String, String> userInfo;    
    
    private ArrayList<HashMap> unseenNotifications;
    // This field is for equality checks, so we dont end up with redudant Timer threads.
    private ArrayList<HashMap> futureNotifications;
    // This should be a sort of collection of Time objects, so there wont be "collisions".
    private ArrayList<Timer> timers;
    
    public Notification(JFrame frame, HashMap<String, String> userInfo, JButton userGUINotificationButton) { 
        this.frame = frame;
        this.userInfo = userInfo;
        this.userGUINotificationButton = userGUINotificationButton;
        unseenNotifications = new ArrayList<>();
        timers = new ArrayList<>();
        userName = this.userInfo.get("userName");
        
        frameAddWindowListener();
        
        createNotificationTimers();
        initiateNotificationUpdateLoop();
    }
    
    public void setSeeNotificationGUIButton(JButton notificationGUIButton) {
        this.notificationGUIButton = notificationGUIButton;
    }
            
    
    /**
     * This method puts future notification timestamps into a timer.
     * When The timer goes off, the method checkForNotifications will be called.
     */
    public void createNotificationTimers() {
        // Will be changed to only contain notification in close time proximity.
        String futureNotificationQuery = "AND Notification.notificationTime > CURRENT_TIMESTAMP()";
        // Gets the notifications from the database.
        ArrayList<HashMap> newFutureNotifications;
        if (futureNotifications != null) {    
            newFutureNotifications = connector.getUserNotifications(futureNotificationQuery, userName);
            ArrayList<HashMap> tempList = newFutureNotifications;
            //System.out.println("futureNotifications.size(): " + this.futureNotifications.size());
            //System.out.println("newList.size(): " + newFutureNotifications.size());
            // calls a method that check for notification equality so there wont be redundant Timer threads.
            newFutureNotifications = getUniqueNotification(newFutureNotifications);
            futureNotifications = tempList;
            //System.out.println("newList.size(): " + newFutureNotifications.size());
            
        }
        else {
            futureNotifications = connector.getUserNotifications(futureNotificationQuery, userName);  
            newFutureNotifications = futureNotifications;
            //System.out.println("this.futureNotifications.size(): " + this.futureNotifications.size());
        }
        
        if (newFutureNotifications.size() > 0) {
            
            Date date;
            //Timer timer = new Timer();
            for (HashMap notification : newFutureNotifications) {

                Timer timer = new Timer();
                int idNotification = (Integer) notification.get("idNotification");  
                Timestamp stamp = (Timestamp) notification.get("timestamp");
                
                TimerTask task = new TimerTask() {
                    @Override
                    public void run() {
                        //checkForNotifications();
                        markNotificationAsReady(idNotification);
                        // PUT Notification in a form of ready to view list.
                    }
                };
                        
                SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss");
                try {
                    date = SDF.parse(stamp.toString());
                    //System.out.println(date.toString());
                    timer.schedule(task, date);
                    // Adds the timer to a list to avoid rogue threads after System.exit
                    timers.add(timer);

                } catch (ParseException ex) {
                    Logger.getLogger(Notification.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        checkForNotifications();
    }
    
    /**
     * This method compares the futureNotifications list with another list
     * and returns an ArrayList containing only the none overlapping unique elements.
     */
    private ArrayList<HashMap> getUniqueNotification(ArrayList<HashMap> unseenNotifications) {
        Set newSet = new HashSet(unseenNotifications);
        Set oldSet = new HashSet(this.futureNotifications);
        // returns a SetView that only contains the elements that are unique for one Set. 
        SetView setView = Sets.symmetricDifference(newSet, oldSet); 
        unseenNotifications = new ArrayList(setView);
        return unseenNotifications;
    }
    
    /**
     * Checks the database for unseen notifications that match the current timestamp.
     * If there are unseen notifications that got a timestamp less or equal to current time 
     * The notification buttons will be renamed so that the user will see the amount of unseen notifications.
     * 
     */
    public void checkForNotifications() {
        //String currentUnseenNotificationQuery = "AND Notification.notificationTime <= CURRENT_TIMESTAMP()";
        // Gets all unseen notifications for the current user.
        ArrayList<HashMap> notificationList = connector.getUserNotifications("", userName);
        // Removes the notifications already registered by the Timers
        notificationList = getUniqueNotification(notificationList);
        // adds the old unseen notifications to the unseenNotifications list
        unseenNotifications.addAll(notificationList);
        //System.out.println("unseen notificationList.size(): " + notificationList.size());
        if (unseenNotifications.size() > 0) {
            userGUINotificationButton.setText("Varsler(" + unseenNotifications.size() + ")");
            frame.pack();
            frame.repaint();
            try {
                notificationGUIButton.setText("Varsler(" + unseenNotifications.size() + ")");
            } 
            catch (NullPointerException np) {
               System.out.println("No active notificationGUI"); 
            }
            //panel.repaint();
            
        }
    }    
    
    /**
     * Gets a notification with the id specified by the parameter .
     * @param idNotification the identifier of the notification that will be returned by the database
     */
    private void markNotificationAsReady(int idNotification) {
        for (HashMap notification: futureNotifications) {
            if ((int)notification.get("idNotification") == idNotification) {
                unseenNotifications.add(notification);
            }
        }
        if (unseenNotifications.size() > 0) {
            userGUINotificationButton.setText("Varsler(" + unseenNotifications.size() + ")");
            frame.pack();
            frame.repaint();
            try {
                notificationGUIButton.setText("Varsler(" + unseenNotifications.size() + ")");
            } 
            catch (NullPointerException np) {
               System.out.println("No active notificationGUI"); 
            }
            //panel.repaint();
        }     
        
    }

    /**
     * Adds a windowListener to the frame that will close down all
     * timer threads made by the Notification object.
     * 
     */
    public void frameAddWindowListener() {
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // Closing down all Timer threads
                removeNotificationThreads();
                frame.dispose();
                System.exit(0);
            }
        });   
    }
    
    /**
     * Cancels all notification timer threads, only use this if you are 100% sure its a 
     * good idea.
     */
    public void removeNotificationThreads() {
        // Closing down all Timer threads
        System.out.println("Closing down all Timer threads");
        for (Timer timer: timers) {
            timer.purge();
            timer.cancel();
        }       
    }

    /**
     * This method initiate the notification "update loop".
     * a Timer that checks for notifications at fixed intervals
     */
    private void initiateNotificationUpdateLoop() {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                checkForNotifications();
            }
        };
        Timer timer = new Timer();        
        // Will currently check for updates every 5 minutes
        timer.scheduleAtFixedRate(task, 300000, 300000);
        timers.add(timer);
    }



    public ArrayList<HashMap> getUnseenNotifications() {
        return unseenNotifications;
    }
    
    /**
     * Marks all the notifications in the list as seen.
     * 
     * @param seenNotifications list of notification IDs.
     */
    public void markNotificationsAsSeen(ArrayList<Integer> seenNotifications) {
        connector.markNotificationsAsSeen(seenNotifications);
        unseenNotifications.clear();

    }

}
