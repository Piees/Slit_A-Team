/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package notification;

import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;
import db.dbConnectorRemote;
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
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Set;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JDialog;

import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;
import slitclient.EJBConnector;

/**
 *
 * @author Viktor Setervang
 */
public class Notification {
    private JButton mainGUINotificationButton;
    
    private String userName;
    private final EJBConnector ejbConnector = EJBConnector.getInstance();
    private final dbConnectorRemote connector = ejbConnector.getEjbRemote();    
   
    private JFrame frame;
    private JPanel panel;
    private JDialog dialog;
    
    private JButton seeNotificationButton;   
    //private HintTextField notificationTime;
    //private JTextField notificationTime;
    //private String actualTimestamp;
    private JLabel displayedTimestamp;
    private JTextField notificationText;
    private JButton createNotificationButton;
    
    private HashMap<String, String> userInfo;
    private HashMap<String, String> dateMap;
    
    
    private ArrayList<HashMap> unseenNotifications;
    private ArrayList<HashMap> notificationList;
    // This field is for equality checks, so we dont end up with redudant Timer threads.
    private ArrayList<HashMap> futureNotifications;
    // This should be a sort of collection of Time objects, so there wont be "collisions".
    private ArrayList<Timer> timers;
    
    public Notification(JFrame frame, HashMap<String, String> userInfo, JButton mainGUINotificationButton) { 
        this.frame = frame;
        this.userInfo = userInfo;
        this.mainGUINotificationButton = mainGUINotificationButton;
        unseenNotifications = new ArrayList<>();
        timers = new ArrayList<>();
        userName = this.userInfo.get("userName");
        dateMap = new HashMap<>();
       	seeNotificationButton = new JButton("Ingen varsler");
        dialog = new JDialog();
        panel = new JPanel();

        
        frameAddWindowListener();
        
        createNotificationTimers();
        //checkForNotifications();
    }
    
    /**
     * GUI containing a "see notification button" and possibility for 
     * creating new notifications
     */
    public void createNotification() {
        // Bit of a hack
        panel.removeAll();
        
        
        displayedTimestamp = new JLabel(" ");
        notificationText = new JTextField(20);

        JButton setNotificationTimeButton = new JButton("Sett dato");
        JLabel alertTextLabel = new JLabel("<html><b>Varsel tekst</b></html>");
        createNotificationButton = new JButton("Opprett varsel");

        panel.add(seeNotificationButton);
        panel.add(new JLabel(" "));
        panel.add(new JLabel("<html><b>Opprett nytt varsel:<b></html>"));
        panel.add(setNotificationTimeButton);
	panel.add(displayedTimestamp);	
	panel.add(alertTextLabel);
        panel.add(notificationText);
	panel.add(createNotificationButton);
        
        dialog.add(panel);
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));  

        panel.setSize(650, 450);
        dialog.setSize(650, 450);
        panel.repaint();
        dialog.setVisible(true);
        dialog.pack();
        dialog.repaint();
        createNotificationButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createNewNotification();
            }
        });

        seeNotificationButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                seeNotifications();
            }
        });   
        
        setNotificationTimeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                datepicker();
            }
        });     
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
                //date.setTime(stamp.getTime());

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
    private void checkForNotifications() {
        //String currentUnseenNotificationQuery = "AND Notification.notificationTime <= CURRENT_TIMESTAMP()";
        // Gets all unseen notifications for the current user.
        ArrayList<HashMap> notificationList = connector.getUserNotifications("", userName);
        // Removes the notifications already registered by the Timers
        notificationList = getUniqueNotification(notificationList);
        // adds the old unseen notifications to the unseenNotifications list
        unseenNotifications.addAll(notificationList);
        //System.out.println("unseen notificationList.size(): " + notificationList.size());
        if (unseenNotifications.size() > 0) {
            seeNotificationButton.setText(unseenNotifications.size() + " nye varsler");
            mainGUINotificationButton.setText("Varsler(" + unseenNotifications.size() + ")");
            panel.repaint();
            frame.pack();
        }
    }
    
//    /**
//     * 
//     */
//    private ArrayList<HashMap> removeRegisteredNotification2(ArrayList<HashMap> oldUnseenNotifications) {
//        Set newSet = new HashSet(oldUnseenNotifications);
//        Set oldSet = new HashSet(this.futureNotifications);
//        // returns a SetView that only contains the elements that are unique for one Set. 
//        SetView setView = Sets.difference(newSet, oldSet); 
//        //this.futureNotifications = newFutureNotifications;
//        newFutureNotifications = new ArrayList(setView);
//        
//        return newFutureNotifications;       
//    }
    
    
    
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
            seeNotificationButton.setText(unseenNotifications.size() + " nye varsler");
            mainGUINotificationButton.setText("Varsler(" + unseenNotifications.size() + ")");
            panel.repaint();
        }           
        
    }


    
    /**
     * Action listener for the CreateNotificationButton
     * 
     * Needs to call createNotificationTimers() with just the new Notification 
     * data, so there wont be redundant Timer threads.
     */
    private void createNewNotification() { 
        // time might be wrong format
        String time = dateMap.get("year") + "-" + dateMap.get("month") + "-" + dateMap.get("day") + " " + dateMap.get("time");
        String text = notificationText.getText();
        
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
//            // DETTE FoR PROGRAMMET TIL å STOPPE OPP I 1 sekund, 
//            // SLIK AT DB MEST SANSYNLIGVIS REKKER å OPPDATERE SEG(INGEN GARANTI), 
//            // VI BøR NOK IKKE HA LIGNENDE LOGIKK I DEN ENDELIGE VERSJONEN.
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException ex) {
//                Logger.getLogger(Notification.class.getName()).log(Level.SEVERE, null, ex);
//            }
            createNotificationTimers();
        }
        
    }
    
    
    /**
     * Used by the seeNotificationButton.
     * This method displays all unseen notifications.
     */
    private void seeNotifications() {    
        // This must be the first thing that happens, incase a new notification sets it to true;
        //seeNotificationButton.setVisible(false);

       // String currentUnseenNotificationQuery = "AND Notification.notificationTime <= CURRENT_TIMESTAMP()";
       // notificationList = connector.getUserNotifications(currentUnseenNotificationQuery, userName);
        if (unseenNotifications.size() == 0) {
//            JOptionPane.showMessageDialog(frame,
//            "Ingen varsler");
//            dialog.dispose();
            return;
        }

        panel.removeAll();  
        panel.validate();

        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));  
        //frame.repaint();

        ArrayList<Integer> seenNotifications = new ArrayList<>();
        for (HashMap notification : unseenNotifications) {
            String timestamp = notification.get("timestamp").toString();
            String notificationText = (String) notification.get("notificationText");
            String viewFormat = timestamp + ": \n" + notificationText;
            // This looks like shit, but it its just an early prototype after all
            seenNotifications.add((Integer) notification.get("idNotification"));
            JLabel label = new JLabel(viewFormat);
            panel.add(label);
        }
        JButton goBack = new JButton("Go back");
        panel.add(goBack);
        panel.validate();

        goBack.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { 
                connector.markNotificationsAsSeen(seenNotifications);
                unseenNotifications.clear();
                // This is probably an ugly hack.
                //dialog.removeAll();
                //dialog.setVisible(false);
                dialog.dispose();
                createNotificationTimers();
            }
        });
        this.seeNotificationButton.setText("Ingen varsler");
        this.mainGUINotificationButton.setText("Varsler");
        panel.repaint();
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
    
    /**
     * GUI for choosing a date and time when creating new notifications
     */
    private void datepicker() {
        System.out.println("Datepicker button clicked");
        UtilDateModel model = new UtilDateModel();
        //model.setDate(20,04,2014);
        // Need this...
        Properties p = new Properties();
        p.put("text.today", "Today");
        p.put("text.month", "Month");
        p.put("text.year", "Year");
        JDatePanelImpl datePanel = new JDatePanelImpl(model, p);
        // Don't know about the formatter, but there it is...
        DateLabelFormatter DLF = new DateLabelFormatter();
        JDatePickerImpl datePicker = new JDatePickerImpl(datePanel, DLF);
        //datePicker.repaint();
        //datePicker.setVisible(true);
        JDialog dateDialog = new JDialog();
        JPanel datePanel2 = new JPanel();
        
        JLabel timeLabel = new JLabel("Klokkeslett: \"hh:mm\"");
        HintTextField timeTextField = new HintTextField("hh:mm");
        JButton approveDateButton = new JButton("Enter dato");
        approveDateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    //actualTimestamp = (datePicker.getModel().getValue().toString());
                    String checkTimeFormat = DLF.chechTimeFormat(timeTextField.getText());
                    if (!checkTimeFormat.equals("correct input")) {
                        JOptionPane.showMessageDialog(frame,
                        checkTimeFormat,
                        "Feil klokkeslett",
                        JOptionPane.ERROR_MESSAGE);
                    }
                    else {
                        dateMap = (DLF.valueToDateString(datePicker.getModel().getValue()));
                        dateMap.put("time", timeTextField.getText() + ":00");
                        dateMap.put("timeDisplay", timeTextField.getText());
                        if (dateMap != null) {
                            displayedTimestamp.setText("<html><b>Varsel tid:</b> " + dateMap.get("dow") + " " + 
                                    dateMap.get("day") + "-" + dateMap.get("month") + 
                                    "-" + dateMap.get("year") + "  " + dateMap.get("timeDisplay") + "</html>");
                            panel.repaint();
                            dateDialog.dispose();                      }
                        else {
                            JOptionPane.showMessageDialog(frame,
                            "Sett dato!",
                            "Dato",
                            JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } catch (ParseException ex) {
                    Logger.getLogger(Notification.class.getName()).log(Level.SEVERE, null, ex);
                    dateDialog.dispose();
                }
                
                
            }
        });
        dateDialog.add(datePanel2);
        datePanel2.setLayout(new BoxLayout(datePanel2, BoxLayout.Y_AXIS));
        datePanel2.add(datePicker);             
        datePanel2.add(timeLabel);
        datePanel2.add(timeTextField);
        datePanel2.add(approveDateButton);
        dateDialog.setSize(200, 150);
        datePanel2.repaint();
        dateDialog.setVisible(true);
        //java.sql.Date selectedDate = (java.sql.Date) datePicker.getModel().getValue();


    }
    

    /**
     * Changes the visible state of the seeNotificationButton
     * @param flag sets the visible state of the seeNotificationButton
     * @deprecated
     */
    @Deprecated
    private void setNBVisibility(boolean flag) {
        seeNotificationButton.setVisible(flag);
        panel.repaint();
    }
    
    /**
     * The first Notification GUI that is represented to the user. The user 
     * can here choose to create or see notifications.
     */
    @Deprecated
    public void notificationDialog() {
        JButton createNotificationBtn = new JButton("Nytt varsel");
        seeNotificationButton = new JButton("Se varsler");
        JDialog dialog = new JDialog();
        JPanel panel = new JPanel();
        //panel.setLayout(new GridLayout(0, 2));
        panel.add(createNotificationBtn);
        panel.add(seeNotificationButton);
        dialog.add(panel);
        
        panel.repaint();
        dialog.setVisible(true);
        dialog.pack();
        dialog.repaint();
        
        createNotificationBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createNotification();
            }
        });
        
        //seeNotificationButton.setVisible(false);
        seeNotificationButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                seeNotifications();
            }
        });

    }

//    public static void main(String[] args) {
//        new Notification();
//
//    }

}
