
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author aberard
 */
public class Validation {
    
    private static DBConnect dbConnect = new DBConnect();
    
    
    public static String validateUsername(String username) throws SQLException{
        Connection con = dbConnect.getConnection();
        
        if (con == null) {
            throw new SQLException("Can't get database connection");
        }
        PreparedStatement ps = con.prepareStatement(
                        "SElECT customer.id FROM customer WHERE customer.username = ?");
        ps.setString(1, username);
        
        ResultSet result = ps.executeQuery();

        if(result.next()) {
            result.close();
            con.close();
            return "Username is already taken.";
        }
        ps = con.prepareStatement(
                        "SELECT employee.id FROM employee WHERE employee.username = ?");
        ps.setString(1, username);
        
        result = ps.executeQuery();

        if(result.next()) {
            result.close();
            con.close();
            return "Username is already taken.";
        }
        
        if(username.replace(" ", "").length() != username.length()){
            return "No spaces allowed in usernames.";
        }
        
        return "valid";
    }
    
    public static String validateUsername(String username, int id) throws SQLException{
        Connection con = dbConnect.getConnection();
        
        if (con == null) {
            throw new SQLException("Can't get database connection");
        }
        PreparedStatement ps = con.prepareStatement(
                        "SElECT customer.id FROM customer WHERE customer.username = ? AND customer.id != ?");
        ps.setString(1, username);
        ps.setInt(2, id);
        
        ResultSet result = ps.executeQuery();

        if(result.next()) {
            result.close();
            con.close();
            return "Username is already taken.";
        }
        ps = con.prepareStatement(
                        "SELECT employee.id FROM employee WHERE employee.username = ? AND employee.id != ?");
        ps.setString(1, username);
        ps.setInt(2, id);
        
        result = ps.executeQuery();

        if(result.next()) {
            System.out.println("Here");
            System.out.println(result.getString("id"));
            result.close();
            con.close();
            return "Username is already taken.";
        }
        return "valid";
    }
    
    public static String validatePassword(String password) {
        if(password.length() < 6){
            return "Password must be six characters long,";
        }
        if(!(Validation.containsDigit(password))){
            return "Password must contain at least one number.";
        }
        return "valid";
    }
        
    public static boolean containsDigit(final String aString){
        return aString != null && !aString.isEmpty() && aString.chars().anyMatch(Character::isDigit);
    }
    
    public static Calendar getCalObj(String date_string){
        String[] parts = date_string.split("/");
        
        int month = Integer.parseInt(parts[0]);
        int day = Integer.parseInt(parts[1]);
        int year = Integer.parseInt(parts[2]);
        
        Calendar date = Calendar.getInstance();
        date.set(year, month - 1, day);
        return date;
    }

    public static String validDate(String date_string){
        int day;
        int month;
        int year;
        
        System.out.println("Validating date.");

        String[] parts = date_string.split("/");
        
        /* Checks if there are non number values other than the '/' */
        String check = date_string.replaceAll("/", "");
        if(!(check.matches("[0-9]+"))){
            return "Date is not all digits.";
        }
        
        /* Checks to make sure there is a year month and day */
        if(parts.length != 3){
            return "Incorrect date format. Must be mm/dd/yyyy";
        }
        
        /* Checks to make sure the numbers are two digits for month and day and four digits for year */
        if(parts[0].length() != 2 || parts[1].length() != 2 || parts[2].length() != 4){
            return "Incorrect date format. Must be mm/dd/yyyy";
        }
        
        /* Check if day is valid meaning non negative. Doesn't check for like Feb 31st though. */
        month = Integer.parseInt(parts[0]);
        day = Integer.parseInt(parts[1]);
        year = Integer.parseInt(parts[2]);
        
        if(month < 1 || month > 12 || day < 1 || day > 31 ){
            return "Incorrect date.";
        }
        
        /* Now make sure it is in the future or on the current day */
        Calendar today = Calendar.getInstance();
        Calendar date = Validation.getCalObj(date_string);
        
        if(date.compareTo(today) < 0){
            return "Date is in the past.";
        }
        return "valid";
    }
    
}
