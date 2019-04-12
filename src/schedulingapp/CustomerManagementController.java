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
import java.sql.Statement;
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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author brandon
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
                        + "', ' ', '1', '00000', '" + phone 
                        + "', NOW(),"
                        + "'" + LogInController.un_string 
                        + "', NOW(),"
                        + "'" + LogInController.un_string + "')";
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
                + "', '" + addressID
                + "', '1"
                + "', NOW(),"
                + "'" + LogInController.un_string 
                + "', NOW(),"
                + "'" + LogInController.un_string + "')";
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
        try {
            Integer removeId = customerTableView.getSelectionModel().getSelectedItem().getID();
            String update = "DELETE FROM customer WHERE customerId = " + removeId;
            stmt = DBConnection.conn.createStatement();
            stmt.executeUpdate(update);
        } catch (SQLException ex) {
            System.err.print(ex);
        } catch (NullPointerException ex) {
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