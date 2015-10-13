/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package my.GUI;


import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.JXTaskPaneContainer;

/**
 *
 * @author Arild-Bærbar
 */
public class TabModuloversikt {
    private final int IS109 = 5;
    private final int IS110 = 10;
    
    public TabModuloversikt()   {
        
    }
    /**
     * Lager moduloversikttaben. I denne er det fem eksempel-moduler som hardkodes
     * inn, må lages metode for å hente moduler fra database etc.
     * Jobber med å få laget en utvidbar/kollapsbar funksjon når du trykker på
     * en modul, slik at du kan se all modulinformasjon på samme side som
     * moduloversikten er.
     * @return JPanel tab2Panel returnerer panel med innholdet i tab2
     */
    public JPanel makeModuloversiktTab()    {
        JPanel tab2Panel = new JPanel();
        /*tab2Panel.setLayout(new BoxLayout (tab2Panel, BoxLayout.Y_AXIS));
        
        JPanel modul1Panel = new JPanel();
        JButton modul1Button = new JButton("Modul 1");
        modul1Panel.add(modul1Button);
        JLabel modul1Label = new JLabel("<html>Innlevering:<font color='green'>Godkjent</font></html>");
        modul1Panel.add(modul1Label);
        tab2Panel.add(modul1Panel);
        
        JPanel modul2Panel = new JPanel();
        JButton modul2Button = new JButton("Modul 2");
        modul2Panel.add(modul2Button);
        JLabel modul2Label = new JLabel("<html>Innlevering:<font color='red'>Ikke godkjent</font></html>");
        modul2Panel.add(modul2Label);
        tab2Panel.add(modul2Panel);
        
        JPanel modul3Panel = new JPanel();
        JButton modul3Button = new JButton("Modul 3");
        modul3Panel.add(modul3Button);
        JLabel modul3Label = new JLabel("<html>Innlevering:<font color='yellow'>Under vurdering</font></html>");
        modul3Panel.add(modul3Label);
        tab2Panel.add(modul3Panel);
        
        JPanel modul4Panel = new JPanel();
        JButton modul4Button = new JButton("Modul 4");
        modul4Panel.add(modul4Button);
        JLabel modul4Label = new JLabel("<html>Innlevering:<font color='black'>Ikke levert</font></html>");
        modul4Panel.add(modul4Label);
        tab2Panel.add(modul4Panel);
        
        JPanel modul5Panel = new JPanel();
        JButton modul5Button = new JButton("Modul 5");
        modul5Panel.add(modul5Button);
        JLabel modul5Label = new JLabel("<html>Innlevering:<font color='black'>Ikke levert</font></html>");
        modul5Panel.add(modul5Label);
        tab2Panel.add(modul5Panel);
        */
        
        Component accordion = makeAccordion();
        tab2Panel.add(accordion);
        return tab2Panel;
    }   
    public Component makeAccordion() {
        JXPanel panel = new JXPanel();
        panel.setLayout(new BorderLayout());
        
        ArrayList<Modul> modulList = new ArrayList<Modul>(Modul.makeModules(IS109));  
  
     JXTaskPaneContainer taskPaneContainer = new JXTaskPaneContainer();  
     for (Modul modul : modulList)  {
         JXTaskPane modulPane = new JXTaskPane(modul.getName() + modul.getStatus());
         modulPane.setCollapsed(true);
         modulPane.add(modul.getInnhold());
         taskPaneContainer.add(modulPane);
     }
    
     //modul1Pane.addActionListener(new PaneActionListener());
         
     panel.add(taskPaneContainer);//, BorderLayout.CENTER);
     
     return panel;
    }
    class PaneActionListener implements ActionListener   {
        public PaneActionListener() {
            
        }
        public void actionPerformed(ActionEvent e)  {
            //modul1.setText("an action performed");
        }
    }
}
        
        