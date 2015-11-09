
package slitclient;

import db.dbConnectorRemote;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 *
 * @author Viktor Setervang
 */
public class FileDownloader {
    EJBConnector ejbConnector;
    dbConnectorRemote dbConnector;
    String filepath;
    
    public FileDownloader() {
        EJBConnector ejbConnector = EJBConnector.getInstance();
        dbConnectorRemote dbConnector = ejbConnector.getEjbRemote(); 
        filepath = javax.swing.filechooser.FileSystemView.getFileSystemView().getHomeDirectory().getAbsolutePath();
        // Alternate filepath
        //System.out.println(javax.swing.filechooser.FileSystemView.getFileSystemView().getDefaultDirectory());
    }
    
    /**
     * The name of this class is a bit misleading, it doesnt download the file,
     * but works more like a file writer.
     * 
     * @param fileData
     * @param filename
     * @return 
     */
    public String downloadResourceFile(byte[] fileData, String fullFilename) {
        try {
            String[] fileMetadata = getFileMetaData(fullFilename);
            String filename = fileMetadata[0];
            String filetype = fileMetadata[1];
            String filepath = checkIfFileAlreadyExists(filename, filetype);
            FileOutputStream out = new FileOutputStream(filepath);
            out.write(fileData);
            out.close();
            return fullFilename + " successfully downloaded";
        } catch (IOException ex) {
            Logger.getLogger(TabModuloversikt.class.getName()).log(Level.SEVERE, null, ex);
        }   
        
        return "Downloading " + fullFilename + " failed";
    }   
    
    /**
     * Downloads a file from the DB and saves it to desktop-directory of users system
     * 
     * @param userName the user name of the user that has delivered this file
     * @param idModul the id of the modul we're downloading the delivered file for
     */
    public void downloadDeliveryFile(String userName, int idModul) {
        // Hente fra database : dbConnector.getFileFromDelivery();
        // Enten åpne filutforsker for å velge mappe hvor det skal lagres, eller 
        // automatisk lagre i default download mappe.
        EJBConnector ejbConnector = EJBConnector.getInstance();
        dbConnectorRemote dbConnector = ejbConnector.getEjbRemote();
        
        String filename = "modul" + idModul + "_" + userName;
               
        byte[] byteData = dbConnector.getDeliveryFile(userName, idModul);
        String oldFileName = dbConnector.getDeliveryFilename(userName, idModul);
        
        try {
            String[] fileMetadata = getFileMetaData(oldFileName);
            String filetype = fileMetadata[1];
            String filepath = checkIfFileAlreadyExists(filename, filetype);
            FileOutputStream out = new FileOutputStream(filepath);
            out.write(byteData);
            out.close();
        } catch (Exception ex) {
            Logger.getLogger(TabModuloversikt.class.getName()).log(Level.SEVERE, null, ex);
        }
                
    }
    
    /**
     * This method checks if a file with the same name already exists in the 
     * in the designated filepath. If it already exist the new file will be 
     * given a unique name.
     */
    private String checkIfFileAlreadyExists(String filename, String filetype) {
        boolean uniqueFile = false;
        String path = filepath;
        String newFilename = filename;
        int i = 1;
        while (!uniqueFile) {
            if (new File(path).exists()) {
                newFilename = filename + "(" + i + ")";
                path = filepath + "/" + newFilename + "." + filetype;
                i++;
            }
            else {
                uniqueFile = true;
            }
        }
        return path;
    }
    
    /**
     * Splits a filename into filename and filetype
     * 
     * return index 0 is filename, index 1 is filetype
     */
    private String[] getFileMetaData(String filename) {
        return filename.split(Pattern.quote("."));       
    }
}
