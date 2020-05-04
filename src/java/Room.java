
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
public class Room {
    private DBConnect dbConnect = new DBConnect();
    private Integer room_num;
    private float basePrice;
    private float newPrice;
    private String input_date_string;
    private Calendar inputDate = null;
    private String choice;
    
    private int date_month;
    private int date_day;
    private int date_year;
    private String dateErrorMessage;
  
    public Integer getRoomNum() {return room_num;}
    public String getChoice() {return choice;}
    public void setChoice(String choice) {this.choice = choice;}
    
    public List<Room> getAllRooms() throws SQLException {
        Connection con = dbConnect.getConnection();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }

        PreparedStatement ps
                = con.prepareStatement(
                        "select * from Room ORDER BY room_num");

        //get customer data from database
        ResultSet result = ps.executeQuery();

        List<Room> room_list = new ArrayList<Room>();

        while (result.next()) {
            Room room = new Room();
            room_list.add(room);
        }
        result.close();
        con.close();
        return room_list;
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
        ps.setInt(2, room_num);
            
        ps.executeUpdate();
        
        
        con.commit();
        con.close();
        return "refresh";
    }
}
