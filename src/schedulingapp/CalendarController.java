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
import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.TimeZone;
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
import javafx.scene.control.DatePicker;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import schedulingapp.LogInController;

/**
 * FXML Controller class
 *
 * @author brandon <bcjames035@gmail.com>
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
    
    @FXML private RadioButton allRB;
    @FXML private RadioButton weekRB;
    @FXML private RadioButton monthRB;
    
    private ToggleGroup toggleGroup;
    
    private final ObservableList<Appointment> appointments = FXCollections.observableArrayList(Appointment.extractor()); 
    
    private Integer id; 
    private String title;
    private String location;
    private LocalTime start;
    private int duration;
    private LocalTime end;
    private LocalDateTime startLDT;
    private LocalDateTime endLDT;
    private Timestamp startTs;
    private Timestamp endTs;
    private Integer customer;
    private LocalDate date;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
    private Integer offset = (((TimeZone.getDefault().getRawOffset())/1000)/60/60); // Convers miliseconds to seconds, then minutes, then hours
    
    private ArrayList<LocalDate> dateArray = new ArrayList<LocalDate>();
    private ArrayList<LocalTime> startArray = new ArrayList<LocalTime>();
    private ArrayList<LocalTime> endArray = new ArrayList<LocalTime>();
    private ArrayList<Integer> idArray = new ArrayList<Integer>();
    
    private LocalTime openT;
    private LocalDateTime open;
    private LocalDateTime close;
    
    public CalendarController() {
        
    }
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        toggleGroup = new ToggleGroup();
        allRB.setToggleGroup(toggleGroup);
        weekRB.setToggleGroup(toggleGroup);
        monthRB.setToggleGroup(toggleGroup);
        
        toggleGroup.selectToggle(allRB);
        
        populateCalendar();
    }
    
    /**
     *
     * @param event
     */
    
    public void reportsButtonPushed(ActionEvent event)
    {
        try{
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(SchedulingApp.class.getResource("Reports.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.err.println(e);
        }
    }
    public void saveButtonPushed(ActionEvent event)
    {

        /* Retrieves ID of selected row to later reference database. If text
        *  field is blank, the current value of the cell is used for the SQL
        *  UPDATE statement. This avoids NullPointerException when the TextField
        *  is left empty when the Save button is pushed.
        */
        if(calendarTableView.getSelectionModel().isEmpty()){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Appointment Update Error");
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
            start = LocalTime.parse(startField.getText(), timeFormatter);
        }
        if(durationField.getText() == null || durationField.getText().trim().isEmpty()) {
            if(!(startField.getText() == null || startField.getText().trim().isEmpty()))
            {
                start = calendarTableView.getSelectionModel().getSelectedItem().getStart();
                end = calendarTableView.getSelectionModel().getSelectedItem().getEnd();
                duration = (int) ChronoUnit.HOURS.between(start, end);
                start = LocalTime.parse(startField.getText(), timeFormatter);
                endLDT = LocalDateTime.of(date, start).plus(duration, ChronoUnit.HOURS).plus(-offset, ChronoUnit.HOURS);
            } else {
            end = calendarTableView.getSelectionModel().getSelectedItem().getEnd();
            endLDT = LocalDateTime.of(date, end).plus(-offset, ChronoUnit.HOURS);
            }
        } else {
            duration = Integer.parseInt(durationField.getText());
            endLDT = LocalDateTime.of(date, start).plus(duration, ChronoUnit.HOURS).plus(-offset,ChronoUnit.HOURS);
        }

        startLDT = LocalDateTime.of(date, start).plus(-offset,ChronoUnit.HOURS);

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
        if(noOverlap(id) && inBusinessHours())
        {
            try {
            Statement stmt = DBConnection.conn.createStatement();
            stmt.executeUpdate(sql);
            } catch (SQLException ex) {
                Logger.getLogger(CalendarController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    
        populateCalendar();
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
            
            startLDT = LocalDateTime.of(date, start).plus(-offset,ChronoUnit.HOURS);
            endLDT = LocalDateTime.of(date, end).plus(-offset,ChronoUnit.HOURS);
            
            
            startTs = Timestamp.valueOf(startLDT); //this value can be inserted into database
            endTs = Timestamp.valueOf(endLDT); //this value can be inserted into database        

            title = titleField.getText();
            location = locationField.getText();
            
            
            customer = Integer.parseInt(customerField.getText());            
            
            if(noOverlap() && inBusinessHours())
            {
                String sql = 
                        "INSERT INTO appointment ("
                        + "customerId, "
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
                        + "lastUpdateBy, "
                        + "type, "
                        + "userId) "
                        + "VALUES ('" + customer
                        + "', '" + title
                        + "', ' "
                        + "', '" + location
                        + "', ' "
                        + "', ' "
                        + "', '" + startTs
                        + "', '" + endTs
                        + "', NOW()"
                        + ", '" + LogInController.getUsername()
                        + "', NOW()"
                        + ", '" + LogInController.getUsername()
                        + "', ' "
                        + "', '" + LogInController.getUserID() + "')";
            Statement stmt = DBConnection.conn.createStatement();
            stmt.executeUpdate(sql);
            }

        } catch (SQLException ex) {
            System.err.print(ex);
        } catch (NullPointerException e) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Appointment Creation Error");
            alert.setContentText("Please enter a value for all fields.");
            alert.showAndWait();
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
    
    public void allRadioButtonSelected() {
        populateCalendar();
    }
    
    public void weekRadioButtonSelected() {
        populateCalendar(7);
    }
    
    public void monthRadioButtonSelected() {
        populateCalendar(30);
    }
    
    public void populateCalendar() {
        calendarTableView.getItems().clear();
        dateArray.clear();
        startArray.clear();
        endArray.clear();
        idArray.clear();
        
        try {
            String sql = "SELECT appointmentId, title, location, start, end, customerId FROM appointment";
            ResultSet rs = DBConnection.conn.createStatement().executeQuery(sql);
            
            while(rs.next()){
                Appointment appt = new Appointment();
                LocalDateTime ldtStart = LocalDateTime.parse(rs.getString("start"), formatter).plus(offset, ChronoUnit.HOURS);
                date = ldtStart.toLocalDate();
                LocalTime ltStart= (LocalDateTime.parse(rs.getString("start"), formatter).toLocalTime()).plus(offset, ChronoUnit.HOURS);
                LocalTime ltEnd = (LocalDateTime.parse(rs.getString("end"), formatter).toLocalTime()).plus(offset, ChronoUnit.HOURS);
                
                appt.setID(rs.getInt("appointmentId"));
                appt.setTitle(rs.getString("title"));
                appt.setLocation(rs.getString("location"));
                appt.setDate(date);
                appt.setStart(ltStart);
                appt.setEnd(ltEnd);
                appt.setCustomer(rs.getInt("customerId"));
                
                dateArray.add(date);
                startArray.add(ltStart);
                endArray.add(ltEnd);
                idArray.add(appt.getID());
                
                appointments.add(appt);
            }
            calendarTableView.setItems(appointments);
        } catch (SQLException ex) {
            System.err.print(ex);
        }
     
        // This lambda expression was used to improve the readability of the code
        // as well as to decrease overall .JAR file size by avoiding additional 
        // .CLASS files from inner classes.
        idCol.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getID()).asObject());

        // This lambda expression was used to improve the readability of the code
        // as well as to decrease overall .JAR file size by avoiding additional 
        // .CLASS files from inner classes.
        titleCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTitle()));
        locationCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getLocation()));
        dateCol.setCellValueFactory(new PropertyValueFactory<Appointment, LocalDate>("date"));
        startCol.setCellValueFactory(new PropertyValueFactory<Appointment, LocalTime>("start"));
        endCol.setCellValueFactory(new PropertyValueFactory<Appointment, LocalTime>("end"));
        customerCol.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getCustomer()).asObject());
    
    }
    
    public void populateCalendar(int days) {
        calendarTableView.getItems().clear();
        dateArray.clear();
        startArray.clear();
        endArray.clear();
        idArray.clear();
        
        try {
            String sql = "SELECT appointmentId, title, location, start, end, customerId FROM appointment";
            ResultSet rs = DBConnection.conn.createStatement().executeQuery(sql);
            LocalDate now = LocalDate.now();
            LocalDate date;
            LocalDate cutoff = now.plus(days, ChronoUnit.DAYS);
            
            while(rs.next()){
                Appointment appt = new Appointment();
                LocalDateTime ldtStart = LocalDateTime.parse(rs.getString("start"), formatter).plus(offset, ChronoUnit.HOURS);
                date = ldtStart.toLocalDate();
                LocalTime ltStart= (LocalDateTime.parse(rs.getString("start"), formatter).toLocalTime()).plus(offset, ChronoUnit.HOURS);;
                LocalTime ltEnd = (LocalDateTime.parse(rs.getString("end"), formatter).toLocalTime()).plus(offset, ChronoUnit.HOURS);
                
                appt.setID(rs.getInt("appointmentId"));
                appt.setTitle(rs.getString("title"));
                appt.setLocation(rs.getString("location"));
                appt.setDate(date);
                appt.setStart(ltStart);
                appt.setEnd(ltEnd);
                appt.setCustomer(rs.getInt("customerId"));
                
                dateArray.add(date);
                startArray.add(ltStart);
                endArray.add(ltEnd);
                idArray.add(appt.getID());
                
                if(date.isAfter(now) && date.isBefore(cutoff)) 
                {    
                    appointments.add(appt);
                }
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
    
    public boolean noOverlap(Integer id) {
        for(int i = 0; i < appointments.size(); i++) {
            if(id.equals(idArray.get(i))) // Returns true (no overlap, okay to proceed)
            {                             // if comparing an appointment to itself
             return true;
            } else {
                if(date.equals(dateArray.get(i)))
                {
                    if((start.isAfter(startArray.get(i)) && start.isBefore(endArray.get(i)))
                            || (end.isAfter(startArray.get(i)) && end.isBefore(endArray.get(i)))
                            || start.equals(startArray.get(i)) || end.equals(endArray.get(i)))
                        {
                            Alert alert = new Alert(Alert.AlertType.WARNING);
                            alert.setTitle("Appointment Creation Error");
                            alert.setContentText("An existing appointment conflics with this time.");
                            alert.showAndWait();
                            return false;
                        }
                }
            }
        }
        return true;
    }
    
    public boolean noOverlap() {
        for(int i = 0; i < appointments.size(); i++) {
            if(date.equals(dateArray.get(i)))
            {
                if((start.isAfter(startArray.get(i)) && start.isBefore(endArray.get(i)))
                        || (end.isAfter(startArray.get(i)) && end.isBefore(endArray.get(i)))
                        || start.equals(startArray.get(i)) || end.equals(endArray.get(i)))
                    {
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("Appointment Creation Error");
                        alert.setContentText("An existing appointment conflics with this time.");
                        alert.showAndWait();
                        return false;
                    }
            }
        }
        return true;
    }
    
    public boolean inBusinessHours() {
        openT = LocalTime.parse("08:00", timeFormatter).plus(-offset,ChronoUnit.HOURS);
        open = LocalDateTime.of(date, openT);
        close = LocalDateTime.of(date, openT).plus(10, ChronoUnit.HOURS);
        
        if(date.getDayOfWeek().equals(DayOfWeek.SATURDAY) 
                || date.getDayOfWeek().equals(DayOfWeek.SUNDAY)
                || startLDT.isBefore(open)
                || endLDT.isAfter(close))
        {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Appointment Update Error");
            alert.setContentText("Please select a date and time during normal business hours.");
            alert.showAndWait();
            return false;
        }
        return true;
    }
}
