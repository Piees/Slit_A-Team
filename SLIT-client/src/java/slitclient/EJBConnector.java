/*
 * THIS CLASS IS USING A NOT THREAD SAFE SINGLETON PATTERN.
 *
 * The purpose of this class is to connect to the EJB dbConnectorRemote, and provide 
 * this connection to other classes thats needs it by using a simple getMethod.
 *  
 * This way we will only need to change the adress one place if we ever need
 * to change the lookup adress.
 *
 * TO USE THIS CLASS WRITE:
 * EJBConnector ejbConnector = EJBConnector.getInstance();
 */
package slitclient;

import db.dbConnectorRemote;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 *
 * @author Viktor Setervang
 */
public class EJBConnector {
    private static dbConnectorRemote ejbRemote;
   
    // creates the only instance of EJBConnector.
    private static final EJBConnector INSTANCE = new EJBConnector();
    
    /**t
     * The constructor is private so no class other then itself can call it.
     */
    private EJBConnector() {
        System.out.println("Starting...");
        System.out.println("Establishing connection to the bean..");
        InitialContext initialContext;        
        try {
            initialContext = new InitialContext();
            ejbRemote = (dbConnectorRemote) initialContext.lookup("java:global/SLIT/SLIT-ejb/dbConnector"); 
        } catch (NamingException ex) {
            Logger.getLogger(EJBConnector.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println(ex);
            System.exit(1);
        }    
    }

    /**
     * Gets the single instance of the EJBConnector.
     * Its static so no different variations of it exist.
     * 
     * @return the instance of the CommandList
     */
    public static EJBConnector getInstance() {
        return INSTANCE;
    }
    
    /**
     * Use this method to get the enterprise java bean connection
     * 
     * @return 
     */
    public dbConnectorRemote getEjbRemote() {
        return ejbRemote;
    }
    

}


