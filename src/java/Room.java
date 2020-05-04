
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Date;
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
import java.util.TimeZone;
import javax.el.ELContext;

/**
 *
 * @author joshdebest
 */
@Named(value = "room")
@SessionScoped
@ManagedBean
public class Room implements Serializable {
    
    private String room_num;
    private String choice;    
    private float newPrice;
    private String inputStr; 
    private Calendar inputDate = null;
    private List<String> choices;
    private String dateErrorMessage = "";
    
    private DBConnect dbConnect = new DBConnect();
  
    public String getInputStr() {return inputStr;}
    public void setInputStr(String inputStr) {this.inputStr = inputStr;}
    public String getRoom_num() {return room_num;}
    public void setRoom_num(String room_num) {this.room_num = room_num;}
    public String getChoice() {return choice;}
    public void setChoice(String choice) {this.choice = choice;}
    public float getNewPrice() {return newPrice;}
    public void setNewPrice(float newPrice) {this.newPrice = newPrice;}
    public String getDateErrorMessage() {return dateErrorMessage;}
    public void setDateErrorMessage(String dateErrorMessage) {this.dateErrorMessage = dateErrorMessage;}
    
    public String[] getAllRooms() throws SQLException {
        choices = new ArrayList<>();
        String temp_room_num;
        String temp_room_price;
        String[] temp;
        Connection con = dbConnect.getConnection();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }

        PreparedStatement ps
                = con.prepareStatement("SELECT * from room");

        //get list of all room numbers from database
        ResultSet roomResults = ps.executeQuery();
        while (roomResults.next()) {
            temp_room_num = roomResults.getString("room_num");
            choices.add(temp_room_num);
        }
        roomResults.close();
        con.close();
        
        temp = new String[choices.size()];
        List<String> condenser = choices;
        for(int i = 0; i < condenser.size(); i++){
            temp[i] = condenser.get(i);
        }
        return temp;        
    }
    public List<Room> getPrices() throws SQLException{
        List<Room> list = new ArrayList<Room>();
        Connection con = dbConnect.getConnection();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }

        PreparedStatement ps
                = con.prepareStatement("SELECT * from room");

        //get list of all room numbers from database
        ResultSet roomResults = ps.executeQuery();
        while (roomResults.next()) {
            Room room = new Room();
            room.setRoom_num(roomResults.getString("room_num"));
            room.setNewPrice(Float.parseFloat(roomResults.getString("base_price").substring(1)));
            list.add(room);
        }
        roomResults.close();
        con.close();

        return list;  
    }
    
    public void validateDate(FacesContext context, UIComponent component, Object value)
            throws ValidatorException, SQLException {
        
        inputStr = value.toString();
        String returnable = Validation.validDate(inputStr);
        
        
        if(returnable.equals("valid")){
            inputDate = Validation.getCalObj(inputStr);
        }
        else{
            dateErrorMessage = returnable;
            FacesMessage errorMessage = new FacesMessage(dateErrorMessage);
            throw new ValidatorException(errorMessage);
        }
    }
    
    public String changeRoomPrice() throws SQLException {
        ELContext elContext = FacesContext.getCurrentInstance().getELContext();
        Login login = (Login) elContext.getELResolver().getValue(elContext, null, "login");
        
        Connection con = dbConnect.getConnection();
        //java.sql.Date inputDate = new java.sql.Date(date_year, date_month, date_day);

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }
        con.setAutoCommit(false);
        
        PreparedStatement ps
            = con.prepareStatement("INSERT INTO room_prices VALUES (?, ?, ?)");
        ps.setFloat(1, newPrice);
        ps.setDate(2, new Date(inputDate.getTime().getTime()));
        ps.setString(3, choice);
            
        ps.executeUpdate();
        
        con.commit();
        con.close();
        return "success";
    }
    
    public String go() throws ValidatorException, SQLException {
        return "success";
    }
}
