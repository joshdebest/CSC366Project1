
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
    private String startDateErrorMessage = "";
    private String endDateErrorMessage = "";
    private String room_num;
    private String choice;
    private List<String> choices;
    private String bedChoice;
    private String viewChoice;
    private String[] bedChoices = {"single king", "double queen"};
    private String[] viewChoices = {"ocean", "pool"};
    private Integer resID;
    private Integer customerID = 0;
    private String resUsername = "";
    
    private DBConnect dbConnect = new DBConnect();

    public String getBedChoice() {return bedChoice;}
    public void setBedChoice(String bedChoice) {this.bedChoice = bedChoice;}
    public String getViewChoice() {return viewChoice;}
    public void setViewChoice(String viewChocie) {this.viewChoice = viewChocie;}
    public String[] getBedChoices() {return bedChoices;}
    public void setBedChoices(String[] bedChoices) {this.bedChoices = bedChoices;}
    public String[] getViewChoices() {return viewChoices;}
    public void setViewChoices(String[] viewChoices) {this.viewChoices = viewChoices;}
    public String getChoice() {return choice;}
    public void setChoice(String choice) {this.choice = choice;}
    public String getRoom_num() {return room_num;}
    public void setRoom_num(String room_num) {this.room_num = room_num;}
    public String getEndDateErrorMessage() {return endDateErrorMessage;}
    public void setEndDateErrorMessage(String endDateErrorMessage) {this.endDateErrorMessage = endDateErrorMessage;}
    public String getStart_date_string() {return start_date_string;}
    public String getStartDateErrorMessage(){return startDateErrorMessage;}
    public void setStart_date_string(String start_date_string) {this.start_date_string = start_date_string;}
    public String getEnd_date_string() {return end_date_string;}
    public void setEnd_date_string(String end_date_string) {this.end_date_string = end_date_string;}
    public Integer getCustomerID(){return this.customerID;}
    public void setCustomerID(Integer customerID){this.customerID = customerID;}
    public Integer getResID(){return this.resID;}
    public void setResID(Integer resID){this.resID = resID;}
    public String getResUsername() {return resUsername;}
    public void setResUsername(String resUsername) {this.resUsername = resUsername;}
    
    /* The choices returned are simply the room numbers that have the correct view and bed type
       that are available on the dates chosen. */
    @SuppressWarnings("empty-statement")
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
        
        /* First we get all the rooms that have the correct bed and view */
        PreparedStatement ps = con.prepareStatement(
                "SELECT room_num, view, bed_type FROM room WHERE view = ? AND bed_type = ?");
        ps.setString(1, viewChoice);
        ps.setString(2, bedChoice);
        
        ResultSet roomResults = ps.executeQuery();
        
        /* For each room it see's if it is available for the selected dates */
        while (roomResults.next()) {
            temp_room_num = roomResults.getString("room_num");
            
            /* This query finds out if any reservations coincide with the selected dates */
            ps = con.prepareStatement("SELECT start_date, end_date FROM reservation "
                    + "WHERE room_number = ? AND "
                    + "((start_date BETWEEN ? AND ?) OR "
                    + "(end_date BETWEEN ? AND ?) OR "
                    + "(? BETWEEN start_date AND end_date + INTERVAL '-1 day'))");
            
            /* Lots of changing around dates and stuff just to make it work with the 
               inclusice BETWEEN */
            temp_end = (Calendar) end_date.clone();
            temp_end.add(Calendar.DATE, -1);
            ps.setString(1, temp_room_num);
            ps.setDate(2, new Date(start_date.getTime().getTime()));
            ps.setDate(3, new Date(temp_end.getTime().getTime()));
            temp_start = (Calendar) start_date.clone();
            temp_start.add(Calendar.DATE, +1);
            ps.setDate(4, new Date(temp_start.getTime().getTime()));
            ps.setDate(5, new Date(end_date.getTime().getTime()));
            ps.setDate(6, new Date(start_date.getTime().getTime()));
            temp_results = ps.executeQuery();
            
            /* If there is a result then that room isn't free the entire time needed 
               so if there is no result then the room is free and we add it to the
               list of choices returned */
            if(!(temp_results.next())){
                choices.add(temp_room_num);
            }   
            temp_results.close();
        }
        
        roomResults.close();
        con.close();
        
        if(choices.isEmpty()){
            String[] returnable = {"No available rooms. Click go to go back to date selection."};
            return returnable;
        }
        
        /* Here we just turn the array list into a String[] so it can be returned properly */
        String[] temp = new String[choices.size()];
        List<String> condenser = choices;
        for(int i = 0; i < condenser.size(); i++){
            temp[i] = condenser.get(i);
        }
        return temp;
    }
    
    public void validateStartDate(FacesContext context, UIComponent component, Object value)
            throws ValidatorException, SQLException {
        start_date_string = value.toString();
        
        String returnable = Validation.validDate(start_date_string);
        
        if(returnable.equals("valid")){
            start_date = Validation.getCalObj(start_date_string);
        }
        else{
            startDateErrorMessage = returnable;
            FacesMessage errorMessage = new FacesMessage(startDateErrorMessage);
            throw new ValidatorException(errorMessage);
        }
    }
    
    public void validateEndDate(FacesContext context, UIComponent component, Object value)
            throws ValidatorException, SQLException {
        end_date_string = value.toString();
        
        String returnable = Validation.validDate(end_date_string);
        
        if(!(returnable.equals("valid"))){
            endDateErrorMessage = returnable;
            FacesMessage errorMessage = new FacesMessage(endDateErrorMessage);
            throw new ValidatorException(errorMessage);
        }
        
        end_date = Validation.getCalObj(end_date_string);
        
        /* Extra checks to make sure the end date isn't the same as the start date */
        if(start_date == null){
            end_date = null;
            endDateErrorMessage = "Must have valid start date first.";
            FacesMessage errorMessage = new FacesMessage(endDateErrorMessage);
            throw new ValidatorException(errorMessage);
        }
        
        if(end_date.compareTo(start_date) <= 0){
            end_date = null;
            endDateErrorMessage = "End date is not after start date.";
            FacesMessage errorMessage = new FacesMessage(endDateErrorMessage);
            throw new ValidatorException(errorMessage);
        }
    }   
    
    public String go() throws ValidatorException, SQLException {
        if(resUsername != ""){
            customerID = Customer.userNameToId(resUsername);
        }
        return "success";
    }

    public String createRes() throws ValidatorException, SQLException {
        if(choices.isEmpty()){
            return "no rooms";
        }
        
        ELContext elContext = FacesContext.getCurrentInstance().getELContext();
        Login login = (Login) elContext.getELResolver().getValue(elContext, null, "login");
        
        Connection con = dbConnect.getConnection();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }
        if(customerID == 0){
            customerID = login.getId();
        }

        PreparedStatement ps
                = con.prepareStatement(
                        "INSERT INTO reservation VALUES (?, ?, ?, ?)");
        ps.setDate(1, new Date(start_date.getTime().getTime()));
        ps.setDate(2, new Date(end_date.getTime().getTime()));
        ps.setString(3, choice.substring(0, 3));
        ps.setInt(4, customerID);
        
        ps.executeUpdate();
        
        ps = con.prepareStatement(
                        "SELECT reservation.reservation_id FROM reservation WHERE start_date = ? AND end_date = ? AND room_number = ?");
        ps.setDate(1, new Date(start_date.getTime().getTime()));
        ps.setDate(2, new Date(end_date.getTime().getTime()));
        ps.setString(3, choice.substring(0, 3));
        
        ResultSet result = ps.executeQuery();
        
        result.next();
        
        resID = result.getInt("reservation_id");
        
        con.close();
        
        start_date_string = "";
        end_date_string = "";
        start_date = null;
        end_date = null;
        startDateErrorMessage = "";
        endDateErrorMessage = "";
        room_num = null;
        bedChoice = null; 
        viewChoice = null;
        
        return "success";
    }    
        public List<Reservation> getResByID() throws ValidatorException, SQLException {
        ELContext elContext = FacesContext.getCurrentInstance().getELContext();
        Login login = (Login) elContext.getELResolver().getValue(elContext, null, "login");
        
        Connection con = dbConnect.getConnection();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }

        PreparedStatement ps
                = con.prepareStatement(
                        "SELECT * FROM reservation WHERE customer_id = ?");

        ps.setInt(1, login.getId());
        
        ResultSet result = ps.executeQuery();
        List<Reservation> list = new ArrayList<Reservation>();
        while(result.next()){
            Reservation res = new Reservation();
            res.setCustomerID(result.getInt("customer_id"));
            res.setResID(result.getInt("reservation_id"));
            res.setRoom_num(result.getString("room_number"));
            res.setStart_date_string(result.getString("start_date"));
            res.setEnd_date_string(result.getString("end_date"));
            list.add(res);
        }
        result.close();
        con.close();
        return list;
    }  
    public String cancel(Integer resID) throws ValidatorException, SQLException{
                    Connection con = dbConnect.getConnection();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }

        PreparedStatement ps
                = con.prepareStatement(
                        "DELETE FROM reservation WHERE reservation_id = ?");
        ps.setInt(1,resID);
        //get customer data from database
        Integer row = ps.executeUpdate();
        con.close();
        return "success";
    }
    public List<Reservation> getResList() throws ValidatorException, SQLException {

        Connection con = dbConnect.getConnection();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }

        PreparedStatement ps
                = con.prepareStatement(
                        "SELECT * FROM reservation");

        ResultSet result = ps.executeQuery();
        List<Reservation> list = new ArrayList<Reservation>();
        while(result.next()){
            Reservation res = new Reservation();
            res.setCustomerID(result.getInt("customer_id"));
            res.setResID(result.getInt("reservation_id"));
            res.setRoom_num(result.getString("room_number"));
            res.setStart_date_string(result.getString("start_date"));
            res.setEnd_date_string(result.getString("end_date"));
            list.add(res);
        }
        result.close();
        con.close();
        return list;

    }  
        public List<Reservation> getResByCustID() throws ValidatorException, SQLException {
        ELContext elContext = FacesContext.getCurrentInstance().getELContext();
        Connection con = dbConnect.getConnection();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }

        PreparedStatement ps
                = con.prepareStatement(
                        "SELECT * FROM reservation WHERE customer_id = ?");

        ps.setInt(1, customerID);
        
        ResultSet result = ps.executeQuery();
        List<Reservation> list = new ArrayList<Reservation>();
        while(result.next()){
            Reservation res = new Reservation();
            res.setCustomerID(result.getInt("customer_id"));
            res.setResID(result.getInt("reservation_id"));
            res.setRoom_num(result.getString("room_number"));
            res.setStart_date_string(result.getString("start_date"));
            res.setEnd_date_string(result.getString("end_date"));
            list.add(res);
        }
        result.close();
        con.close();
        return list;
    }  

    }

