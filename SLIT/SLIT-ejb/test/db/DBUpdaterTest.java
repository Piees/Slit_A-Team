/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package db;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import javax.ejb.embeddable.EJBContainer;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import slitcommon.DeliveryStatus;

/**
 *
 * @author Arild
 */
public class DBUpdaterTest {
    
    public DBUpdaterTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
//
//    /**
//     * Test of dbConnection method, of class DBUpdater.
//     */
//    @Test
//    public void testDbConnection() throws Exception {
//        System.out.println("dbConnection");
//        EJBContainer container = javax.ejb.embeddable.EJBContainer.createEJBContainer();
//        DBUpdaterRemote instance = (DBUpdaterRemote)container.getContext().lookup("java:global/classes/DBUpdater");
//        Connection result = instance.dbConnection();
//        assert result != null;
//        container.close();
//    }

    /**
     * Test of addDeliveryEvaluation method, of class DBUpdater.
     */
    @Test
    public void testAddDeliveryEvaluation() throws Exception {
        System.out.println("addDeliveryEvaluation");
        String evaluationValue = "Bra..";
        String evaluatedByValue = "even";
        int whereValue1 = 1;
        String whereValue2 = "arildh14";
        DeliveryStatus evaluationStatus = DeliveryStatus.GODKJENT;
        EJBContainer container = javax.ejb.embeddable.EJBContainer.createEJBContainer();
        DBUpdaterRemote instance = (DBUpdaterRemote)container.getContext().lookup("java:global/classes/DBUpdater");
        String expResult = "Lagret i database.";
        String result = instance.addDeliveryEvaluation(evaluationValue, evaluatedByValue, whereValue1, whereValue2, evaluationStatus);
        assertEquals(expResult, result);
        container.close();
    }

   

    /**
     * Test of updateModul method, of class DBUpdater.
     */
    @Test
    public void testUpdateModul() throws Exception {
        System.out.println("updateModul");
        ArrayList<String> listOfEdits = new ArrayList(Arrays.asList("Hei"));
        int idModul = 1;
        EJBContainer container = javax.ejb.embeddable.EJBContainer.createEJBContainer();
        DBUpdaterRemote instance = (DBUpdaterRemote)container.getContext().lookup("java:global/classes/DBUpdater");
        String expResult = "Modul ble endret.";
        String result = instance.updateModul(listOfEdits, idModul);
        assertEquals(expResult, result);
        container.close();
    }

}
