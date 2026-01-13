package controller.signUpController;

import db.DBConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SignUpFormController {

    @FXML
    private TextField txtEmail;

    @FXML
    private TextField txtFirstName;

    @FXML
    private TextField txtLastName;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private PasswordField txtReEnterPassword;

    @FXML
    void btnOnActionBacktoLogin(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/LoginForm.fxml"));
            Stage stage = (Stage) txtEmail.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    @FXML
    void btnOnActionRegister(ActionEvent event) {
        String firstName = txtFirstName.getText().trim();
        String lastName = txtLastName.getText().trim();
        String email = txtEmail.getText().trim();
        String password = txtPassword.getText();
        String rePassword = txtReEnterPassword.getText();

        if(firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty() || rePassword.isEmpty()){
            showAlert("Error","All fields must be filled in!");
            return;
        }

        if(!email.endsWith("@gmail.com")){
            showAlert("Error","The email must end with @gmail.com!");
            return;
        }


        if(!password.equals(rePassword)){
            showAlert("Error","Password does not match!");
            return;
        }

        if(!isValidPassword(password)){
            showAlert("Error","Password must be at least 8 characters, Uppercase, Lowercase, Special character (!@#$%^&*)!");
            return;
        }

        if(userExists(email)){
            showAlert("Error","Email is already registered!");
            return;
        }

        if(registerUser(firstName,lastName,email,password)){
            showAlert("Success","Registration successful! Please login.");
            try {
                Parent root = FXMLLoader.load(getClass().getResource("/view/login.fxml"));
                Stage stage = (Stage) txtEmail.getScene().getWindow();
                stage.setScene(new Scene(root));
            } catch (Exception e){
                e.printStackTrace();
            }
        } else {
            showAlert("Error","Registration failed. Please try again.");
        }
    }

    private boolean userExists(String email){
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            String sql = "SELECT id FROM userDetails WHERE email=?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1,email);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    private boolean registerUser(String firstName, String lastName, String email, String password){
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            String sql = "INSERT INTO userDetails(FirstName,LastName,Email,Password) VALUES(?,?,?,?)";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1,firstName);
            preparedStatement.setString(2,lastName);
            preparedStatement.setString(3,email);
            preparedStatement.setString(4, BCrypt.hashpw(password,BCrypt.gensalt()));
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    private boolean isValidPassword(String password){
        String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*]).{8,}$";
        return password.matches(regex);
    }

    private void showAlert(String title, String message){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
