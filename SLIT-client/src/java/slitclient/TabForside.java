/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package slitclient;

import db.DBUtilRemote;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.lang.reflect.Array;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

/**
 *
 * @author Arild
 */
public class TabForside {
    
    private DBUtilRemote dbUtil;
    private JTextField searchField;
    private JPanel contactPanel;
    private GridBagLayout tab1Layout;
    private JPanel tab1Panel;
    private JScrollPane scrollContactPanel;
    private boolean initialRun = true;
    
    public TabForside() {
        EJBConnector ejbConnector = EJBConnector.getInstance();
        
        this.dbUtil = ejbConnector.getDBUtil();
        dbUtil.updateUsersHashMap();
    }
    
    /**
     * Lager forside-taben. Returnerer den til MakeGUI.makeTabs()
     * @return JPanel tab1Panel panel med innholdet i tab 1
     */
    public JPanel makeForsideTab()    {
        tab1Panel = new JPanel();
        tab1Layout = new GridBagLayout();
        tab1Panel.setLayout(tab1Layout);
        
        contactPanel = makeContactPanel();
        scrollContactPanel = new JScrollPane(contactPanel){
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(400,200);
            }
        };
//        GridBagConstraints gbcCP = new GridBagConstraints();
        scrollContactPanel.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollContactPanel.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
//        gbcCP.gridx = 2;
//        gbcCP.gridy = 1;
//        gbcCP.gridheight = 2;
//        gbcCP.anchor = GridBagConstraints.EAST;
//        gbcCP.insets = new Insets(-350, 0, 10, -200);
//        tab1Layout.setConstraints(scrollContactPanel, gbcCP);
//        tab1Panel.add(scrollContactPanel);
        
        tab1Panel.setLayout(new BoxLayout(tab1Panel, BoxLayout.X_AXIS));
        tab1Panel.add(makeMessagesPanel());
        tab1Panel.add(scrollContactPanel);
        
    return tab1Panel;
    }    
    
    
    public class ForsideTab extends JPanel {
        
        @Override
        public Dimension getMinimumSize() {
            return new Dimension(720, 450);
        }
        
        @Override
        public Dimension getPreferredSize() {
            return new Dimension(720, 450);
        }
        
        public ForsideTab() {
        
        }
    }
       
    /**
     * Lager messagesPanel som er inni forside-taben. Returnerer til makeForsideTab()
     * @return JPanel messagesPanel panelet med meldinger
     */
    private JPanel makeMessagesPanel()   {
        JPanel messagesPanel = new JPanel();
        messagesPanel.setLayout(new BoxLayout(messagesPanel, BoxLayout.Y_AXIS));
    
        JLabel messagesHeader = new JLabel("Meldinger:");
        messagesPanel.add(messagesHeader);

        JLabel demoMessage1 = new JLabel("<html><u>Husk å installere BlueJ</u><br>"
                + "Når du installerer er det viktig at...</html>");
        messagesPanel.add(demoMessage1);

        JLabel demoMessage2 = new JLabel("<html><u>Nyttige tips i Java</u><br>"
                + "Det kan være praktisk å...</html>");
        messagesPanel.add(demoMessage2);
    return messagesPanel;
    }
    
    public class borderPanel extends JPanel {
        
        public borderPanel() {
            setBorder(new TitledBorder (new EtchedBorder(), 
                "Kontakter"));
        }
    }
    
    /**
     * Lager contactPanel som er inni forside-taben. Returnerer til makeForsideTab()
     * @return JPanel contactPanel panelet som viser kontaktene (lærere)
     */
    private JPanel makeContactPanel()    {
        if(contactPanel == null) {
            contactPanel = new borderPanel();
        }
        GridBagLayout contactLayout = new GridBagLayout();
        contactPanel.setLayout(contactLayout);
        int contactHelpery = 1;
        boolean contactHelperToRight = false;
        HashMap<String, Map> allUsersLimitedHashMap = new HashMap<>();
        
        if(initialRun) {
            GridBagConstraints gbcSearchField = new GridBagConstraints();
            searchField = new JTextField(20);
            searchField.addKeyListener(new ContactSearchKeyListener());
            gbcSearchField.gridx = 0;
            gbcSearchField.gridy = 0;
            gbcSearchField.gridwidth = 2;
            contactLayout.setConstraints(searchField, gbcSearchField);
            contactPanel.add(searchField); 
        }
        try {
            if(searchField.getText().length() <= 0) {
            allUsersLimitedHashMap = dbUtil.getAllUsersHashMap();
            } 
            else {
                for(Map.Entry<String, Map> entry : dbUtil.getAllUsersHashMap().entrySet()) {
                    if(Pattern.matches(".*" + searchField.getText().toUpperCase() + ".*", 
                            entry.getValue().get("fname").toString().toUpperCase() + " " 
                            + entry.getValue().get("lname").toString().toUpperCase())) {
                    allUsersLimitedHashMap.put(entry.getKey(), entry.getValue());
                    }
                }
            } 
        }
        catch(Exception e) {
            System.out.println(e);
        }
        for(Map.Entry<String, Map> entry : allUsersLimitedHashMap.entrySet()) {
            String mail = (String) entry.getValue().get("mail");
            String name = (String) entry.getValue().get("fname") + " " +
                    entry.getValue().get("lname");
            int contactHelperx = (contactHelperToRight) ? 1 : 0;
            GridBagConstraints gbcContact = new GridBagConstraints();
            JButton contactLabel = new JButton(String.format("<html>%s<br>", name)
                            + String.format("<a href=''>%s</a></html>", mail)
            );
            contactLabel.setOpaque(false);
            contactLabel.setBackground(new Color(0, 0, 0, 0));
            contactLabel.setToolTipText("Mailto: " + mail);
            contactLabel.addActionListener(new sendMailActionListener(mail));
            if(contactHelperToRight) {
                contactLabel.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, Color.BLACK));
            } else {
                contactLabel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 0, Color.BLACK));
            }
            gbcContact.gridx = contactHelperx;
            gbcContact.gridy = contactHelpery;
            gbcContact.ipady = 5;
            gbcContact.ipadx = 15;
            gbcContact.anchor = GridBagConstraints.WEST; 
            contactLayout.setConstraints(contactLabel, gbcContact);
            contactPanel.add(contactLabel);
            if(contactHelperToRight == true) {
                contactHelpery +=1;
            }
            contactHelperToRight = !contactHelperToRight;
        }
        initialRun = false;
    return contactPanel;
    }
    
    class ContactSearchKeyListener implements KeyListener {

        @Override
        public void keyTyped(KeyEvent e) {
        }

        @Override
        public void keyPressed(KeyEvent e) {
        }

        @Override
        public void keyReleased(KeyEvent e) {
            Component[] contactPanelComponents = contactPanel.getComponents();
            for(int i = 0; i < contactPanelComponents.length; i++) {
                if(Array.get(contactPanelComponents, i) instanceof JButton) {
                contactPanel.remove((Component) Array.get(contactPanelComponents, i));
                }
            }
            makeContactPanel();
            contactPanel.revalidate();
        }
    }
    
    /**
     * Sender mail til en av kontaktene
     * @param mail e-post adressen til kontakten
     */
    private void sendMail(String mail) {
        Desktop desktop = Desktop.getDesktop();
        try {
            String message = "mailto:" + mail;
            URI uri = URI.create(message);
            desktop.mail(uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * ActionListener class for kontakt-knappene
     */
    class sendMailActionListener implements ActionListener {

        String mail;

        public sendMailActionListener(String mail) {
            this.mail = mail;
        }

        public void actionPerformed(ActionEvent e) {
            sendMail(mail);
            Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
            clpbrd.setContents(new StringSelection(mail), null);
        }
    }
}

