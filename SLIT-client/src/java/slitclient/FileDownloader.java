
package slitclient;

import tabmoduloversikt.TabModuloversikt;
import db.DBQuerierRemote;
import db.dbConnectorRemote;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * This class is for handles all the logic regarding downloading files to the
 * clients computer
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
        // Path to the user's desktop directory
        filepath = javax.swing.filechooser.FileSystemView.getFileSystemView().getHomeDirectory().getAbsolutePath();
        // Alternate filepath, path to the user's default document folder. 
        // On windows: C:\Users\Username\Documents  On linux: On mac: 
        //System.out.println(javax.swing.filechooser.FileSystemView.getFileSystemView().getDefaultDirectory());
    }
    /**
     * Downloads a file from the DB and saves it to the user's desktop-directory.
     * 
     * @param userName the user name of the user that has delivered this file.
     * @param idModul the id of the module that the file was assigned to.
     */
    public String downloadDeliveryFile(String userName, int idModul) {
        EJBConnector ejbConnector = EJBConnector.getInstance();
        DBQuerierRemote dbQuerier = ejbConnector.getDBQuerier();
        String filename = "modul" + idModul + "_" + userName;
               
        byte[] byteData = dbQuerier.getDeliveryFile(userName, idModul);
        String oldFileName = dbQuerier.getDeliveryFilename(userName, idModul);
        
        try {
            String[] fileMetadata = getFileMetaData(oldFileName);
            String filetype = fileMetadata[1];
            String filepath = checkIfFileAlreadyExists(filename, filetype);
            FileOutputStream out = new FileOutputStream(filepath);
            out.write(byteData);
            out.close();
            return filename + " nedlasting fullført.";
        } catch (Exception ex) {
            Logger.getLogger(TabModuloversikt.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return filename + " nedlasting mislykkes.";         
    }
    
    /**
     * Writes the file content to a new file on the user's desktop directory.
     * 
     * @param fileData the content of the file
     * @param fullFilename the name of the file including its file type.
     * @return a string with the download success status
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
            return fullFilename + " nedlasting fullført.";
        } catch (IOException ex) {
            Logger.getLogger(TabModuloversikt.class.getName()).log(Level.SEVERE, null, ex);
        }   
        
        return fullFilename + " nedlasting mislykkes.";
    }
    
    /**
     * To prevent overwriting old files this method checks if a file with the 
     * same name already exists in the in the designated filepath. 
     * If it already exist the new file will be given a unique name.
     * 
     * @param filename the name of the file to check for
     * @param filetype the filetype of the file
     * @return a unique path name
     */
     private String checkIfFileAlreadyExists(String filename, String filetype) {
        boolean uniqueFile = false;
        String path = filepath + "/" + filename + "." + filetype;
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
     * @param filename to be split
     * @return array containing filename and filetype (MIME-type).
     */
    private String[] getFileMetaData(String filename) {
        return filename.split(Pattern.quote("."));       
    }
}
