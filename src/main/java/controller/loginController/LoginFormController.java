package controller.loginController;

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
import model.dto.UserDTO;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginFormController {

    @FXML
    private TextField txtEmail;

    @FXML
    private PasswordField txtPassword;

    @FXML
    void btnOnActionSignIn(ActionEvent event) {
        String email = txtEmail.getText().trim();
        String password = txtPassword.getText().trim();

        if(email.isEmpty() || password.isEmpty()){
            showAlert("Error","The Email and Password fields cannot be blank!");
            return;
        }

        if(!email.endsWith("@gmail.com")){
            showAlert("Error","The email must end with @gmail.com!");
            return;
        }

        if(!userExists(email)){
            showAlert("Info","User does not exist. Please sign up.");
            return;
        }

        UserDTO userDTO = authenticate(email, password);
        if(userDTO != null){
            showAlert("Success","Login successful!");
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/WelcomeForm.fxml"));
                Parent root = loader.load();

                controller.welcomeController.WelcomeFormController welcomeController = loader.getController();
                welcomeController.setUserName(userDTO.getFirstName());

                Stage stage = (Stage) txtEmail.getScene().getWindow();
                stage.setScene(new Scene(root));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            showAlert("Error","Password incorrect!");
        }


    }

    @FXML
    void btnOnActionCreateAccount(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/SignUpForm.fxml"));
            Stage stage = (Stage) txtEmail.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean userExists(String email){
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            String sql = "SELECT id FROM userDetails WHERE email=?";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        } catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    private UserDTO authenticate(String email, String password){
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            String sql = "SELECT * FROM userDetails WHERE email=?";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                String storedHash = resultSet.getString("Password");
                if(BCrypt.checkpw(password, storedHash)){
                    return new UserDTO(
                            resultSet.getInt("ID"),
                            resultSet.getString("FirstName"),
                            resultSet.getString("LastName"),
                            resultSet.getString("Email"),
                            storedHash
                    );
                }
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    private void showAlert(String title, String message){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
