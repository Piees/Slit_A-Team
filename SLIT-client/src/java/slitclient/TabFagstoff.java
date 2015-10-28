/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package slitclient;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 *
 * @author Arild
 */
public class TabFagstoff {
    HashMap<String, String> userInfo;
    private JFrame frame;
    
    public TabFagstoff(HashMap<String, String> userInfo, JFrame frame)    {
        this.userInfo = userInfo;
        this.frame = frame;
    }
    /**
     * Dette er taben for fagstoff. ForelÃ¸pig er den helt tom.
     * @return JPanel tab3Panel returnerer panel med innholdet i tab 3
     */
    public JPanel makeFagstoff() {
        JPanel tab3Panel = new JPanel();
        JTextField textField = new JTextField("Her kommer alt fagstoff.");
        tab3Panel.add(textField);
        
        JButton addResourceButton = new JButton("Last opp ressurs");
        tab3Panel.add(addResourceButton);
        addResourceButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
               addResourceDialog();
            }
        });
        return tab3Panel;
    }    
    
    private void addResourceDialog() {
        GUIFileUploader fileUploader = new GUIFileUploader();
        
        JDialog addResourceDialog = new JDialog(frame, "Last opp ressurs", true);
        addResourceDialog.setLayout(new GridLayout(0, 1));
        JPanel contentpane = (JPanel) addResourceDialog.getContentPane();
        
        JTextField title = new JTextField();
        JTextField resourceText = new JTextField();
        JTextField url = new JTextField();
        JLabel resourceFile = new JLabel("Ingen fil valgt");
        
        JButton chooseFileButton = new JButton("Velg fil");
        JButton uploadResourceButton = new JButton("Last opp ressurs");
        
        contentpane.add(title);
        contentpane.add(resourceText);
        contentpane.add(url);
        contentpane.add(resourceFile);
        contentpane.add(chooseFileButton);
        contentpane.add(uploadResourceButton);
        
        addResourceDialog.pack();
        addResourceDialog.setVisible(true);
        
        chooseFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
              resourceFile.setText(fileUploader.startFileExplorer(frame));
            }
        });
        
        uploadResourceButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (title.getText().length() > 0) {
                    if (resourceText.getText().length() == 0 && url.getText().length() == 0 && resourceFile.getText().equals("Ingen fil valgt")) {
                        JOptionPane.showMessageDialog(null, "Et av ressursfeltene må utfylles"); 
                    }
                    else {
                        JOptionPane.showMessageDialog(null, fileUploader.uploadResource(userInfo.get("userName"), title.getText(), resourceText.getText(), url.getText()));            
                    }
                } 
                else { 
                    JOptionPane.showMessageDialog(null, "Sett ressurstittel!");  
                }

            }
        });
    }
}
