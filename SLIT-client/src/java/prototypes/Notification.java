/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prototypes;

import db.dbConnectorRemote;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import java.sql.Timestamp;
import java.util.logging.Level;
import java.util.logging.Logger;
import slitclient.EJBConnector;
 

/**
 *
 * @author Viktor Setervang
 */
public class Notification {
    private String userName = "viktos08";
    private JButton seeNotificationButton;   
    private EJBConnector ejbConnector = EJBConnector.getInstance();
    private dbConnectorRemote connector = ejbConnector.getEjbRemote();

    public Notification() {        
       	JFrame frame = new JFrame("Notification");
	frame.setSize(500, 350);
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	placeComponents(frame);
	frame.setVisible(true); 
    }

    private void placeComponents(JFrame frame) {
	frame.setLayout(null);
        
        checkNotifications(frame);
        
	JLabel notificationLabel = new JLabel("Notification Time");
	notificationLabel.setBounds(10, 50, 110, 25);
	frame.add(notificationLabel);
        
	HintTextField notificationTime = new HintTextField("yyyy-mm-dd hh:mm:ss");
	notificationTime.setBounds(130, 50, 160, 25);
	frame.add(notificationTime);

	JLabel alertTextLabel = new JLabel("Notification Text");
	alertTextLabel.setBounds(10, 80, 110, 25);
	frame.add(alertTextLabel);

	JTextField notificationText = new JTextField(20);
	notificationText.setBounds(130, 80, 160, 25);
	frame.add(notificationText);

	JButton createNotificationButton = new JButton("Create Notification");
	createNotificationButton.setBounds(10, 120, 180, 25);
	frame.add(createNotificationButton);

        
        createNotificationButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {    

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
                    dbConnector.insertIntoDB("Notification", columns, values);
                    System.out.println("ugly hack inc!");
                    // DETTE FÅR PROGRAMMET TIL Å STOPPE OPP I 1 sekund, 
                    // SLIK AT DB REKKER Å OPPDATERE SEG, VI BØR NOK IKKE HA
                    // LIGNENDE LOGIKK I DEN ENDELIGE VERSJONEN.
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Notification.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    checkNotifications(frame);
                }

            }  
        });
        

    }
    
    private void checkNotifications(JFrame frame) {
        // Using the logged in userName
        // Den faktiske queryen. Den funker i mysql-workbench
        String query = "SELECT * " +
                       "FROM Notification " +
                       "WHERE userName=? " +
                       "AND Notification.notificationTime <= CURRENT_TIMESTAMP()" +
                       "AND Notification.seen=?";

        ArrayList<ArrayList> notificationList = connector.getUserNotifications(query, userName);
        System.out.println("notifications: " + notificationList.size());
        if (notificationList.size() > 0) {
            seeNotificationButton = new JButton(notificationList.size() + " Notifications!" );
            seeNotificationButton.setBounds(10, 10, 160, 25);
            frame.add(seeNotificationButton);
            SNBAddActionListener(frame, notificationList);
        }
    }
        
    private void SNBAddActionListener(JFrame frame, ArrayList<ArrayList> notificationList) {
        seeNotificationButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {  
                frame.getContentPane().removeAll();
                
                frame.setLayout(new GridLayout(0, 1));  
                
                ArrayList<Integer> seenNotifications = new ArrayList<>();
                for (ArrayList notification : notificationList) {
                    String timestamp = notification.get(1).toString();
                    String notificationText = (String) notification.get(2);
                    String viewFormat = timestamp + ": \n" + notificationText;
                    // This looks like shit, but it its just an early prototype after all
                    seenNotifications.add((Integer) notification.get(0));
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
                        frame.dispose();
                        new Notification();
                    }
                });
            }
        });        
    }
    public static void main(String[] args) {
        new Notification();
        
    }

}
