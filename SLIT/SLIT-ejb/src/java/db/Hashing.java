/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */




package db;

    import java.security.MessageDigest;
    import java.security.NoSuchAlgorithmException;
    import java.security.SecureRandom;
    import java.sql.Connection;
    import java.sql.SQLException;
    import java.sql.Statement;

/**
 *
 * @author Peter
 */

public class Hashing {
        
    
   // @Override
    public boolean addNewUser() throws SQLException, NoSuchAlgorithmException {
        String userType = "Student";
        String mail = "peter@hagane.no";
        String fName = "test";
        String lName = "peter_test";
        String userName = "fornavn+etternavn+int";
        String pwd = "passord1";
        String salt = getSalt();
        
        String securePassword = getSecurePassword(pwd, salt);
        
        
        Statement stmt = null;
        try {
           // Connection dbConnection = dbConnection();
            //stmt = dbConnection.createStatement();

            String insertUserSQL = "INSERT INTO USER (userType, mail, fName, lName, userName, pwd, salt) VALUES "
                    + "('" + userType + "','" + mail + "','" + fName + "','" + lName + "','" + userName + "','" + securePassword + "','" + salt + "');";
            System.err.println("insertUser: " + insertUserSQL);
            stmt.executeUpdate(insertUserSQL);

            System.out.println("User inserted.");
        } catch (SQLException e) {
            System.out.println("User not inserted. Try again!");
            return false;
        } finally {
            stmt.close();
        }
        return true;
    }
    
    public static void Hashing() throws NoSuchAlgorithmException {
    
    String passwordToHash = "password";
    String salt = getSalt();
    
    String securePassword = getSecurePassword(passwordToHash, salt);
    System.out.println(securePassword);
    
    }
    
    
    
    
   private static String getSecurePassword(String passwordToHash, String salt){
       String generatedPassword = null;
        try {
            MessageDigest hashValue = MessageDigest.getInstance("SHA-512");
            hashValue.update(salt.getBytes());
            byte[] bytes = hashValue.digest(passwordToHash.getBytes());
            StringBuilder sb = new StringBuilder();
            for(int i=0; i< bytes.length ;i++)
            {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            generatedPassword = sb.toString();
        }
        catch (NoSuchAlgorithmException e)
        {
            System.out.println(e);
        }
    return generatedPassword;
}
    
    
    //leggt til salt
    private static String getSalt() throws NoSuchAlgorithmException{
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        byte[] salt = new byte[8];
        sr.nextBytes(salt);
        return salt.toString();
    }
    
    
}
