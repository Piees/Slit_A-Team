/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package slitclient;

import db.DBQuerierRemote;
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
import javax.swing.BoxLayout;
import javax.swing.JScrollPane;
import notification.DateHandler;

/**
 * @author Arild Høyland
 * @author Viktor Setervang
 * @author Håkon Gilje
 */
public class TabFagstoff {

    HashMap<String, String> userInfo;
    private JFrame frame;

    public TabFagstoff(HashMap<String, String> userInfo, JFrame frame) {
        this.userInfo = userInfo;
        this.frame = frame;
    }

    /**
     * Dette er taben for fagstoff. Foreløpig er den helt tom.
     *
     * @return JPanel tabFagstoffPanel returnerer panel med innholdet i tab 3
     */
    public JScrollPane makeFagstoff() {
        JPanel tabFagstoffPanel = new JPanel();
        if (userInfo.get("userType").equals("teacher")) {
            JButton addResourceButton = new JButton("Last opp ressurs");
            tabFagstoffPanel.add(addResourceButton);
            addResourceButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    addResourceDialog();
                }
            });
        }
        tabFagstoffPanel = makeContent(tabFagstoffPanel);
        JScrollPane tabFagstoffScrollPanel = new JScrollPane(tabFagstoffPanel);
        tabFagstoffPanel.setLayout(new BoxLayout(tabFagstoffPanel, BoxLayout.PAGE_AXIS));
        tabFagstoffPanel.repaint();
        return tabFagstoffScrollPanel;
    }

    /**
     * Opens a dialog window where the teacher can add a new resource that will
     * be displayed in TabFagstoff
     */
    private void addResourceDialog() {
        FileUploader fileUploader = new FileUploader();

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
                    } else {
                        String confirmationString = fileUploader.uploadResource(userInfo.get("userName"), title.getText(), resourceText.getText(), url.getText(), false);
                        JOptionPane.showMessageDialog(frame, confirmationString, confirmationString, 1);
                        if (confirmationString.equals("Opplastning vellykket!")) {
                            addResourceDialog.dispose();
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(addResourceDialog, "Sett ressurstittel!", "Sett ressurstittel!", 1);
                }

            }
        });
    }
    
    /**
     * Gets the resource content from the database and puts it into a panel 
     * ready for display to the user.
     * 
     * @param tabPanel the panel that the content gets added to.
     * @return panel containing all the resource elements
     */
    private JPanel makeContent(JPanel tabPanel) {
        EJBConnector ejbConnector = EJBConnector.getInstance();
        DBQuerierRemote dbQuerier = ejbConnector.getDBQuerier();
        ArrayList<HashMap> resources = dbQuerier.getResources();
        if (!resources.isEmpty()) {
            for (int i = resources.size() - 1; i >= 0; i--) {
                if (!Boolean.parseBoolean(resources.get(i).get("isMessage").toString())) {
                    ArrayList<String> resourceContent = new ArrayList<>();
                    String title = "<b>" + 
                            resources.get(i).get("title").toString() + "</b>";
                    resourceContent.add(title);
                                     
                    if (resources.get(i).get("resourceText") != null) {
                        String resourceText = resources.get(i).get("resourceText").toString();
                        resourceContent.add(resourceText);
                    }
 
                    if (resources.get(i).get("url") != null) {
                        String url = resources.get(i).get("url").toString();
                        resourceContent.add(url);
                    }
                    
                    String userName = resources.get(i).get("userName").toString();
                    String timestamp = resources.get(i).get("resourceDate").toString();
                    DateHandler dh = new DateHandler();
                    timestamp = dh.removeFractionalSeconds(timestamp);
                    
                    String resourcePresentation = "<html>";
                    for (int index = 0; index < resourceContent.size(); index++) {
                        if (!resourceContent.get(index).equals("")) {
                            if (index + 1 != resourceContent.size()) {
                                resourcePresentation += resourceContent.get(index) + "<br>";
                            } else {
                                resourcePresentation += resourceContent.get(index) + "</html>";
                            }
                        }
                    }
                    
                    JLabel resourceContentLabel = new JLabel(resourcePresentation);
                    tabPanel.add(new JLabel(" "));
                    tabPanel.add(resourceContentLabel);

                    if (resources.get(i).get("fileName") != null) {
                        String filename = resources.get(i).get("fileName").toString();
                        int idResources = (Integer) resources.get(i).get("idResource");
                        byte[] fileData = dbQuerier.getResourceFile(idResources);
                        JButton downloadFileButton = new JButton(filename);
                        downloadFileButton.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                FileDownloader downloader = new FileDownloader();
                                JOptionPane.showMessageDialog(tabPanel,
                                        downloader.downloadResourceFile(fileData, filename));
                            }
                        });
                        tabPanel.add(downloadFileButton);
                    }
                    JLabel resourceSignatureLabel = new JLabel("<html><i>" + 
                            userName + " " + timestamp + "</i></html>");
                    tabPanel.add(resourceSignatureLabel);
                }
            }

        }
        return tabPanel;
    }
}
