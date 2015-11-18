/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prototypes;

import db.dbConnectorRemote;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import slitclient.EJBConnector;

import slitclient.Login;

/**
 *
 * @author zteff1
 */
public class CreateUser {
    
    public CreateUser() {        
       	JFrame cframe = new JFrame("CreateUser");
	cframe.setSize(300, 270);
	cframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	placeComponents(cframe);
	cframe.setVisible(true); 
    }
    
    
    private void placeComponents(JFrame cframe) {
	cframe.setLayout(null);
	JLabel roleLabel = new JLabel("Choose Role");
	roleLabel.setBounds(10, 10, 80, 25);
	cframe.add(roleLabel);
        
	String[] roles = { "teacher", "student" };
        JComboBox roleCombo = new JComboBox(roles);
	roleCombo.setBounds(100, 10, 160, 25);
	cframe.add(roleCombo);

	JLabel nameLabel = new JLabel("Name");
	nameLabel.setBounds(10, 40, 80, 25);
	cframe.add(nameLabel);

	JTextField nameText = new JTextField(20);
	nameText.setBounds(100, 40, 160, 25);
	cframe.add(nameText);
        
        JLabel snameLabel = new JLabel("Surname");
	snameLabel.setBounds(10, 70, 80, 25);
	cframe.add(snameLabel);

	JTextField snameText = new JTextField(20);
	snameText.setBounds(100, 70, 160, 25);
	cframe.add(snameText);
        
        JLabel mailLabel = new JLabel("Mail");
	mailLabel.setBounds(10, 100, 80, 25);
	cframe.add(mailLabel);

	JTextField mailText = new JTextField(20);
	mailText.setBounds(100, 100, 160, 25);
	cframe.add(mailText);
        
        JLabel unameLabel = new JLabel("Username");
	unameLabel.setBounds(10, 130, 80, 25);
	cframe.add(unameLabel);

	JTextField unameText = new JTextField(20);
	unameText.setBounds(100, 130, 160, 25);
	cframe.add(unameText);
        
        JLabel passwordLabel = new JLabel("Password");
	passwordLabel.setBounds(10, 160, 80, 25);
	cframe.add(passwordLabel);

	JPasswordField passwordText = new JPasswordField(20);
	passwordText.setBounds(100, 160, 160, 25);
	cframe.add(passwordText);
        
        JButton nextButton = new JButton("Next");
	nextButton.setBounds(10, 200, 80, 25);
	cframe.add(nextButton);
        
        JButton cancelButton = new JButton("Cancel");
	cancelButton.setBounds(180, 200, 80, 25);
	cframe.add(cancelButton);

        
        cancelButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent ev) {
                new Login(); 
                cframe.dispose();
            }
        });
        
        
         nextButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent ev) {
                try {
                    //henter tilfeldig salt
                    String salt = getSalt();
                    //setter brukerens passordinput
                    String preHashPass = (passwordText.getText());
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
                    values.add(unameText.getText());
                    values.add(snameText.getText());
                    values.add(nameText.getText());
                    values.add(securePassword); //sender kryptert passord til dbconnect
                    values.add(mailText.getText());
                    values.add(salt);//sender salt til dbconnect
                    
                    EJBConnector ejbConnector = EJBConnector.getInstance();
                    dbConnectorRemote dbConnector = ejbConnector.getEjbRemote();
                    dbConnector.insertIntoDB(newUser, columns, values);
                    
                    System.out.println("Ny bruker lagret i databasen.");
                    
                    System.out.println(roleCombo.getSelectedItem()+ unameText.getText()+ snameText.getText()+ nameText.getText()+ securePassword + mailText.getText());
                    
                    new Login();
                    cframe.dispose();
                } catch (NoSuchAlgorithmException ex) {
                    Logger.getLogger(CreateUser.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        });
         
    }

    //
       private static String getEncryptedPassword(String preHashPass, String salt){
       String generatedPassword = null;
        try {
            
            MessageDigest hashValue = MessageDigest.getInstance("SHA-512");
            hashValue.update(salt.getBytes()); //legger salt til message digest (verdien som brukes til å hashe)
            byte[] bytes = hashValue.digest(preHashPass.getBytes()); //hent innholdet i "bytes"
            StringBuilder sb = new StringBuilder();
            for(int i=0; i< bytes.length ;i++)//konverterer hvert tall i "bytes" fra desimal til hex
            {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            generatedPassword = sb.toString(); //hele "bytes" er nå konvertert til hex, i stringformat
        }
        catch (NoSuchAlgorithmException e)
        {
            System.out.println(e);
        }
    return generatedPassword;
}
    
    //genererer tilfeldig salt med RNG, returner som string
    private static String getSalt() throws NoSuchAlgorithmException{
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        byte[] salt = new byte[8];
        sr.nextBytes(salt);
        return salt.toString();
    }
}