/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
/**
 *
 * @author joshdebest
 */
@Named(value = "ExtraCharge")
@SessionScoped
@ManagedBean
public class ExtraCharge implements Serializable {
    private DBConnect dbConnect = new DBConnect();
    private String desc;
    private float price;
    private int res_id;
    private int charge_type;
    private List<String> types_list;

    
    public int getResID(){
        return res_id;
    }
    public void setResID(int res_id){
        this.res_id = res_id;
    }
    public int getChargeID(){
        return res_id;
    }
    public void setChargeID(int charge_type){
        this.charge_type = charge_type;
    }
    public String getDesc() {return desc;}
    public void setDesc(String choice) {this.desc = desc;}
    
    public String[] getChargeTypes() throws SQLException {
        types_list = new ArrayList<>();
        String temp_desc;
        String[] temp;
        Connection con = dbConnect.getConnection();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }

        PreparedStatement ps
                = con.prepareStatement("SELECT description from charge_type");

        //get list of all room numbers from database
        ResultSet typeResults = ps.executeQuery();
        while (typeResults.next()) {
            temp_desc = typeResults.getString("description");
            types_list.add(temp_desc);
        }
        typeResults.close();
        con.close();
        
        temp = new String[types_list.size()];
        List<String> condenser = types_list;
        for(int i = 0; i < condenser.size(); i++){
            temp[i] = condenser.get(i);
        }
        return temp;        
    }
    
    public String addChargeType() throws SQLException {
        Connection con = dbConnect.getConnection();
        if (con == null) {
            throw new SQLException("Can't get database connection");
        }
        PreparedStatement ps = con.prepareStatement(
                "INSERT into charge_type(description, price) values (" + desc + "," + price + ")");
        
        ResultSet Res = ps.executeQuery();
        Res.close();
        con.close();
        return "success";
    }
    
    public String addCharge() throws SQLException {
        Connection con = dbConnect.getConnection();
        if (con == null) {
            throw new SQLException("Can't get database connection");
    }
        PreparedStatement ps = con.prepareStatement(
                "INSERT into extra_charge(reservation_id, charge_type) values (" + res_id + "," + charge_type + ")");
        
        ResultSet Res = ps.executeQuery();
        Res.close();
        con.close();
        return "success";
    }
}
