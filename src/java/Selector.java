/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.el.ELContext;
import javax.inject.Named;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

/**
 *
 * @author stanchev
 */
@Named(value = "selector")
@ManagedBean
@SessionScoped
public class Selector implements Serializable {

    private List<String> choices;
    private DBConnect dbConnect = new DBConnect();
    private String choice;

    public String[] getChoices() {
        ELContext elContext = FacesContext.getCurrentInstance().getELContext();
        Login login = (Login) elContext.getELResolver().getValue(elContext, null, "login");
        choices = new ArrayList<>();
        
        if(login.isEmployee()){
            choices.add("Add Charges");
            choices.add("Check In a Customer");
            choices.add("Check Out a Customer");
            choices.add("Check Reservations");
            choices.add("Change username/password");
            choices.add("Create Customer");
            choices.add("Make reservation");
            choices.add("Make reservation");
            choices.add("View Room Prices");
        }
        if(login.isAdmin()){
            choices.add("Add employee account");
            choices.add("Change username/password");
            choices.add("Change Room Prices");
            choices.add("Delete employee account.");
            choices.add("View Room Prices");
        }
        if(login.isCustomer()){
            choices.add("Make reservation");
            choices.add("Check Reservation");
        }
        
        String[] temp = new String[choices.size()];
        List<String> condenser = choices;
        for(int i = 0; i < condenser.size(); i++){
            temp[i] = condenser.get(i);
        }
        return temp;
    }

    public void setChoices(String[] choices) {
        List<String> temp = new ArrayList<>();
        for(int i = 0; i < choices.length; i++){
            temp.add(choices[i]);
        }
        this.choices = temp;
    }

    public String getChoice() throws SQLException {
        return choice;
    }

    public void setChoice(String choice) {
        this.choice = choice;
    }

    public String transition() {
        ELContext elContext = FacesContext.getCurrentInstance().getELContext();
        Login login = (Login) elContext.getELResolver().getValue(elContext, null, "login");
        switch (choice) {
            case "List All Customers":
                return "listCustomers";
            case "Change username/password":
                return "changeSettings";
            case "Add employee account":
                return "createEmployee";
            case "Rebuild Room Database":
                return "rebuildRooms"; 
            case "Make reservation":
                if(login.isEmployee()){
                    return "makeReservationEmp";
                }
                return "makeReservation";                 
            case "Delete employee account.":
                return "deleteEmployee";
            case "Check Reservation":
                return "checkReservation";
            case "Create Customer":
                return "register";
            case "Check Reservations":
                return "checkReservations";
            case "Change Room Prices":
                return "setRoomPrice";
            case "Add Charges":
                return "extraCharges";
            case "Check In a Customer":
                return "checkIn";
            case "Check Out a Customer":
                return "checkOut";
            case "View Room Prices":
                return "SelectDate";
            default:
                return null;
        }
    }

}
