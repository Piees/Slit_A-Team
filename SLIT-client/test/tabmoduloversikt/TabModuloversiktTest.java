/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tabmoduloversikt;

import java.awt.Component;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import org.jdesktop.swingx.JXTaskPane;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Arild
 */
public class TabModuloversiktTest {
    
    public TabModuloversiktTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }

    /**
     * Test of makeModuloversiktTab method, of class TabModuloversikt.
     */
    @Test
    public void testMakeModuloversiktTab() {
        System.out.println("makeModuloversiktTab");
        TabModuloversikt instance = null;
        JPanel expResult = null;
        JPanel result = instance.makeModuloversiktTab();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of makeAccordion method, of class TabModuloversikt.
     */
    @Test
    public void testMakeAccordion() {
        System.out.println("makeAccordion");
        TabModuloversikt instance = null;
        Component expResult = null;
        Component result = instance.makeAccordion();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of makeModulList method, of class TabModuloversikt.
     */
    @Test
    public void testMakeModulList() {
        System.out.println("makeModulList");
        int numberOfModuls = 0;
        TabModuloversikt instance = null;
        JScrollPane expResult = null;
        JScrollPane result = instance.makeModulList(numberOfModuls);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of displayModulText method, of class TabModuloversikt.
     */
    @Test
    public void testDisplayModulText() {
        System.out.println("displayModulText");
        LinkedHashMap map = null;
        JXTaskPane modulPane = null;
        TabModuloversikt instance = null;
        instance.displayModulText(map, modulPane);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getModulContent method, of class TabModuloversikt.
     */
    @Test
    public void testGetModulContent() {
        System.out.println("getModulContent");
        TabModuloversikt instance = null;
        ArrayList<LinkedHashMap> expResult = null;
        ArrayList<LinkedHashMap> result = instance.getModulContent();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    public class TabModuloversiktImpl extends TabModuloversikt {

        public TabModuloversiktImpl() {
            super(null, null);
        }

        public JScrollPane makeModulList(int numberOfModuls) {
            return null;
        }
    }
    
}
