package schedulingapp;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
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
    private String alertTitle;
    private String alertText;
    
    public void submitButtonPushed(ActionEvent event)
    {
        try {
            un_string = username.getText();
            pw_string = password.getText();
            if(username.getText().trim().isEmpty() || password.getText().trim().isEmpty()) {
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
            if(rs.next() == true)
            {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(SchedulingApp.class.getResource("Calendar.fxml"));
                Parent root = loader.load();
                Scene scene = new Scene(root);
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(scene);
                stage.show();
                
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle(alertTitle);
                alert.setContentText(alertText);
                alert.showAndWait();
            }
        } catch (SQLException e) {
            System.err.print(e);
        } catch (IOException ex) {
            System.err.println(ex);
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
    
    public void detectLocale() {
        
    }
    
    public static String getUsername(){
        return un_string;
    }
    
    public static Integer getUserID() {
        return userID;
    }
}
