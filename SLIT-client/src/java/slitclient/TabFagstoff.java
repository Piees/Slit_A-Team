/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package slitclient;

import db.dbConnectorRemote;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.util.Arrays;
import javax.swing.BoxLayout;
import notification.DateHandler;

/**
 * @author Arild Høyland
 * @author Viktor Setervang 
 * @author Håkon Gilje
 */
public class TabFagstoff {
    HashMap<String, String> userInfo;
    private JFrame frame;
    
    public TabFagstoff(HashMap<String, String> userInfo, JFrame frame)    {
        this.userInfo = userInfo;
        this.frame = frame;
    }
    /**
     * Dette er taben for fagstoff. Foreløpig er den helt tom.
     * @return JPanel tab3Panel returnerer panel med innholdet i tab 3
     */
    public JPanel makeFagstoff() {
        JPanel tab3Panel = new JPanel();
        if(userInfo.get("userType").equals("teacher"))  {
            JButton addResourceButton = new JButton("Last opp ressurs");
            tab3Panel.add(addResourceButton);
            addResourceButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                   addResourceDialog();
                }
            });
        }
        tab3Panel = makeContent(tab3Panel);
        tab3Panel.setLayout(new BoxLayout(tab3Panel, BoxLayout.PAGE_AXIS));
        tab3Panel.repaint();
        return tab3Panel;
    }    
    
    /**
     * Opens a dialog window where the teacher can add a new resource that will
     * be displayed in TabFagstoff
     */
    private void addResourceDialog() {
        GUIFileUploader fileUploader = new GUIFileUploader();
        
        JDialog addResourceDialog = new JDialog(frame, "Last opp ressurs");//, true);
        addResourceDialog.setLayout(new GridLayout(0, 1));
        JPanel contentPane = (JPanel) addResourceDialog.getContentPane();
        
        JLabel titleLabel = new JLabel("Gi ressursen en tittel");
        JTextField title = new JTextField();
        JLabel resourceTextLabel = new JLabel("Din melding her:");
        JTextField resourceText = new JTextField();
        JLabel urlLabel = new JLabel("URL her:");
        JTextField url = new JTextField();
        JLabel resourceFileLabel = new JLabel("Fil som skal lastes opp:");
        JLabel resourceFile = new JLabel("Ingen fil valgt");
        
        JButton chooseFileButton = new JButton("Velg fil");
        JButton uploadResourceButton = new JButton("Last opp ressurs");
        
        contentPane.add(titleLabel);
        contentPane.add(title);
        contentPane.add(resourceTextLabel);
        contentPane.add(resourceText);
        contentPane.add(urlLabel);
        contentPane.add(url);
        contentPane.add(resourceFileLabel);
        contentPane.add(resourceFile);
        contentPane.add(chooseFileButton);
        contentPane.add(uploadResourceButton);
        
        addResourceDialog.pack();
        addResourceDialog.setVisible(true);
        
        chooseFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Før start fileexplorer");
                resourceFile.setText(fileUploader.startFileExplorer(frame));
            }
        });
        
        uploadResourceButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (title.getText().length() > 0) {
                    if (resourceText.getText().length() == 0 && url.getText().length() == 0 && resourceFile.getText().equals("Ingen fil valgt")) {
                        JOptionPane.showMessageDialog(addResourceDialog, "Et av ressursfeltene må fylles ut", "Et av ressursfeltene må fylles ut", 1); 
                    }
                    else {
                        String confirmationString = fileUploader.uploadResource(userInfo.get("userName"), title.getText(), resourceText.getText(), url.getText());
                        JOptionPane.showMessageDialog(frame, confirmationString, confirmationString, 1);
                        if(confirmationString.equals("Opplastning vellykket!")) {
                            addResourceDialog.dispose();
                        }
                    }
                }
                else { 
                    JOptionPane.showMessageDialog(addResourceDialog, "Sett ressurstittel!", "Sett ressurstittel!", 1);  
                }

            }
        });
    }
        
    private JPanel makeContent(JPanel tab3Panel) {
        EJBConnector ejbConnector = EJBConnector.getInstance();
        dbConnectorRemote dbConnector = ejbConnector.getEjbRemote();
        ArrayList<HashMap> resources = dbConnector.getResources();

        for (int i = resources.size()-1; i >= 0; i--) {
            String title = resources.get(i).get("title").toString(); 
            int idResources = (Integer) resources.get(i).get("idResource");
            String resourceText = resources.get(i).get("resourceText").toString();
            String url = resources.get(i).get("url").toString();
            String filename = resources.get(i).get("fileName").toString();
            String userName = resources.get(i).get("userName").toString();
            String timestamp = resources.get(i).get("resourceDate").toString();
            DateHandler dh = new DateHandler();
            timestamp = dh.removeFractionalSeconds(timestamp);
            byte[] fileData = dbConnector.getResourceFile(idResources);

            ArrayList<String> checkStrings = new ArrayList<>(Arrays.asList(title, resourceText, url));
            String resourcePresentation = "<html>";
            for (int index = 0; index < checkStrings.size(); index++) {
                if (!checkStrings.get(index).equals("")) {
                    if (index+1 != checkStrings.size()) {
                        resourcePresentation += checkStrings.get(index) + "<br>";
                    }
                    else {
                    resourcePresentation += checkStrings.get(index) + "</html>";
                    }
                }
            }
            JLabel resourceContentLabel = new JLabel(resourcePresentation);
            tab3Panel.add(new JLabel(" "));
            tab3Panel.add(resourceContentLabel);


            if (fileData != null) {
                JButton downloadFileButton = new JButton(filename);
                downloadFileButton.addActionListener(new ActionListener() {      
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        FileDownloader downloader = new FileDownloader();
                        //downloader.downloadResourceFile(fileData, filename);
                        JOptionPane.showMessageDialog(tab3Panel, 
                                downloader.downloadResourceFile(fileData, filename));                       }
                });
                tab3Panel.add(downloadFileButton);
            }
            JLabel resourceSignatureLabel = new JLabel(userName + " " + timestamp);
            tab3Panel.add(resourceSignatureLabel);
        }
        return tab3Panel;
    }    
}

