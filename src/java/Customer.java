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

@Named(value = "customer")
@SessionScoped
@ManagedBean
public class Customer implements Serializable {

    private DBConnect dbConnect = new DBConnect();
    private Integer customerID;
    private String name;
    private String address;
    private Date created_date;
    private String first_name;
    private String last_name;

    
    public Integer getCustomerID() throws SQLException {
        if (customerID == null) {
            Connection con = dbConnect.getConnection();

            if (con == null) {
                throw new SQLException("Can't get database connection");
            }

            PreparedStatement ps
                    = con.prepareStatement(
                            "select max(customer.id)+1 from customer");
            ResultSet result = ps.executeQuery();
            if (!result.next()) {
                return null;
            }
            customerID = result.getInt(1);
            result.close();
            con.close();
        }
        return customerID;
    }

    public void setCustomerID(Integer customerID) {
        this.customerID = customerID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Date getCreated_date() {
        return created_date;
    }

    public void setCreated_date(Date created_date) {
        TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
        this.created_date = created_date;
    }

    public String showCustomer() {
        return "showCustomer";
    }
    
    public static int userNameToId(String u_name) throws SQLException {
        DBConnect dbConnect = new DBConnect();
        Connection con = dbConnect.getConnection();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }

        PreparedStatement ps
                = con.prepareStatement(
                        "select customer.id from customer where customer.username = ?");
        ps.setString(1, u_name);
        //get customer data from database
        ResultSet result = ps.executeQuery();

        result.next();

        return result.getInt("id");
    }

    public Customer getCustomer() throws SQLException {
        Connection con = dbConnect.getConnection();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }

        PreparedStatement ps
                = con.prepareStatement(
                        "select * from customer where customer.id = " + customerID);

        //get customer data from database
        ResultSet result = ps.executeQuery();

        result.next();

        name = result.getString("name");
        return this;
    }

    public List<Customer> getCustomerList() throws SQLException {

        Connection con = dbConnect.getConnection();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }

        PreparedStatement ps
                = con.prepareStatement(
                        "select customer.id, customer.first_name, customer.last_name from customer order by customer.id");

        //get customer data from database
        ResultSet result = ps.executeQuery();

        List<Customer> list = new ArrayList<Customer>();

        while (result.next()) {
            Customer cust = new Customer();

            cust.setCustomerID(result.getInt("id"));
            cust.setName(result.getString("first_name"));

            //store all data into a List
            list.add(cust);
        }
        result.close();
        con.close();
        return list;
    }
    
    public String[] getCustomerListUsernames() throws SQLException {

        Connection con = dbConnect.getConnection();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }

        PreparedStatement ps
                = con.prepareStatement(
                        "select customer.username from customer order by customer.username");

        //get customer data from database
        ResultSet result = ps.executeQuery();

        List<String> list = new ArrayList<String>();

        while (result.next()) {
            list.add(result.getString("username"));
        }
        result.close();
        con.close();

        String[] returnable = new String[list.size()];
        for(int i = 0; i < list.size(); i++){
            returnable[i] = list.get(i);
        }
        
        return returnable;
    }

    public void customerIDExists(FacesContext context, UIComponent componentToValidate, Object value)
            throws ValidatorException, SQLException {

        if (!existsCustomerId((Integer) value)) {
            FacesMessage errorMessage = new FacesMessage("ID does not exist");
            throw new ValidatorException(errorMessage);
        }
    }

    public void validateCustomerID(FacesContext context, UIComponent componentToValidate, Object value)
            throws ValidatorException, SQLException {
        int id = (Integer) value;
        if (id < 0) {
            FacesMessage errorMessage = new FacesMessage("ID must be positive");
            throw new ValidatorException(errorMessage);
        }
        if (existsCustomerId((Integer) value)) {
            FacesMessage errorMessage = new FacesMessage("ID already exists");
            throw new ValidatorException(errorMessage);
        }
    }

    private boolean existsCustomerId(int id) throws SQLException {
        Connection con = dbConnect.getConnection();
        if (con == null) {
            throw new SQLException("Can't get database connection");
        }

        PreparedStatement ps = con.prepareStatement("select * from customer where customer.customer_id = " + id);

        ResultSet result = ps.executeQuery();
        if (result.next()) {
            result.close();
            con.close();
            return true;
        }
        result.close();
        con.close();
        return false;
    }
    
    private String delete(int id) throws SQLException {
        Connection con = dbConnect.getConnection();
        if (con == null) {
            throw new SQLException("Can't get database connection");
        }

        PreparedStatement ps = con.prepareStatement("DELETE FROM customer WHERE id = ?");
        ps.setInt(1, id);
        Integer row = ps.executeUpdate();
        con.close();
        return "success";
    }
    public Integer getCustomerByName() throws SQLException {
        Connection con = dbConnect.getConnection();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }

        PreparedStatement ps
                = con.prepareStatement(
                        "select * from customer where customer.username = ?");
        ps.setString(1,name);

        //get customer data from database
        ResultSet result = ps.executeQuery();

        result.next();

        Integer id = result.getInt("id");
        return id;
    }
    public String getCustomerByNamePartOne() throws SQLException {
        getCustomerByName();
        return "go";
    }
}
