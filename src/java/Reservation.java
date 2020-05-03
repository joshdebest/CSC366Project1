
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import javax.annotation.ManagedBean;
import javax.el.ELContext;
import javax.faces.application.FacesMessage;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.inject.Named;


/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author stanchev
 */
@Named(value = "reservation")
@SessionScoped
@ManagedBean
public class Reservation implements Serializable {

    private String start_date_string;
    private String end_date_string;
    private Date start_date = null;
    private Date end_date = null;
    private int start_day;
    private int start_month;
    private int start_year;
    private int end_day;
    private int end_month;
    private int end_year;
    private String startDateErrorMessage = "";
    private String endDateErrorMessage = "";
    private String room_num;
    private String choice;
    private List<String> choices;
    
    private DBConnect dbConnect = new DBConnect();

    public String getChoice() {
        return choice;
    }

    public void setChoice(String choice) {
        this.choice = choice;
    }

    public String[] getChoices() throws SQLException {
        System.out.println("HERE 1");
        choices = new ArrayList<>();
        String niceStr;
        System.out.println("HERE 1");
        Connection con = dbConnect.getConnection();
        
        if (con == null) {
            throw new SQLException("Can't get database connection");
        }
        System.out.println("HERE 1");
        PreparedStatement ps = con.prepareStatement("select room_num, view, bed_type from room");
        
        ResultSet result = ps.executeQuery();
        System.out.println("HERE 1");
        while (result.next()) {
            niceStr = result.getString("room_num") + " -- " + result.getString("view")
                    + " view -- " + result.getString("bed_type");
            choices.add(niceStr);
        }
        result.close(); 
        con.close();
        System.out.println("HERE 1");
        String[] temp = new String[choices.size()];
        List<String> condenser = choices;
        for(int i = 0; i < condenser.size(); i++){
            temp[i] = condenser.get(i);
        }
        System.out.println("HERE 1");
        return temp;
    }

    public String getRoom_num() {
        return room_num;
    }

    public void setRoom_num(String room_num) {
        this.room_num = room_num;
    }
    
    public String getEndDateErrorMessage() {
        return endDateErrorMessage;
    }

    public void setEndDateErrorMessage(String endDateErrorMessage) {
        this.endDateErrorMessage = endDateErrorMessage;
    }

    public String getStart_date_string() {
        return start_date_string;
    }
    
    public String getStartDateErrorMessage(){
        return startDateErrorMessage;
    }

    public void setStart_date_string(String start_date_string) {
        this.start_date_string = start_date_string;
    }

    public String getEnd_date_string() {
        return end_date_string;
    }

    public void setEnd_date_string(String end_date_string) {
        this.end_date_string = end_date_string;
    }
    
    private static boolean containsDigit(final String aString){
        return aString != null && !aString.isEmpty() && aString.chars().anyMatch(Character::isDigit);
    }
    
    public void validateStartDate(FacesContext context, UIComponent component, Object value)
            throws ValidatorException, SQLException {
        start_date_string = value.toString();
        
        String[] parts = start_date_string.split("/");
        
        // Checks if there are non number values
        String check = start_date_string.replaceAll("/", "");
        if(!(check.matches("[0-9]+"))){
            start_date = null;
            startDateErrorMessage = "Date is not all digits.";
            FacesMessage errorMessage = new FacesMessage(startDateErrorMessage);
            throw new ValidatorException(errorMessage);
        }
        
        if(parts.length != 3){
            start_date = null;
            startDateErrorMessage = "Incorrect date format.";
            FacesMessage errorMessage = new FacesMessage(startDateErrorMessage);
            throw new ValidatorException(errorMessage);
        }
        
        if(parts[0].length() != 2 || parts[1].length() != 2 || parts[2].length() != 4){
            start_date = null;
            startDateErrorMessage = "Incorrect date format.";
            FacesMessage errorMessage = new FacesMessage(startDateErrorMessage);
            throw new ValidatorException(errorMessage);
        }
        
        // Check if day is valid
        start_month = Integer.parseInt(parts[0]);
        start_day = Integer.parseInt(parts[1]);
        start_year = Integer.parseInt(parts[2]);
        
        if(start_month < 1 || start_month > 12 || start_day < 1 || start_day > 31 ){
            start_date = null;
            startDateErrorMessage = "Incorrect date.";
            FacesMessage errorMessage = new FacesMessage(startDateErrorMessage);
            throw new ValidatorException(errorMessage);
        }
        
        //Now make sure it is in the future.
        java.util.Date today = Calendar.getInstance().getTime();
        start_date = new Date(start_year, start_month, start_day);
        
        if(start_year < today.getYear()){
            start_date = null;
            startDateErrorMessage = "Date is in the past.";
            FacesMessage errorMessage = new FacesMessage(startDateErrorMessage);
            throw new ValidatorException(errorMessage);
        }
        else if(start_year == today.getYear()){
            if(start_month < today.getMonth()){
                start_date = null;
                startDateErrorMessage = "Date is in the past.";
                FacesMessage errorMessage = new FacesMessage(startDateErrorMessage);
                throw new ValidatorException(errorMessage);
            }
            else if(start_month == today.getMonth()){
                if(start_day < today.getDay()){
                    start_date = null;
                    startDateErrorMessage = "Date is in the past.";
                    FacesMessage errorMessage = new FacesMessage(startDateErrorMessage);
                    throw new ValidatorException(errorMessage);
                }
            }
        }  
    }
    
    public void validateEndDate(FacesContext context, UIComponent component, Object value)
            throws ValidatorException, SQLException {
        end_date_string = value.toString();
        
        String[] parts = end_date_string.split("/");
        
        // Checks if there are non number values
        String check = end_date_string.replaceAll("/", "");
        if(!(check.matches("[0-9]+"))){
            endDateErrorMessage = "Date is not all digits.";
            FacesMessage errorMessage = new FacesMessage(endDateErrorMessage);
            throw new ValidatorException(errorMessage);
        }
        
        if(parts.length != 3){
            endDateErrorMessage = "Incorrect date format.";
            FacesMessage errorMessage = new FacesMessage(endDateErrorMessage);
            throw new ValidatorException(errorMessage);
        }
        
        if(parts[0].length() != 2 || parts[1].length() != 2 || parts[2].length() != 4){
            endDateErrorMessage = "Incorrect date format.";
            FacesMessage errorMessage = new FacesMessage(endDateErrorMessage);
            throw new ValidatorException(errorMessage);
        }
        
        // Check if day is valid
        end_month = Integer.parseInt(parts[0]);
        end_day = Integer.parseInt(parts[1]);
        end_year = Integer.parseInt(parts[2]);
        
        if(end_month < 1 || end_month > 12 || end_day < 1 || end_day > 31 ){
            endDateErrorMessage = "Incorrect date.";
            FacesMessage errorMessage = new FacesMessage(endDateErrorMessage);
            throw new ValidatorException(errorMessage);
        }
        
        //Now make sure it is in the future.
        java.util.Date today = Calendar.getInstance().getTime();
        end_date = new Date(end_year, end_month, end_day);
        
        if(start_date == null){
            return;
        }
        
        if(end_date.compareTo(start_date) <= 0){
            endDateErrorMessage = "End date is not after start date.";
            FacesMessage errorMessage = new FacesMessage(endDateErrorMessage);
            throw new ValidatorException(errorMessage);
        }
        else{
            System.out.println("I KNEW IT PART 2");
        }
    }   
    
    public String go() throws ValidatorException, SQLException {
        return "success";
    }

    public String createRes() throws ValidatorException, SQLException {
        ELContext elContext = FacesContext.getCurrentInstance().getELContext();
        Login login = (Login) elContext.getELResolver().getValue(elContext, null, "login");
        
        Connection con = dbConnect.getConnection();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }

        PreparedStatement ps
                = con.prepareStatement(
                        "INSERT INTO reservation VALUES (?, ?, ?, ?)");
        ps.setDate(1, start_date);
        ps.setDate(2, end_date);
        ps.setString(3, choice.substring(0, 3));
        ps.setInt(4, login.getId());
        
        ps.executeUpdate();
        
        con.close();
        return "success";
    }    
}
