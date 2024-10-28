/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package movie.ticket.booking.system;

import java.io.IOException;
import movie.ticket.booking.system.database; 
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 *
 * @author ASUS
 */
public class FXMLDocumentController implements Initializable {
    
   @FXML
    private Label label;

    @FXML
    private Button signIn_close;

    @FXML
    private Hyperlink signIn_createAccount;

    @FXML
    private AnchorPane signIn_form;

    @FXML
    private Button signIn_loginBtn;

    @FXML
    private Button signIn_minimize;

    @FXML
    private PasswordField signIn_password;

    @FXML
    private TextField signIn_username;

    @FXML
    private Hyperlink signUp_alreadyHaveAccount;

    @FXML
    private Button signUp_btn;

    @FXML
    private Button signUp_close;

    @FXML
    private TextField signUp_email;

    @FXML
    private AnchorPane signUp_form;

    @FXML
    private Button signUp_minimize;

    @FXML
    private PasswordField signUp_password;

    @FXML
    private TextField signUp_username;
    
    private Connection connect;
    private PreparedStatement prepare;
    private Statement statement;
    private ResultSet result;
    
    public void signIn() throws IOException {
    String sql = "SELECT * FROM admin WHERE username = ? AND password = ?";

    connect = database.connectDb();
    if (connect == null) {
        System.out.println("Database connection failed.");
        return;
    }

    try {
        prepare = connect.prepareStatement(sql);
        prepare.setString(1, signIn_username.getText());
        prepare.setString(2, signIn_password.getText());

        result = prepare.executeQuery();

        Alert alert;

        if (signIn_username.getText().isEmpty() || signIn_password.getText().isEmpty()) {
            alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error Message");
            alert.setHeaderText(null);
            alert.setContentText("Please fill all the blank fields");
            alert.showAndWait();
        } else {
            if (result.next()) {

                Parent root = FXMLLoader.load(getClass().getResource("dashboard.fxml"));
                
                Stage stage = (Stage) signIn_username.getScene().getWindow(); // Get the current stage
                Scene scene = new Scene(root);
                
                stage.setScene(scene);
                stage.show();

                alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Information Message");
                alert.setHeaderText(null);
                alert.setContentText("Successfully Logged In!");
                alert.showAndWait();
            } else {
                alert = new Alert(AlertType.ERROR);
                alert.setTitle("Error Message");
                alert.setHeaderText(null);
                alert.setContentText("Wrong Username or Password.");
                alert.showAndWait();
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
        System.out.println("SQL Error: " + e.getMessage());
    } finally {
        try {
            if (result != null) result.close();
            if (prepare != null) prepare.close();
            if (connect != null) connect.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}

    public void signUp() {
    String sqlCheck = "SELECT * FROM admin WHERE username = ? OR email = ?";
    String sqlInsert = "INSERT INTO admin (username, password, email) VALUES (?, ?, ?)";

    connect = database.connectDb();
    if (connect == null) {
        System.out.println("Database connection failed.");
        return;
    }

    try {
        if (signUp_username.getText().isEmpty() || signUp_password.getText().isEmpty() || signUp_email.getText().isEmpty()) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error Message");
            alert.setHeaderText(null);
            alert.setContentText("Please fill all the blank fields");
            alert.showAndWait();
            return; 
        }
        
        String username = signUp_username.getText();
        if (!isValidName(username)) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error Message");
            alert.setHeaderText(null);
            alert.setContentText("Name must start with a capital letter.");
            alert.showAndWait();
            return;
        }

        if (signUp_password.getText().length() < 8) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error Message");
            alert.setHeaderText(null);
            alert.setContentText("Password must be at least 8 characters long.");
            alert.showAndWait();
            return;
        }

        String email = signUp_email.getText();
        if (!isValidEmail(email)) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error Message");
            alert.setHeaderText(null);
            alert.setContentText("Please enter a valid email address.");
            alert.showAndWait();
            return;
        }

        prepare = connect.prepareStatement(sqlCheck);
        prepare.setString(1, username);
        prepare.setString(2, email);
        result = prepare.executeQuery();

        if (result.next()) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error Message");
            alert.setHeaderText(null);
            alert.setContentText("Username or Email already exists.");
            alert.showAndWait();
            return;
        }
        
        prepare = connect.prepareStatement(sqlInsert);
        prepare.setString(1, username);
        prepare.setString(2, signUp_password.getText());
        prepare.setString(3, email);

        int rowsAffected = prepare.executeUpdate();

        Alert alert;
        if (rowsAffected > 0) {
            alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Information Message");
            alert.setHeaderText(null);
            alert.setContentText("Successfully created a new account!");
            alert.showAndWait();
        } else {
            alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error Message");
            alert.setHeaderText(null);
            alert.setContentText("Sign Up Failed. Please try again.");
            alert.showAndWait();
        }
    } catch (SQLException e) {
        e.printStackTrace();
        System.out.println("SQL Error: " + e.getMessage());
    } finally {
        try {
            if (result != null) result.close();
            if (prepare != null) prepare.close();
            if (connect != null) connect.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}

private boolean isValidName(String name) {
    String[] parts = name.split(" ");
    for (String part : parts) {
        if (part.isEmpty() || !Character.isUpperCase(part.charAt(0))) {
            return false;
        }
    }
    return true;
}

private boolean isValidEmail(String email) {
    String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    return email.matches(emailRegex);
}
    
    public void switchForm(ActionEvent event){
        if (event.getSource()== signIn_createAccount){
            signIn_form.setVisible(false);
            signUp_form.setVisible(true);
            
        } else if(event.getSource()==signUp_alreadyHaveAccount){
            signIn_form.setVisible(true);
            signUp_form.setVisible(false);
        }
    }
    
    public void signIn_close(){
        System.exit(0);   
        
    }
    public void signIn_minimize(){
        Stage stage = (Stage)signIn_form.getScene().getWindow();
        stage.setIconified(true);
        
    }
    public void signUp_close(){
        System.exit(0);
        
    }
    public void signUp_minimize(){
        Stage stage = (Stage)signUp_form.getScene().getWindow();
        stage.setIconified(true);
        
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
}
