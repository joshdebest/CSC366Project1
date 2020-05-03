import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.annotation.ManagedBean;
import javax.el.ELContext;
import javax.faces.application.FacesMessage;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.inject.Named;

@Named(value = "employee")
@SessionScoped
@ManagedBean
public class Employee implements Serializable {

    private DBConnect dbConnect = new DBConnect();
    private String username;
    private String password;
    private String first_name;
    private String last_name;
    
    private String usernameErrorMessage;

    public String getUsername() {return username;}
    public void setUsername(String username) {this.username = username;}
    public String getPassword() {return password;}
    public void setPassword(String password) {this.password = password;}
    public String getFirst_name() {return first_name;}
    public void setFirst_name(String first_name) {this.first_name = first_name;}
    public String getLast_name() {return last_name;}
    public void setLast_name(String last_name) {this.last_name = last_name;}


    public void validateUsername(FacesContext context, UIComponent component, Object value)
            throws ValidatorException, SQLException{
        ELContext elContext = FacesContext.getCurrentInstance().getELContext();
        Login login = (Login) elContext.getELResolver().getValue(elContext, null, "login");
        
        String returnable = Validation.validateUsername(value.toString(), login.getId());
        
        if(!(returnable.equals("valid"))){
            usernameErrorMessage = returnable;
            FacesMessage errorMessage = new FacesMessage(usernameErrorMessage);
            throw new ValidatorException(errorMessage);
        }       
    }
    
    public String go() throws ValidatorException, SQLException {
        Connection con = dbConnect.getConnection();
        
        if (con == null) {
            throw new SQLException("Can't get database connection");
        }

        PreparedStatement ps
                = con.prepareStatement(
                        "INSERT INTO employee VALUES (?, ?, ?, ?, ?)");
        ps.setString(1, username);
        ps.setString(2, password);
        ps.setString(3, first_name);
        ps.setString(4, last_name);
        ps.setBoolean(5, false);
        
        ps.executeUpdate();
        
        con.close();
        
        return "success";
    }
    
    public String rebuildFloors() throws ValidatorException, SQLException {
        Connection con = dbConnect.getConnection();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }

        PreparedStatement ps = con.prepareStatement("DROP TABLE room");        
        
        try{
            ps.executeUpdate();
        }
        catch(SQLException e){
            System.out.println("Already deleted.");
        }
        
        ps = con.prepareStatement("CREATE TABLE room ("
                + "room_num text NOT NULL, "
                + "floor integer NOT NULL, "
                + "view text NOT NULL, "
                + "bed_type text NOT NULL, "
                + "base_price money NOT NULL DEFAULT 100, "
                + "PRIMARY KEY (room_num))");
        ps.execute();
        
        String formatNum;
        String view;
        String bed_type;
        for(int floor = 1; floor <= 5; floor++){
            for(int roomNum = 1; roomNum <= 12; roomNum++){
                formatNum = String.valueOf(floor) + String.format("%02d", roomNum);
                if(roomNum <= 6){
                    view = "ocean";
                } 
                else{
                    view = "pool";
                }
                if(roomNum % 2 == 0){
                    bed_type = "double queen";
                }
                else{
                    bed_type = "single king";
                }
                
                ps = con.prepareStatement("INSERT INTO room(room_num, floor, view, bed_type) VALUES (?, ?, ?, ?)");
                ps.setString(1, formatNum);
                ps.setInt(2, floor);
                ps.setString(3, view);
                ps.setString(4, bed_type);
                ps.executeUpdate();
            }
        }
        con.close();
        
        return "success";
    }

}
