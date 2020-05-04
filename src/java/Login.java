
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
@Named(value = "login")
@SessionScoped
@ManagedBean
public class Login implements Serializable {

    private String username;
    private DBConnect dbConnect = new DBConnect();
   
    private String password;
    
    private boolean isEmployee;
    private boolean isAdmin;
    private boolean isCustomer;
    private int id = 0;
    
    private UIInput loginUI;

    public boolean isEmployee() {return isEmployee;}
    public boolean isAdmin() {return isAdmin;}
    public boolean isCustomer(){return isCustomer;}
    public int getId() {return id;}
    public void setId(int id) {this.id = id;}
    public UIInput getLoginUI() {return loginUI;}
    public void setLoginUI(UIInput loginUI) {this.loginUI = loginUI;}
    public String getUsername() {return username;}
    public void setUsername(String username) {this.username = username;}
    public String getPassword() {return password;}
    public void setPassword(String password) {this.password = password;}

    public void validate(FacesContext context, UIComponent component, Object value)
            throws ValidatorException, SQLException {
        username = loginUI.getLocalValue().toString();
        password = value.toString();
        
        Connection con = dbConnect.getConnection();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }

        PreparedStatement ps
                = con.prepareStatement(
                        "select customer.id from customer where customer.username = ? AND customer.password = ?");
        ps.setString(1, username);
        ps.setString(2, password);
        
        //get customer data from database

        ResultSet result = ps.executeQuery();

        if(result.next()) {
            id = result.getInt("id");
            result.close();
            con.close();
            isEmployee = false;
            isAdmin = false;
            isCustomer = true;
            return; 
        }
        
        ps = con.prepareStatement(
                        "select employee.id, employee.is_admin from employee where employee.username = ? AND employee.password = ?");
        ps.setString(1, username);
        ps.setString(2, password);
        
        result = ps.executeQuery();

        if(result.next()) {
            id = result.getInt("id");
            isAdmin = result.getBoolean("is_admin");
            result.close();
            con.close();
            isEmployee = true;
            isCustomer = false;
            return; 
        }
        
        FacesMessage errorMessage = new FacesMessage("Wrong login/password");
        throw new ValidatorException(errorMessage);       
    }

    public String go() {
        
        //Util.invalidateUserSession();
        return "success";
    }
     
    public String register() {
        //Util.invalidateUserSession();
        return "register";
    }
    
    public String logout() {
        Util.invalidateUserSession();
        return "logout";
    }

}
