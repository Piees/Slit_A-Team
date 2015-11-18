/**
 * This is the login screen that greets the user of the SLIT program.
 *
 * The GUI design and logic is just blatantly ripped of a netbeans example, the
 * only exception is the loginButtons ActionListener.
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
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import prototypes.CreateUser;
import prototypes.EditUser;

/**
 *
 * @author Viktor Setervang
 */
public class Login {

    JTextField userText;
    JPasswordField passwordText;
    ActionListener loginAction;
    JFrame frame;
    private static final String LOGO_PATH = "src/img/slit_Blogo.png";

    public ImageIcon loadLogo() {
        ImageIcon icon = null;
      try {
         BufferedImage img = ImageIO.read(new File(LOGO_PATH));
         icon = new ImageIcon(img);
      } catch (IOException e) {
         e.printStackTrace();
      }
      return icon;
    }
    
    public Login() {
        frame = new JFrame("Login");
        frame.setSize(900, 650);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        placeComponents(frame);
        frame.setVisible(true);
    }

    class LoginKeyAction implements KeyListener {

        public LoginKeyAction() {

        }

        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                String userName = userText.getText();
                String pwd = passwordText.getText();
                login(userName, pwd);
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
        }

        @Override
        public void keyTyped(KeyEvent e) {
        }
    }

    public void login(String userName, String pwd) {
        EJBConnector ejbConnector = EJBConnector.getInstance();
        dbConnectorRemote dbConnector = ejbConnector.getEjbRemote();
        dbConnector.updateUsersHashMap();
        HashMap<String, String> loginResult = dbConnector.login(userName, pwd);

        if (loginResult.size() > 1) {
            if (loginResult.get("userType").equals("student")) {
                //create StudentGUI object with the list
                StudentGUI studentGUI = new StudentGUI(loginResult);
                frame.setVisible(false);
            } else if (loginResult.get("userType").equals("teacher")
                    || loginResult.get("userType").equals("helpTeacher")) {
                //create TeacherGUI object with the list
                TeacherGUI teacherGUI = new TeacherGUI(loginResult);
                frame.setVisible(false);
            } else {
                JOptionPane.showMessageDialog(null, "userType invalid, "
                        + "contact the database manager");
            }
        } else {
            JOptionPane.showMessageDialog(null, loginResult.get("error1"));
        }
    }

    private void placeComponents(JFrame frame) {
        frame.setLayout(null);
        frame.getContentPane().setBackground(Color.WHITE);
        
        JLabel logoLabel = new JLabel(loadLogo());
        logoLabel.setBounds(75, 100, 400, 400);
        frame.add(logoLabel);
        
        JLabel userLabel = new JLabel("Username");
        userLabel.setBounds(500, 250, 80, 25);
        frame.add(userLabel);

        userText = new JTextField(20);
        userText.setBounds(590, 250, 160, 25);
        frame.add(userText);

        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setBounds(500, 280, 80, 25);
        frame.add(passwordLabel);

        passwordText = new JPasswordField(20);
        passwordText.addKeyListener(new LoginKeyAction());
        passwordText.setBounds(590, 280, 160, 25);
        frame.add(passwordText);

        JButton createButton = new JButton("create user");
        createButton.setBounds(585, 310, 80, 25);
        frame.add(createButton);

        JButton loginButton = new JButton("login");
        loginButton.setBounds(500, 310, 80, 25);
        frame.add(loginButton);

        JButton forgotPwdButton = new JButton("forgot my password!");
        forgotPwdButton.setBounds(670, 310, 80, 25);
        frame.add(forgotPwdButton);

//        loginButton.addActionListener(new LoginAction()); //listener her
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                login(userText.getText(), passwordText.getText());
            }
        });

        forgotPwdButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new EditUser();
                frame.dispose();
//            JButton source = (JButton) e.getSource();
//            JOptionPane.showMessageDialog(source, source.getText()
//                        + " button has been pressed");                
            }
        });

        createButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ev) {
                new CreateUser();
                frame.dispose();
            }
        });
    }

}
