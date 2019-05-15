/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package schedulingapp;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.TimeZone;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Brandon James <bcjames0357@gmail.com>
 */
public class ReportsController implements Initializable {

    private ToggleGroup toggleGroup;
    @FXML private ChoiceBox<String> consultants = new ChoiceBox<String>();
    private ObservableList<String> userArray = FXCollections.observableArrayList();
    private ArrayList<String> locationArray = new ArrayList<String>();
    private ArrayList<String> monthArray = new ArrayList<String>();
    private ResultSet rs;
    private String sql;
    private final Integer offset = (((TimeZone.getDefault().getRawOffset())/1000)/60/60);
    
    @FXML private TableView <Appointment> calendarTableView;
    @FXML private TableColumn <Appointment, String> titleCol;
    @FXML private TableColumn <Appointment, String> locationCol;
    @FXML private TableColumn <Appointment, LocalDate> dateCol;
    @FXML private TableColumn <Appointment, LocalTime> startCol;
    @FXML private TableColumn <Appointment, LocalTime> endCol;
    @FXML private TableColumn <Appointment, Integer> customerCol;
    @FXML private TableColumn <Appointment, Integer> idCol;
    @FXML private RadioButton consultantRB;
    @FXML private RadioButton monthlyRB;
    @FXML private RadioButton regionalRB;
    
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private LocalDate date;
    private final ObservableList<Appointment> appointments = FXCollections.observableArrayList(Appointment.extractor());
    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Type by month
        // Schedule for each user
        // Type by location
        
        toggleGroup = new ToggleGroup();
        consultantRB.setToggleGroup(toggleGroup);
        monthlyRB.setToggleGroup(toggleGroup);
        regionalRB.setToggleGroup(toggleGroup);
        
        toggleGroup.selectToggle(consultantRB);
        
        userBox();
    }    
    
    public void userBox() {
        
        try{
            sql = "SELECT DISTINCT contact FROM appointment";
            rs = DBConnection.conn.createStatement().executeQuery(sql);
            String cons = "";
            while(rs.next())
            {
                cons = rs.getString("contact").toLowerCase();
                cons = Character.toString(cons.charAt(0)).toUpperCase() 
                        + cons.substring(1,cons.length());
                System.out.println(cons);
                consultants.getItems().add(cons);
            }
            
            consultants.showingProperty().addListener((obs, wasShowing, isNowShowing) -> 
            {
                try{
                    consultants.getSelectionModel().getSelectedItem();
                    populateCalendar(consultants.getSelectionModel().getSelectedItem());
                } catch(NullPointerException e) {} // Ignore click if no item selected

            });
        } catch (SQLException e) {
            System.err.println(e);
        }
    }
    
    /**
     *
     * @param event
     */
    public void monthlyRadioButtonSelected(ActionEvent event) {
        try{
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(SchedulingApp.class.getResource("MonthlyReports.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.err.println(e);
        }
    }
    
    
    public void regionalRadioButtonSelected(ActionEvent event) {
        try{
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(SchedulingApp.class.getResource("RegionalReports.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.err.println(e);
        }
    }
    
    public void populateCalendar(String user) {
        calendarTableView.getItems().clear();
        
        try {
            sql = "SELECT contact, appointmentId, title, location, start, end, customerId FROM appointment";
            rs = DBConnection.conn.createStatement().executeQuery(sql);
            
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
                
                if(rs.getString("contact").toLowerCase().equals(user.toLowerCase()))
                {
                    appointments.add(appt);
                }
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
}
