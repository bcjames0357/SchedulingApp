/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package schedulingapp;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Observable;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
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
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Callback;

/**
 * FXML Controller class
 *
 * @author brandon
 */
public class CalendarController implements Initializable {
    
    @FXML private TableView <Appointment> calendarTableView;
    @FXML private TableColumn <Appointment, String> titleCol;
    @FXML private TableColumn <Appointment, String> locationCol;
    @FXML private TableColumn <Appointment, LocalDate> dateCol;
    @FXML private TableColumn <Appointment, LocalTime> startCol;
    @FXML private TableColumn <Appointment, LocalTime> endCol;
    @FXML private TableColumn <Appointment, Integer> customerCol;
    @FXML private TableColumn <Appointment, Integer> idCol;
    
    @FXML private TextField titleField;
    @FXML private TextField locationField;
    @FXML private TextField startField; 
    @FXML private TextField durationField;
    @FXML private TextField customerField;
    @FXML private DatePicker datePicker;
    
    private final ObservableList<Appointment> appointments = FXCollections.observableArrayList(Appointment.extractor()); 
    
    private Integer id; 
    private String title;
    private String location;
    private LocalTime start;
    private Integer duration;
    private LocalTime end;
    private LocalDateTime startLDT;
    private LocalDateTime endLDT;
    private ZonedDateTime startUTC;
    private ZonedDateTime endUTC;
    private Timestamp startTs;
    private Timestamp endTs;
    private Integer customer;
    private LocalDate date;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    public CalendarController() {
        
    }
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        populateCalendar();
    }
    
    /**
     *
     * @param event
     */
    public void saveButtonPushed(ActionEvent event)
    {

        /* Retrieves ID of selected row to later reference database. If text
        *  field is blank, the current value of the cell is used for the SQL
        *  UPDATE statement. This avoids NullPointerException when the TextField
        *  is left empty when the Save button is pushed.
        */
        if(calendarTableView.getSelectionModel().isEmpty()){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Customer Update Error");
            alert.setContentText("Please select an appointment to update.");
            alert.showAndWait();
            return;
        }
        id = calendarTableView.getSelectionModel().getSelectedItem().getID();

        if(datePicker.getValue() == null) {
            date = calendarTableView.getSelectionModel().getSelectedItem().getDate();
        } else {
        date = datePicker.getValue();
        }
        if(startField.getText() == null || startField.getText().trim().isEmpty()) {
            start = calendarTableView.getSelectionModel().getSelectedItem().getStart();
        } else {
            System.out.println(startField.getText());
            start = LocalTime.parse(startField.getText(), timeFormatter);
        }
        if(durationField.getText() == null || durationField.getText().trim().isEmpty()) {
            end = calendarTableView.getSelectionModel().getSelectedItem().getEnd();
        } else {
            duration = Integer.parseInt(durationField.getText());
            end = start.plus(duration, ChronoUnit.HOURS);
        }

        startLDT = LocalDateTime.of(date, start);
        endLDT = LocalDateTime.of(date, end);

        startTs = Timestamp.valueOf(startLDT); //this value can be inserted into database
        endTs = Timestamp.valueOf(endLDT); //this value can be inserted into database        

        if(titleField.getText() == null || titleField.getText().trim().isEmpty()) {
            title = calendarTableView.getSelectionModel().getSelectedItem().getTitle();
        } else {
            title = titleField.getText();
        }
        if(locationField.getText() == null || locationField.getText().trim().isEmpty()) {
            location = calendarTableView.getSelectionModel().getSelectedItem().getLocation();
        } else {
            location = locationField.getText();
        }

        if(customerField.getText() == null || customerField.getText().trim().isEmpty()) {
            customer = calendarTableView.getSelectionModel().getSelectedItem().getCustomer();
        } else {
            customer = Integer.parseInt(customerField.getText());
        }

        String sql = "UPDATE appointment "
                + "SET title = '" + title
                + "', location = '" + location
                + "', start = '" + startTs
                + "', end = '" + endTs
                + "', customerId = '" + customer
                + "' WHERE appointmentId = '" + id + "'";
        
        try {
        Statement stmt = DBConnection.conn.createStatement();
        stmt.executeUpdate(sql);
        } catch (SQLException ex) {
            Logger.getLogger(CalendarController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        populateCalendar();
    }
    
    public void weekButtonPushed(ActionEvent event)
    {
        
    }
    
    public void monthButtonPushed(ActionEvent event)
    {
        
    }

    public void deleteButtonPushed(ActionEvent event)
    {
        Statement stmt = null;
        try {
            Integer removeId = calendarTableView.getSelectionModel().getSelectedItem().getID();
            String update = "DELETE FROM appointment WHERE appointmentId = " + removeId;
            stmt = DBConnection.conn.createStatement();
            stmt.executeUpdate(update);
        } catch (SQLException ex) {
            System.err.print(ex);
        } catch (NullPointerException ex) {
            System.err.println(ex);
        }
        populateCalendar();
    }
    
    public void addButtonPushed(ActionEvent event)
    {
        try
        {
            date = datePicker.getValue();
            start = LocalTime.parse(startField.getText(), timeFormatter);
            duration = Integer.parseInt(durationField.getText());
            end = start.plus(duration, ChronoUnit.HOURS);
            
            startLDT = LocalDateTime.of(date, start);
            endLDT = LocalDateTime.of(date, end);
            
            
            startTs = Timestamp.valueOf(startLDT); //this value can be inserted into database
            endTs = Timestamp.valueOf(endLDT); //this value can be inserted into database        

            title = titleField.getText();
            location = locationField.getText();
            
            
            customer = Integer.parseInt(customerField.getText());
            date = datePicker.getValue();
            
            
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Appointment Creation Error");
            alert.setContentText("Please enter a value for all fields.");
            alert.showAndWait();
        }
        
        try {
            String sql = "INSERT INTO appointment (customerId, "
                    + "title, "
                    + "description, "
                    + "location, "
                    + "contact, "
                    + "url, "
                    + "start, "
                    + "end, "
                    + "createDate, "
                    + "createdBy, "
                    + "lastUpdate, "
                    + "lastUpdateBy) "
                + "VALUES (" + customer
                + ", '" + title
                + "', ' "
                + "', '" + location
                + "', ' "
                + "', ' "
                + "', '" + startTs
                + "', '" + endTs
                + "', NOW()"
                + ", '" + LogInController.un_string
                + "', NOW()"
                + ", '" + LogInController.un_string +"')"; 
        Statement stmt = DBConnection.conn.createStatement();
        stmt.executeUpdate(sql);
        
        
        } catch (SQLException ex) {
            System.err.print(ex);
        }
        populateCalendar();
    }
    
    public void viewCustomerButtonPushed(ActionEvent event)
    {
        try {
            if(calendarTableView.getSelectionModel().isEmpty()) {
                CustomerManagementController.setViewAllBool(true);
            } else {
                CustomerManagementController.setViewAllBool(false);
                int viewID = calendarTableView.getSelectionModel().getSelectedItem().getCustomer();
                CustomerManagementController.setViewID(viewID);
            }
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(SchedulingApp.class.getResource("CustomerManagement.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.err.print(e);
        }
    }
    
    public void populateCalendar() {
        calendarTableView.getItems().clear();
        try {
            String sql = "SELECT appointmentId, title, location, start, end, customerId FROM appointment";
            ResultSet rs = DBConnection.conn.createStatement().executeQuery(sql);
            
            while(rs.next()){
                Appointment appt = new Appointment();
                appt.setID(rs.getInt("appointmentId"));
                appt.setTitle(rs.getString("title"));
                appt.setLocation(rs.getString("location"));
                appt.setDate(LocalDateTime.parse(rs.getString("start"), formatter).toLocalDate());
                appt.setStart(LocalDateTime.parse(rs.getString("start"), formatter).toLocalTime());
                appt.setEnd(LocalDateTime.parse(rs.getString("end"), formatter).toLocalTime());
                appt.setCustomer(rs.getInt("customerId"));
                
                appointments.add(appt);
            }
            calendarTableView.setItems(appointments);
        } catch (SQLException ex) {
            System.err.print(ex);
        }
        
        idCol.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getID()).asObject());
        titleCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTitle()));
        locationCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getLocation()));
        dateCol.setCellValueFactory(new PropertyValueFactory<Appointment, LocalDate>("date"));
        startCol.setCellValueFactory(new PropertyValueFactory<Appointment, LocalTime>("start"));
        endCol.setCellValueFactory(new PropertyValueFactory<Appointment, LocalTime>("end"));
        customerCol.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getCustomer()).asObject());
    }
}
    

