/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package slitclient;


import db.dbConnectorRemote;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.JXTaskPaneContainer;

/**
 *
 * @author Arild
 */
public class TabModuloversikt {
    private final int IS109 = 5;
    private final int IS110 = 10;
    private String userType;
    private String userName;
    private JFrame frame;
    
    public TabModuloversikt(String userType, JFrame frame)   {
        this.userType = userType;
        this.frame = frame;
    }
    /**
     * Lager moduloversikttaben. 
     * Lager utvidbar liste med antall moduler.
     * @return JPanel tab2Panel returnerer panel med innholdet i tab2
     */
    public JPanel makeModuloversiktTab()    {
        JPanel tab2Panel = new JPanel();
        
        if(userType.equals("teacher"))  {
            JButton createModulButton = new JButton("Opprett modul");
                createModulButton.addActionListener(new ActionListener()  {
                    @Override
                    public void actionPerformed (ActionEvent e)  {
                        createModul();
                    }
                });
                
            tab2Panel.add(createModulButton);
        }
        
        Component accordion = makeAccordion();
        tab2Panel.add(accordion);
//        tab2Panel.add(testQuery());
//        tab2Panel.add(testQuery2());
        return tab2Panel;
    }   
    public Component makeAccordion(){
        JXPanel panel = new JXPanel();
        panel.setLayout(new BorderLayout());
        
        ArrayList<Modul> modulList = new ArrayList<Modul>(Modul.makeModules(IS109));  
    EJBConnector ejbConnector = EJBConnector.getInstance();
    dbConnectorRemote dbConnector = ejbConnector.getEjbRemote();
    int numberOfModuls = dbConnector.countRows("*", "Modul");
    panel.add(makeModulList(numberOfModuls));// = makeModulList(numberOfModuls); 
    
    return panel;
    }
    
    public JXTaskPaneContainer makeModulList(int numberOfModuls)    {
        JXTaskPaneContainer modulListContainer = new JXTaskPaneContainer();  
        int i = 1;
        while (i <= numberOfModuls)  {
            JXTaskPane modulPane;
            
            ArrayList<String> columns = new ArrayList();
            ArrayList<String> tables = new ArrayList();
            ArrayList<String> where = new ArrayList();
            columns.add("*");
            tables.add("Modul");
            where.add("idModul = " + i + ";");
            EJBConnector ejbConnector = EJBConnector.getInstance();
            dbConnectorRemote dbConnector = ejbConnector.getEjbRemote();
            ArrayList<String> moduls = dbConnector.multiQuery(columns, tables, where);
            if (userType.equals("student")) {    
                columns.clear();
                tables.clear();
                where.clear();
                columns.add("deliveryStatus");
                tables.add("Delivery");
                where.add("idModul = " + i + " AND deliveredBy = '" + userName + "';");
                ArrayList<String> deliveryStatus = dbConnector.multiQuery(columns, tables, where);
                if(deliveryStatus.size() > 0)   {
                    modulPane = new JXTaskPane("Modul " + moduls.get(0) + deliveryStatus.get(0));
                }
                else    {
                    modulPane = new JXTaskPane(moduls.get(0) + "    Ikke levert");
                }
                modulPane.setCollapsed(true);
                addContent(moduls, modulPane);
                modulListContainer.add(modulPane);
                i++;
                }
            else    {
                int numberOfDeliveries = dbConnector.countRows("*", "Delivery WHERE idModul = " + moduls.get(0));
                int numberOfStudents = dbConnector.countRows("*", "User WHERE userType = 'student'");
                modulPane = new JXTaskPane("Modul " + moduls.get(0) + "         "
                            + numberOfDeliveries + "/" + numberOfStudents + " innleveringer");
                modulPane.setCollapsed(true);
                addContent(moduls, modulPane);
                modulListContainer.add(modulPane);
                i++;
                }
            }
        
        return modulListContainer;
    }
    public void addContent(ArrayList<String> content, JXTaskPane modulPane)   {
        for(String string : content)    {
            JLabel label = new JLabel(string);
            modulPane.add(label);
        }
    }
    
//    public JLabel testQuery()     {
//        EJBConnector ejbConnector = new EJBConnector();
//        dbConnectorRemote dbConnector = ejbConnector.getEjbRemote();
//        ArrayList<String> columns = new ArrayList<>();
//        ArrayList<String> tables = new ArrayList<>();
//        ArrayList<String> where = new ArrayList<>();
//        columns.add("*");
//        tables.add("User");
//        where.add("userName = 'arildh14'");
//        ArrayList<String> queryResults = dbConnector.multiQuery(columns, tables, where);
//        String labelString = "";
//        for (String string : queryResults)  {
//            labelString += string;
//        }
//        JLabel label = new JLabel(labelString);
//        return label;
//    }
//    public JLabel testQuery2()     {
//        EJBConnector ejbConnector = new EJBConnector();
//        dbConnectorRemote dbConnector = ejbConnector.getEjbRemote();
//        String query = "SELECT description FROM Modul WHERE idModul = '2';";
//        JLabel label = new JLabel();
//        label.setText(dbConnector.singleQuery(query, "description"));
//        return label;
//    }
    public void createModul()   {
        JDialog createModulDialog = new JDialog(frame, true);
        JPanel contentPane = (JPanel) createModulDialog.getContentPane();
        JTextField createModulDesc = new JTextField("Modulbeskrivelse");
        JTextField createModulLearningObj = new JTextField("Læringsmål i denne modulen");
        JTextField createModulRes = new JTextField("Ressurser");
        JTextField createModulEx = new JTextField("Oppgave");
        JTextField createModulEval = new JTextField("Godkjenning");
              
        contentPane.add(createModulDesc);
        contentPane.add(createModulLearningObj);
        contentPane.add(createModulRes);
        contentPane.add(createModulEx);
        contentPane.add(createModulEval);
        
        JButton createModulButton = new JButton("Opprett modul");
        createModulButton.addActionListener(new ActionListener()    {
            @Override
            public void actionPerformed(ActionEvent e)  {
            ArrayList<String> columns = new ArrayList();
            ArrayList<Object> values = new ArrayList();
            String modul = "Modul";
            columns.add("description");
            columns.add("learningObj");
            columns.add("resources");
            columns.add("excercise");
            columns.add("evalForm");values.add(createModulDesc.getText());
            values.add(createModulLearningObj.getText());
            values.add(createModulRes.getText());
            values.add(createModulEx.getText());
            values.add(createModulEval.getText());
            
            //how is idModul set? Auto increment? Or should it be specified for each insert?
            /*String insert = "INSERT INTO Modul (idModul, description, learningObj,"
                    + " resources, excercise, evalForm) "
                 + "VALUES ('NULL', '" + modulDesc + "', '" + modulLearningObj + "', '" 
                    + modulRes + "', '" + modulEx + "', '" + modulEval + "';";
              */  EJBConnector ejbConnector = EJBConnector.getInstance();
                dbConnectorRemote dbConnector = ejbConnector.getEjbRemote();
                dbConnector.insertIntoDB(modul, columns, values);
                //System.out.println(insert);
                //we need a method for inserting into database here
                //dbConnector.insert(insert);
                System.out.println("Lagrer modul i database");
                
            }
        });
        contentPane.add(createModulButton);
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
        createModulDialog.setSize(250, 250);
        createModulDialog.pack();
        createModulDialog.setVisible(true);
    }
}
        
        