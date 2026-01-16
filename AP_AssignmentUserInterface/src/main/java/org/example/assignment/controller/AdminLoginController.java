package org.example.assignment.controller;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.assignment.AdminDatabase;
import org.example.assignment.AdminSceneSwitcher;

import java.io.IOException;

public class AdminLoginController extends Application {

    @FXML
    private TextField userField;  // matches FXML fx:id="userField"

    @FXML
    private PasswordField passwordField;  // matches FXML fx:id="passwordField"

    @FXML
    private Label loginValidationLabel;  // matches FXML fx:id="loginValidationLabel"

    @FXML
    private Button loginButton;  // matches FXML fx:id="loginButton"

    protected Stage stage;
    protected Scene scene;

    @FXML
    private void setToAdminRegister(ActionEvent event) throws IOException {
        AdminSceneSwitcher.switchScene(event, "/org/example/assignment/Adminregisterpage.fxml");
    }

    @FXML
    private void handleAdminLogin(ActionEvent event) throws IOException {
        String username = userField.getText();   // ✅ fixed variable name
        String password = passwordField.getText();  // ✅ fixed variable name

        if (AdminDatabase.validateLogin(username, password)) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/assignment/AdminDashboard.fxml"));
            Parent root = loader.load();
            AdminDashboardController controller = loader.getController();
            controller.setAdminUsername(username);

            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            scene = new Scene(root, 800, 600);
            stage.setScene(scene);
            stage.show();
        } else {
            loginValidationLabel.setText("Invalid admin username or password.");
            loginValidationLabel.setStyle("-fx-text-fill: #b30000; -fx-font-weight: bold;");
        }
    }

    public void initialize() {
        loginValidationLabel.setText("");
        loginButton.setStyle("-fx-background-color: #87a8d5;");  // ✅ fixed variable name

        loginButton.setOnMouseEntered(e -> loginButton.setStyle(
                "-fx-background-color: #87a8d5; -fx-translate-y: 2; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 8, 0, 0, 2);"));

        loginButton.setOnMouseExited(e -> loginButton.setStyle(
                "-fx-background-color: #87a8d5; -fx-translate-y: 0;"));
    }

    @FXML
    private void forgotPassword(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/assignment/Adminforgotpassword.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(AdminLoginController.class.getResource("/org/example/assignment/Adminloginpage.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);
        stage.setTitle("Titan Resort Admin Login");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
