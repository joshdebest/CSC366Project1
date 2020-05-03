
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Date;

import javax.annotation.ManagedBean;
import javax.el.ELContext;
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

    private String username;
    private String password;
    private String first_name;
    private String last_name;
    private String email;
    private String street_address; 
    private String city; 
    private String state; 
    private String cc_num;
    private String cc_crc;
    private String cc_date;
    private int cc_date_month;
    private int cc_date_day;
    private int cc_date_year;
    
    private String dateErrorMessage;
    private String passwordErrorMessage;
    private String usernameErrorMessage;
    private String ccNumErrorMesssage;
    private String ccCRCErrorMessage;
    private String ccDateErrorMessage;
    
    private DBConnect dbConnect = new DBConnect();  
    private UIInput loginUI;

    /* So many stupid getters and setters. I really should go through and clean these up */
    public String getDateErrorMessage() {return dateErrorMessage;}
    public void setDateErrorMessage(String dateErrorMessage) {this.dateErrorMessage = dateErrorMessage;}
    public String getCc_date() {return cc_date;}
    public void setCc_date(String cc_date) {this.cc_date = cc_date;}
    public void setCc_num(String cc_num) {this.cc_num = cc_num;}
    public void setCc_crc(String cc_ccv) {this.cc_crc = cc_ccv;}
    public String getCc_num() {return cc_num;}
    public String getCc_crc() {return cc_crc;}
    public void setFirst_name(String first_name) {this.first_name = first_name;}
    public void setLast_name(String last_name) {this.last_name = last_name;}
    public void setEmail(String email) {this.email = email;}
    public String getFirst_name() {return first_name;}
    public String getLast_name() {return last_name;}
    public String getEmail() {return email;}
    public UIInput getLoginUI() {return loginUI;}
    public void setLoginUI(UIInput loginUI) {this.loginUI = loginUI;}
    public String getUsername() {return username;}
    public void setUsername(String login) {this.username = login;}
    public String getPassword() {return password;}
    public void setPassword(String password) {this.password = password;}
    public String getStreet_address() {return street_address;}
    public void setStreet_address(String street_address) {this.street_address = street_address;}
    public String getCity() {return city;}
    public void setCity(String city) {this.city = city;}
    public String getState() {return state;}
    public void setState(String state) {this.state = state;}
    public String getPasswordErrorMessage() {return passwordErrorMessage;}
    public void setPasswordErrorMessage(String passwordErrorMessage) {this.passwordErrorMessage = passwordErrorMessage;}
    public String getUsernameErrorMessage() {return usernameErrorMessage;}
    public void setUsernameErrorMessage(String usernameErrorMessage) {this.usernameErrorMessage = usernameErrorMessage;}
    public String getCcNumErrorMesssage() {return ccNumErrorMesssage;}
    public void setCcNumErrorMesssage(String ccNumErrorMesssage) {this.ccNumErrorMesssage = ccNumErrorMesssage;}
    public String getCcCRCErrorMessage() {return ccCRCErrorMessage;}
    public void setCcCRCErrorMessage(String ccCRCErrorMessage) {this.ccCRCErrorMessage = ccCRCErrorMessage;}
    public String getCcDateErrorMessage() {return ccDateErrorMessage;}
    public void setCcDateErrorMessage(String ccDateErrorMessage) {this.ccDateErrorMessage = ccDateErrorMessage;}
    
    public void validatePassword(FacesContext context, UIComponent component, Object value)
            throws ValidatorException, SQLException {
        String returnable = Validation.validatePassword(value.toString());
        
        if(!(returnable.equals("valid"))){
            passwordErrorMessage = returnable;
            FacesMessage errorMessage = new FacesMessage(passwordErrorMessage);
            throw new ValidatorException(errorMessage);
        }
    }
    
    public void validateUsername(FacesContext context, UIComponent component, Object value)
            throws ValidatorException, SQLException{
        
        String returnable = Validation.validateUsername(value.toString());
        
        if(!(returnable.equals("valid"))){
            usernameErrorMessage = returnable;
            FacesMessage errorMessage = new FacesMessage(usernameErrorMessage);
            throw new ValidatorException(errorMessage);
        }       
    }
    
    public void validateEmail(FacesContext context, UIComponent component, Object value)
            throws ValidatorException, SQLException {
        String email = value.toString();
        if(!(email.contains("@"))){
            FacesMessage errorMessage = new FacesMessage("Email is invalid.");
            throw new ValidatorException(errorMessage);
        }
    }
    
    public void validateCCNum(FacesContext context, UIComponent component, Object value)
            throws ValidatorException, SQLException {
        String cc_num = value.toString();
        if(value.toString().length() != 16){
            ccNumErrorMesssage = "Credit card numer must be sixteen digits long.";
            FacesMessage errorMessage = new FacesMessage(ccNumErrorMesssage);
            throw new ValidatorException(errorMessage);
        }
        
        if(!(cc_num.matches("[0-9]+"))){
            ccNumErrorMesssage = "Credit Card Number is not all digits.";
            FacesMessage errorMessage = new FacesMessage(ccNumErrorMesssage);
            throw new ValidatorException(errorMessage);
        }
    }
    
    public void validateCRC(FacesContext context, UIComponent component, Object value)
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
        
        String returnable = Validation.validDate(date);
        
        if(!(returnable.equals("valid"))){
            dateErrorMessage = returnable;
            FacesMessage errorMessage = new FacesMessage(dateErrorMessage);
            throw new ValidatorException(errorMessage);
        }
        else{
            String[] parts = date.split("/");
            cc_date_month = Integer.parseInt(parts[0]);
            cc_date_day = Integer.parseInt(parts[1]);
            cc_date_year = Integer.parseInt(parts[2]);
        }
    }

    public String go() throws ValidatorException, SQLException {
        Connection con = dbConnect.getConnection();
        
        Date inputDate = new Date(cc_date_year, cc_date_month, cc_date_day);

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }

        PreparedStatement ps
                = con.prepareStatement(
                        "INSERT INTO customer VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
        ps.setString(1, username);
        ps.setString(2, password);
        ps.setString(3, first_name);
        ps.setString(4, last_name);
        ps.setString(5, email);
        ps.setString(6, street_address);
        ps.setString(7, city);
        ps.setString(8, state);
        ps.setString(9, cc_num);
        ps.setString(10, cc_crc);
        ps.setDate(11, inputDate);
        
        ps.executeUpdate();
        con.close();
        
        return "success";
    }
    
    public String update() throws ValidatorException, SQLException {
        Connection con = dbConnect.getConnection();
       
        ELContext elContext = FacesContext.getCurrentInstance().getELContext();
        Login outerLogin = (Login) elContext.getELResolver().getValue(elContext, null, "login");
       
        if (con == null) {
            throw new SQLException("Can't get database connection");
        }

        PreparedStatement ps
                = con.prepareStatement(
                        "UPDATE employee SET username = ?, password = ? WHERE employee.id = ?");
        ps.setString(1, outerLogin.getUsername());
        ps.setString(2, outerLogin.getPassword());
        ps.setInt(3, outerLogin.getId());
        
        ps.executeUpdate();
        
        con.close();
        
        return "success";
    }
    
}
