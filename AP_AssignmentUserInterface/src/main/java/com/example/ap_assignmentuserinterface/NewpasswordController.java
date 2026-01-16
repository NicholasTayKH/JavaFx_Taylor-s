package com.example.ap_assignmentuserinterface;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Objects;

import Classes.UserDatabase;
import org.w3c.dom.Text;

public class NewpasswordController {
    @FXML private AnchorPane loadingOverlay;
    @FXML private ImageView loadingView;
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
    public void handleSubmit(ActionEvent event) throws IOException{
        String newPassword = confirmPasswordField.getText();
        UserDatabase.updatePassword(username,newPassword);
        loading(()->{
            LoginController.SceneSwitcher.switchToLoginPage(event);
        });
    }
    public void initialize(){
        Image loadingGif = new Image("https://assets-v2.lottiefiles.com/a/f4e27526-6d34-11ee-9e9d-c3524086e28f/9leK2ag1kk.gif", true);
        loadingView.setImage(loadingGif);
        loadingView.setFitWidth(120);
        loadingView.setFitHeight(120);
        loadingView.setPreserveRatio(true);

        Rectangle clip = new Rectangle(120, 120);
        clip.setArcWidth(30);
        clip.setArcHeight(30);


        loadingView.setClip(clip);
        loadingOverlay.setVisible(false);
        confirmPasswordField.textProperty().addListener((obs, oldVal, newVal) -> checkPasswordMatch());
        passwordField.textProperty().addListener((obs, oldVal, newVal) -> checkPasswordMatch());

        submitButton.setDisable(true);
        passwordField.textProperty().addListener((obs, oldVal, newVal) -> validateForm());
        confirmPasswordField.textProperty().addListener((obs, oldVal, newVal) -> validateForm());
        // Default style
        backButton.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;");

        // Hover in
        backButton.setOnMouseEntered(e -> backButton.setStyle(
                "-fx-background-color: rgba(255, 255, 255, 0.2); " +
                        "-fx-text-fill: #FFD700; " +
                        "-fx-font-size: 14px; " +
                        "-fx-font-weight: bold;"));

        // Hover out
        backButton.setOnMouseExited(e -> backButton.setStyle(
                "-fx-background-color: transparent; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 14px; " +
                        "-fx-font-weight: bold;"));
    }
    @FXML
    private void handleBack(ActionEvent event){
        loading(()->{
            LoginController.SceneSwitcher.switchToLoginPage(event);
        });
    }
    private void loading(Runnable afterLoading){
        loadingOverlay.setVisible(true);

        Task<Void> loginTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                // TODO: Replace with real login logic
                Thread.sleep(2000);
                return null;
            }

            @Override
            protected void succeeded() {
                Platform.runLater(() -> {
                    loadingOverlay.setVisible(false);
                    afterLoading.run();
                });
            }

            @Override
            protected void failed() {
                Platform.runLater(() -> {
                    loadingOverlay.setVisible(false);
                });
            }
        };

        new Thread(loginTask).start();
    }
}
