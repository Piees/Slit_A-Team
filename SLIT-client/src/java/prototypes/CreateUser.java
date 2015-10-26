/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prototypes;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

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
	JLabel roleLabel = new JLabel("Role");
	roleLabel.setBounds(10, 10, 80, 25);
	cframe.add(roleLabel);
        
	JTextField roleText = new JTextField(20);
	roleText.setBounds(100, 10, 160, 25);
	cframe.add(roleText);

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
            /**
            static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
            static final String DB_URL = "jdbc:mysql://peterhagane.net:3306/a_team";
            private static final String USERNAME = "steffen";
            private static final String PASSWORD = "a_team";
            static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
            static final String DB_URL = "jdbc:mysql://peterhagane.net:3306/a_team";
            private static final String USERNAME = "yngve";
            private static final String PASSWORD = "a_team";
                Class.forName("com.mysql.jdbc.Driver")
                conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
                stmt = conn.createStatement();
                
                String sql = "INSERT INTO User " + 
                "VALUES (roleText.getText()+ unameText.getText()+ snameText.getText()+ nameText.getText()+ passwordText.getText()+ mailText.getText())";
                **/
                System.out.println(roleText.getText()+ unameText.getText()+ snameText.getText()+ nameText.getText()+ passwordText.getText()+ mailText.getText());
            
            }/**catch(SQLException se){
                se.printStackTrace();
            }catch(Exception e){
                e.printStackTrace();
            }finally{
                try{
                    if(stmt!=null)
                        conn.close();
                }catch(SQLException se){
                }// do nothing
                try{
                    if(conn!=null)
                        conn.close();
                }catch(SQLException se){
                se.printStackTrace();
                }     
            }
            **/
        });
         
        
        
    }

}