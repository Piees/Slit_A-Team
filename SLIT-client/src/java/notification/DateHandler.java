/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package notification;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 * This class handles data conversions related to Date and Timestamps.
 * 
 * 
 * @author Viktor Setervang
 */
public class DateHandler {
    
    /**
     * Checks if the time provided by the user is in a valid format.
     * 
     * @param time the string to be checked
     * @return a string explaining the validity of the time format.
     */
    public String checkTimeFormat(String time) {
        if (time.length() != 5) {
            // Needs to be a return that mark it as faulty input
            return "Klokkeslett skal være i format: tt:mm";
        }
        String[] time1 = time.split(":");
        
        int hour;
        int minute;
        try {
            hour = Integer.parseInt(time1[0]);
            minute = Integer.parseInt(time1[1]);
        }  catch (NumberFormatException ex) {

            return "Klokkeslett skal være i format: tt:mm";
        }
        if (hour >=0 && hour < 24 && minute >= 0 && minute < 60 ) {
            return "correct input";
        }
        else {
            return "Klokkeslett må være mellom: 00:00 - 23:59";
        }
                    
    }
    
    /**
     * Gets the current timestamp.
     * 
     * @return timestamp of current time.
     */
    public Timestamp getCurrentTimestamp() {
        // 1) create a java calendar instance
        Calendar calendar = Calendar.getInstance();
        // 2) get a java.util.Date from the calendar instance.
        //    this date will represent the current instant, or "now".
        Date now = calendar.getTime();
        // 3) a java current time (now) instance
        Timestamp currentTimestamp = new java.sql.Timestamp(now.getTime());
        return currentTimestamp;
    }
 
    /**
     * Parses a Timestamp to Date using the SimpleDateFormat class.
     * 
     * @param stamp to be parsed
     * @return a Date parsed from the stamp
     * @throws ParseException if the stamp cannot be parsed
     */
    public Date timestampToDate(Timestamp stamp) throws ParseException {
        SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss");
        Date date = SDF.parse(stamp.toString());
        return date;
    }
    
    /**
     * This method removes fractional seconds from timestamps, use this to give 
     * cleaner timestamp output in GUI.
     */
    public String removeFractionalSeconds(String timestamp) {
        return timestamp.substring(0, timestamp.length() -5);
    }
    
    /**
     * Splits a Date value into its respective pieces. Converts the pieces into
     * a format that fits with Timestamp and puts them in a HashMap.
     * 
     * @param value the date to be split 
     * @return HashMap containing the relevant date data. 
     * HashMap keys: "day", "month", "year" and "dow"
     * @throws ParseException 
     */
    public HashMap<String, String> splitDateIntoHashMap(Object value) throws ParseException {
        if (value != null) {
            Date date = (Date) value;
            HashMap dateFormat = parseToDateFormat(date);
            return dateFormat;
        }
        return null;
    }
    
    /**
     * Splits the date into its respective pieces.
     * Parses the month name into a number and puts the relevant date data 
     * into a HashMap. 
     * 
     * @param date value to be parsed
     * @return HashMap containing the relevant date data, keys: "day", "month", 
     * "year" and "dow" (day of week).
     */
    private HashMap<String, String> parseToDateFormat(Date date) {
        String datePreparsed = date.toString();
        // day of week
        String dow = datePreparsed.substring(0, 3);
        String removedDow = datePreparsed.substring(4);
        String month = removedDow.split(" ")[0];
        month = monthNameToNumber(month);
        String removedMon = removedDow.substring(4);
        String day = removedMon.substring(0, 2);
        String year = "";
        if (removedMon.length() == 20) {
            year = removedMon.substring(16);
        }
        else if (removedMon.length() == 16) {
            year = removedMon.substring(12);
        }
        else {
            year = "year: invalid date length";
        } 
        HashMap<String, String> dateMap = new HashMap<>();
        dateMap.put("day", day);
        dateMap.put("month", month);
        dateMap.put("year", year);
        dateMap.put("dow", dow);
        
        return dateMap;
    }
    
    /**
     * Converts the respective month name into a number.
     * 
     * @param month to be converted to a number
     * @return number representation of the month
     */
    private String monthNameToNumber(String month) {
        String monthNumber = "";
        switch (month) {
        case "Jan":
             monthNumber = "1";
             break;
        case "Feb":
             monthNumber = "2";
             break;
        case "Mar":
             monthNumber = "3";
             break;
        case "Apr":
             monthNumber = "4";
             break;
        case "May":
             monthNumber = "5";
             break;
        case "Jun":
             monthNumber = "6";
             break;
        case "Jul":
             monthNumber = "7";
             break;
        case "Aug":
             monthNumber = "8";
             break;  
        case "Sep":
             monthNumber = "9";
             break;
        case "Oct":
             monthNumber = "10";
             break;
        case "Nov":
             monthNumber = "11";
             break;
        case "Dec":
             monthNumber = "12";
             break;        
        default:
            monthNumber = "invalid month";         
        }
        return monthNumber;
    }
}
