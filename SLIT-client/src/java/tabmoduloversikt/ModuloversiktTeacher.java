/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tabmoduloversikt;

import db.DBDeleterRemote;
import db.DBInserterRemote;
import db.DBQuerierRemote;
import db.DBUpdaterRemote;
import db.DBUtilRemote;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import notification.NotificationCreater;
import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.JXTaskPaneContainer;
import slitclient.FileDownloader;
import slitcommon.DeliveryStatus;

/**
 * @author Arild Høyland
 * @author Viktor Setervang
 * @author Håkon Gilje
 */
public class ModuloversiktTeacher extends TabModuloversikt {

    public ModuloversiktTeacher(HashMap<String, String> userInfo, JFrame frame) {
        super(userInfo, frame);
    }

    /**
     * Creates the tab with with collapsible modul panes. teacher-user logged
     * in, adds button for creating new modules
     *
     * @return JPanel containing the components created
     */
    @Override
    public JPanel makeModuloversiktTab() {
        tab2Panel = new JPanel();
        tab2Panel.setLayout(new BoxLayout(tab2Panel, BoxLayout.Y_AXIS));

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

        Component accordion = makeAccordion();

        tab2Panel.add(accordion);
        return tab2Panel;
    }

    /**
     * Creates the collapsible panes for each module. If student-user logged in,
     * each module header shows module name and status of current delivery for
     * this module. If teacher-user logged in, module header shows module name
     * and number of deliveries for this module divided with total number of
     * students
     *
     *
     * @return JXTaskPaneContainer the container containing all the collapsible
     * panes with module content
     */
    @Override //Get modul content
    protected JScrollPane makeModulList() {
        DBUtilRemote dbUtil = ejbConnector.getDBUtil();
        JXTaskPaneContainer modulListContainer = new JXTaskPaneContainer();
        //we add the container to a scrollPane, so we can scroll in it if necessary
        JScrollPane scrollPane = new JScrollPane(modulListContainer);
        ArrayList<LinkedHashMap> moduls = getModulContent();

        for (LinkedHashMap modul : moduls) {
            JXTaskPane modulPane;
            int idModul = Integer.parseInt(modul.get("idModul").toString());

            //check userType of the currently logged in user, so we know what
            //kind of header to make for each module
            //if user is teacher, we want to know how many deliveries there are
            //for this module, divided on the total number of students in the DB
            int numberOfDeliveries = dbUtil.countRows("*", "Delivery WHERE idModul = " + idModul);
            int numberOfStudents = dbUtil.countRows("*", "User WHERE userType = 'student'");
            //create the modul-pane header using the given data
            modulPane = new JXTaskPane("Modul " + idModul
                    + "    " + numberOfDeliveries + "/"
                    + numberOfStudents + " har levert");
            //as above, set pane to collapsed from the beginning
            modulPane.setCollapsed(true);
            //display the content for this module. with a button that
            //shows all deliveries made for this module
            addModulContentTeacher(modul, modulPane, idModul);
            modulListContainer.add(modulPane);
            idModul++;

        }
        return scrollPane;
    }

    /**
     * Adds the content for each module by looping trhough the arraylist and
     * displaying all results as labels. Adds a button for showing all
     * deliveries for this module
     *
     * @param content list of content in modules
     * @param modulPane the modulPane component labels should be added to
     * @param idModul the idModul of the current module
     */
    private void addModulContentTeacher(LinkedHashMap<String, String> modul, JXTaskPane modulPane, int idModul) {
        //for each map in the content-list,
        //call method for displaying the text content of this module
        displayModulText(modul, modulPane);
        //adds a button for opening a dialog showing list of deliveries for this module
        JButton openDeliveryListButton = new JButton("Se innleveringer");
        modulPane.add(openDeliveryListButton);
        openDeliveryListButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openDeliveryListDialog(idModul);
            }
        });
    }

    /**
     * Window for showing all deliveries for the chosen module. Gets all
     * deliveries for the current module from the DB, showing them in a
     * GirdBagLayout-grid. Each delivery has a button which downloads the
     * delivery-file and opens the evaluation window by calling
     * openEvaluationDialog(..)
     *
     * @param idModul the idModul of the selected module
     */
    private void openDeliveryListDialog(int idModul) {
        JDialog deliveryListDialog = new JDialog(frame, "Innleveringer i modul " + idModul, true);
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
        DBQuerierRemote dbQuerier = ejbConnector.getDBQuerier();

        //arraylists with columns, tables and where-conditions for this query
        ArrayList<String> columns = new ArrayList(Arrays.asList("deliveryDate", "evaluationDate", "deliveryStatus", "deliveredBy", "evaluatedBy"));
        ArrayList<String> tables = new ArrayList<>();
        ArrayList<String> where = new ArrayList<>();
        tables.add("Delivery");
        where.add("idModul = " + idModul);

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
                panel.setPreferredSize(new Dimension(140, 20));
                panel.setMinimumSize(new Dimension(140, 20));
                panel.setMaximumSize(new Dimension(140, 20));
                panel.setBorder(BorderFactory.createLineBorder(Color.black));
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
                    String userName = map.get("deliveredBy").toString();

                    FileDownloader downloader = new FileDownloader();
                    JOptionPane.showMessageDialog(deliveryListDialog,
                            "Besvarelsen ble lagret på skrivebordet",
                            downloader.downloadDeliveryFile(userName, idModul), 1);
                    openEvaluationDialog(deliveryListDialog, idModul, userName);
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
     * @param idModul the idModul-number of the module that's being evaluated
     * @param userName the userName of the teacher-user that is evaluating the
     * delivery
     */
    protected void openEvaluationDialog(JDialog deliveryListDialog, int idModul, String userName) {
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
                    String returnString = uploadEvaluationToDB(evaluation.getText(), idModul, evaluationStatusEnum, userName);
                    //displays the confirmation string in a messageDialog
                    JOptionPane.showMessageDialog(openEvaluationDialog, returnString, returnString, 1);
                    //if the uploading was successful, this dialog can be closed
                    if (returnString.equals("Lagret i database.")) {
                        openEvaluationDialog.dispose();
                        NotificationCreater nc = new NotificationCreater();
                        nc.createNewNotification(userName, "modul " + idModul + ": " + evaluationStatusEnum.toString());
                        deliveryListDialog.dispose();
                        openDeliveryListDialog(idModul);
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
     * @param idModul idModule of the module this delivery belongs to
     * @param evaluationStatus the enum-value of the evaluation, either GODKJENT
     * or IKKEGODKJENT
     * @param userName the user name of the student that has made this delivery
     * @return confirmation string showing result of update-statement
     */
    private String uploadEvaluationToDB(String evaluation, int idModul, DeliveryStatus evaluationStatus,
            String userName) {
        //call the addDeliveryEvaluation of dbConnector with the specified values 
        DBUpdaterRemote dbUpdater = ejbConnector.getDBUpdater();
        //calls the method and returns the confirmation string
        return dbUpdater.addDeliveryEvaluation(evaluation, userInfo.get("userName"),
                idModul, userName, evaluationStatus);

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
        DBQuerierRemote dbQuerier = ejbConnector.getDBQuerier();

        ArrayList<String> columns = new ArrayList<>(Arrays.asList("idModul"));
        ArrayList<String> table = new ArrayList<>(Arrays.asList("Modul"));
        ArrayList<LinkedHashMap> moduls = dbQuerier.multiQueryHash(columns, table, null);

        ArrayList<Integer> selectedModuls = new ArrayList<>();
        for (LinkedHashMap modul : moduls) {
            int idModul = Integer.parseInt(modul.get("idModul").toString());
            selectedModuls.add(idModul);
        }

        //add every number in the list to an array, which the user can choose from
        Integer[] chooseModul = selectedModuls.toArray(new Integer[selectedModuls.size()]);
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

        DBQuerierRemote dbQuerier = ejbConnector.getDBQuerier();

        //get all attributes of the module we wish to edit
        ArrayList<String> columns = new ArrayList(Arrays.asList("*"));
        ArrayList<String> table = new ArrayList(Arrays.asList("Modul"));
        ArrayList<String> where = new ArrayList(Arrays.asList("idModul = " + idModul));
        ArrayList<LinkedHashMap> content = dbQuerier.multiQueryHash(columns, table, where);

        LinkedHashMap mapContent = content.get(0);
        //create headers and labels for all required information for a module
        JLabel modulTitleLabel = new JLabel("Tittel på modulen:");
        JTextArea editModulTitle = new JTextArea(mapContent.get("title").toString());

        JLabel modulDescriptionLabel = new JLabel("Beskrivelse av modulen:");
        JTextArea editModulDesc = new JTextArea(mapContent.get("description").toString());

        JLabel modulLearningObjLabel = new JLabel("Modulens læringsmål:");
        JTextArea editModulLearningObj = new JTextArea(mapContent.get("learningObj").toString());

        JLabel modulResourcesLabel = new JLabel("Ressurser for denne modulen:");
        JTextArea editModulRes = new JTextArea(mapContent.get("resources").toString());

        JLabel modulExcerciseLabel = new JLabel("Oppgave:");
        JTextArea editModulEx = new JTextArea(mapContent.get("excercise").toString());

        JLabel modulEvaluationForm = new JLabel("Godkjenning av modulen/oppgaven:");
        JTextArea editModulEval = new JTextArea(mapContent.get("evalForm").toString());

        //add headers and labels for all required information to the dialog
        contentPane.add(modulTitleLabel);
        contentPane.add(editModulTitle);

        contentPane.add(modulDescriptionLabel);
        contentPane.add(editModulDesc);

        contentPane.add(modulLearningObjLabel);
        contentPane.add(editModulLearningObj);

        contentPane.add(modulResourcesLabel);
        contentPane.add(editModulRes);

        contentPane.add(modulExcerciseLabel);
        contentPane.add(editModulEx);

        contentPane.add(modulEvaluationForm);
        contentPane.add(editModulEval);

        //button for saving edit to DB
        JButton editModulButton = new JButton("Lagre endringer");
        contentPane.add(editModulButton);
        editModulButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ArrayList<String> listOfEdits = new ArrayList(Arrays.asList(editModulTitle.getText(),
                        editModulDesc.getText(), editModulLearningObj.getText(),
                        editModulRes.getText(), editModulEx.getText(),
                        editModulEval.getText()));
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
        if (deliveries.isEmpty()) {
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

        } else {
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
        DBDeleterRemote dbDeleter = ejbConnector.getDBDeleter();
        String confirmationString = dbDeleter.deleteModul(idModul);
        JOptionPane.showMessageDialog(frame, confirmationString, confirmationString, 1);
    }
}
