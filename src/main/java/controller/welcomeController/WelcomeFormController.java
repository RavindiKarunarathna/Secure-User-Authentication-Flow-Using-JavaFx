package controller.welcomeController;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class WelcomeFormController {

    @FXML
    private TextField txtFirstName;

    @FXML
    void btnOnActionLogOut(ActionEvent event) {
        Stage stage = (Stage) txtFirstName.getScene().getWindow();
        stage.close();
    }

    public void setUserName(String firstName) {
        txtFirstName.setText("Welcome, " + firstName +" !!");
        txtFirstName.setEditable(false);
    }

}
