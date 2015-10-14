/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package my.GUI;


import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JPanel;
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
     * Lager moduloversikttaben. 
     * Lager utvidbar liste med antall moduler.
     * @return JPanel tab2Panel returnerer panel med innholdet i tab2
     */
    public JPanel makeModuloversiktTab()    {
        JPanel tab2Panel = new JPanel();
             
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
        
        