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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.TimeZone;
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
public class RegionalReportsController implements Initializable {

    /**
     * Initializes the controller class.
     */
    private ToggleGroup toggleGroup;
    @FXML private ChoiceBox<String> locations = new ChoiceBox<String>();

    private ResultSet rs;
    private String sql;
    
    @FXML private TableView <Appointment> calendarTableView;
    @FXML private TableColumn <Appointment, String> titleCol;
    @FXML private TableColumn <Appointment, String> locationCol;
    @FXML private TableColumn <Appointment, Integer> customerCol;
    @FXML private RadioButton consultantRB;
    @FXML private RadioButton monthlyRB;
    @FXML private RadioButton regionalRB;
    
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
        
        toggleGroup.selectToggle(regionalRB);
        
        locationBox();
    }    
    
    public void consultantsRadioButtonSelected(ActionEvent event) {
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
    
    public void locationBox() {
        
        try{
            sql = "SELECT DISTINCT location FROM appointment ORDER BY location";
            rs = DBConnection.conn.createStatement().executeQuery(sql);
            while(rs.next())
            {
                locations.getItems().add(rs.getString("location"));
            }
            
            locations.showingProperty().addListener((obs, wasShowing, isNowShowing) -> 
            {
                locations.getSelectionModel().getSelectedItem();
                populateCalendar(locations.getSelectionModel().getSelectedItem());
            });
        } catch (SQLException e) {
            System.err.println(e);
        }
    }
    
    public void populateCalendar(String location) {
        calendarTableView.getItems().clear();
        
        try {
            sql = "SELECT location, description, COUNT(*) as total FROM appointment WHERE location = '" + location + "' GROUP BY description";
            rs = DBConnection.conn.createStatement().executeQuery(sql);
            
            while(rs.next()){
                Appointment appt = new Appointment();
                
                appt.setTitle(rs.getString("description"));
                appt.setLocation(rs.getString("location"));
                appt.setCustomer(rs.getInt("total"));
                
                if(rs.getString("location").equals(location))
                {
                    appointments.add(appt);
                }
            }
            calendarTableView.setItems(appointments);
        } catch (SQLException ex) {
            System.err.print(ex);
        }
     
        titleCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTitle()));
        locationCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getLocation()));
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
