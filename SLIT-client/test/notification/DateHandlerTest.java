/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package notification;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for the DateHandler class
 * 
 * @author Viktor Setervang
 */
public class DateHandlerTest {
    
    public DateHandlerTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }

    /**
     * Test of checkTimeFormat method, of class DateHandler.
     */
    @Test
    public void testCheckTimeFormat() {
        System.out.println("checkTimeFormat");
        String time = "12:26";
        DateHandler instance = new DateHandler();
        String expResult = "correct input";
        String result = instance.checkTimeFormat(time);
        assertEquals(expResult, result);     
    }

    /**
     * Boundary Test of checkTimeFormat method, of class DateHandler.
     */
    @Test
    public void testCheckTimeFormatLowerBoundary() {
        System.out.println("testCheckTimeFormatLowerBoundary");
        String time = "00:00";
        DateHandler instance = new DateHandler();
        String expResult = "correct input";
        String result = instance.checkTimeFormat(time);
        assertEquals(expResult, result);
    }
    
    /**
     * Boundary Test of checkTimeFormat method, of class DateHandler.
     */
    @Test
    public void testCheckTimeFormatUpperBoundary() {
        System.out.println("testCheckTimeFormatUpperBoundary");
        String time = "23:59";
        DateHandler instance = new DateHandler();
        String expResult = "correct input";
        String result = instance.checkTimeFormat(time);
        assertEquals(expResult, result);
    }
    
    /**
     * Negative Test of checkTimeFormat method, of class DateHandler.
     */
    @Test
    public void testCheckTimeFormatNegativeOver() {
        System.out.println("testCheckTimeFormatNegativeOver");
        String time = "24:00";
        DateHandler instance = new DateHandler();
        String expResult = "Klokkeslett må være mellom: 00:00 - 23:59";
        String wrongResult = "correct input";
        
        String result = instance.checkTimeFormat(time);
        assertEquals(expResult, result);
        assertThat(result, is(not(wrongResult)));
    }
    
    /**
     * Negative Test of checkTimeFormat method, of class DateHandler.
     */
    @Test
    public void testCheckTimeFormatNegativeLower() {
        System.out.println("testCheckTimeFormatNegativeLower");
        String time = "-01:00";
        DateHandler instance = new DateHandler();
        String wrongResult = "correct input";
        
        String result = instance.checkTimeFormat(time);
        assertThat(result, is(not(wrongResult)));
        time = "-1:00";
        result = instance.checkTimeFormat(time);
        assertThat(result, is(not(wrongResult)));
    }
    
    /**
     * checkTimeFormat: Tests if the methods breaks if the time string provides is too short.
     */
    @Test
    public void testCheckTimeFormatToShort() {
        System.out.println("testCheckTimeFormatToShort");
        String time = "1:00";
        DateHandler instance = new DateHandler();
        String expResult = "Klokkeslett skal være i format: tt:mm";
        String result = instance.checkTimeFormat(time);
        assertEquals(expResult, result);

    }
    
    /**
     * Test of checkTimeFormat method, of class DateHandler.
     */
    @Test
    public void testCheckTimeFormatToLong() {
        System.out.println("testCheckTimeFormatToLong");
        String time = "17:131";
        DateHandler instance = new DateHandler();
        String expResult = "Klokkeslett skal være i format: tt:mm";
        String result = instance.checkTimeFormat(time);
        assertEquals(expResult, result);;
    }
    

    /**
     * Test of timestampToDate method, of class DateHandler.
     */
    @Test
    public void testTimestampToDate() throws Exception {
        System.out.println("timestampToDate");
        Timestamp stamp = new Timestamp(0);
        DateHandler instance = new DateHandler();
        String expResult = "Thu Jan 01 01:00:00 CET 1970";
        Date result = instance.timestampToDate(stamp);
        String resultAsString = result.toString();
        assertEquals(expResult, resultAsString);
    }

    /**
     * Test of removeFractionalSeconds method, of class DateHandler.
     */
    @Test
    public void testRemoveFractionalSeconds() {
        System.out.println("removeFractionalSeconds");
        String timestamp = "2015-11-24 11:11:00.0";
        DateHandler instance = new DateHandler();
        String expResult = "2015-11-24 11:11";
        String result = instance.removeFractionalSeconds(timestamp);
        assertEquals(expResult, result);
    }

    /**
     * Test of splitDateIntoHashMap method, of class DateHandler.
     */
    @Test
    public void testSplitDateIntoHashMap() throws Exception {
        System.out.println("splitDateIntoHashMap");
        Object value = new Date(0);
        DateHandler instance = new DateHandler();
        HashMap<String, String> result = instance.splitDateIntoHashMap(value);
        assertEquals("Thu", result.get("dow"));
        assertEquals("01", result.get("day"));
        assertEquals("01", result.get("month"));
        assertEquals("1970", result.get("year"));
    }
}
