
import java.sql.SQLException;
import javax.faces.validator.ValidatorException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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

@Named(value = "bill")
@SessionScoped
@ManagedBean
public class Bill implements Serializable {

    private Integer customerID;
    private Integer reservation_id;
    private Integer room_num;
    private Integer cc_num;
    private Date cc_exp;
    private Integer cc_crc;
    private Date curr_date;
    private Integer room_charge;
    private Integer extra_charges;
    private Integer total_charges;

    private DBConnect dbConnect = new DBConnect();

    
    public String go() throws ValidatorException, SQLException {
        return "go";
    } 
    public String printBill(){
        
        return "success";
    }
}
    