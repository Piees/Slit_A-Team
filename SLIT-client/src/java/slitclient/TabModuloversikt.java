/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package slitclient;

import db.DBDeleterRemote;
import db.DBInserterRemote;
import db.DBQuerierRemote;
import db.DBUpdaterRemote;
import db.DBUtilRemote;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import notification.NotificationCreater;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.JXTaskPaneContainer;
import slitcommon.DeliveryStatus;

/**
 * @author Arild Høyland
 * @author Viktor Setervang 
 * @author Håkon Gilje
 */
public class TabModuloversikt {

    private HashMap<String, String> userInfo;
    private JFrame frame;
    private JPanel tab2Panel;
    ArrayList<Integer> modulesArrayList = new ArrayList(Arrays.asList(1, 2, 3, 4, 5));

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

        if (userInfo.get("userType").equals("teacher")) {
            JPanel buttonsPanel = new JPanel();
            JButton createModulButton = new JButton("Opprett modul");
            createModulButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    createModul();
                }
            });
            buttonsPanel.add(createModulButton);
            JButton editModulButton = new JButton("Endre modul");
            editModulButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    //ask the user to choose an existing module to edit, and save 
                    //the user's choice
                    Integer i = selectModul("Velg modulen du vil endre:", "Endre modul");
                    //if there is a number, create the editModulDialog for this modul
                    //if i is null, then the user cancelled/closed the choose-existing-module-dialog
                    if (i != null) {
                        editModul(i);
                    }
                }
            });
            buttonsPanel.add(editModulButton);

            //button for deleting a modul. Follows same principles as editModulButton
            JButton deleteModulButton = new JButton("Slett modul");
            deleteModulButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Integer i = selectModul("Velg modulen du vil slette:", "Slett modul");
                    if (i != null) {
                        deleteModul(i);
                    }
                }
            });
            buttonsPanel.add(deleteModulButton);
            tab2Panel.add(buttonsPanel);
        }
        Component accordion = makeAccordion();
        accordion.setPreferredSize(new Dimension(700, 900));
        accordion.setMaximumSize(new Dimension(700, 900));
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
    public Component makeAccordion() {
        JXPanel panel = new JXPanel();
        panel.setLayout(new BorderLayout());
        //check number of modules in the DB
        EJBConnector ejbConnector = EJBConnector.getInstance();
        DBUtilRemote dbUtil = ejbConnector.getDBUtil();
        int numberOfModuls = dbUtil.countRows("*", "Modul");
        //creates a list witht the given number of modules
        panel.add(makeModulList(numberOfModuls));
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
    private JXTaskPaneContainer makeModulList(int numberOfModuls) {
        JXTaskPaneContainer modulListContainer = new JXTaskPaneContainer();
        int i = 1;
        while (i <= numberOfModuls) {
            JXTaskPane modulPane;

            //create the arraylists for this query
            ArrayList<String> columns = new ArrayList(Arrays.asList("idModul",
                    "title", "description", "learningObj", "resources",
                    "excercise", "evalForm"));
            ArrayList<String> tables = new ArrayList(Arrays.asList("Modul"));
            ArrayList<String> where = new ArrayList();
            where.add("idModul = " + i + ";");

            //execute the query
            EJBConnector ejbConnector = EJBConnector.getInstance();
            DBUtilRemote dbUtil = ejbConnector.getDBUtil();
            DBQuerierRemote dbQuerier = ejbConnector.getDBQuerier();
            ArrayList<LinkedHashMap> moduls = dbQuerier.multiQueryHash(columns, tables, where);

            //check userType of the currently logged in user, so we know what
            //kind of header to make for each module
            if (userInfo.get("userType").equals("student")) {
                //we need to see if student has a delivery for every module, so
                //we empty the arraylists so we can use them again
                columns.clear();
                tables.clear();
                where.clear();
                //make and execute new query, getting the status of the delivery 
                //for this student AND this module
                columns.add("deliveryStatus");
                tables.add("Delivery");
                where.add("idModul = " + i + " AND deliveredBy = '" + userInfo.get("userName") + "';");
                ArrayList<LinkedHashMap> deliveryStatus = dbQuerier.multiQueryHash(columns, tables, where);
                //if the arraylist is not empty, that means this student has 
                //delivered something for this module. Then we get the status of
                //this delivery and add it to the header of the module-pane
                String evaluationStatus = "";
                if (deliveryStatus.size() > 0) {
                    modulPane = new JXTaskPane("Modul " + moduls.get(0).get("idModul")
                            + "     " + deliveryStatus.get(0).get("deliveryStatus"));
                    evaluationStatus = deliveryStatus.get(0).get("deliveryStatus").toString();
                } //if the arraylist is empty, this student has not made a delivery 
                //for this module. We can then add "Not delivered" to the module-pane header
                else {
                    modulPane = new JXTaskPane("Modul " + moduls.get(0).get("idModul") + "    Ikke levert");
                }
                //we wish the collapsible panes to be collapsed from the beginning
                modulPane.setCollapsed(true);
                //get the number of deliveries this student has for this module
                int numberOfDeliveries = deliveryStatus.size();
                //add the content for this module, and one of three buttons 
                //depending on whether a student-user has made a delivery, or it's
                //been evaluated, or it's not seen
                addModulContentStudent(moduls, modulPane, i, numberOfDeliveries, evaluationStatus);
                modulListContainer.add(modulPane);
                i++;
            } else {
                //if user is teacher, we want to know how many deliveries there are
                //for this module, divided on the total number of students in the DB
                int numberOfDeliveries = dbUtil.countRows("*", "Delivery WHERE idModul = " + moduls.get(0).get("idModul"));
                int numberOfStudents = dbUtil.countRows("*", "User WHERE userType = 'student'");
                //create the modul-pane header using the given data
                modulPane = new JXTaskPane("Modul " + moduls.get(0).get("idModul")
                        + "    " + numberOfDeliveries + "/"
                        + numberOfStudents + " har levert");
                //as above, set pane to collapsed from the beginning
                modulPane.setCollapsed(true);
                //display the content for this module. with a button that
                //shows all deliveries made for this module
                addModulContentTeacher(moduls, modulPane, i);
                modulListContainer.add(modulPane);
                i++;
            }
        }
        return modulListContainer;
    }

    /**
     * Adds the content for each module by looping through the arraylist and
     * displaying all results as labels. adds a button for adding deliveries to
     * each module
     *
     * @param content list of content in modules
     * @param modulPane the modulPane component labels should be added to
     * @param i the idModul of the current module
     */
    private void addModulContentStudent(ArrayList<LinkedHashMap> content,
            JXTaskPane modulPane, int i, int numberOfDeliveries, String evaluationStatus) {
        //for each HashMap(containing a module) in the content-list, 
        for (LinkedHashMap map : content) {
            //call method for displaying the text content of this module
            displayModulText(map, modulPane);
            //if the user has made a delivery, make a button for downloading this 
            //and reading the evaluation of the delivery
            if (numberOfDeliveries > 0) {
                // READ EVALUATION BUTTON
                JButton readEvaluationButton = new JButton("Les tilbakemelding");
                modulPane.add(readEvaluationButton);
                readEvaluationButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        readEvaluationDialog(frame, i, userInfo.get("userName"));
                    }
                });
                //delete delivery button, only if status of delivery is "not seen"
                if (evaluationStatus.equals("IKKESETT")) {
                    JButton deleteModuleButton = new JButton("Slett innlevering");
                    modulPane.add(deleteModuleButton);
                    deleteModuleButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            //array with options for confirmation dialog
                            Object[] options = {"Ja", "Nei"};
                            //asks user to confirm action
                            int answer = JOptionPane.showOptionDialog(frame, ""
                                    + "Ønsker du å slette innleveringen?",
                                    "Bekreft sletting", JOptionPane.YES_NO_OPTION,
                                    JOptionPane.QUESTION_MESSAGE, null, options, options[1]);
                            //if user chooses yes
                            if (answer == JOptionPane.YES_OPTION) {
                                deleteDelivery(i);
                            }
                        }
                    });
                }
            }//if user has not made a delivery, make a button for uploading a delivery 
            else {
                // UPLOAD DELIVERY BUTTON
                JButton uploadDeliveryButton = new JButton("Last opp besvarelse");
                modulPane.add(uploadDeliveryButton);
                uploadDeliveryButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        addDeliveryDialog(i);
                    }
                });
            }
        }
    }

    /**
     * Creates a dialog for student-users where they can download their
     * delivered file and read the evaluation for this delivery
     *
     * @param frame parent component of this dialog
     * @param i the idModul-number, identifying which modul this delivery
     * belongs to
     * @param userName the userName of the currently logged in user
     */
    private void readEvaluationDialog(JFrame frame, int i, String userName) {
        JDialog readEvaluationDialog = new JDialog(frame, "Les tilbakemelding", true);
        JPanel contentPane = (JPanel) readEvaluationDialog.getContentPane();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));

        //button for downloading the delivered file
        JButton downloadDeliveryButton = new JButton("Last ned din besvarelse");
        contentPane.add(downloadDeliveryButton);
        downloadDeliveryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FileDownloader downloader = new FileDownloader();
                //the method for downloading a file from the DB to the user's system
                //is called. The confirmation string is shown in a messageDialog
                JOptionPane.showMessageDialog(readEvaluationDialog, downloader.downloadDeliveryFile(userName, i),
                        downloader.downloadDeliveryFile(userName, i), 1);
            }
        });

        //creates all the headers and the lines with the evaluation details for this delivery
        JLabel deliveryDateLabelHeader = new JLabel("<html><b>Besvarelse levert:</b></html>");
        contentPane.add(deliveryDateLabelHeader);
        JLabel deliveryDateLabel = new JLabel();
        contentPane.add(deliveryDateLabel);
        JLabel evaluatedByLabelHeader = new JLabel("<html><b>Vurdert av:</b></html>");
        contentPane.add(evaluatedByLabelHeader);
        JLabel evaluatedByLabel = new JLabel();
        contentPane.add(evaluatedByLabel);
        JLabel evaluatedDateLabelHeader = new JLabel("<html><b>Vurdert:</b></html>");
        contentPane.add(evaluatedDateLabelHeader);
        JLabel evaluatedDateLabel = new JLabel();
        contentPane.add(evaluatedDateLabel);
        JLabel deliveryStatusLabelHeader = new JLabel("<html><b>Vurdering:</b></html>");
        contentPane.add(deliveryStatusLabelHeader);
        JLabel deliveryStatusLabel = new JLabel();
        contentPane.add(deliveryStatusLabel);
        JLabel evaluationCommentLabelHeader = new JLabel("<html><b>Kommentarer:</b></html>");
        contentPane.add(evaluationCommentLabelHeader);
        JLabel evaluationCommentLabel = new JLabel();
        contentPane.add(evaluationCommentLabel);

        //get the evaluation data for this delivery from the DB
        EJBConnector ejbConnector = EJBConnector.getInstance();
        DBQuerierRemote dbQuerier = ejbConnector.getDBQuerier();


        //arraylists with the columns, tables and where-condition for this query
        ArrayList<String> columns = new ArrayList(Arrays.asList("deliveryDate",
                "evaluatedBy", "evaluationDate", "deliveryStatus", "evaluation"));
        ArrayList<String> table = new ArrayList(Arrays.asList("Delivery"));
        ArrayList<String> where = new ArrayList(Arrays.asList("deliveredBy = '"
                + userName + "' AND idModul = " + i));

        //execute the query, get the results in a list of hashmaps
        ArrayList<LinkedHashMap> deliveryList = dbQuerier.multiQueryHash(columns, table, where);
        //this list will only have one entry, so we can safely get the first one
        LinkedHashMap<String, String> deliveryMap = deliveryList.get(0);

        //set all the empty labels to the corresponding value from the hashmap
        //with the values gotten from the DB. If an evaluation is not made, the 
        //values from the DB will be null, and the labels in the GUI will not be shown
        deliveryDateLabel.setText(deliveryMap.get("deliveryDate"));
        evaluatedByLabel.setText(deliveryMap.get("evaluatedBy"));
        evaluatedDateLabel.setText(deliveryMap.get("evaluationDate"));
        deliveryStatusLabel.setText(deliveryMap.get("deliveryStatus"));
        evaluationCommentLabel.setText(deliveryMap.get("evaluation"));

        readEvaluationDialog.pack();
        readEvaluationDialog.setVisible(true);
    }

    /**
     * Deletes a delivery from the DB, using the given idModul and the userName
     * of the currently logged in user
     *
     * @param idModul idModul of the chosen module
     */
    private void deleteDelivery(int idModul) {
        EJBConnector ejbConnector = EJBConnector.getInstance();
        DBDeleterRemote dbDeleter = ejbConnector.getDBDeleter();
        String confirmationString = dbDeleter.deleteDelivery(idModul, userInfo.get("userName"));
        JOptionPane.showMessageDialog(frame, confirmationString, confirmationString, 1);
    }

    private void displayModulText(LinkedHashMap map, JXTaskPane modulPane) {
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
     * Adds the content for each module by looping trhough the arraylist and
     * displaying all results as labels. Adds a button for showing all
     * deliveries for this module
     *
     * @param content list of content in modules
     * @param modulPane the modulPane component labels should be added to
     * @param i the idModul of the current module
     */
    private void addModulContentTeacher(ArrayList<LinkedHashMap> content, JXTaskPane modulPane, int i) {
        //for each map in the content-list,
        for (LinkedHashMap map : content) {
            //call method for displaying the text content of this module
            displayModulText(map, modulPane);
            //adds a button for opening a dialog showing list of deliveries for this module
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
     * Window for showing all deliveries for the chosen module. Gets all
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
        //create the GridBagLayout this dialog will use
        contentPane.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        //create a row with headings for each column, positiong them on the GridBagLayout
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

        //get all deliveries for the current module from the DB
        EJBConnector ejbConnector = EJBConnector.getInstance();
        DBQuerierRemote dbQuerier = ejbConnector.getDBQuerier();

        //arraylists with columns, tables and where-conditions for this query
        ArrayList<String> columns = new ArrayList(Arrays.asList("deliveryDate", "evaluationDate", "deliveryStatus", "deliveredBy", "evaluatedBy"));
        ArrayList<String> tables = new ArrayList<>();
        ArrayList<String> where = new ArrayList<>();
        tables.add("Delivery");
        where.add("idModul = " + i);

        //execute the query, storing result in a list of HashMaps
        ArrayList<LinkedHashMap> deliveryList = dbQuerier.multiQueryHash(columns, tables, where);
        //this counts the current row in the GridBagLayout
        int gridBagYCounter = 1;
        //for each map in the list of deliveries
        for (HashMap map : deliveryList) {
            //this counts the current column (in the current row) in the GridBagLayout
            int gridBagXCounter = 0;
            //for each value in the map, display this in a label
            for (Object value : map.values()) {
                JLabel label;
                if (value != null) {
                    label = new JLabel(value.toString());
                } //if the label is null, display an empty string so we can see where
                //the value would've been if not null
                else {
                    label = new JLabel("");
                }
                //create a panel, with BG color so we can see its borders clearly
                JPanel panel = new JPanel();
                panel.setBackground(Color.WHITE);
                panel.add(label);
                //add the label to the panel
                gbc.fill = GridBagConstraints.VERTICAL;
                gbc.gridx = gridBagXCounter;
                gbc.gridy = gridBagYCounter;
                //add this panel to the GridBagLayout with the specified constraints
                contentPane.add(panel, gbc);
                //increase the column counter, so the next panel will be in the next column
                gridBagXCounter++;
            }
            //at the end of each row, we add a button for opening the dialog where
            //a teacher can give an evaluation. This also downloads the delivery automatically
            JButton openFileButton = new JButton("Gi tilbakemelding");
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
            //place the button in the GridBagLayout with the specfied constraints
            contentPane.add(openFileButton, gbc);
            //increase the GridBagLayout row-counter, so the next elements will be on the next row
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
     * @param userName the userName of the teacher-user that is evaluating the
     * delivery
     */
    private void openEvaluationDialog(JDialog deliveryListDialog, int i, String userName) {
        JDialog openEvaluationDialog = new JDialog(deliveryListDialog, "Gi tilbakemelding", true);
        JPanel contentPane = (JPanel) openEvaluationDialog.getContentPane();

        //set the layout of this dialog to BoxLayout
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));

        contentPane.add(new JLabel("Din tilbakemelding:"));
        JTextField evaluation = new JTextField();

        //create array with the evaluation result (enums)
        DeliveryStatus[] evaluationStatus = {DeliveryStatus.GODKJENT, DeliveryStatus.IKKEGODKJENT};
        //create a drop-down-list with the given array
        JComboBox evaluationStatusList = new JComboBox(evaluationStatus);

        contentPane.add(evaluationStatusList);
        contentPane.add(evaluation);
        //button for uploading this evaluation to the DB
        JButton uploadEvaluationButton = new JButton("Lagre tilbakemelding");
        contentPane.add(uploadEvaluationButton);
        uploadEvaluationButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //get the selected option in the drop-down-list
                DeliveryStatus evaluationStatusEnum = (DeliveryStatus) evaluationStatusList.getSelectedItem();
                //checks that an evaluation comment is written
                if (evaluation.getText().length() > 2) {
                    //calls the method for uploading to DB, storing the confirmation string
                    String returnString = uploadEvaluationToDB(evaluation.getText(), i, evaluationStatusEnum, userName);
                    //displays the confirmation string in a messageDialog
                    JOptionPane.showMessageDialog(openEvaluationDialog, returnString, returnString, 1);
                    //if the uploading was successful, this dialog can be closed
                    if (returnString.equals("Lagret i database.")) {
                        openEvaluationDialog.dispose();
                        NotificationCreater nc = new NotificationCreater();
                        nc.createNewNotification(userName, "modul " + i + ": " + evaluationStatusEnum.toString());
                        deliveryListDialog.dispose();
                        openDeliveryListDialog(i);
                    }

                } //if comment was left empty, inform user that comment is too short
                else {
                    JOptionPane.showMessageDialog(openEvaluationDialog,
                            "Du må skrive en tilbakemelding", "Skriv tilbakemelding", 1);
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
        //call the addDeliveryEvaluation of dbConnector with the specified values 
        EJBConnector ejbConnector = EJBConnector.getInstance();
        DBUpdaterRemote dbUpdater = ejbConnector.getDBUpdater();
        //calls the method and returns the confirmation string
        return dbUpdater.addDeliveryEvaluation(evaluation, userInfo.get("userName"),
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
        //the object responsible for prompting user for file and uploading it to DB
        GUIFileUploader fileUploader = new GUIFileUploader();

        JDialog addDeliveryDialog = new JDialog(frame, "Last opp innlevering");//, true);
        addDeliveryDialog.setLayout(new GridLayout(0, 1));
        JPanel contentPane = (JPanel) addDeliveryDialog.getContentPane();

        JLabel deliveryFile = new JLabel("Ingen fil valgt");

        //button for choosing a file from the users system
        JButton chooseFileButton = new JButton("Velg fil");
        //button for uploading the delivery to the DB
        JButton uploadDeliveryButton = new JButton("Last opp innlevering");

        contentPane.add(deliveryFile);
        contentPane.add(chooseFileButton);
        contentPane.add(uploadDeliveryButton);

        addDeliveryDialog.pack();
        addDeliveryDialog.setVisible(true);

        chooseFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //calls the method which prompts user for file to be uploaded
                //(using OS file browser) and caches it
                //sets label to the name of the chosen file, which is returned from fileUploader
                deliveryFile.setText(fileUploader.startFileExplorer(frame));
            }
        });

        uploadDeliveryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (deliveryFile.getText().equals("Ingen fil valgt")) {
                    //if no file is chosen, inform user
                    JOptionPane.showMessageDialog(addDeliveryDialog, deliveryFile.getText(), "Ingen fil valgt", 1);
                } else {
                    //get the name of the currently logged in user
                    String userName = userInfo.get("userName");
                    //call the method for uploading a file to the database and 
                    //store the confirmation string
                    String confirmationString = fileUploader.uploadDelivery(userName, i);
                    //show the confirmation string
                    JOptionPane.showMessageDialog(addDeliveryDialog, confirmationString, confirmationString, 1);
                    //if confirmation string equals "Upload successful", close this dialog
                    if (confirmationString.equals("Opplastning vellykket!")) {
                        addDeliveryDialog.dispose();
                        NotificationCreater nc = new NotificationCreater();
                        nc.notificationToUserType("teacher", userInfo.get("fName") + " " + userInfo.get("lName") + " levert modul " + i);
                    }
                }
            }
        });
    }

    /**
     * Creates window for creating a new module, storing it in the database and
     * showing the user a confirmation message. If stored successfully, window
     * is closed.
     */
    private void createModul() {
        JDialog createModulDialog = new JDialog(frame, "Opprett ny modul", true);
        JPanel contentPane = (JPanel) createModulDialog.getContentPane();

        //create header for drop-down-list of which modul to create
        JLabel idModulLabel = new JLabel("Velg hvilken modul du ønsker å opprette:");
        //create array of idModuls which specifies which module to create
        Integer[] modules = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15};
        //create drop-down-list of given array
        JComboBox modulesList = new JComboBox(modules);

        //create headers and labels for all required information for a module
        JLabel modulTitleLabel = new JLabel("Tittel på modulen:");
        JTextField createModulTitle = new JTextField();

        JLabel modulDescriptionLabel = new JLabel("Beskrivelse av modulen:");
        JTextField createModulDesc = new JTextField();

        JLabel modulLearningObjLabel = new JLabel("Modulens læringsmål:");
        JTextField createModulLearningObj = new JTextField();

        JLabel modulResourcesLabel = new JLabel("Ressurser for denne modulen:");
        JTextField createModulRes = new JTextField();

        JLabel modulExcerciseLabel = new JLabel("Oppgave:");
        JTextField createModulEx = new JTextField();

        JLabel modulEvaluationForm = new JLabel("Godkjenning av modulen/oppgaven:");
        JTextField createModulEval = new JTextField();

        //add headers and labels for all required information to the dialog
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

        //button for uploading a modul to the DB
        JButton createModulButton = new JButton("Opprett modul");
        createModulButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //we need to check that the chosen idModul does not already exist
                EJBConnector ejbConnector = EJBConnector.getInstance();
                DBUtilRemote dbUtil = ejbConnector.getDBUtil();
                DBInserterRemote dbInserter = ejbConnector.getDBInserter();
                //get the number the user chose in the drop-down-list
                int i = (Integer) modulesList.getSelectedItem();

                //count rows in the Modul-table in the DB with the idModul 
                //atribute equalling the users choice
                int numberOfModuls = dbUtil.countRows("*", "Modul WHERE idModul = " + i);
                //if there is no row with this idModul, this means the modul does not exist
                if (numberOfModuls == 0) {
                    //create insert-statement for DB, with the given columns, table and values
                    ArrayList<String> columns = new ArrayList(Arrays.asList("idModul",
                            "title", "description", "learningObj", "resources",
                            "excercise", "evalForm"));
                    String modul = "Modul";
                    ArrayList<Object> values = new ArrayList();

                    //get all values from the textAreas and add them to the values-list
                    values.add(i);
                    values.add(createModulTitle.getText());
                    values.add(createModulDesc.getText());
                    values.add(createModulLearningObj.getText());
                    values.add(createModulRes.getText());
                    values.add(createModulEx.getText());
                    values.add(createModulEval.getText());

                    //call the method for inserting into the specified table in the DB
                    //call method and store the returned confirmation string
                    String confirmationString = dbInserter.insertIntoDB(modul, columns, values);
                    //show confirmation string in messageDialog
                    JOptionPane.showMessageDialog(createModulDialog, confirmationString, confirmationString, 1);
                    //if confirmation string equals "Upload successful", close this dialog.
                    if (confirmationString.equals("Opplastning vellykket!")) {
                        createModulDialog.dispose();
                    }
                    //if there is a row with this idModul, this module already exists. 
                    //we tell user to use the editModulButton instead
                } else {
                    JOptionPane.showMessageDialog(createModulDialog, "Denne modulen "
                            + "finnes allerede. Bruk knappen \n \"Endre modul\" for å "
                            + "endre den.", "Modul finnes allerede", 1);
                }
            }
        });
        //add button to this dialog
        contentPane.add(createModulButton);
        //set the layout of this dialog to BoxLayout
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
//        createModulDialog.setSize(250, 250);
        createModulDialog.pack();
        createModulDialog.setVisible(true);
    }

    /**
     * This method shows a list of all modules stored in the DB. User chooses
     * one module, and this number is returned
     *
     * @return the idModul of the module that was chosen from the list
     */
    private Integer selectModul(String message, String dialogTitle) {
        //count all rows in the DB table Modul
        EJBConnector ejbConnector = EJBConnector.getInstance();
        DBUtilRemote dbUtil = ejbConnector.getDBUtil();
        int numberOfModuls = dbUtil.countRows("*", "Modul");
        //make a list containing same amount of numbers as rows in the DB-table
        ArrayList<Integer> moduls = new ArrayList<>();
        int i = 1;
        while (i <= numberOfModuls) {
            moduls.add(i);
            i++;
        }
        //add every number in the list to an array, which the user can choose from
        Integer[] chooseModul = moduls.toArray(new Integer[moduls.size()]);

        Integer chosenModul = (Integer) JOptionPane.showInputDialog(frame,
                message, dialogTitle, JOptionPane.PLAIN_MESSAGE,
                null, chooseModul, 1);
        //return the chosen number. If user cancels inputDialog, returns null
        return chosenModul;
    }

    /**
     * Edits a module in the DB. Gets the idModul of the module to be edited,
     * and creates a dialog displaying all of this Moduls content (text). User
     * can edit this text, and click the "Save changes" button to save the
     * changes to the DB.
     *
     * @param idModul the id of the module to be displayed and edited
     */
    private void editModul(int idModul) {
        JDialog editModulDialog = new JDialog(frame, "Endre modul", true);
        JPanel contentPane = (JPanel) editModulDialog.getContentPane();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));

        EJBConnector ejbConnector = EJBConnector.getInstance();
        DBQuerierRemote dbQuerier = ejbConnector.getDBQuerier();

        //get all attributes of the module we wish to edit
        ArrayList<String> columns = new ArrayList(Arrays.asList("*"));
        ArrayList<String> table = new ArrayList(Arrays.asList("Modul"));
        ArrayList<String> where = new ArrayList(Arrays.asList("idModul = " + idModul));
        ArrayList<LinkedHashMap> content = dbQuerier.multiQueryHash(columns, table, where);

        //make a map storing a textarea for each attribute of the modul
        ArrayList<JTextArea> listOfEdits = new ArrayList();
        //for each map (can only be one), loop through all values
        for (LinkedHashMap map : content) {
            //for each value in map, do the following:
            for (Object value : map.values()) {
                //check that its length is longer than 1 (to exclude idModul)
                if (value.toString().length() > 1) {
                    //create a new textArea with the given value
                    JTextArea textArea = new JTextArea(value.toString());
                    //make this textArea editable
                    textArea.setEditable(true);
                    //make this textArea break lines at whole words, so a word isn't split
                    textArea.setWrapStyleWord(true);
                    //add this textArea to the contentPane
                    contentPane.add(textArea);
                    //and to the arrayList of textAreas the user can edit
                    listOfEdits.add(textArea);
                }
            }
        }

        //button for saving edit to DB
        JButton editModulButton = new JButton("Lagre endringer");
        contentPane.add(editModulButton);
        editModulButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //save edits to DB, and store confirmation string
                DBUpdaterRemote dbUpdater = ejbConnector.getDBUpdater();
                String confirmationString = dbUpdater.updateModul(listOfEdits, idModul);
                //display confirmation string to user
                JOptionPane.showMessageDialog(editModulDialog, confirmationString,
                        confirmationString, 1);
                //if edit was saved successfully to DB, close the editModulDialog
                if (confirmationString.equals("Modul ble endret.")) {
                    editModulDialog.dispose();
                }

            }
        });
        editModulDialog.pack();
        editModulDialog.setVisible(true);

    }

    /**
     * Shows a confirmation message, asking user to confirm choice to delete the
     * modul with this idModul
     *
     * @param idModul id of the modul to be deleted
     */
    private void deleteModul(int idModul) {
        EJBConnector ejbConnector = EJBConnector.getInstance();
        DBUtilRemote dbUtil = ejbConnector.getDBUtil();
        DBQuerierRemote dbQuerier = ejbConnector.getDBQuerier();  
        ArrayList<String> columns = new ArrayList(Arrays.asList("title"));
        ArrayList<String> table = new ArrayList(Arrays.asList("Modul"));
        ArrayList<String> where = new ArrayList(Arrays.asList("idModul = " + idModul));

        ArrayList<LinkedHashMap> result = dbQuerier.multiQueryHash(columns, table, where);
        //check how many moduls there are
        //we need to delete the last modul, otherwise the creation of GUI will not
        //work the next time the system is launched
        int numberOfModuls = dbUtil.countRows("*", "Modul");
        //we also need to check if the modul to be deleted has any deliveries
        //if it does, we cannot delete it 
        columns.clear();
        columns.add("*");
        table.clear();
        table.add("Delivery");
        
        ArrayList<LinkedHashMap> deliveries = dbQuerier.multiQueryHash(columns, table, where);
        if (idModul == numberOfModuls && deliveries.isEmpty()) {
            //get the title of the modul, so we can show it to the user
            String title = result.get(0).get("title").toString();
            //the choices the user can pick
            Object[] options = {"Ja", "Nei"};
            //show a message, asking user to confirm wish to delete this modul
            //we print the id and the title of the modul
            int answer = JOptionPane.showOptionDialog(frame, "Ønsker du å slette modul:"
                    + " \"Modul " + idModul + ": " + title + "\"?", "Bekreft sletting",
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[1]);
            if (answer == JOptionPane.YES_OPTION) {
                deleteModulInDB(idModul);
            }
        } else if (idModul != numberOfModuls) {
            JOptionPane.showMessageDialog(frame, "Du må slette den siste modulen",
                    "Ugyldig valg", 1);
        }
        else    {
            JOptionPane.showMessageDialog(frame, "Du kan ikke slette en modul som har innleveringer",
                    "Ugyldig valg", 1);
        }
    }

    /**
     * Deletes a modul from the DB, using the given idModul Shows a confirmation
     * string telling user whether operation was successful
     *
     * @param idModul the id of the modul to be deleted
     */
    private void deleteModulInDB(int idModul) {
        EJBConnector ejbConnector = EJBConnector.getInstance();
        DBDeleterRemote dbDeleter = ejbConnector.getDBDeleter();
        String confirmationString = dbDeleter.deleteModul(idModul);
        JOptionPane.showMessageDialog(frame, confirmationString, confirmationString, 1);
    }
}
