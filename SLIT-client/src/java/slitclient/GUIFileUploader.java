/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package slitclient;

import slitcommon.DeliveryStatus;
import db.dbConnectorRemote;
import java.io.File;
import java.util.ArrayList;
import javax.swing.*;
import slitclient.EJBConnector;
 
/**
 *
 * @author Viktor Setervang
 */
public class GUIFileUploader {
    private File file;
  
    public String startFileExplorer() {
        JFrame frame = new JFrame("File Explorer");
        frame.setVisible(true);  
        return startFileExplorer(frame);
    }
    
    
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
    
    public String uploadResource(String userName, String title, 
            String resourceText, String url) {
        EJBConnector ejbConnector = EJBConnector.getInstance();
        //InserterRemote uploader = ejbConnector.getInserter();
        dbConnectorRemote uploader = ejbConnector.getEjbRemote();
        
        ArrayList<String> columns = new ArrayList<>();
        ArrayList<Object> values = new ArrayList<>();
        columns.add("userName");
        values.add(userName);
        if (file != null) {
            columns.add("resourceFile");
            values.add(file);
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
    public String uploadDelivery(String title, String userName, int idModul) {
        EJBConnector ejbConnector = EJBConnector.getInstance();
        dbConnectorRemote uploader = ejbConnector.getEjbRemote();
        String table = "Delivery";
        ArrayList<String> columns = new ArrayList<>();
        ArrayList<Object> values = new ArrayList<>();
        columns.add("title");
        values.add(title);
        columns.add("deliveryFile");
        values.add(file);
        columns.add("deliveredBy");
        values.add(userName);
        columns.add("idModul");
        values.add(idModul);
        columns.add("deliveryStatus");
        values.add(DeliveryStatus.IKKESETT);
        if(0 < uploader.countRows("*", "Delivery WHERE idModul = " + idModul + " AND deliveredBy = '" + userName +"';"))   {
            return "Du har allerede levert inn.";
        }
        else    {
            return uploader.insertIntoDB(table, columns, values);
        }
    }

  
       
}