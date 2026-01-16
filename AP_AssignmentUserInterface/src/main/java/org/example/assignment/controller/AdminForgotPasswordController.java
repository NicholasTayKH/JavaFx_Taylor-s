package org.example.assignment.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.example.assignment.AdminDatabase;
import org.example.assignment.AdminEmailService;
import org.example.assignment.AdminSceneSwitcher;

import java.io.IOException;
import java.util.Objects;
import java.util.Random;

public class AdminForgotPasswordController {

    @FXML
    private Button backButton;
    @FXML
    private AnchorPane overlayPane;
    @FXML
    private TextField userField;
    @FXML
    private Label userMatchLabel;
    private String verificationCode;
    @FXML
    private Label codeMatchLabel;
    @FXML
    private TextField codeField;
    @FXML
    private Button cancelOverlayButton;
    @FXML
    private Button verifyCode;

    @FXML
    public void initialize() {
        verifyCode.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 20; -fx-border-radius: 20; -fx-border-color: transparent; -fx-border-width: 2; -fx-padding: 8 16;");
        verifyCode.setOnMouseEntered(e -> verifyCode.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 20; -fx-border-radius: 20; -fx-border-color: darkgreen; -fx-border-width: 2; -fx-padding: 8 16; -fx-translate-y: 2;"));
        verifyCode.setOnMouseExited(e -> verifyCode.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 20; -fx-border-radius: 20; -fx-border-color: transparent; -fx-border-width: 2; -fx-padding: 8 16;"));

        backButton.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;");
        backButton.setOnMouseEntered(e -> backButton.setStyle("-fx-background-color: rgba(255, 255, 255, 0.2); -fx-text-fill: #FFD700; -fx-font-size: 14px; -fx-font-weight: bold;"));
        backButton.setOnMouseExited(e -> backButton.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;"));

        cancelOverlayButton.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;");
        cancelOverlayButton.setOnMouseEntered(e -> cancelOverlayButton.setStyle("-fx-background-color: rgba(255, 255, 255, 0.2); -fx-text-fill: #FFD700; -fx-font-size: 14px; -fx-font-weight: bold;"));
        cancelOverlayButton.setOnMouseExited(e -> cancelOverlayButton.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;"));
    }

    public void hideOverlay() {
        overlayPane.setVisible(false);
    }

    @FXML
    private void handleSubmit() {
        String username = userField.getText().trim();
        if (AdminDatabase.findAdmin(username) != null) {
            String email = AdminDatabase.findEmailByUsername(username);
            userMatchLabel.setText("");

            Random random = new Random();
            verificationCode = String.valueOf(100000 + random.nextInt(900000));

            new Thread(() -> {
                try {
                    AdminEmailService.getInstance().sendSimpleEmail(
                            email,
                            "Admin Password Reset Code",
                            "Your verification code is: " + verificationCode
                    );

                    javafx.application.Platform.runLater(() -> overlayPane.setVisible(true));

                } catch (Exception e) {
                    e.printStackTrace();
                    javafx.application.Platform.runLater(() -> {
                        userMatchLabel.setText("✖ Failed to send email.");
                        userMatchLabel.setStyle("-fx-text-fill: #ff474c;");
                    });
                }
            }).start();
        } else {
            userMatchLabel.setText("✖ Admin Username Not Found");
            userMatchLabel.setStyle("-fx-text-fill: #ff474c;");
        }
    }

    @FXML
    private void verifyCode(ActionEvent event) throws IOException {
        String inputCode = codeField.getText().trim();
        codeMatchLabel.setText("");

        if (Objects.equals(inputCode, verificationCode)) {
            String username = userField.getText();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/assignment/AdminNewPassword.fxml"));
            Parent root = loader.load();

            AdminNewPasswordController controller = loader.getController();
            controller.setUsername(username);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } else {
            codeMatchLabel.setText("Invalid Code, Please try again...");
            codeMatchLabel.setStyle("-fx-text-fill: #ff474c;");
        }
    }

    @FXML
    private void handleBack(ActionEvent event) throws IOException {
        AdminSceneSwitcher.switchToAdminLoginPage(event);
    }
}
