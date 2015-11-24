/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tabmoduloversikt;

import db.DBQuerierRemote;
import db.DBUtilRemote;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.JXTaskPaneContainer;
import slitclient.EJBConnector;

/**
 * @author Arild Høyland
 * @author Viktor Setervang
 * @author Håkon Gilje
 */
public abstract class TabModuloversikt {

    protected HashMap<String, String> userInfo;
    protected JFrame frame;
    protected JPanel tab2Panel;
    protected EJBConnector ejbConnector = EJBConnector.getInstance();

    /**
     * Constructor for class TabModuloversikt. Stores the containing
     * SWING-component and a HashMap with information about the currently logged
     * in user
     *
     * @param userInfo map with information about the currently logged in user
     * @param frame containing SWING-component
     */
    public TabModuloversikt(HashMap<String, String> userInfo, JFrame frame) {
        this.userInfo = userInfo;
        this.frame = frame;
    }

    /**
     * Creates the tab with with collapsible modul panes. Ff teacher-user logged
     * in, adds button for creating new modules
     *
     * @return JPanel containing the components created
     */
    public JPanel makeModuloversiktTab() {
        tab2Panel = new JPanel();
        tab2Panel.setLayout(new BoxLayout(tab2Panel, BoxLayout.Y_AXIS));
        Component accordion = makeAccordion();

        tab2Panel.add(accordion);
        return tab2Panel;
    }

    /**
     * Creates the containing element for the collapsible modul panes Counts the
     * number of modules in the DB, and calls the makeModulList-method with this
     * number as parameter
     *
     * @return the component containing the created collapsible modul panes
     */
    protected Component makeAccordion() {
        JXPanel panel = new JXPanel();
        panel.setLayout(new BorderLayout());
        //check number of modules in the DB
        DBUtilRemote dbUtil = ejbConnector.getDBUtil();
        int numberOfModuls = dbUtil.countRows("*", "Modul");
        //creates a list witht the given number of modules
        panel.add(makeModulList(numberOfModuls));
        panel.setPreferredSize(new Dimension(700, 900));
        panel.setMaximumSize(new Dimension(700, 900));
        return panel;
    }

    /**
     * Creates the collapsible panes for each module. If student-user logged in,
     * each module header shows module name and status of current delivery for
     * this module. If teacher-user logged in, module header shows module name
     * and number of deliveries for this module divided with total number of
     * students
     *
     * @param numberOfModuls the number of modules to be created
     * @return JXTaskPaneContainer the container containing all the collapsible
     * panes with module content
     */
    protected abstract JXTaskPaneContainer makeModulList(int numberOfModuls);

    protected void displayModulText(LinkedHashMap map, JXTaskPane modulPane) {
        //for each value in the current HashMap, display values
        for (Object value : map.values()) {
            //we do not wish to display the idModul-value, so we check if the
            //length of the value is longer than 1 character
            if (value.toString().length() > 1) {
                JTextArea textArea = new JTextArea(value.toString());
                textArea.setEditable(false);
                textArea.setWrapStyleWord(true);
                modulPane.add(textArea);
            }
        }
    }

    /**
     * Creates the collapsible panes for each module. If student-user logged in,
     * each module header shows module name and status of current delivery for
     * this module. If teacher-user logged in, module header shows module name
     * and number of deliveries for this module divided with total number of
     * students
     *
     * @param numberOfModuls the number of modules to be created
     * @return JXTaskPaneContainer the container containing all the collapsible
     * panes with module content
     */
    //Get modul content
    protected ArrayList<LinkedHashMap> getModulContent() {
        //create the arraylists for this query
        ArrayList<String> columns = new ArrayList(Arrays.asList("*"));
        ArrayList<String> tables = new ArrayList(Arrays.asList("Modul"));
        //execute the query
        DBQuerierRemote dbQuerier = ejbConnector.getDBQuerier();
        ArrayList<LinkedHashMap> moduls = dbQuerier.multiQueryHash(columns, tables, null);
        return moduls;
    }
}
