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
public class MonthlyReportsController implements Initializable {

    /**
     * Initializes the controller class.
     */
    private ToggleGroup toggleGroup;
    @FXML private ChoiceBox<String> months = new ChoiceBox<String>();
    private ResultSet rs;
    private String sql;
    
    @FXML private TableView <Appointment> calendarTableView;
    @FXML private TableColumn <Appointment, String> titleCol;
    @FXML private TableColumn <Appointment, String> locationCol;
    @FXML private TableColumn <Appointment, Integer> customerCol;
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
        sql = "SELECT title, description, location, MONTH(start), contact FROM appointment";
        
        // Type by month
        // Schedule for each user
        // Type by location
        
        toggleGroup = new ToggleGroup();
        consultantRB.setToggleGroup(toggleGroup);
        monthlyRB.setToggleGroup(toggleGroup);
        regionalRB.setToggleGroup(toggleGroup);
        
        toggleGroup.selectToggle(monthlyRB);
        
        monthBox();
    }
    
    public void monthBox() {
        
        
        try{
            sql = "SELECT DISTINCT MONTHNAME(start) as \"Month\", MONTH(start) as \"month_\" FROM appointment ORDER BY month_";
        
            rs = DBConnection.conn.createStatement().executeQuery(sql);
            while(rs.next())
            {
                months.getItems().add(rs.getString("Month"));
            }
            months.showingProperty().addListener((obs, wasShowing, isNowShowing) -> 
            {
                try{
                    months.getSelectionModel().getSelectedItem();
                    populateCalendar(months.getSelectionModel().getSelectedItem());
                } catch (NullPointerException e){} // Ignore click if no item selected
            });
        } catch (SQLException e) {
            System.err.println(e);
        }        
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
    
    public void populateCalendar(String month) {
        calendarTableView.getItems().clear();
        
        try {
            sql = "SELECT MONTHNAME(start) as month, description, COUNT(*) AS 'total' FROM appointment WHERE MONTHNAME(start) = '" + month + "' GROUP BY description";
            rs = DBConnection.conn.createStatement().executeQuery(sql);
            
            while(rs.next()){
                Appointment appt = new Appointment();
                
                appt.setTitle(rs.getString("month"));
                appt.setLocation(rs.getString("description"));
                appt.setCustomer(rs.getInt("total"));
                
                //if(rs.getString("month").equals(month)){
                    appointments.add(appt);
                   // System.out.println("Month Pop Cal, rs month = " + rs.getString("month"));
                //}
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
