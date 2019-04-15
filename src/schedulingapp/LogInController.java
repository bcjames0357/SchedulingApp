package schedulingapp;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author brandon <bcjames035@gmail.com>
 */
public class LogInController implements Initializable {
    
    @FXML private TextField username; 
    @FXML private TextField password;
    @FXML private Button searchButton;
    private static String un_string;
    private static String pw_string;
    private static Integer userID;
    private final Locale locale = Locale.getDefault();
    private Integer offset = (((TimeZone.getDefault().getRawOffset())/1000)/60/60);
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private String alertTitle;
    private String alertText;
    private BufferedWriter _eventWriter;
    private File _file = new File("userlog.txt");
    
    public void submitButtonPushed(ActionEvent event) throws LoginException, LogFileException
    {
        try {
            
            un_string = username.getText();
            pw_string = password.getText();
            if(username.getText().trim().isEmpty() || password.getText().trim().isEmpty()) {
                alertTitle = "Login error";
                alertText = "Please enter a username and password.";
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle(alertTitle);
                alert.setContentText(alertText);
                alert.showAndWait();
                return;
            }
        } catch (NullPointerException e) {
            System.err.println(e); 
        }
        String sql = 
                "SELECT userName, password, userId "
                + "FROM user "
                + "WHERE userName = \"" + un_string 
                + "\" AND password = \"" + pw_string + "\"";
        ResultSet rs;
        
        try {
            rs = DBConnection.conn.createStatement().executeQuery(sql);
            if(rs.next())
            {
                userID = rs.getInt("userId");
                String log = "successful login";
                LocalDateTime ts = LocalDateTime.now();
                logEvent(ts, log, un_string);
                appointmentSoon();
                
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(SchedulingApp.class.getResource("Calendar.fxml"));
                Parent root = loader.load();
                Scene scene = new Scene(root);
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(scene);
                stage.show();
                
            } else {
                alertTitle = "Login error";
                alertText = "Invalid username or password.";
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle(alertTitle);
                alert.setContentText(alertText);
                alert.showAndWait();
            }
        } catch (SQLException e) {
            throw new LoginException(e);
        } catch (IOException e) {
            throw new LogFileException(e);
        }
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        if(locale.getLanguage().equals(new Locale("de").getLanguage())){
            searchButton.setText("Suche");
            username.setPromptText("Nutzername");
            password.setPromptText("Passwort");
            alertTitle = "Login Fehler";
            alertText = "Ungültige Login-Referenzen";
        }
    }
    
    public static String getUsername(){
        return un_string;
    }
    
    public static Integer getUserID() {
        return userID;
    }
    
    public void appointmentSoon() throws SQLException {
        ResultSet rs = DBConnection.conn.createStatement().executeQuery("SELECT start, appointmentId FROM appointment");
        LocalDateTime ldtStart; 
        LocalDateTime now = LocalDateTime.now();
        
        while(rs.next())
        {
            ldtStart = LocalDateTime.parse(rs.getString("start"), formatter).plus(offset, ChronoUnit.HOURS);
            long until = now.until(ldtStart, ChronoUnit.MINUTES);
            
            if(until <= 15 && until > 0){
                int apptId = rs.getInt("appointmentId");
                Alert alert = new Alert (Alert.AlertType.INFORMATION);
                alert.setTitle("Upcoming appointment!");
                alert.setContentText("Appointment #" + apptId + " is starting soon!");
                alert.showAndWait();
            } else if(until > -15 && until <= 0) {
                int apptId = rs.getInt("appointmentId");
                Alert alert = new Alert (Alert.AlertType.INFORMATION);
                alert.setTitle("Upcoming appointment!");
                alert.setContentText("Appointment #" + apptId + " has already started!");
                alert.showAndWait();
            }
        }
    }
    
     public void logEvent(LocalDateTime time, String event, String user)throws IOException{

        if(_eventWriter == null)

            _eventWriter = new BufferedWriter(new FileWriter(_file, true));

        

        try {

            _eventWriter.write(time + "|  " + event + " -> " + user + " ");

            _eventWriter.newLine();

        } 

        catch(IOException ex){

            //Logger.getLogger(EventLogger.class.getName()).log(Level.SEVERE, null, ex);

        } 

        finally{

          if (_eventWriter != null) 

            _eventWriter.close();

        }

    }
}
