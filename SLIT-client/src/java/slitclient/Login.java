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
 * This class handles the client-side login logic necessary for login into the
 * SLIT system.
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
        frame.setSize(700, 400);
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

    /**
     * The method called upon when the user tries to log into the system.
     * If username and password is a match the main user GUI will open. 
     * 
     * @param userName provided by the user
     * @param pwd password provided by the user.
     */
    public void login(String userName, String pwd) {
        EJBConnector ejbConnector = EJBConnector.getInstance();
        dbConnectorRemote dbConnector = ejbConnector.getEjbRemote();
        dbConnector.updateUsersHashMap();
        HashMap<String, String> loginResult = dbConnector.login(userName, pwd);

        if (loginResult.size() > 1) {
                UserGUI userGUI = new UserGUI(loginResult);
                frame.setVisible(false);
        } else {
            JOptionPane.showMessageDialog(null, loginResult.get("error1"));
        }
    }

    /**
     * Places the component of the Login GUI
     * @param frame were the components are held.
     */
    private void placeComponents(JFrame frame) {
        frame.setLayout(null);
        frame.getContentPane().setBackground(Color.WHITE);
        
        JLabel logoLabel = new JLabel(loadLogo());
        logoLabel.setBounds(20, 40, 300, 300);
        frame.add(logoLabel);
        
        JLabel userLabel = new JLabel("Username");
        userLabel.setBounds(350, 150, 80, 25);
        frame.add(userLabel);

        userText = new JTextField(20);
        userText.setBounds(440, 150, 160, 25);
        frame.add(userText);

        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setBounds(350, 180, 80, 25);
        frame.add(passwordLabel);

        passwordText = new JPasswordField(20);
        passwordText.addKeyListener(new LoginKeyAction());
        passwordText.setBounds(440, 180, 160, 25);
        frame.add(passwordText);

        JButton createButton = new JButton("create user");
        createButton.setBounds(435, 210, 80, 25);
        frame.add(createButton);

        JButton loginButton = new JButton("login");
        loginButton.setBounds(350, 210, 80, 25);
        frame.add(loginButton);

        JButton forgotPwdButton = new JButton("forgot my password!");
        forgotPwdButton.setBounds(520, 210, 80, 25);
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
