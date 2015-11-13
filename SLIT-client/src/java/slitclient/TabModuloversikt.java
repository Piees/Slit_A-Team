/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package slitclient;

import db.dbConnectorRemote;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.JXTaskPaneContainer;
import slitcommon.DeliveryStatus;

/**
 *
 * @author Arild, Viktor, H�kon
 */
public class TabModuloversikt {

    private HashMap<String, String> userInfo;
    private JFrame frame;

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
     * Creates the tab with with collapsible modul panes if teacher-user logged
     * in, adds button for creating new modules
     *
     * @return JPanel containing the components created
     */
    public JPanel makeModuloversiktTab() {
        JPanel tab2Panel = new JPanel();

        if (userInfo.get("userType").equals("teacher")) {
            JButton createModulButton = new JButton("Opprett modul");
            createModulButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    createModul();
                }
            });
            tab2Panel.add(createModulButton);
        }
        Component accordion = makeAccordion();
        tab2Panel.add(accordion);
        return tab2Panel;
    }

    /**
     * Creates the containing element for the collapsible modul panes
     *
     * @return the component containing the reated collapsible modul panes
     */
    public Component makeAccordion() {
        JXPanel panel = new JXPanel();
        panel.setLayout(new BorderLayout());
        EJBConnector ejbConnector = EJBConnector.getInstance();
        dbConnectorRemote dbConnector = ejbConnector.getEjbRemote();
        int numberOfModuls = dbConnector.countRows("*", "Modul");
        panel.add(makeModulList(numberOfModuls));
        return panel;
    }

    /**
     * Creates the collapsible panes for each module If student-user logged in,
     * each module header shows module name and status of current delivery for
     * this module If teacher-user logged in, module header shows module name
     * and number of deliveries for this module divided on total number of
     * students
     *
     * @param numberOfModuls the number of modules to be created
     * @return JXTaskPaneContainer the container containing all the collapsible
     * panes with module content
     */
    public JXTaskPaneContainer makeModulList(int numberOfModuls) {
        JXTaskPaneContainer modulListContainer = new JXTaskPaneContainer();
        int i = 1;
        while (i <= numberOfModuls) {
            JXTaskPane modulPane;

            ArrayList<String> columns = new ArrayList();
            ArrayList<String> tables = new ArrayList();
            ArrayList<String> where = new ArrayList();
            columns.add("idModul");
            columns.add("title");
            columns.add("description");
            columns.add("learningObj");
            columns.add("resources");
            columns.add("excercise");
            columns.add("evalForm");
            tables.add("Modul");
            where.add("idModul = " + i + ";");
            EJBConnector ejbConnector = EJBConnector.getInstance();
            dbConnectorRemote dbConnector = ejbConnector.getEjbRemote();
            ArrayList<HashMap> moduls = dbConnector.multiQueryHash(columns, tables, where);
            if (userInfo.get("userType").equals("student")) {
                columns.clear();
                tables.clear();
                where.clear();
                columns.add("deliveryStatus");
                tables.add("Delivery");
                where.add("idModul = " + i + " AND deliveredBy = '" + userInfo.get("userName") + "';");
                ArrayList<HashMap> deliveryStatus = dbConnector.multiQueryHash(columns, tables, where);
                if (deliveryStatus.size() > 0) {
                    modulPane = new JXTaskPane("Modul " + moduls.get(0).get("title")
                            + deliveryStatus.get(0).get("deliveryStatus"));
                } else {
                    modulPane = new JXTaskPane(moduls.get(0).get("title") + "    Ikke levert");
                }
                modulPane.setCollapsed(true);
                addContent(moduls, modulPane, i);
                modulListContainer.add(modulPane);
                i++;
            } else {
                int numberOfDeliveries = dbConnector.countRows("*", "Delivery WHERE idModul = " + moduls.get(0).get("idModul"));
                int numberOfStudents = dbConnector.countRows("*", "User WHERE userType = 'student'");
                modulPane = new JXTaskPane("Modul " + moduls.get(0).get("idModul") 
                        + ":" + moduls.get(0).get("title") + "         "
                        + numberOfDeliveries + "/" + numberOfStudents + " innleveringer");
                modulPane.setCollapsed(true);
                addContent(moduls, modulPane, i);
                modulListContainer.add(modulPane);
                i++;
            }
        }
        return modulListContainer;
    }

    /**
     * Adds the content for each module by looping through the arraylist and
     * displaying all results as labels If student-user, adds a button for
     * adding deliveries to each module If teacher-user, adds a button for
     * opening list of deleveries for each module
     *
     * @param content list of content in modules
     * @param modulPane the modulPane component labels should be added to
     * @param i the idModul of the current module
     */
    public void addContent(ArrayList<HashMap> content, JXTaskPane modulPane, int i) {
        for (HashMap map : content) {
            for (Object value : map.values()) {
                JLabel label = new JLabel(value.toString());
                modulPane.add(label);
            }
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
        } //DELIVERY LIST BUTTON
        else if (userInfo.get("userType").equals("teacher")) {
            JButton openDeliveryListButton = new JButton("Se innleveringer");
            modulPane.add(openDeliveryListButton);
            openDeliveryListButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    openDeliveryListDialog(i);
                }
            });
        }

    }

    /**
     * Window for showing all deliveries for the chosen module Gets all
     * deliveries for the current module from the DB, showing them in a
     * GirdBagLayout-grid. Each delivery has a button which downloads the
     * delivery-file and opens the evaluation window by calling
     * openEvaluationDialog(..)
     *
     * @param i the idModul of the selected module
     */
    private void openDeliveryListDialog(int i) {
        JDialog deliveryListDialog = new JDialog(frame, "Innleveringer i modul " + i, true);
        JPanel contentPane = (JPanel) deliveryListDialog.getContentPane();
        contentPane.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        JLabel deliveredDateLabel = new JLabel("Leveringsdato:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        contentPane.add(deliveredDateLabel, gbc);
        JLabel evaluatedDateLabel = new JLabel("Vurderingsdato:");
        gbc.gridx = 1;
        gbc.gridy = 0;
        contentPane.add(evaluatedDateLabel, gbc);
        JLabel deliveryStatusLabel = new JLabel("Vurderingsstatus:");
        gbc.gridx = 2;
        gbc.gridy = 0;
        contentPane.add(deliveryStatusLabel, gbc);
        JLabel deliveredByLabel = new JLabel("Levert av:");
        gbc.gridx = 3;
        gbc.gridy = 0;
        contentPane.add(deliveredByLabel, gbc);
        JLabel evaluatedByLabel = new JLabel("Vurdert av:");
        gbc.gridx = 4;
        gbc.gridy = 0;
        contentPane.add(evaluatedByLabel, gbc);

        EJBConnector ejbConnector = EJBConnector.getInstance();
        dbConnectorRemote dbConnector = ejbConnector.getEjbRemote();
        ArrayList<String> columns = new ArrayList(Arrays.asList("deliveryDate", "evaluationDate", "deliveryStatus", "deliveredBy", "evaluatedBy"));
        ArrayList<String> tables = new ArrayList<>();
        ArrayList<String> where = new ArrayList<>();
        tables.add("Delivery");
        where.add("idModul = " + i);

        ArrayList<HashMap> deliveryList = dbConnector.multiQueryHash(columns, tables, where);
        int gridBagYCounter = 1;
        for(HashMap map : deliveryList) {
            int gridBagXCounter = 0;
            for (Object value : map.values()) {
                JLabel label;
                if(value != null)   {
                    label = new JLabel(value.toString());
                }
                else    {
                    label = new JLabel("");
                }
                JPanel panel = new JPanel();
                panel.setBackground(Color.WHITE);
                panel.add(label);
                gbc.fill = GridBagConstraints.VERTICAL;
                gbc.gridx = gridBagXCounter;
                gbc.gridy = gridBagYCounter;
                contentPane.add(panel, gbc);
                gridBagXCounter++;
            }
            JButton openFileButton = new JButton("åpne fil");
            openFileButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                        //int userNameIndex = deliveryList.size()-1;
                    //int userNameIndex = userNameIndex; 
                    //                    for (String s : TabModuloversikt.this.deliveryList) {
                    //                        System.out.println(s);
                    //                    }
                    String userName = map.get("deliveredBy").toString();
    //                    System.out.println(deliveryList.size());
                    //                    System.out.println(userNameIndex);
                    //                    System.out.println(userName);
                    //                    System.out.println(userNameIndex + " " + userName);
                    FileDownloader downloader = new FileDownloader();
                    downloader.downloadDeliveryFile(userName, i);
                    openEvaluationDialog(deliveryListDialog, i, userName);

                }
            });

            gbc.gridx = gridBagXCounter;
            gbc.gridy = gridBagYCounter;
            contentPane.add(openFileButton, gbc);
            gridBagYCounter++;
        }
        deliveryListDialog.pack();
        deliveryListDialog.setVisible(true);

    }


    /**
     * Add window for teacher-users, in order to give evaluation on deliveries
     * User needs to choose one alternative from the drop-down list and then
     * enter an evaluation comment. When button store-evaluation is clicked,
     * method uploadEvaluationToDB is called. This returns a confirmation
     * string. If stored successfully in DB, window is closed.
     *
     * @param deliveryListDialog the parent component of this JDialog
     * @param i the idModul-number of the module that's being evaluated
     * @param userName the userName of the user that has made a delivery for
     * this module
     */
    private void openEvaluationDialog(JDialog deliveryListDialog, int i, String userName) {
        JDialog openEvaluationDialog = new JDialog(deliveryListDialog, "Gi tilbakemelding", true);
        JPanel contentPane = (JPanel) openEvaluationDialog.getContentPane();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
        contentPane.add(new JLabel("Din tilbakemelding:"));
        JTextField evaluation = new JTextField();
        DeliveryStatus[] evaluationStatus = {DeliveryStatus.GODKJENT, DeliveryStatus.IKKEGODKJENT};
        JComboBox evaluationStatusList = new JComboBox(evaluationStatus);
        contentPane.add(evaluationStatusList);
        contentPane.add(evaluation);
        JButton uploadEvaluationButton = new JButton("Lagre tilbakemelding");
        contentPane.add(uploadEvaluationButton);
        uploadEvaluationButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DeliveryStatus evaluationStatusEnum = (DeliveryStatus) evaluationStatusList.getSelectedItem();
                if (evaluation.getText().length() > 2) {
                    String returnString = uploadEvaluationToDB(evaluation.getText(), i, evaluationStatusEnum, userName);
                    JOptionPane.showMessageDialog(openEvaluationDialog, returnString, returnString, 1);
                    if (returnString.equals("Lagret i database.")) {
                        openEvaluationDialog.dispose();
                    }
                } else {
                    JOptionPane.showMessageDialog(openEvaluationDialog, "Du m� skrive en tilbakemelding", "Du m� skrive en tilbakemelding", 1);
                }
            }
        });
        openEvaluationDialog.pack();
        openEvaluationDialog.setVisible(true);
    }

    /**
     * Used by the addEvaluationDialog() method for storing an evaluation in the
     * DB. Calls the updating-method in dbConnector, which returns a
     * confirmation string. This is returned to addEvaluationMethod()
     *
     * @param evaluation the evaluation that should be stored in DB
     * @param i idModule of the module this delivery belongs to
     * @param evaluationStatus the enum-value of the evaluation, either GODKJENT
     * or IKKEGODKJENT
     * @param userName the user name of the student that has made this delivery
     * @return confirmation string showing result of update-statement
     */
    private String uploadEvaluationToDB(String evaluation, int i, DeliveryStatus evaluationStatus,
            String userName) {
        EJBConnector ejbConnector = EJBConnector.getInstance();
        dbConnectorRemote dbConnector = ejbConnector.getEjbRemote();
        return dbConnector.addDeliveryEvaluation(evaluation, userInfo.get("userName"),
                i, userName, evaluationStatus);

    }

    /**
     * Adds window for adding a delivery to a module (for a student-user) When
     * clicking button for choosing file, default OS file explorer opens and
     * user needs to select which file to upload. Only supports uploading one
     * file. When user clicks button for uploading delivery, the file and user
     * information is stored as a new row in the database table Delivery. Show
     * user a confirmation dialog. If stored successfully, window is closed.
     *
     * @param i which module this delivery belongs to
     */
    private void addDeliveryDialog(int i) {
        GUIFileUploader fileUploader = new GUIFileUploader();

        JDialog addDeliveryDialog = new JDialog(frame, "Last opp innlevering");//, true);
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
                    JOptionPane.showMessageDialog(addDeliveryDialog, deliveryFile.getText(), "Ingen fil valgt", 1);
                } else {
                    String userName = userInfo.get("userName");
                    String confirmationString = fileUploader.uploadDelivery(userName, i);
                    JOptionPane.showMessageDialog(addDeliveryDialog, confirmationString, confirmationString, 1);
                    if (confirmationString.equals("Opplastning vellykket!")) {
                        addDeliveryDialog.dispose();
                    }
                }
            }
        });
    }

    /**
     * Adds window for creating a new module, storing it in the database and
     * showing the user a confirmation dialog. If stored successfully, window is
     * closed.
     */
    public void createModul() {
        JDialog createModulDialog = new JDialog(frame, "Opprett ny modul", true);
        JPanel contentPane = (JPanel) createModulDialog.getContentPane();

        JLabel idModulLabel = new JLabel("Velg hvilken modul du �nsker � opprette:");
        Integer[] modules = {1, 2, 3, 4, 5};
        JComboBox modulesList = new JComboBox(modules);

        JLabel modulTitleLabel = new JLabel("Tittel p� modulen:");
        JTextField createModulTitle = new JTextField();

        JLabel modulDescriptionLabel = new JLabel("Beskrivelse av modulen:");
        JTextField createModulDesc = new JTextField();

        JLabel modulLearningObjLabel = new JLabel("Modulens l�ringsm�l:");
        JTextField createModulLearningObj = new JTextField();

        JLabel modulResourcesLabel = new JLabel("Ressurser for denne modulen:");
        JTextField createModulRes = new JTextField();

        JLabel modulExcerciseLabel = new JLabel("Oppgave:");
        JTextField createModulEx = new JTextField();

        JLabel modulEvaluationForm = new JLabel("Godkjenning av modulen/oppgaven:");
        JTextField createModulEval = new JTextField();

        contentPane.add(idModulLabel);
        contentPane.add(modulesList);

        contentPane.add(modulTitleLabel);
        contentPane.add(createModulTitle);

        contentPane.add(modulDescriptionLabel);
        contentPane.add(createModulDesc);

        contentPane.add(modulLearningObjLabel);
        contentPane.add(createModulLearningObj);

        contentPane.add(modulResourcesLabel);
        contentPane.add(createModulRes);

        contentPane.add(modulExcerciseLabel);
        contentPane.add(createModulEx);

        contentPane.add(modulEvaluationForm);
        contentPane.add(createModulEval);

        JButton createModulButton = new JButton("Opprett modul");
        createModulButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
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

                int i = (Integer) modulesList.getSelectedItem();
                values.add(i);
                values.add(createModulTitle.getText());
                values.add(createModulDesc.getText());
                values.add(createModulLearningObj.getText());
                values.add(createModulRes.getText());
                values.add(createModulEx.getText());
                values.add(createModulEval.getText());

                EJBConnector ejbConnector = EJBConnector.getInstance();
                dbConnectorRemote dbConnector = ejbConnector.getEjbRemote();
                String confirmationString = dbConnector.insertIntoDB(modul, columns, values);
                JOptionPane.showMessageDialog(createModulDialog, confirmationString, confirmationString, 1);
                System.out.println(confirmationString);
                if (confirmationString.equals("Opplastning vellykket!")) {
                    createModulDialog.dispose();
                }

            }
        });
        contentPane.add(createModulButton);
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
        createModulDialog.setSize(250, 250);
        createModulDialog.pack();
        createModulDialog.setVisible(true);
    }
}
