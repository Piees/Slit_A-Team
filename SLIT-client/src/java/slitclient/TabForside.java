/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package slitclient;

import db.dbConnectorRemote;
import db.dbConnector;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
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
    
    dbConnectorRemote dbConnector;
    
    public TabForside() {
        EJBConnector ejbConnector = EJBConnector.getInstance();
        
        this.dbConnector = ejbConnector.getEjbRemote();
        dbConnector.updateUsersHashMap();
    }
    
    /**
     * Lager forside-taben. Returnerer den til MakeGUI.makeTabs()
     * @return JPanel tab1Panel panel med innholdet i tab 1
     */
    public JPanel makeForsideTab()    {
        JPanel tab1Panel = new JPanel();
        GridBagLayout tab1Layout = new GridBagLayout();
        tab1Panel.setLayout(tab1Layout);
        
        JPanel nextLecturePanel = makeLecturePanel();
        JScrollPane scrollLecturePanel = new JScrollPane(nextLecturePanel);
        GridBagConstraints gbcNLP = new GridBagConstraints();
        scrollLecturePanel.setVerticalScrollBarPolicy ( ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS );
        scrollLecturePanel.setHorizontalScrollBarPolicy ( ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER );
        gbcNLP.gridx = 0;
        gbcNLP.gridy = 0; //1
        gbcNLP.gridheight = 3;
        gbcNLP.insets = new Insets(-80, -125, -50, 15);
        gbcNLP.fill = GridBagConstraints.VERTICAL;
        tab1Layout.setConstraints(scrollLecturePanel, gbcNLP); //nextlecpan
        tab1Panel.add(scrollLecturePanel); //nextlecpan

        JPanel messagesPanel = makeMessagesPanel();
        GridBagConstraints gbcMP = new GridBagConstraints();
        gbcMP.gridx = 1;
        gbcMP.gridy = 1;
        gbcMP.fill = GridBagConstraints.VERTICAL;
        gbcMP.insets = new Insets(0, 2, 0, 0);
        tab1Layout.setConstraints(messagesPanel, gbcMP);
        tab1Panel.add(messagesPanel);

        JPanel contactPanel = makeContactPanel();
        JScrollPane scrollContactPanel = new JScrollPane(contactPanel);
        GridBagConstraints gbcCP = new GridBagConstraints();
        scrollContactPanel.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollContactPanel.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        gbcCP.gridx = 2;
        gbcCP.gridy = 1;
        gbcCP.gridheight = 2;
        gbcCP.insets = new Insets(-350, 0, 10, -200);
        tab1Layout.setConstraints(scrollContactPanel, gbcCP);
        tab1Panel.add(scrollContactPanel);

        JPanel activityPanel = makeActivityPanel();
        GridBagConstraints gbcAP = new GridBagConstraints();
        gbcAP.gridx = 2;
        gbcAP.gridy = 3;
        gbcAP.fill = GridBagConstraints.VERTICAL;
        tab1Layout.setConstraints(activityPanel, gbcAP);
        tab1Panel.add(activityPanel);
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
     * Lager nextLecturePanelet som er inni forside-taben. Returnerer til makeForsideTab()
     * @return JPanel nextLecturePanel panelet med neste forelesninger
     */
    public class LecturePanel extends JPanel   {
       
        @Override
        public Dimension getPreferredSize() {
            return new Dimension(250, 500);
        }
        
        public LecturePanel() {
            //setMinimumSize(new Dimension(200, 25));
            setBorder(new TitledBorder (new EtchedBorder(), 
                "Neste Forelesning"));
        }
    }
            
    public JPanel makeLecturePanel() {
        LecturePanel LecturePanel = new LecturePanel();
        LecturePanel.setLayout(new BoxLayout(LecturePanel, BoxLayout.Y_AXIS));

        /*JLabel nextLectureHeader = new JLabel("Neste forelesning:");
        LecturePanel.add(nextLectureHeader);*/

        JLabel nextLecture1 = new JLabel("<html><u>Onsdag 23. september,</u><br>08:15-11:00."
                + "<br>Tema: Abstraksjon</html>");
        LecturePanel.add(nextLecture1);

        JLabel nextLecture2 = new JLabel("<html><u>Tirsdag 01. oktober,</u><br>08:15-10:00."
                + "<br>Tema: Modularisering</html>");
        LecturePanel.add(nextLecture2);
        return LecturePanel;
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
        JPanel contactPanel = new borderPanel();
        GridBagLayout contactLayout = new GridBagLayout();
        contactPanel.setLayout(contactLayout);
        
        
        
        System.out.println("Pre-FISH");
        int contactHelpery = 1;
        boolean contactHelperToRight = false;
//        HashMap<String, Map> fishmap = this.dbConnector.getAllUsersHashMap();
        for(Map.Entry<String, Map> entry : dbConnector.getAllUsersHashMap().entrySet()) { //this.??
            String key = entry.getKey();
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
            gbcContact.ipady = 10;
            gbcContact.ipadx = 5;
            gbcContact.anchor = GridBagConstraints.WEST;
            contactLayout.setConstraints(contactLabel, gbcContact);
            contactPanel.add(contactLabel);
            if(contactHelperToRight == true) {
                contactHelpery +=1;
            }
            contactHelperToRight = !contactHelperToRight;
        }

        GridBagConstraints gbcSearchField = new GridBagConstraints();
        JTextField searchField = new JTextField(20);
        searchField.addKeyListener(new ContactSearchKeyListener());
        gbcSearchField.gridx = 0;
        gbcSearchField.gridy = 0;
        gbcSearchField.gridwidth = 2;
        contactLayout.setConstraints(searchField, gbcSearchField);
        //searchField.addActionListener(new returnSearchResults); //tbi
        contactPanel.add(searchField); 
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
            System.out.println("FISH!");
        }
    }
    
    /**
     * Lager activityPanel som er inni forside-taben. Returnerer til makeForsideTab()
     * @return JPanel activityPanel panelet som viser siste hendelser i systemet
     */
    private JPanel makeActivityPanel()  {
        JPanel activityPanel = new JPanel();
        activityPanel.setLayout(new BoxLayout(activityPanel, BoxLayout.Y_AXIS));

        JLabel lastActivityHeader = new JLabel("<html><u>Sist aktivitet</u></html>");
        activityPanel.add(lastActivityHeader);

        JPanel activity1 = new JPanel();
        activity1.setLayout(new BoxLayout(activity1, BoxLayout.Y_AXIS));
        JLabel activity1Label = new JLabel("Modul 2 godkjent");
        JButton modulComment = new JButton("Se tilbakemelding");
        activity1.add(activity1Label);
        activity1.add(modulComment);

        activityPanel.add(activity1);

        JPanel activity2 = new JPanel();
        activity2.setLayout(new BoxLayout(activity2, BoxLayout.Y_AXIS));
        JLabel activity2Label = new JLabel("Forelesningsplan høst 2015");
        JButton goToActivity = new JButton("Les mer...");
        activity2.add(activity2Label);
        activity2.add(goToActivity);

        activityPanel.add(activity2);
    return activityPanel;
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
        }
    }
}
