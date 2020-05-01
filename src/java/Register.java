
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.annotation.ManagedBean;
import javax.faces.application.FacesMessage;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
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
@Named(value = "register")
@SessionScoped
@ManagedBean
public class Register implements Serializable {

    private String login;
    private String first_name;
    private String last_name;
    private String email;
    private String address;
    private String password;
    private String cc_num;
    private String cc_ccv;
    private String cc_date;
    
    private DBConnect dbConnect = new DBConnect();  
    private UIInput loginUI;

    public String getCc_date() {
        return cc_date;
    }

    public void setCc_date(String cc_date) {
        this.cc_date = cc_date;
    }

    public void setCc_num(String cc_num) {
        this.cc_num = cc_num;
    }

    public void setCc_ccv(String cc_ccv) {
        this.cc_ccv = cc_ccv;
    }

    public String getCc_num() {
        return cc_num;
    }

    public String getCc_ccv() {
        return cc_ccv;
    }
    
    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getFirst_name() {
        return first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public String getEmail() {
        return email;
    }

    public String getAddress() {
        return address;
    } 

    public UIInput getLoginUI() {
        return loginUI;
    }

    public void setLoginUI(UIInput loginUI) {
        this.loginUI = loginUI;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
    private static boolean containsDigit(final String aString){
        return aString != null && !aString.isEmpty() && aString.chars().anyMatch(Character::isDigit);
    }

    public void validatePassword(FacesContext context, UIComponent component, Object value)
            throws ValidatorException, SQLException {
        password = value.toString();
        if(password.length() < 6){
            FacesMessage errorMessage = new FacesMessage("Password not long enough");
            throw new ValidatorException(errorMessage);
        }
        if(!(containsDigit(password))){
            FacesMessage errorMessage = new FacesMessage("Password had no numbers");
            throw new ValidatorException(errorMessage);
        }
    }
    
    public void validateEmail(FacesContext context, UIComponent component, Object value)
            throws ValidatorException, SQLException {
        email = value.toString();
        if(!(email.contains("@"))){
            FacesMessage errorMessage = new FacesMessage("Email is invalid.");
            throw new ValidatorException(errorMessage);
        }
    }
    
    public void validateCCNum(FacesContext context, UIComponent component, Object value)
            throws ValidatorException, SQLException {
        String cc_num = value.toString();
        if(cc_num.length() != 16){
            FacesMessage errorMessage = new FacesMessage("Credit Card Number is not 16 digits long.");
            throw new ValidatorException(errorMessage);
        }
        
        if(!(cc_num.matches("[0-9]+"))){
            FacesMessage errorMessage = new FacesMessage("Credit Card Number is not all digits.");
            throw new ValidatorException(errorMessage);
        }
    }
    
    public void validateCCV(FacesContext context, UIComponent component, Object value)
            throws ValidatorException, SQLException {
        String cc_ccv = value.toString();
        if(cc_ccv.length() != 3){
            FacesMessage errorMessage = new FacesMessage("Credit Card CRC Number is not 3 digits long.");
            throw new ValidatorException(errorMessage);
        }
        
        if(!(cc_ccv.matches("[0-9]+"))){
            FacesMessage errorMessage = new FacesMessage("Credit Card CRC Number is not all digits.");
            throw new ValidatorException(errorMessage);
        }
    }
    
    public void validateCCDate(FacesContext context, UIComponent component, Object value)
            throws ValidatorException, SQLException {
        String date = value.toString();
        
        String[] parts = date.split("/");
        
        // Checks if there are non number values
        String check = date.replaceAll("/", "");
        if(!(check.matches("[0-9]+"))){
            FacesMessage errorMessage = new FacesMessage("Date is not all digits.");
            throw new ValidatorException(errorMessage);
        }
        
        if(parts.length != 3){
            FacesMessage errorMessage = new FacesMessage("Incorrect date format.");
            throw new ValidatorException(errorMessage);
        }
        
        if(parts[0].length() != 2 || parts[1].length() != 2 || parts[2].length() != 4){
            FacesMessage errorMessage = new FacesMessage("Incorrect date format.");
            throw new ValidatorException(errorMessage);
        }
        
        // Check if day is valid
        int month = Integer.parseInt(parts[0]);
        int day = Integer.parseInt(parts[1]);
        
        if(month < 1 || month > 12 || day < 1 || day > 31 ){
            FacesMessage errorMessage = new FacesMessage("Incorrect date.");
            throw new ValidatorException(errorMessage);
        }
    }

    public String go() {
        //Util.invalidateUserSession();
        
        return "success";
    }
}
