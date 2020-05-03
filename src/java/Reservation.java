
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
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
    private Calendar start_date = null;
    private Calendar end_date = null;
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
    private String bedChoice;
    private String viewChoice;
    private String[] bedChoices = {"single king", "double queen"};
    private String[] viewChoices = {"ocean", "pool"};
    
    private DBConnect dbConnect = new DBConnect();

    public String getBedChoice() {
        return bedChoice;
    }

    public void setBedChoice(String bedChoice) {
        this.bedChoice = bedChoice;
    }

    public String getViewChoice() {
        return viewChoice;
    }

    public void setViewChoice(String viewChocie) {
        this.viewChoice = viewChocie;
    }

    public String[] getBedChoices() {
        return bedChoices;
    }

    public void setBedChoices(String[] bedChoices) {
        this.bedChoices = bedChoices;
    }

    public String[] getViewChoices() {
        return viewChoices;
    }

    public void setViewChoices(String[] viewChoices) {
        this.viewChoices = viewChoices;
    }

    public String getChoice() {
        return choice;
    }

    public void setChoice(String choice) {
        this.choice = choice;
    }

    public String[] getChoices() throws SQLException {
        choices = new ArrayList<>();
        Calendar temp_start;
        Calendar temp_end;
        String temp_room_num;

        ResultSet temp_results;
        Connection con = dbConnect.getConnection();
        
        if (con == null) {
            throw new SQLException("Can't get database connection");
        }
        PreparedStatement ps = con.prepareStatement(
                "SELECT room_num, view, bed_type "
              + "From room "
              + "WHERE view = ? AND bed_type = ?");
        ps.setString(1, viewChoice);
        ps.setString(2, bedChoice);
        
        ResultSet roomResults = ps.executeQuery();
        
        while (roomResults.next()) {
            temp_room_num = roomResults.getString("room_num");
            
            ps = con.prepareStatement("SELECT start_date, end_date FROM reservation WHERE room_number = ? AND start_date BETWEEN ? AND ?");
            temp_end = (Calendar) end_date.clone();
            temp_end.add(Calendar.DATE, -1);
            ps.setString(1, temp_room_num);
            ps.setDate(2, new Date(start_date.getTime().getTime()));
            ps.setDate(3, new Date(temp_end.getTime().getTime()));
            temp_results = ps.executeQuery();
            
            if(!(temp_results.next())){
                ps = con.prepareStatement("SELECT start_date, end_date FROM reservation WHERE room_number = ? AND end_date BETWEEN ? AND ?");
                temp_start = (Calendar) start_date.clone();
                temp_start.add(Calendar.DATE, +1);
                ps.setString(1, temp_room_num);
                ps.setDate(2, new Date(temp_start.getTime().getTime()));
                ps.setDate(3, new Date(end_date.getTime().getTime()));
                temp_results = ps.executeQuery();

                if(!(temp_results.next())){
                    ps = con.prepareStatement("SELECT start_date, end_date FROM reservation WHERE room_number = ? AND ? BETWEEN start_date AND end_date");
                    temp_start = (Calendar) start_date.clone();
                    temp_start.add(Calendar.DATE, +1);
                    ps.setString(1, temp_room_num);
                    temp_end = (Calendar) end_date.clone();
                    temp_end.add(Calendar.DATE, -1);
                    ps.setDate(2, new Date(temp_start.getTime().getTime()));
                    temp_results = ps.executeQuery();
                    if(!(temp_results.next())){
                        choices.add(temp_room_num);
                    }
                }
            }   
        }
        roomResults.close();
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
        Calendar today = Calendar.getInstance();
        System.out.println("THIS RIGHT HERE");
        System.out.println(today.toString());
        System.out.println(today.YEAR);
        System.out.println(today.MONTH);
        System.out.println(today.DATE);
        start_date = Calendar.getInstance();
        start_date.set(start_year, start_month - 1, start_day);
        
        if(start_date.compareTo(today) < 0){
            start_date = null;
            startDateErrorMessage = "Date is in the past.";
            FacesMessage errorMessage = new FacesMessage(startDateErrorMessage);
            throw new ValidatorException(errorMessage);
        }
        else{
            System.out.println("THIS IS DOPE");
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
        Calendar today = Calendar.getInstance();
        end_date = Calendar.getInstance();
        end_date.set(end_year, end_month - 1, end_day);
        
        if(start_date == null){
            endDateErrorMessage = "Must have valid start date first.";
            FacesMessage errorMessage = new FacesMessage(endDateErrorMessage);
            throw new ValidatorException(errorMessage);
        }
        
        if(end_date.compareTo(start_date) <= 0){
            endDateErrorMessage = "End date is not after start date.";
            FacesMessage errorMessage = new FacesMessage(endDateErrorMessage);
            throw new ValidatorException(errorMessage);
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
        ps.setDate(1, new Date(start_date.getTime().getTime()));
        ps.setDate(2, new Date(end_date.getTime().getTime()));
        ps.setString(3, choice.substring(0, 3));
        ps.setInt(4, login.getId());
        
        ps.executeUpdate();
        
        con.close();
        return "success";
    }    
}
