/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package slitclient;

import db.DBInserterRemote;
import db.DBUtilRemote;
import slitcommon.DeliveryStatus;
import java.io.File;
import java.util.ArrayList;
import javax.swing.*;
 
/**
 * This class handles all the logic of the file uploading from SLIT-client to
 * the SLIT server application. 
 * 
 * @author Viktor Setervang
 */
public class FileUploader {
    private File file;
  
    /**
     * starts the file explorer GUI. Where the user can select a file to upload
     * 
     * @return the name of the file selected by the user
     */
    public String startFileExplorer() {
        JFrame frame = new JFrame("File Explorer");
        frame.setVisible(true);  
        return startFileExplorer(frame);
    }
   
    /**
     * starts the file explorer GUI. Where the user can select a file to upload
     * 
     * @param frame the frame used by the file explorer GUI.
     * @return the name of the file selected by the user
     */
    public String startFileExplorer(JFrame frame) {
        System.out.println("start file explorer started");
        final JFileChooser fileDialog = new JFileChooser();
        int returnVal = fileDialog.showOpenDialog(frame);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            file = fileDialog.getSelectedFile();
            return file.getName();
        }
        else{
            return ("Ingen fil valgt");
        }
    }
    
    /**
     * Uploads a resource that will be displayed in TabFagstoff.
     * If the user has provided a file using the method startFileExplorer this
     * will also be uploaded to the server.
     * 
     * @param userName the userName of the uploader
     * @param title the title of the resource
     * @param resourceText the text content of the resource
     * @param url to a beneficial web page.
     * @return a string regarding the upload success, 
     * if successful: "Opplastning vellykket!",  
     * if not successful: "Opplastning feilet!"
     */
    public String uploadResource(String userName, String title, 
            String resourceText, String url) {
        EJBConnector ejbConnector = EJBConnector.getInstance();
        //InserterRemote uploader = ejbConnector.getInserter();
        DBInserterRemote uploader = ejbConnector.getDBInserter();
        
        ArrayList<String> columns = new ArrayList<>();
        ArrayList<Object> values = new ArrayList<>();
        columns.add("userName");
        values.add(userName);
        if (file != null) {
            columns.add("resourceFile");
            values.add(file);
            columns.add("fileName");
            values.add(file.getName());
            // remove after test.
            System.out.println(file.getName());
        }
        
        if (title != null) {
            columns.add("title");
            values.add(title);        
        }
            
        if (resourceText != null) {
            columns.add("resourceText");
            values.add(resourceText);        
        }
          
        if (url != null) {
            columns.add("url");
            values.add(url);        
        }
        return uploader.insertIntoDB("Resources", columns, values);

    }
    
    /**
     * Uploads a module assignment.
     * 
     * @param userName of the student delivering the assignment
     * @param idModul the id (PK) of the module whose the assignment belongs  
     * @return a string regarding the upload success, 
     * if successful: "Opplastning vellykket!",  
     * if not successful: "Opplastning feilet!"
     */
    public String uploadDelivery(String userName, int idModul) {
        EJBConnector ejbConnector = EJBConnector.getInstance();
        DBUtilRemote dbUtil = ejbConnector.getDBUtil();
        DBInserterRemote uploader = ejbConnector.getDBInserter();
        String table = "Delivery";
        ArrayList<String> columns = new ArrayList<>();
        ArrayList<Object> values = new ArrayList<>();
        columns.add("deliveryFile");
        values.add(file);
        columns.add("fileName");
        values.add(file.getName());
        // remove after test.
        //System.out.println(file.getName());
        columns.add("deliveredBy");
        values.add(userName);
        columns.add("idModul");
        values.add(idModul);
        columns.add("deliveryStatus");
        values.add(DeliveryStatus.IKKESETT);
        if(0 < dbUtil.countRows("*", "Delivery WHERE idModul = " + idModul + " AND deliveredBy = '" + userName +"';"))   {
            return "Du har allerede levert inn.";
        }
        else    {
            return uploader.insertIntoDB(table, columns, values);
        }
    }

  
       
}