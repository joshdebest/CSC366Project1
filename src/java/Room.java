
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javax.annotation.ManagedBean;
import javax.faces.application.FacesMessage;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.inject.Named;
import java.util.Date;
import java.util.TimeZone;
import javax.el.ELContext;
import javax.faces.bean.ManagedProperty;
/**
 *
 * @author joshdebest
 */
@Named(value = "room")
@SessionScoped
@ManagedBean
public class Room implements Serializable {
    
    private DBConnect dbConnect = new DBConnect();
    private String room_num;
    private String choice;    
    private float newPrice;
    private String input_date_string;
    private Calendar inputDate = null;
    private List<String> choices;
    private int date_month;
    private int date_day;
    private int date_year;
    private String dateErrorMessage;
  
    public String getRoom_num() {return room_num;}
    public void setRoom_num(String room_num) {this.room_num = room_num;}
    public String getChoice() {return choice;}
    public void setChoice(String choice) {this.choice = choice;}
    public float getNewPrice() {return newPrice;}
    public void setNewPrice(String choice) {this.choice = choice;}
    public String getEndDateErrorMessage() {return dateErrorMessage;}
    public void setEndDateErrorMessage(String endDateErrorMessage) {this.dateErrorMessage = endDateErrorMessage;}
    public String getInputDateString() {return input_date_string;}
    public void setInputDateString(String input_data_string) {this.input_date_string = input_date_string;}
    
    
    public String[] getAllRooms() throws SQLException {
        Connection con = dbConnect.getConnection();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }

        PreparedStatement ps
                = con.prepareStatement("SELECT room_num from room");

        //get list of all room numbers from database
        ResultSet result = ps.executeQuery();
        result.close();
        con.close();
        System.out.println(result);
        String[] temp = new String[choices.size()];
        List<String> condenser = choices;
        for(int i = 0; i < condenser.size(); i++){
            temp[i] = condenser.get(i);
        }
        return temp;        
    }
    
    public void validateDate(FacesContext context, UIComponent component, Object value)
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
            date_month = Integer.parseInt(parts[0]);
            date_day = Integer.parseInt(parts[1]);
            date_year = Integer.parseInt(parts[2]);
        }
    }
    
    public String changeRoomPrice() throws SQLException {
        Connection con = dbConnect.getConnection();
        java.sql.Date inputDate = new java.sql.Date(date_year, date_month, date_day);

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }
        con.setAutoCommit(false);
        
        PreparedStatement ps
            = con.prepareStatement("INSERT INTO room_prices VALUES (?, ?, ?)");
        ps.setFloat(1, newPrice);
        ps.setDate(11, inputDate);
        ps.setString(2, room_num);
            
        ps.executeUpdate();
        
        con.commit();
        con.close();
        return "refresh";
    }
}
