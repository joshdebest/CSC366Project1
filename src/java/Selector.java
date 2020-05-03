/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
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

    private List<String> choices = new ArrayList<>(Arrays.asList("List All Customers"));
    private DBConnect dbConnect = new DBConnect();
    private String choice;

    public String[] getChoices() {
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
        ELContext elContext = FacesContext.getCurrentInstance().getELContext();
        Login login = (Login) elContext.getELResolver().getValue(elContext, null, "login");
        
        if(login.isAdmin()){
            choices.add("Change username/password");
            choices.add("Add employee account");
            choices.add("Rebuild Room Database");
        }
        else if(login.isCustomer()){
            choices.add("Make reservtion");
        }
        
        return choice;
    }

    public void setChoice(String choice) {
        this.choice = choice;
    }

    public String transition() {
        switch (choice) {
            case "List All Customers":
                return "listCustomers";
            case "Change username/password":
                return "changeSettings";
            case "Add employee account":
                return "createEmployee";
            case "Rebuild Room Database":
                return "rebuildRooms"; 
            case "Make reservtion":
                return "makeReservation";                 
            default:
                return null;
        }
    }

}
