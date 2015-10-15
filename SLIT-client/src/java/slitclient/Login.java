/**
 * This is the login screen that greets the user of the SLIT program.
 * 
 * The GUI design and logic is just blatantly ripped of a netbeans 
 * example, the only exception is the loginButtons ActionListener.
 *
 */
package slitclient;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import db.dbConnectorRemote;
import java.util.ArrayList;

/**
 *
 * @author Viktor Setervang
 */

public class Login {
    
    public Login() {        
       	JFrame frame = new JFrame("Login");
	frame.setSize(300, 150);
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	placeComponents(frame);
	frame.setVisible(true); 
    }

    private void placeComponents(JFrame frame) {
	frame.setLayout(null);
	JLabel userLabel = new JLabel("User");
	userLabel.setBounds(10, 10, 80, 25);
	frame.add(userLabel);
	JTextField userText = new JTextField(20);
	userText.setBounds(100, 10, 160, 25);
	frame.add(userText);

	JLabel passwordLabel = new JLabel("Password");
	passwordLabel.setBounds(10, 40, 80, 25);
	frame.add(passwordLabel);

	JPasswordField passwordText = new JPasswordField(20);
	passwordText.setBounds(100, 40, 160, 25);
	frame.add(passwordText);

	JButton loginButton = new JButton("login");
	loginButton.setBounds(10, 80, 80, 25);
	frame.add(loginButton);

	JButton forgotPwdButton = new JButton("forgot my password!");
	forgotPwdButton.setBounds(180, 80, 80, 25);
	frame.add(forgotPwdButton);
                
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {    
                String userName = userText.getText();
                String pwd = passwordText.getText();
                
                EJBConnector ejbConnector = new EJBConnector();
                dbConnectorRemote dbConnector = ejbConnector.getEjbRemote();
                ArrayList<String> loginResult = dbConnector.login(userName, pwd);
              
                if (loginResult.size() == 2) {
                    // index 0 == userName, index 1 == userType
                    if (loginResult.get(1).equals("student")) {
                        MakeGUI studentGUI = new MakeGUI();
                        //StudentGUI studentGUI = new StudentGUI(loginResult.get(0))
                    }
                    else if (loginResult.get(1).equals("teacher") || 
                            loginResult.get(1).equals("helpTeacher")) {
                        MakeGUI teacherGUI = new MakeGUI();
                        //TeacherGUI teacherGUI = new teacherGUI(loginResult.get(0))
                    }
                    else {
                        JOptionPane.showMessageDialog(null, "userType invalid, "
                                + "contact the database manager");                    
                    }
                }
                else {
                    JOptionPane.showMessageDialog(null, loginResult.get(0));    
                }
            }
        });
                
        forgotPwdButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            JButton source = (JButton) e.getSource();
            JOptionPane.showMessageDialog(source, source.getText()
                        + " button has been pressed");                
            }
        });
    }
    //public static void main(String[] args) {          
     //   new Login();
   // }
}