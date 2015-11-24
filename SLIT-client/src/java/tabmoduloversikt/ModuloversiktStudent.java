/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tabmoduloversikt;

import db.DBDeleterRemote;
import db.DBQuerierRemote;
import db.DBUtilRemote;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import notification.NotificationCreater;
import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.JXTaskPaneContainer;
import slitclient.EJBConnector;
import slitclient.FileDownloader;
import slitclient.GUIFileUploader;

/**
 *
 * @author Viktor Setervang
 */
public class ModuloversiktStudent extends TabModuloversikt {

    public ModuloversiktStudent(HashMap<String, String> userInfo, JFrame frame) {
        super(userInfo, frame);
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
    @Override
    protected JXTaskPaneContainer makeModulList(int numberOfModuls) {
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
    protected void readEvaluationDialog(JFrame frame, int i, String userName) {
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

}
