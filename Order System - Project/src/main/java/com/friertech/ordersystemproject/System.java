package com.friertech.ordersystemproject;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class System extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("LoginScreen.fxml")));
        Scene loginScreen = new Scene(root);
        stage.setResizable(false);
        stage.setTitle("Order Management System");
        stage.setScene(loginScreen);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}