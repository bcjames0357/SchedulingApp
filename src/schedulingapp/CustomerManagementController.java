/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package schedulingapp;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author brandon <bcjames035@gmail.com>
 */
public class CustomerManagementController implements Initializable {
    
    @FXML private TableView<Customer> customerTableView;
    @FXML private TableColumn<Customer, Integer> idCol;
    @FXML private TableColumn<Customer, String> nameCol;
    @FXML private TableColumn<Customer, String> addressCol;
    @FXML private TableColumn<Customer, String> phoneCol;
    
    @FXML private TextField nameField;
    @FXML private TextField addressField;
    @FXML private TextField phoneField;
    @FXML private TextField searchField;
    
    private Integer id;
    private String name;
    private String address;
    private String phone;
    private Integer addressID;
    
    private static boolean viewAll = true;
    private static int viewID;
    
    private final ObservableList<Customer> customers = FXCollections.observableArrayList(Customer.extractor());

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        if(getViewAllBool()){
            populateCustomers();
        } else {
            populateCustomers(viewID);
        }
    }
    
    public boolean getViewAllBool() {
        return viewAll;
    }
    
    public static void setViewAllBool(boolean viewBool) {
        viewAll = viewBool;
    }
    
    public static void setViewID(int id) {
        viewID = id;
    }
    
    public int getViewID() {
        return viewID;
    }
    
    public void addButtonPushed() {
        if( nameField.getText().trim().isEmpty() || 
            addressField.getText().trim().isEmpty() || 
            phoneField.getText().trim().isEmpty()) {
            
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Customer creation error");
            alert.setContentText("All fields are required.");
            alert.showAndWait();
            return;
        }
        try {
            name = nameField.getText();
            address = addressField.getText();
            phone = phoneField.getText();
            
            if(!validatePhoneNumber(phone))
            {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Data Entry Error");
                alert.setContentText("Please enter a valid phone number.");
                alert.showAndWait();
                return;
            }
            /* Check if an existing address entity matches the provided address
            *  and phone number. Note that address and phone number are inseperable
            *  due to the database structure. 
            */
            String sql = "SELECT * FROM address WHERE address LIKE '" 
                    + address + "' AND phone LIKE '" + phone + "'";
            ResultSet rs = DBConnection.conn.createStatement().executeQuery(sql);
            if(rs.next()){
                addressID = rs.getInt("addressId");
            } else {
            sql = "INSERT INTO address (address, address2, cityId, postalCode, phone, "
                        + "createDate, createdBy,lastUpdate, lastUpdateBy) "
                        + "VALUES ('" + address
                        + "', ' ', (SELECT cityId FROM city WHERE cityId = '1')"
                        + ", '00000', '" + phone 
                        + "', NOW(),"
                        + "'" + LogInController.getUsername()
                        + "', NOW(),"
                        + "'" + LogInController.getUsername() + "')";
            Statement stmt = DBConnection.conn.createStatement();
            stmt.executeUpdate(sql);
            sql = "SELECT * FROM address WHERE address LIKE '" 
                    + address + "' AND phone LIKE '" + phone + "'";
            rs = DBConnection.conn.createStatement().executeQuery(sql);
            rs.next();
            addressID = rs.getInt("addressId");
            }
            sql = "INSERT INTO customer (customerName, addressId, active, createDate, createdBy,"
                + "lastUpdate, lastUpdateBy) VALUES ('" 
                + name
                + "', (SELECT addressId FROM address WHERE addressId = '" + addressID 
                + "'), '1"
                + "', NOW(),"
                + "'" + LogInController.getUsername() 
                + "', NOW(),"
                + "'" + LogInController.getUsername() + "')";
            Statement stmt = DBConnection.conn.createStatement();
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            System.err.print(e);
        }
        
        populateCustomers();
    }
    
    public void deleteButtonPushed () {
        if(customerTableView.getSelectionModel().isEmpty()){
            return;
        }
        Statement stmt = null;
        Alert alert;
        try {
            alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm Delete");
            alert.setHeaderText("Delete selected appointment.");
            alert.setContentText("Are you sure? This action cannot be undone.");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK){
                Integer removeId = customerTableView.getSelectionModel().getSelectedItem().getID();
                String update = "DELETE FROM customer WHERE customerId = " + removeId;
                stmt = DBConnection.conn.createStatement();
                stmt.executeUpdate(update);
            }
        } catch (SQLIntegrityConstraintViolationException ex) {
                alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Customer deletion error!");
                alert.setContentText("A customer with existing appointments cannot be deleted.");
                alert.showAndWait();
        } catch (SQLException ex) {
            System.err.println(ex);
        }
        populateCustomers();

    }
    
    public void saveButtonPushed() {
        /* Retrieves ID of selected row to later reference database. If text
        *  field is blank, the current value of the cell is used for the SQL
        *  UPDATE statement. This avoids NullPointerException when the TextField
        *  is left empty when the Save button is pushed.
        */
        if(customerTableView.getSelectionModel().isEmpty()){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Customer Update Error");
            alert.setContentText("Please select a customer to update.");
            alert.showAndWait();
            return;
        }
        id = customerTableView.getSelectionModel().getSelectedItem().getID();
        addressID = customerTableView.getSelectionModel().getSelectedItem().getAddressID();
        
        if(nameField.getText().equals("") || nameField.getText().trim().isEmpty()) {
            name = customerTableView.getSelectionModel().getSelectedItem().getName();
        } else {
            name = nameField.getText();
        }
        if(addressField.getText().equals("") || addressField.getText().trim().isEmpty()) {
            address = customerTableView.getSelectionModel().getSelectedItem().getAddress();
        } else {
            address = addressField.getText();
        }
        if(phoneField.getText() == null || phoneField.getText().trim().isEmpty()) {
            phone = customerTableView.getSelectionModel().getSelectedItem().getPhone();
        } else {
            phone = phoneField.getText();
            if(!validatePhoneNumber(phone))
            {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Data Entry Error");
                alert.setContentText("Please enter a valid phone number.");
                alert.showAndWait();
                return;
            }
        }

        String sqlCust = "UPDATE customer "
                + "SET customerName = '" + name
                + "' WHERE customerId = '" + id + "'";
        
        String sqlAddr = "UPDATE address "
                + "SET address = '" + address
                + "', phone = '" + phone  
                + "' WHERE addressId = '" + addressID + "'";
        try {
        Statement stmt = DBConnection.conn.createStatement();
        stmt.executeUpdate(sqlCust);
        stmt.executeUpdate(sqlAddr);
        } catch (SQLException ex) {
            Logger.getLogger(CalendarController.class.getName()).log(Level.SEVERE, null, ex);
        }
        if(viewAll == true) {
            populateCustomers();
        } else {
            populateCustomers(viewID);
        }
        
    }
    
    private static boolean validatePhoneNumber(String phoneNo) 
    {
        //validate phone numbers of format "1234567890"
        if (phoneNo.matches("\\d{10}")) return true;
        //validating phone number with -, . or spaces
        else if(phoneNo.matches("\\d{3}[-\\.\\s]\\d{3}[-\\.\\s]\\d{4}")) return true;
        //validating phone number where area code is in braces ()
        else if(phoneNo.matches("\\(\\d{3}\\)[-\\.\\s]?\\d{3}[-\\.\\s]?\\d{4}")) return true;
        //return false if nothing matches the input
        else return false;
    }
    
    public void returnToCalendarButtonPushed(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(SchedulingApp.class.getResource("Calendar.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.err.print(e);
        }
    }
    
    public void viewAllCustomersButtonPushed() {
        viewAll = true;
        populateCustomers();
    }
    
    public void searchButtonPushed(ActionEvent event) {
        if(searchField.getText().trim().isEmpty()){
            customerTableView.getItems().clear();
            return;
        }
        String searchString = searchField.getText().trim();
        try{
            String sql = "SELECT customer.customerId, customer.customerName, "
                    + "address.address, address.phone, address.addressId "
                    + "FROM customer "
                    + "JOIN address "
                    + "ON customer.addressId = address.addressId "
                    + "WHERE "
                    + "INSTR(customer.customerId, '" + searchString + "') "
                    + "OR INSTR(customer.customerName, '" + searchString + "') "
                    + "OR INSTR(address.address, '" + searchString + "') "
                    + "OR INSTR(address.phone, '" + searchString + "') "
                    + "OR INSTR(address.addressId, '" + searchString + "') ";
            
            ResultSet rs = DBConnection.conn.createStatement().executeQuery(sql);
            populateCustomers(rs);
        } catch(SQLException e) {
            System.err.println(e);
        }
    }
    
    public void populateCustomers() {
        viewAll = true;
        customerTableView.getItems().clear();
        
        try {
            String sql = "SELECT customer.customerId, customer.customerName, "
                    + "address.address, address.phone, address.addressId "
                    + "FROM customer "
                    + "JOIN address "
                    + "ON customer.addressId = address.addressId";
            
            ResultSet rs = DBConnection.conn.createStatement().executeQuery(sql);
            
            while(rs.next()){
                Customer customer = new Customer();
                customer.setID(rs.getInt("customerId"));
                customer.setName(rs.getString("customerName"));
                customer.setAddress(rs.getString("address"));
                customer.setPhone(rs.getString("phone"));
                customer.setAddressID(rs.getInt("addressId"));
                customers.add(customer);
            }
            customerTableView.setItems(customers);
        } catch (SQLException e) {
            System.err.print(e);
        }
        
        idCol.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getID()).asObject());
        nameCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        addressCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAddress()));
        phoneCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPhone()));
    }
    
    public void populateCustomers(ResultSet rs) {
        viewAll = true;
        customerTableView.getItems().clear();
        
        try {
            while(rs.next()){
                Customer customer = new Customer();
                customer.setID(rs.getInt("customerId"));
                customer.setName(rs.getString("customerName"));
                customer.setAddress(rs.getString("address"));
                customer.setPhone(rs.getString("phone"));
                customer.setAddressID(rs.getInt("addressId"));
                customers.add(customer);
            }
            customerTableView.setItems(customers);
        } catch (SQLException e) {
            System.err.print(e);
        }
        
        idCol.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getID()).asObject());
        nameCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        addressCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAddress()));
        phoneCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPhone()));
    }
    
    public void populateCustomers(Integer id) {
        customerTableView.getItems().clear();
        
        try {
            String sql = "SELECT customer.customerId, customer.customerName, "
                    + "address.address, address.phone, address.addressId "
                    + "FROM customer "
                    + "JOIN address "
                    + "ON customer.addressId = address.addressId "
                    + "WHERE customer.customerId = '" + id + "'";
            
            ResultSet rs = DBConnection.conn.createStatement().executeQuery(sql);
            
            while(rs.next()){
                Customer customer = new Customer();
                customer.setID(rs.getInt("customerId"));
                customer.setName(rs.getString("customerName"));
                customer.setAddress(rs.getString("address"));
                customer.setPhone(rs.getString("phone"));
                customer.setAddressID(rs.getInt("addressId"));
                customers.add(customer);
            }
            customerTableView.setItems(customers);
        } catch (SQLException e) {
            System.err.print(e);
        }
        
        idCol.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getID()).asObject());
        nameCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        addressCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAddress()));
        phoneCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPhone()));
    }
}
