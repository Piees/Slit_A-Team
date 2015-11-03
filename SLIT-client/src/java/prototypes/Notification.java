/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prototypes;

import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;
import db.dbConnectorRemote;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
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
import slitclient.EJBConnector;
import java.util.Set;

/**
 *
 * @author Viktor Setervang
 */
public class Notification {
    private String userName = "viktos08";
    private EJBConnector ejbConnector = EJBConnector.getInstance();
    private dbConnectorRemote connector = ejbConnector.getEjbRemote();    
   
    private JFrame frame;
    private JButton seeNotificationButton;   
    private HintTextField notificationTime;
    private JTextField notificationText;
    private JButton createNotificationButton;
    
    private ArrayList<HashMap> notificationList;
    // This field is for equality checks, so we dont end up with redudant Timer threads.
    private ArrayList<HashMap> futureNotifications;
    // This should be a sort of collection of Time objects, so there wont be "collisions".
    private ArrayList<Timer> timers;
    
    public Notification() { 
        timers = new ArrayList<>();
        
       	frame = new JFrame("Notification");
        frameAddWindowListener();
        frame.setVisible(true);
        
	frame.setSize(500, 350);
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	placeComponents();
	frame.setVisible(true); 
    }

    private void placeComponents() {
	frame.setLayout(null);
        
	JLabel notificationLabel = new JLabel("Notification Time");
	notificationLabel.setBounds(10, 50, 110, 25);
	frame.add(notificationLabel);
        
	notificationTime = new HintTextField("yyyy-mm-dd hh:mm:ss");
	notificationTime.setBounds(130, 50, 160, 25);
	frame.add(notificationTime);

	JLabel alertTextLabel = new JLabel("Notification Text");
	alertTextLabel.setBounds(10, 80, 110, 25);
	frame.add(alertTextLabel);

	notificationText = new JTextField(20);
	notificationText.setBounds(130, 80, 160, 25);
	frame.add(notificationText);

	createNotificationButton = new JButton("Create Notification");
	createNotificationButton.setBounds(10, 120, 180, 25);
	frame.add(createNotificationButton);
        
        //seeNotificationButton = new JButton(notificationList.size() + " Notifications!" );
        seeNotificationButton = new JButton("Notifications");
        seeNotificationButton.setBounds(10, 10, 160, 25);
        frame.add(seeNotificationButton);
        seeNotificationButton.setVisible(false);
        
        
        createNotificationButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createNewNotification();
            }
        });
        
        SNBAddActionListener();
        createNotificationTimers();
        //checkForNotifications();        
        frame.repaint();        
    }
    
    /**
     * This method puts future notification timestamps into a timer.
     * When The timer goes off, the method checkForNotifications will be called.
     */
    private void createNotificationTimers() {
        // Will be changed to only contain notification in close time proximity.
        String futureNotificationQuery = "AND Notification.notificationTime > CURRENT_TIMESTAMP()";
        // Gets the notifications from the database.
        ArrayList<HashMap> newFutureNotifications;
        if (futureNotifications != null) {    
            newFutureNotifications = connector.getUserNotifications(futureNotificationQuery, userName);
            //sSystem.out.println("futureNotifications.size(): " + this.futureNotifications.size());
            //System.out.println("newList.size(): " + newFutureNotifications.size());
            // calls a method that check for notification equality so there wont be redundant Timer threads.
            newFutureNotifications = removeRegisteredNotification(newFutureNotifications);
            //System.out.println("newList.size(): " + newFutureNotifications.size());
            
        }
        else {
            futureNotifications = connector.getUserNotifications(futureNotificationQuery, userName);  
            newFutureNotifications = futureNotifications;
            System.out.println("this.futureNotifications.size(): " + this.futureNotifications.size());
        }
        
        if (newFutureNotifications.size() > 0) {
            
            // Can a timer have multiple scheduled timers?

            Date date;
            //Timer timer = new Timer();
            for (HashMap notification : newFutureNotifications) {
                TimerTask task = new TimerTask() {
                    @Override
                    public void run() {
                        checkForNotifications();
                    }
                };
                
                Timer timer = new Timer();
                Timestamp stamp = (Timestamp) notification.get("timestamp");
                SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss");
                try {
                    date = SDF.parse(stamp.toString());
                    System.out.println(date.toString());
                    timer.schedule(task, date);
                    timers.add(timer);

                } catch (ParseException ex) {
                    Logger.getLogger(Notification.class.getName()).log(Level.SEVERE, null, ex);
                }
                //date.setTime(stamp.getTime());

            }
        }
        checkForNotifications();
    }
    
    /**
     * This method compares the old futureNotification list with the new list
     * and returns an ArrayList containing only the unique elements.
     * It also updates the futureNotifactions list
     * 
     * (metoden bør kanskje skifte navn til noe mer beskrivende)
     */
    public ArrayList<HashMap> removeRegisteredNotification(ArrayList<HashMap> newFutureNotifications) {
        Set newSet = new HashSet(newFutureNotifications);
        Set oldSet = new HashSet(this.futureNotifications);
        // returns a SetView that only contains the elements that are unique for one Set. 
        SetView setView = Sets.symmetricDifference(newSet, oldSet); 
        this.futureNotifications = newFutureNotifications;
        newFutureNotifications = new ArrayList(setView);
        
        return newFutureNotifications;
    }
    
    private void checkForNotifications() {
        String currentUnseenNotificationQuery = "AND Notification.notificationTime <= CURRENT_TIMESTAMP()";
        notificationList = connector.getUserNotifications(currentUnseenNotificationQuery, userName);
        System.out.println("unseen notificationList.size(): " + notificationList.size());
        if (notificationList.size() > 0) {
            seeNotificationButton.setVisible(true);
            frame.repaint();
        }
    }
    
    public void setNBVisibility(boolean flag) {
        seeNotificationButton.setVisible(flag);
        frame.repaint();
    }
    
    /**
     * Action listener for the CreateNotificationButton
     * 
     * Needs to call createNotificationTimers() with just the new Notification 
     * data, so there wont be redundant Timer threads.
     */
    private void createNewNotification() { 
        String time = notificationTime.getText();
        String text = notificationText.getText();
        System.out.println(text);
        /*
        timestamp in format yyyy-[m]m-[d]d hh:mm:ss[.f...]. 
        The fractional seconds may be omitted. The leading zero 
        for mm and dd may also be omitted. 
        */
        Timestamp timestamp = null;
        try {
            timestamp = Timestamp.valueOf(time);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "provide a valid "
                    + "notification date and text");                
        }
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
            System.out.println(dbConnector.insertIntoDB("Notification", columns, values));
//            System.out.println("ugly hack inc!");
//            // DETTE FÅR PROGRAMMET TIL Å STOPPE OPP I 1 sekund, 
//            // SLIK AT DB MEST SANSYNLIGVIS REKKER Å OPPDATERE SEG(INGEN GARANTI), 
//            // VI BØR NOK IKKE HA LIGNENDE LOGIKK I DEN ENDELIGE VERSJONEN.
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException ex) {
//                Logger.getLogger(Notification.class.getName()).log(Level.SEVERE, null, ex);
//            }
            createNotificationTimers();
        }
        
    }
    
    

    private void SNBAddActionListener() {
        seeNotificationButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {    
            // This must be the first thing that happens, incase a new notification sets it to true;
            //seeNotificationButton.setVisible(false);

            String currentUnseenNotificationQuery = "AND Notification.notificationTime <= CURRENT_TIMESTAMP()";
            notificationList = connector.getUserNotifications(currentUnseenNotificationQuery, userName);
            System.out.println("notifications: " + notificationList.size());

            frame.getContentPane().removeAll();                
            frame.setLayout(new GridLayout(0, 1));  

            ArrayList<Integer> seenNotifications = new ArrayList<>();
            for (HashMap notification : notificationList) {
                String timestamp = notification.get("timestamp").toString();
                String notificationText = (String) notification.get("notificationText");
                String viewFormat = timestamp + ": \n" + notificationText;
                // This looks like shit, but it its just an early prototype after all
                seenNotifications.add((Integer) notification.get("idNotification"));
                JLabel label = new JLabel(viewFormat);
                frame.add(label);
            }
            JButton goBack = new JButton("Go back");
            frame.add(goBack);
            frame.validate();

            goBack.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) { 
                    connector.markNotificationsAsSeen(seenNotifications);
                    // This is probably an ugly hack.
                    frame.dispose();
                    new Notification();
                }
            });
            }
        });
    }
    
    public void frameAddWindowListener() {
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // Closing down all Time threads
                for (Timer timer: timers) {
                    timer.purge();
                    timer.cancel();
                }
                frame.dispose();
                System.exit(0);
            }
        });   
    }
    

    public static void main(String[] args) {
        new Notification();

    }

}
