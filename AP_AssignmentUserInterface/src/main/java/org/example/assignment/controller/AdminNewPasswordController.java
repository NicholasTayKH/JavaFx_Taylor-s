package org.example.assignment.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.example.assignment.AdminDatabase;
import org.example.assignment.AdminSceneSwitcher;

import java.io.IOException;
import java.util.Objects;

public class AdminNewPasswordController {

    @FXML
    private TextField passwordField;
    @FXML
    private TextField confirmPasswordField;
    @FXML
    private Label passwordMatchLabel;
    @FXML
    private Button submitButton;
    @FXML
    private Button backButton;

    private String username;

    public void setUsername(String username) {
        this.username = username;
    }

    private void checkPasswordMatch() {
        String password = passwordField.getText();
        String confirm = confirmPasswordField.getText();

        if (!confirm.isEmpty()) {
            if (password.equals(confirm)) {
                passwordMatchLabel.setText("✔ Passwords match");
                passwordMatchLabel.setStyle("-fx-text-fill: #64e3a1;");
            } else {
                passwordMatchLabel.setText("✖ Passwords do not match");
                passwordMatchLabel.setStyle("-fx-text-fill: #ff474c;");
            }
        } else {
            passwordMatchLabel.setText("");
        }
    }

    private void validateForm() {
        boolean isFormValid =
                !passwordField.getText().isEmpty() &&
                        !confirmPasswordField.getText().isEmpty() &&
                        Objects.equals(passwordField.getText(), confirmPasswordField.getText());

        submitButton.setDisable(!isFormValid);
    }

    @FXML
    public void handleSubmit(ActionEvent event) throws IOException {
        String newPassword = confirmPasswordField.getText();
        AdminDatabase.updatePassword(username, newPassword);
        AdminSceneSwitcher.switchToAdminLoginPage(event);  // ✅ Fixed
    }

    @FXML
    public void initialize() {
        confirmPasswordField.textProperty().addListener((obs, oldVal, newVal) -> checkPasswordMatch());
        passwordField.textProperty().addListener((obs, oldVal, newVal) -> checkPasswordMatch());

        submitButton.setDisable(true);
        passwordField.textProperty().addListener((obs, oldVal, newVal) -> validateForm());
        confirmPasswordField.textProperty().addListener((obs, oldVal, newVal) -> validateForm());

        backButton.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;");
        backButton.setOnMouseEntered(e -> backButton.setStyle(
                "-fx-background-color: rgba(255, 255, 255, 0.2); -fx-text-fill: #FFD700; -fx-font-size: 14px; -fx-font-weight: bold;"));
        backButton.setOnMouseExited(e -> backButton.setStyle(
                "-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;"));
    }

    @FXML
    private void handleBack(ActionEvent event) throws IOException {
        AdminSceneSwitcher.switchToAdminLoginPage(event);  // ✅ Fixed
    }
}