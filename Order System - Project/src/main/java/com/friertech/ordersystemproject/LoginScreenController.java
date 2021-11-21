package com.friertech.ordersystemproject;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;

import static java.lang.System.out;

public class LoginScreenController implements Initializable {
    LoginHandler lh = new LoginHandler();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }
    @FXML
    private Button LoginButton;
    @FXML
    private TextField ID;
    @FXML
    private PasswordField PASSWORD;
    @FXML
    private Text LoginError;

    @FXML
    protected void onLoginButtonClick() throws IOException {
        String user = ID.getText().toString(), pass = PASSWORD.getText().toString();

        if(user.isBlank() || pass.isBlank() || !lh.requestIDConfirmation(user) || !lh.requestPasswordConfirmation(pass)){
            LoginError.setVisible(true);
            /** Invalid credentials */
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setHeaderText("An error has occurred");
            alert.setContentText("Ooops, seems like the credentials did not match! Please check username/password again!");

            alert.showAndWait();
        } else {
            out.println("Redirecting to servers");
            LoginError.setVisible(false);

            TextInputDialog dialog = new TextInputDialog("super secret code here");
            dialog.setTitle("Is it really you?");
            dialog.setHeaderText("Verification needed!");
            dialog.setContentText("Please enter your secret code:");

            // Traditional way to get the response value.
            Optional<String> result = dialog.showAndWait();
            if (result.isPresent()){
                out.println(result.get() + "," + "kevin");
                if(result.get().equals("kevin")){
                    // success
                    Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("DashScreen.fxml")));
                    Stage stage = (Stage) LoginButton.getScene().getWindow();
                    Scene dashScreen = new Scene(root);
                    stage.setResizable(false);
                    stage.setTitle("Order Management System");
                    stage.setScene(dashScreen);
                    stage.show();
                } else {
                    Alert alert = new Alert(AlertType.ERROR);
                    alert.setTitle("Error Dialog");
                    alert.setHeaderText("Verification failed!");
                    alert.showAndWait();
                }
            }

        }
    }
}