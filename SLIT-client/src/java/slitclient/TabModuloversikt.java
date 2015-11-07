/*
 *  test
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package slitclient;


import db.dbConnectorRemote;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
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
    private HashMap<String, String> userInfo;
    private JFrame frame;
    //ArrayList<String> deliveryList;
    
    public TabModuloversikt(HashMap<String, String> userInfo, JFrame frame)   {
        this.userInfo = userInfo;
        this.frame = frame;
    }
    /**
     * Lager moduloversikttaben. 
     * Lager utvidbar liste med antall moduler.
     * @return JPanel tab2Panel returnerer panel med innholdet i tab2
     */
    public JPanel makeModuloversiktTab()    {
        JPanel tab2Panel = new JPanel();
        
        if(userInfo.get("userType").equals("teacher"))  {
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
            if (userInfo.get("userType").equals("student")) {    
                columns.clear();
                tables.clear();
                where.clear();
                columns.add("deliveryStatus");
                tables.add("Delivery");
                where.add("idModul = " + i + " AND deliveredBy = '" + userInfo.get("userName") + "';");
                ArrayList<String> deliveryStatus = dbConnector.multiQuery(columns, tables, where);
                if(deliveryStatus.size() > 0)   {
                    modulPane = new JXTaskPane("Modul " + moduls.get(0) + deliveryStatus.get(0));
                }
                else    {
                    modulPane = new JXTaskPane(moduls.get(0) + "    Ikke levert");
                }
                modulPane.setCollapsed(true);
                addContent(moduls, modulPane, i);
                modulListContainer.add(modulPane);
                i++;
                }
            else    {
                int numberOfDeliveries = dbConnector.countRows("*", "Delivery WHERE idModul = " + moduls.get(0));
                int numberOfStudents = dbConnector.countRows("*", "User WHERE userType = 'student'");
                modulPane = new JXTaskPane("Modul " + moduls.get(0) + "         "
                            + numberOfDeliveries + "/" + numberOfStudents + " innleveringer");
                modulPane.setCollapsed(true);
                addContent(moduls, modulPane, i);
                modulListContainer.add(modulPane);
                i++;
                }
            }
        
        return modulListContainer;
    }
    public void addContent(ArrayList<String> content, JXTaskPane modulPane, int i)   {
        for(String string : content)    {
            JLabel label = new JLabel(string);
            modulPane.add(label);
        }
            // UPLOAD DELIVERY BUTTON
        if (userInfo.get("userType").equals("student")) {
            JButton uploadDeliveryButton = new JButton("Opplast oppgave");
            modulPane.add(uploadDeliveryButton);
            uploadDeliveryButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    addDeliveryDialog(i);
                }
            });
        }
        else if (userInfo.get("userType").equals("teacher"))    {
            JButton openDeliveryListButton = new JButton("Se innleveringer");
            modulPane.add(openDeliveryListButton);
            openDeliveryListButton.addActionListener(new ActionListener ()  {
                @Override
                public void actionPerformed(ActionEvent e)  {
                    openDeliveryListDialog(i);
                }
            });
        }
                
    }
        
    private void openDeliveryListDialog(int i)   {
        JDialog deliveryListDialog = new JDialog();
        JPanel contentPane = (JPanel) deliveryListDialog.getContentPane();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
        EJBConnector ejbConnector = EJBConnector.getInstance();
        dbConnectorRemote dbConnector = ejbConnector.getEjbRemote();
        ArrayList<String> columns = new ArrayList(Arrays.asList("deliveryDate", "evaluationDate", "deliveryStatus", "deliveredBy", "evaluatedBy"));
        ArrayList<String> tables = new ArrayList<>();
        ArrayList<String> where = new ArrayList<>();
        tables.add("Delivery");
        where.add("idModul = " + i);
        
        ArrayList<String> deliveryList = dbConnector.multiQuery(columns, tables, where);
        int index = 0;
        while (index < deliveryList.size()) {
            System.out.println(index);
            JPanel deliveryLine = new JPanel();
            int lineIndex = index;
        
            while(lineIndex < index + columns.size())    {
                System.out.println("lineIndex:" + lineIndex);
                JLabel label = new JLabel(deliveryList.get(lineIndex));
                deliveryLine.add(label);
                lineIndex++;
                //if (lineIndex == index + columns.size()) {
            }
                int userNameIndex = index + columns.size() -2;  
        
            JButton openFileButton = new JButton("Åpne fil");
            openFileButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) { 
                    //int userNameIndex = deliveryList.size()-1;
                    //int userNameIndex = userNameIndex; 
//                    for (String s : TabModuloversikt.this.deliveryList) {
//                        System.out.println(s);
//                    }
                    String userName = deliveryList.get(userNameIndex);
                    System.out.println(deliveryList.size());
                    System.out.println(userNameIndex);
                    System.out.println(userName);
                    System.out.println(userNameIndex + " " + userName);
                    downloadFile(userName, i);
                    openEvaluationDialog(deliveryListDialog, i);
                }
            });
            
            
            deliveryLine.add(openFileButton);
            contentPane.add(deliveryLine);
            index += columns.size();
        }
        deliveryListDialog.pack();
        deliveryListDialog.setVisible(true);
        
    }
    
    private void downloadFile(String userName, int idModul) {
        // Hente fra database : dbConnector.getFileFromDelivery();
        // Enten åpne filutforsker for å velge mappe hvor det skal lagres, eller 
        // automatisk lagre i default download mappe.
        System.out.println("idModul " + idModul + " userName: " + userName);
        EJBConnector ejbConnector = EJBConnector.getInstance();
        dbConnectorRemote dbConnector = ejbConnector.getEjbRemote();
        
        //InputStream inputStream = dbConnector.getFileFromDelivery(userName, idModul);
        String fileName = "modul" + idModul + "_" + userName;
        String path = javax.swing.filechooser.FileSystemView.getFileSystemView().getHomeDirectory().getAbsolutePath();
        String filepath = path.concat("/" + fileName);
        
        System.out.println(filepath);
        //System.out.println(javax.swing.filechooser.FileSystemView.getFileSystemView().getDefaultDirectory());
        
        byte[] byteData = dbConnector.getFileFromDelivery(userName, idModul);
       
        try {
            FileOutputStream out = new FileOutputStream(filepath);
            out.write(byteData);
            out.close();
        } catch (IOException ex) {
            Logger.getLogger(TabModuloversikt.class.getName()).log(Level.SEVERE, null, ex);
        }
        
//        try {
//            Files.write(byteArray, new File(path));
//        } catch (IOException ex) {
//            Logger.getLogger(TabModuloversikt.class.getName()).log(Level.SEVERE, null, ex);
//        }    
            
            
//        try {
//            // Hardcoded path on my computer
//            outputStream = new FileOutputStream(new File("/home/archheretic/Downloads/test"));
//
//            int read = 0;
//            // This might restrict the size of the file and fuck things up
//            byte[] bytes = new byte[1024];
//
//            while ((read = inputStream.read(bytes)) != -1) {
//                outputStream.write(bytes, 0, read);
//            }
//        
//        } catch (IOException ex) {
//            Logger.getLogger(TabModuloversikt.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        // Not sure if its neccerary to close the file like this.
//        finally {
//            if (inputStream != null) {
//                    try {
//                            inputStream.close();
//                    } catch (IOException e) {
//                        System.out.println(e);
//                    }
//            }
//            if (outputStream != null) {
//                    try {
//                            // outputStream.flush();
//                            outputStream.close();
//                    } catch (IOException e) {
//                        System.out.println(e);                        
//                    }
//
//            }
//	}
//        


        
    }
    private void openEvaluationDialog(JDialog deliveryListDialog, int i) {
        JDialog openEvaluationDialog = new JDialog(deliveryListDialog);
        JPanel contentPane = (JPanel) openEvaluationDialog.getContentPane();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
        contentPane.add(new JLabel("Din tilbakemelding:"));
        JTextField evaluation = new JTextField();
        contentPane.add(evaluation);
        JButton uploadEvaluationButton = new JButton("Lagre tilbakemelding");
        contentPane.add(uploadEvaluationButton);
        uploadEvaluationButton.addActionListener(new ActionListener()   {
            @Override
            public void actionPerformed(ActionEvent e)  {
                String returnString = uploadEvaluationToDB(evaluation.getText(), i);
                JOptionPane.showMessageDialog(openEvaluationDialog, returnString);
                if(returnString.equals("Lagret i database."))  {
                    openEvaluationDialog.dispose();
                }
            }
        });
        openEvaluationDialog.pack();
        openEvaluationDialog.setVisible(true);
    }
    
    private String uploadEvaluationToDB(String evaluation, int i)  {
        EJBConnector ejbConnector = EJBConnector.getInstance();
        dbConnectorRemote dbConnector = ejbConnector.getEjbRemote();
        return dbConnector.addDeliveryEvaluation(evaluation, userInfo.get("userName"), i, "viktos08");
        
        
    }
    private void addDeliveryDialog(int i) {
        GUIFileUploader fileUploader = new GUIFileUploader();
        
        JDialog addDeliveryDialog = new JDialog();
        addDeliveryDialog.setLayout(new GridLayout(0, 1));
        JPanel contentpane = (JPanel) addDeliveryDialog.getContentPane();
        
        JLabel deliveryFile = new JLabel("Ingen fil valgt");
        
        JButton chooseFileButton = new JButton("Velg fil");
        JButton uploadDeliveryButton = new JButton("Last opp innlevering");
        
        contentpane.add(deliveryFile);
        contentpane.add(chooseFileButton);
        contentpane.add(uploadDeliveryButton);
        
        addDeliveryDialog.pack();
        addDeliveryDialog.setVisible(true);
        
        chooseFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
              deliveryFile.setText(fileUploader.startFileExplorer(frame));
            }
        });
        
        uploadDeliveryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (deliveryFile.getText().equals("Ingen fil valgt")) {
                    JOptionPane.showMessageDialog(null, deliveryFile.getText()); 
                    }
                else {
                    String userName = userInfo.get("userName");
                    JOptionPane.showMessageDialog(null, fileUploader.uploadDelivery(userName, i));            
                }
            }
        });
    }
    
    
    public void createModul()   {
        JDialog createModulDialog = new JDialog(frame, true);
        JPanel contentPane = (JPanel) createModulDialog.getContentPane();
        JTextField createIdModul = new JTextField("ID-modul");
        JTextField createModulTitle= new JTextField();
        JTextField createModulDesc = new JTextField("Modulbeskrivelse");
        JTextField createModulLearningObj = new JTextField("LÃ¦ringsmÃ¥l i denne modulen");
        JTextField createModulRes = new JTextField("Ressurser");
        JTextField createModulEx = new JTextField("Oppgave");
        JTextField createModulEval = new JTextField("Godkjenning");
              
        contentPane.add(createIdModul);
        contentPane.add(createModulTitle);
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
            columns.add("idModul");
            columns.add("title");
            columns.add("description");
            columns.add("learningObj");
            columns.add("resources");
            columns.add("excercise");
            columns.add("evalForm");
            int i = Integer.parseInt(createIdModul.getText());
            values.add(i);
            values.add(createModulTitle.getText());
            values.add(createModulDesc.getText());
            values.add(createModulLearningObj.getText());
            values.add(createModulRes.getText());
            values.add(createModulEx.getText());
            values.add(createModulEval.getText());
            
                EJBConnector ejbConnector = EJBConnector.getInstance();
                dbConnectorRemote dbConnector = ejbConnector.getEjbRemote();
                dbConnector.insertIntoDB(modul, columns, values);
                //System.out.println(insert);
             
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