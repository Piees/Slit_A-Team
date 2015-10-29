/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package slitclient;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;



/**
 *
 * @author Arild
 */
public class StudentGUI {

    JFrame frame;
    TabForside tabForside;
    TabModuloversikt tabModuloversikt;
    TabFagstoff tabFagstoff;
    String userName;
    String nameOfUser;
    private static final String LOGO_PATH = "src/img/slitlogo.png";
    
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

    /**
     * Constructor for MakeGUI. Oppretter objekter av alle tab-klassene, og
     * kaller makeFrame()
     */
    public StudentGUI(ArrayList<String> userInfo) {
        //gets userName in index 0 from ArrayList userInfo
        userName = userInfo.get(0);
        //joins fname and lname in index 2 and 3 from ArrayList userInfo
        nameOfUser = userInfo.get(2) + " " + userInfo.get(3);
        tabForside = new TabForside();
        //create the moduloversikt-tab for the given userType
        tabModuloversikt = new TabModuloversikt(userInfo.get(1), frame);
        tabFagstoff = new TabFagstoff();
        makeFrame();
    }
    /*
    public String getNameOfUser(String userName)   {
        ArrayList<String> queryResults = dbConnector.multiQuery("SELECT fname, ename "
                                     + "FROM User WHERE userName = " + userName +";");
        String returnString = null;
        for(String string : queryResults)   {
            returnString += string;
        }
        return returnString;
    }*/
    
    public class MainFrame extends JFrame {
        @Override
        public Dimension getMinimumSize() {
            return new Dimension(1024, 700);
        }
        
        @Override
        public Dimension getPreferredSize() {
            return new Dimension(1024, 700);
        }
        public MainFrame(String title) {
            
        }
    }
    
    public class TabPane extends JTabbedPane {
        
        public TabPane() {
            
        }
        
        @Override
        public Dimension getPreferredSize() {
            return new Dimension(1024, 670);
        }
    }
    
    /**
     * Lager vinduet. Vinduet har GridBagLayout (enn så lenge i hvert fall).
     * Kaller makeCommon(). 
     * Kaller også makeTabs().
     */
    public void makeFrame() {
        frame = new MainFrame("SLIT - " + nameOfUser);
        //frame = new JFrame("SLIT - " + nameOfUser);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel contentPane = (JPanel) frame.getContentPane();
        GridBagLayout gblContent = new GridBagLayout();
        contentPane.setLayout(gblContent);
       
        
        JPanel commonContent =  makeCommon();
        GridBagConstraints gbcCommon = new GridBagConstraints();
        gbcCommon.gridx = 0;
        gbcCommon.gridy = 0;
        gbcCommon.gridwidth = 3;
        gblContent.setConstraints(commonContent, gbcCommon);
        
        JTabbedPane tabbedPane = makeTabs();
        GridBagConstraints gbcTab = new GridBagConstraints();
        gbcTab.gridx = 0;
        gbcTab.gridy = 1;
        gbcTab.gridwidth = 3;
        gblContent.setConstraints(tabbedPane, gbcTab);

        contentPane.add(commonContent);
        contentPane.add(tabbedPane);
        
        frame.pack();
        frame.setResizable(false);
        frame.setVisible(true);
    }
    
    /**
     * Lager elementene som er felles for alle faner 
     * (kun topplinja med logo og navn på bruker foreløpig).
     * Har BorderLayout, skal endres. Layout er foreløpig, funker ikke som den
     * bør gjøre nå.
     * @return JPanel content panelet med innholdet som er felles for alle faner
    */
    public JPanel makeCommon() {

        JPanel content = new JPanel();
        content.setLayout(new BorderLayout(300, 25));
        
        JButton menuButton = new JButton("Meny");
        //content.add(menuButton, BorderLayout.WEST);
        content.add(menuButton, BorderLayout.LINE_START);

        JLabel logoLabel = new JLabel(loadLogo());
        content.add(logoLabel, BorderLayout.CENTER);

        JButton nameButton = new JButton(nameOfUser);
        //content.add(nameButton, BorderLayout.EAST);
        content.add(nameButton, BorderLayout.LINE_END);

        return content;
        
    }

 
    /**
     * Lager faner. Kaller de respektive klassene for å lage innholdet, lager 
     * fanene med navn (og innhold) og legger de til tabbedPane.
     * @return JTabbedPane tabbedPane returnerer linja med fanene
     */  
    public JTabbedPane makeTabs()  {
        JTabbedPane tabbedPane = new TabPane();
       
        JComponent tab1 = tabForside.makeForsideTab();
        tabbedPane.addTab("Forside", null, tab1, null);
        
        JComponent tab2 = tabModuloversikt.makeModuloversiktTab();
        tabbedPane.addTab("Moduloversikt", null, tab2, null);
        
        JComponent tab3 = tabFagstoff.makeFagstoff();
        tabbedPane.addTab("Fagstoff", null, tab3, null);
        return tabbedPane;
    }
    
    

    
}
