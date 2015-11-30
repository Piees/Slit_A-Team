/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package notification;

import util.HintTextField;
import util.JTextFieldLimit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;

/**
 * This class contains all the logic of the Notification GUI
 * in the project SLIT-client.
 * 
 * @author Viktor Setervang
 */
public class NotificationGUI {
    
    // This is a button from the UserGUI class
    private JButton mainGUINotificationButton;
   
    private JFrame frame;
    private JPanel panel;
    private JDialog dialog;   
    private JButton seeNotificationButton, createNotificationButton;
    private JLabel displayedTimestamp;
    private JTextFieldLimit notificationText;
    
    private String userName;
    private HashMap<String, String> userInfo, dateMap;

    private Notification notification;
    
    /**
     * Constructor for the NotificationGUI class.
     * 
     * @param frame the frame were most of the information is displayed
     * @param userInfo a hashmap containing all the useful information about a user.
     * @param mainGUINotificationButton the "Varsler" button in the main UserGUI
     * @param notification  the object handling most of the notification logic
     */
    public NotificationGUI(JFrame frame, HashMap<String, String> userInfo, JButton mainGUINotificationButton, Notification notification) { 
        this.notification = notification;
        this.frame = frame;
        this.userInfo = userInfo;
        this.mainGUINotificationButton = mainGUINotificationButton;
        userName = this.userInfo.get("userName");
        dateMap = new HashMap<>();
        seeNotificationButton = new JButton("Ingen varsler");

        notification.setSeeNotificationGUIButton(seeNotificationButton);       
        createNotificationGUI();

    }
    
    /**
     * GUI containing a "see notification button" and possibility for 
     * creating new notifications
     */
    private void createNotificationGUI() {
        if (!notification.getUnseenNotifications().isEmpty()) {
            seeNotificationButton.setText("Varsler(" + notification.getUnseenNotifications().size() + ")");
        }
        dialog = new JDialog(frame, "Varsel");
        panel = new JPanel();
        
        displayedTimestamp = new JLabel(" ");
        notificationText = new JTextFieldLimit(45);

        JButton setNotificationTimeButton = new JButton("Sett tidspunkt");
        JLabel alertTextLabel = new JLabel("<html><b>Varsel tekst (maks 45 tegn).</b></html>");
        createNotificationButton = new JButton("Opprett varsel");

        panel.add(seeNotificationButton);
        panel.add(new JLabel(" "));
        panel.add(new JLabel("<html><b>Opprett nytt varsel:<b></html>"));
        panel.add(setNotificationTimeButton);
	panel.add(displayedTimestamp);	
	panel.add(alertTextLabel);
        panel.add(notificationText);
	panel.add(createNotificationButton);
        panel.add(new JLabel(" "));
        dialog.add(panel);
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));  
        
        // The setSize doesnt seem to do anything
        //panel.setSize(650, 650);
        //dialog.setSize(650, 650);
        panel.repaint();
        dialog.setVisible(true);
        dialog.pack();
        dialog.repaint();

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
        
        createNotificationButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                NotificationCreater nc = new NotificationCreater();
                //String status = createNewNotification();
                String time = dateMap.get("year") + "-" + dateMap.get("month") + "-" + dateMap.get("day") + " " + dateMap.get("time");
                String text = notificationText.getText();
                
                String status = nc.createNewNotification(userName, time, text);
                if (status.equals("Opplastning vellykket!")) {
                    JOptionPane.showMessageDialog(null, "Varsel opprettet");
                }
                // Method below should be in an other class
                notification.createNotificationTimers();
                dialog.dispose();
            }
        });
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
        ArrayList<HashMap> unseenNotifications = notification.getUnseenNotifications();
        if (unseenNotifications.isEmpty()) {
            return;
        }

        panel.removeAll();  
        panel.validate();

        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));  
        //frame.repaint();

        DateHandler dh = new DateHandler();
        ArrayList<Integer> seenNotifications = new ArrayList<>();
        for (HashMap notification : unseenNotifications) {
            String timestamp = notification.get("timestamp").toString();
            timestamp = dh.removeFractionalSeconds(timestamp);
            String notificationText = (String) notification.get("notificationText");
            String viewFormat = timestamp + "\n" + notificationText;
            seenNotifications.add((Integer) notification.get("idNotification"));
            JTextArea nText = new JTextArea(viewFormat);
            nText.setEditable(false);
            nText.setWrapStyleWord(true);
            panel.add(nText);
        }
        JButton goBack = new JButton("Gå tilbake");
        panel.add(goBack);
        panel.validate();

        goBack.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { 
                notification.markNotificationsAsSeen(seenNotifications);
                unseenNotifications.clear();
                dialog.dispose();
            }
        });
        this.seeNotificationButton.setText("Ingen varsler");
        this.mainGUINotificationButton.setText("Varsler");
        panel.repaint();
        //dialog.pack();

    }
     
    /**
     * GUI for choosing a date and time when creating new notifications
     */
    private void datepicker() {
        UtilDateModel model = new UtilDateModel();
        
        //model.setDate(20,04,2014);
        // Need this...
        Properties p = new Properties();
        p.put("text.today", "Today");
        p.put("text.month", "Month");
        p.put("text.year", "Year");
        JDatePanelImpl datePanel = new JDatePanelImpl(model, p);
        datePanel.setToolTipText("Trykk her");
        datePanel.repaint();
        
        DateLabelFormatter DLF = new DateLabelFormatter();
        JDatePickerImpl datePicker = new JDatePickerImpl(datePanel, DLF);
        datePicker.setToolTipText("Trykk her for å åpne kalender");
        datePicker.repaint();

        JDialog dateDialog = new JDialog(dialog, "Varsel Tidspunkt", true);
        JPanel datePanel2 = new JPanel();
        
        JLabel timeLabel = new JLabel("<html><b>Sett varsel tid i format; \"tt:mm\"</b></html>");
        HintTextField timeTextField = new HintTextField("tt:mm");
        JButton approveDateButton = new JButton("Bekreft tidspunkt");
        
        DateHandler dateHandler = new DateHandler();
        
        approveDateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String checkTimeFormat = dateHandler.checkTimeFormat(timeTextField.getText());
                    if (!checkTimeFormat.equals("correct input")) {
                        JOptionPane.showMessageDialog(frame,
                        checkTimeFormat,
                        "Feil klokkeslett",
                        JOptionPane.ERROR_MESSAGE);
                    }
                    else {
                        dateMap = (dateHandler.splitDateIntoHashMap(datePicker.getModel().getValue()));
                        dateMap.put("time", timeTextField.getText() + ":00");
                        dateMap.put("timeDisplay", timeTextField.getText());
                        if (dateMap != null) {
                            displayedTimestamp.setText("<html><b>Varsel tid:</b> " + dateMap.get("dow") + " " + 
                                    dateMap.get("day") + "-" + dateMap.get("month") + 
                                    "-" + dateMap.get("year") + "  " + dateMap.get("timeDisplay") + "</html>");
                            panel.repaint();
                            dialog.pack();
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
        datePanel2.add(new JLabel("<html><b>Trykk på knappen [...] for å sette dato</b></html>"));
        datePanel2.add(datePicker);             
        datePanel2.add(timeLabel);
        datePanel2.add(timeTextField);
        datePanel2.add(approveDateButton);
        datePanel2.repaint();
        dateDialog.pack();
        dateDialog.setVisible(true);
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
                createNotificationGUI();
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
}
