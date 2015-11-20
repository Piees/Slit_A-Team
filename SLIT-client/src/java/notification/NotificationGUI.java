/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package notification;

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
import javax.swing.JTextField;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;

/**
 *
 * @author Viktor Setervang
 */
public class NotificationGUI {
    
    // This is a button from the UserGUI class
    private JButton mainGUINotificationButton;
   
    private JFrame frame;
    private JPanel panel;
    private JDialog dialog;   
    private JButton seeNotificationButton;
    private JLabel displayedTimestamp;
    private JTextField notificationText;
    private JButton createNotificationButton;
    
    private String userName;
    private HashMap<String, String> userInfo;
    private HashMap<String, String> dateMap;

    private Notification notification;
    
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
        // Bit of a hack
        //panel.removeAll();
        if (!notification.getUnseenNotifications().isEmpty()) {
            seeNotificationButton.setText("Varsler(" + notification.getUnseenNotifications().size() + ")");
        }
        dialog = new JDialog();
        panel = new JPanel();
        
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
        
        // The setSize doesnt seem to do anything
        panel.setSize(650, 450);
        dialog.setSize(650, 450);
        panel.repaint();
        dialog.setVisible(true);
        dialog.pack();
        dialog.repaint();
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
        JButton goBack = new JButton("Gå tilbake");
        panel.add(goBack);
        panel.validate();

        goBack.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { 
                notification.markNotificationsAsSeen(seenNotifications);
                unseenNotifications.clear();
                // This is probably an ugly hack.
                //dialog.removeAll();
                //dialog.setVisible(false);
                dialog.dispose();
            }
        });
        this.seeNotificationButton.setText("Ingen varsler");
        this.mainGUINotificationButton.setText("Varsler");
        panel.repaint();
        dialog.pack();

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
        
        JLabel timeLabel = new JLabel("Klokkeslett: \"tt:mm\"");
        HintTextField timeTextField = new HintTextField("tt:mm");
        JButton approveDateButton = new JButton("Enter dato");
        
        DateHandler dateHandler = new DateHandler();
        
        approveDateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    //actualTimestamp = (datePicker.getModel().getValue().toString());
                    String checkTimeFormat = dateHandler.checkTimeFormat(timeTextField.getText());
                    if (!checkTimeFormat.equals("correct input")) {
                        JOptionPane.showMessageDialog(frame,
                        checkTimeFormat,
                        "Feil klokkeslett",
                        JOptionPane.ERROR_MESSAGE);
                    }
                    else {
                        dateMap = (dateHandler.valueToDateString(datePicker.getModel().getValue()));
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
