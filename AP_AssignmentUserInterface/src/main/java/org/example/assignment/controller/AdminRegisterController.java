package org.example.assignment.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.event.ActionEvent;
import javafx.application.Platform;
import org.example.assignment.AdminDatabase;
import org.example.assignment.AdminEmailService;

import java.io.IOException;
import java.util.Random;

public class AdminRegisterController {

    @FXML
    private TextField usernameField;
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField confirmPasswordField;
    @FXML
    private TextField codeField;
    @FXML
    private Button submitButton;
    @FXML
    private Button verifyCodeButton;
    @FXML
    private Label passwordMatchLabel;
    @FXML
    private Label usernameMatchLabel;
    @FXML
    private Label emailVerifyLabel;
    @FXML
    private Label codeMatchLabel;
    @FXML
    private AnchorPane overlayPane;

    private String verificationCode;
    private boolean allowUsernameSubmit = false;
    private boolean allowPasswordSubmit = false;

    private ActionEvent cachedEventForRedirect;  // Store event for scene switch

    @FXML
    public void initialize() {
        confirmPasswordField.textProperty().addListener((obs, oldVal, newVal) -> checkPasswordMatch());
        passwordField.textProperty().addListener((obs, oldVal, newVal) -> checkPasswordMatch());
        usernameField.textProperty().addListener((obs, oldVal, newVal) -> checkUsernameAvailability());
    }

    private void checkPasswordMatch() {
        String pass = passwordField.getText();
        String confirm = confirmPasswordField.getText();
        if (!confirm.isEmpty()) {
            if (pass.equals(confirm)) {
                passwordMatchLabel.setText("✔ Passwords match");
                passwordMatchLabel.setStyle("-fx-text-fill: #64e3a1;");
                allowPasswordSubmit = true;
            } else {
                passwordMatchLabel.setText("✖ Passwords do not match");
                passwordMatchLabel.setStyle("-fx-text-fill: #ff474c;");
                allowPasswordSubmit = false;
            }
        } else {
            passwordMatchLabel.setText("");
        }
    }

    private void checkUsernameAvailability() {
        String username = usernameField.getText();
        if (!username.isEmpty()) {
            if (AdminDatabase.findAdmin(username) != null) {
                usernameMatchLabel.setText("✖ Username exists");
                usernameMatchLabel.setStyle("-fx-text-fill: #ff474c;");
                allowUsernameSubmit = false;
            } else {
                usernameMatchLabel.setText("✔ Username available");
                usernameMatchLabel.setStyle("-fx-text-fill: #64e3a1;");
                allowUsernameSubmit = true;
            }
        } else {
            usernameMatchLabel.setText("");
        }
    }

    @FXML
    private void handleSubmit(ActionEvent event) {
        String email = emailField.getText();

        if (!AdminEmailService.isValidEmail(email)) {
            emailVerifyLabel.setText("✖ Invalid email.");
            emailVerifyLabel.setStyle("-fx-text-fill: #ff474c;");
            return;
        }

        if (!allowUsernameSubmit || !allowPasswordSubmit) {
            emailVerifyLabel.setText("✖ Please fix username or password issues.");
            emailVerifyLabel.setStyle("-fx-text-fill: #ff474c;");
            return;
        }

        verificationCode = String.valueOf(100000 + new Random().nextInt(900000));

        // Store the event for later redirect
        cachedEventForRedirect = event;

        new Thread(() -> {
            try {
                AdminEmailService.getInstance().sendSimpleEmail(email, "Admin Verification Code", "Your code: " + verificationCode);
                Platform.runLater(() -> {
                    emailVerifyLabel.setText("✔ Verification code sent.");
                    emailVerifyLabel.setStyle("-fx-text-fill: #64e3a1;");
                    overlayPane.setVisible(true);
                });
            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    emailVerifyLabel.setText("✖ Failed to send email.");
                    emailVerifyLabel.setStyle("-fx-text-fill: #ff474c;");
                });
            }
        }).start();
    }

    @FXML
    private void handleVerifyCode() {
        String inputCode = codeField.getText();
        if (inputCode.equals(verificationCode)) {
            codeMatchLabel.setText("✔ Code verified");
            codeMatchLabel.setStyle("-fx-text-fill: #64e3a1;");

            String username = usernameField.getText();
            String password = passwordField.getText();
            String email = emailField.getText();

            AdminDatabase.writeAdmin(username, password, email);

            // Use cached event to redirect safely
            showSuccessAlertAndRedirect(cachedEventForRedirect);

        } else {
            codeMatchLabel.setText("✖ Incorrect code");
            codeMatchLabel.setStyle("-fx-text-fill: #ff474c;");
        }
    }

    private void showSuccessAlertAndRedirect(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Registration Successful");
        alert.setHeaderText(null);
        alert.setContentText("Admin registered successfully!");
        alert.showAndWait();

        org.example.assignment.AdminSceneSwitcher.switchScene(event, "/org/example/assignment/Adminloginpage.fxml");
    }


    @FXML
    private void handleBack(ActionEvent event) throws IOException {
        org.example.assignment.AdminSceneSwitcher.switchScene(event, "/org/example/assignment/Adminloginpage.fxml");
    }
}
