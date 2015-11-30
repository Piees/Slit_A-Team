/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prototypes;

import db.DBDeleterRemote;
import db.DBInserterRemote;
import db.DBUpdaterRemote;
import db.DBUtilRemote;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import slitclient.EJBConnector;

/**
 * This is the core notification class. Its main purpose is to check for new
 * notifications and register seen notifications It also handles the threads
 * used to display new notifications,
 *
 * @author Steffen Sande
 * @author Arild Høyland
 * @author Yngve Ranestad
 * @author Peter Hagane
 */
public class EditUser {

    DBUtilRemote dbUtil;

    public EditUser() {

        EJBConnector ejbConnector = EJBConnector.getInstance();
        this.dbUtil = ejbConnector.getDBUtil();
        System.out.println("EditUser class, before calling dbUtil.updateUsersHashMap();");
        dbUtil.updateUsersHashMap();
        System.out.println("EditUser class, after calling dbUtil.updateUsersHashMap();");

        JFrame frame = new JFrame();

        frame.setSize(900, 470);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        setComponents(frame);

    }

    private void setComponents(JFrame FFrame) {
        FFrame.setLayout(null);

        String[] columns = {"User", "First Name", "Last Name", "Role", "Mail"};
        JTable table = new JTable();
        table.setAutoCreateRowSorter(true);

        DefaultTableModel model = new DefaultTableModel();
        model.setColumnIdentifiers(columns);

        table.setModel(model);

        table.setBackground(Color.LIGHT_GRAY);
        table.setForeground(Color.black);
        Font font = new Font("Courier", Font.BOLD, 12);
        table.setFont(font);
        table.setRowHeight(30);
        JScrollPane pane = new JScrollPane(table);
        pane.setBounds(0, 0, 880, 200);

        JLabel unameLabel = new JLabel("Username");
        JLabel nameLabel = new JLabel("Name");
        JLabel snameLabel = new JLabel("Surname");
        JLabel roleLabel = new JLabel("Choose Role");
        JLabel mailLabel = new JLabel("Mail");
        JLabel passwordLabel = new JLabel("Password");

        String[] roles = {"teacher", "student"};
        JComboBox roleCombo = new JComboBox(roles);
        JTextField textId = new JTextField();
        JTextField textFname = new JTextField();
        JTextField textLname = new JTextField();
        JTextField textMail = new JTextField();
        JPasswordField textPassword = new JPasswordField();

        JButton btnAdd = new JButton("Add");
        JButton btnDelete = new JButton("Delete");
        JButton btnUpdate = new JButton("Update");
        JButton btnCancel = new JButton("Cancel");

        roleLabel.setBounds(300, 220, 100, 25);
        unameLabel.setBounds(300, 250, 100, 25);
        nameLabel.setBounds(300, 280, 100, 25);
        snameLabel.setBounds(300, 310, 100, 25);
        mailLabel.setBounds(300, 340, 100, 25);
        passwordLabel.setBounds(300, 370, 100, 25);

        roleCombo.setBounds(380, 220, 100, 25);
        textId.setBounds(380, 250, 100, 25);
        textFname.setBounds(380, 280, 100, 25);
        textLname.setBounds(380, 310, 100, 25);
        textMail.setBounds(380, 340, 100, 25);
        textPassword.setBounds(380, 370, 100, 25);

        btnAdd.setBounds(490, 220, 100, 25);
        btnUpdate.setBounds(490, 265, 100, 25);
        btnDelete.setBounds(490, 310, 100, 25);
        btnCancel.setBounds(490, 355, 100, 25);

        FFrame.add(pane);

        FFrame.add(unameLabel);
        FFrame.add(nameLabel);
        FFrame.add(snameLabel);
        FFrame.add(roleLabel);
        FFrame.add(mailLabel);
        FFrame.add(passwordLabel);

        FFrame.add(roleCombo);
        FFrame.add(textId);
        FFrame.add(textFname);
        FFrame.add(textLname);
        FFrame.add(textMail);
        FFrame.add(textPassword);

        FFrame.add(btnAdd);
        FFrame.add(btnDelete);
        FFrame.add(btnUpdate);
        FFrame.add(btnCancel);

        Object[] row = new Object[5];

        /**
         * Retrieves all of user information in the database and put them in to
         * JTable.
         */
        for (Map.Entry<String, Map> entry : dbUtil.getAllUsersHashMap().entrySet()) {
            row[0] = entry.getValue().get("userName");
            row[1] = entry.getValue().get("fname");
            row[2] = entry.getValue().get("lname");
            row[3] = entry.getValue().get("userType");
            row[4] = entry.getValue().get("mail");
            model.addRow(row);
        }

        btnAdd.addActionListener(new ActionListener() {

            @Override

            /**
             * Retrieves data from jtextFields and add it in to jTable.
             */
            public void actionPerformed(ActionEvent e) {

                row[0] = textId.getText();
                row[1] = textFname.getText();
                row[2] = textLname.getText();
                row[3] = roleCombo.getSelectedItem();
                row[4] = textMail.getText();
//                row[5] = textPassword.getText();

                /**
                 * Retrieves data from jtextFields and add it in to database.
                 */
                try {
                    //henter tilfeldig salt
                    String salt = getSalt();
                    //setter brukerens passordinput
                    String preHashPass = (textPassword.getText());
                    //krypterer passordet med salt
                    String securePassword = getEncryptedPassword(preHashPass, salt);

                    ArrayList<String> columns = new ArrayList();
                    ArrayList<Object> values = new ArrayList();
                    String newUser = "User";
                    columns.add("userType");
                    columns.add("userName");
                    columns.add("lName");
                    columns.add("fName");
                    columns.add("pwd");
                    columns.add("mail");
                    columns.add("salt");
                    values.add(roleCombo.getSelectedItem());
                    values.add(textId.getText());
                    values.add(textFname.getText());
                    values.add(textLname.getText());
                    values.add(securePassword); //sender kryptert passord til dbconnect
                    values.add(textMail.getText());
                    values.add(salt);//sender salt til dbconnect

                    EJBConnector ejbConnector = EJBConnector.getInstance();
                    DBInserterRemote dbInserter = ejbConnector.getDBInserter();
                    dbInserter.insertIntoDB(newUser, columns, values);

                    System.out.println("Ny bruker lagret i databasen.");

                    System.out.println(roleCombo.getSelectedItem() + textId.getText() + textFname.getText() + textLname.getText() + securePassword + textMail.getText());

                } catch (NoSuchAlgorithmException ex) {
                    Logger.getLogger(CreateUser.class.getName()).log(Level.SEVERE, null, ex);
                }

                model.addRow(row);
            }
        });

        /**
         * Bring all values from selected row to jtextField.
         */
        table.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {

                int i = table.getSelectedRow();

                textId.setText(model.getValueAt(i, 0).toString());
                textFname.setText(model.getValueAt(i, 1).toString());
                textLname.setText(model.getValueAt(i, 2).toString());
                roleCombo.setSelectedItem(model.getValueAt(i, 3).toString());
                textMail.setText(model.getValueAt(i, 4).toString());
//                Password.setText(model.getValueAt(i, 5).toString());

            }
        });

        /**
         * Update selected user information
         */
        btnUpdate.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                // get selected row information
                int i = table.getSelectedRow();
                // replace the old information in jTable with the new one from jtextField.
                if (i >= 0) {
                    model.setValueAt(textId.getText(), i, 0);
                    model.setValueAt(textFname.getText(), i, 1);
                    model.setValueAt(textLname.getText(), i, 2);
                    model.setValueAt(roleCombo.getSelectedItem(), i, 3);
                    model.setValueAt(textMail.getText(), i, 4);

                    //put values from jtextField in to Array.
                    ArrayList<String> listOfEdits = new ArrayList(Arrays.asList(
                            textFname.getText(), textLname.getText(), textMail.getText(),
                            roleCombo.getSelectedItem()));
                    // get the method and make connection to DB
                    EJBConnector ejbConnector = EJBConnector.getInstance();
                    DBUpdaterRemote dbUpdater = ejbConnector.getDBUpdater();
                    JOptionPane.showMessageDialog(null, dbUpdater.updateUser(textId.getText(), listOfEdits));

                } else {
                    System.out.println("Update Error");
                }
            }
        });

        /**
         * Delete user and user information from DB.
         */
        btnDelete.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                int i = table.getSelectedRow();
                if (i >= 0) {

                    model.removeRow(i);

                    JOptionPane.showMessageDialog(null, deleteUserDB(textId.getText()));

                } else {
                    System.out.println("Delete Error");
                }
            }
        });

        // close EditUser frame
        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ev) {
                FFrame.dispose();
            }
        });
    }

    private static String getEncryptedPassword(String preHashPass, String salt) {
        String generatedPassword = null;
        try {

            MessageDigest hashValue = MessageDigest.getInstance("SHA-512");
            hashValue.update(salt.getBytes()); //legger salt til message digest (verdien som brukes til å hashe)
            byte[] bytes = hashValue.digest(preHashPass.getBytes()); //hent innholdet i "bytes"
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bytes.length; i++)//konverterer hvert tall i "bytes" fra desimal til hex
            {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            generatedPassword = sb.toString(); //hele "bytes" er nå konvertert til hex, i stringformat
        } catch (NoSuchAlgorithmException e) {
            System.out.println(e);
        }
        return generatedPassword;
    }

    //genererer tilfeldig salt med RNG, returner som string
    private static String getSalt() throws NoSuchAlgorithmException {
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        byte[] salt = new byte[8];
        sr.nextBytes(salt);
        return salt.toString();
    }

    private String deleteUserDB(String userName) {
        EJBConnector ejbConnector = EJBConnector.getInstance();
        DBDeleterRemote dbDeleter = ejbConnector.getDBDeleter();
        return dbDeleter.deleteUser(userName);
    }

}
