package com.example.ap_assignmentuserinterface;

import Classes.NotificationDatabase;  //(Delete this for Admin)
import javafx.application.Platform;
import javafx.concurrent.Task;
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
import Classes.UserDatabase;

import java.io.File;
import java.util.Objects;
import java.util.Random;


import javafx.event.ActionEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

import java.io.IOException;
import java.util.EventObject;

import static com.example.ap_assignmentuserinterface.EmailService.isValidEmail;

public class RegisterController {

    @FXML
    private Button verifyCode;

    @FXML
    private TextField usernameField;

    @FXML
    private TextField emailField;

    @FXML
    private Button submitButton;

    @FXML
    private TextField passwordField;

    @FXML
    private TextField confirmPasswordField;

    @FXML
    private Label passwordMatchLabel;

    @FXML
    private TextField codeField;

    private String verificationCode;

    @FXML
    private Label codeMatchLabel;

    @FXML
    private AnchorPane overlayPane;

    @FXML
    private Label emailVerifyLabel;

    @FXML
    private Button backButton;

    @FXML
    private Button cancelOverlayButton;


    @FXML private AnchorPane loadingOverlay;
    @FXML private ImageView loadingView;


    // Example: hide overlay
    public void hideOverlay() {
        overlayPane.setVisible(false);
    }
    @FXML
    public void initialize() {
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

        verifyCode.setStyle("-fx-background-color: #4CAF50; " +
                "-fx-text-fill: white; " +
                "-fx-font-weight: bold; " +
                "-fx-background-radius: 20; " +
                "-fx-border-radius: 20; " +
                "-fx-border-color: transparent; " +  // Keep border space
                "-fx-border-width: 2; " +
                "-fx-padding: 8 16;");
        // Hover in
        verifyCode.setOnMouseEntered(e -> {
            verifyCode.setStyle("-fx-background-color: #4CAF50; " +
                    "-fx-text-fill: white; " +
                    "-fx-font-weight: bold; " +
                    "-fx-background-radius: 20; " +
                    "-fx-border-radius: 20; " +
                    "-fx-border-color: darkgreen; " +
                    "-fx-border-width: 2; " +
                    "-fx-padding: 8 16; " +
                    "-fx-translate-y: 2;");
        });

        // Hover out
        verifyCode.setOnMouseExited(e -> {
            verifyCode.setStyle("-fx-background-color: #4CAF50; " +
                    "-fx-text-fill: white; " +
                    "-fx-font-weight: bold; " +
                    "-fx-background-radius: 20; " +
                    "-fx-border-radius: 20; " +
                    "-fx-border-color: transparent; " +
                    "-fx-border-width: 2; " +
                    "-fx-padding: 8 16;");
        });
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

        // Default style
        cancelOverlayButton.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;");

        // Hover in
        cancelOverlayButton.setOnMouseEntered(e -> cancelOverlayButton.setStyle(
                "-fx-background-color: rgba(255, 255, 255, 0.2); " +
                        "-fx-text-fill: #FFD700; " +
                        "-fx-font-size: 14px; " +
                        "-fx-font-weight: bold;"));

        // Hover out
        cancelOverlayButton.setOnMouseExited(e -> cancelOverlayButton.setStyle(
                "-fx-background-color: transparent; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 14px; " +
                        "-fx-font-weight: bold;"));

        // Password match checking
        confirmPasswordField.textProperty().addListener((obs, oldVal, newVal) -> checkPasswordMatch());
        passwordField.textProperty().addListener((obs, oldVal, newVal) -> checkPasswordMatch());

        usernameField.textProperty().addListener((obs, oldVal, newVal) -> checkUsernameMatch());

        // Prevent spacebar key input
        usernameField.addEventFilter(javafx.scene.input.KeyEvent.KEY_TYPED, event -> {
            if (event.getCharacter().equals(" ")) {
                event.consume();  // blocks the space character
            }
        });

        // Prevent spacebar key input
        emailField.addEventFilter(javafx.scene.input.KeyEvent.KEY_TYPED, event -> {
            if (event.getCharacter().equals(" ")) {
                event.consume();  // blocks the space character
            }
        });
        submitButton.setDisable(true);
        usernameField.textProperty().addListener((obs, oldVal, newVal) -> validateForm());
        emailField.textProperty().addListener((obs, oldVal, newVal) -> validateForm());
        passwordField.textProperty().addListener((obs, oldVal, newVal) -> validateForm());
        confirmPasswordField.textProperty().addListener((obs, oldVal, newVal) -> validateForm());
    }
    private boolean allowUsernameSubmit;
    private boolean allowPasswordSubmit;

    private void checkPasswordMatch() {
        String password = passwordField.getText();
        String confirm = confirmPasswordField.getText();

        if (!confirm.isEmpty()) {
            if (password.equals(confirm)) {
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


    @FXML
    private Label usernameMatchLabel;

    private void checkUsernameMatch(){
        String username = usernameField.getText();

        if(!username.isEmpty()){
            if(UserDatabase.findUser(username) != null){
                usernameMatchLabel.setText("✖ Username Existed");
                usernameMatchLabel.setStyle("-fx-text-fill: #ff474c;");
                allowUsernameSubmit=false;
            }else{
                usernameMatchLabel.setText("✔ Username Accepted");
                usernameMatchLabel.setStyle("-fx-text-fill: #64e3a1;");
                allowUsernameSubmit=true;
            }
        }else{
            usernameMatchLabel.setText("");
        }
    }

    private void validateForm() {
        boolean isFormValid =
                !usernameField.getText().isEmpty() &&
                        !emailField.getText().isEmpty() &&
                        !passwordField.getText().isEmpty() &&
                        !confirmPasswordField.getText().isEmpty() &&
                        allowUsernameSubmit &&
                        allowPasswordSubmit &&
                        Objects.equals(passwordField.getText(), confirmPasswordField.getText());

        submitButton.setDisable(!isFormValid);
    }
    public void registerUser(String username, String password, String email) {
        UserDatabase.writeUser(username, password, email);
        int newUserId = UserDatabase.getNextId() - 1; // last added ID
    }

    @FXML
    private void handleSubmit() {
        String email = emailField.getText();
        loading(()->{
            sendEmail(email);
        });
    }

    private void sendEmail(String email){
        Random random = new Random();
        int number = 100000 + random.nextInt(900000);
        verificationCode = String.valueOf(number);
        if (!isValidEmail(email)) {
            emailVerifyLabel.setText("✖ Invalid email address.");
            emailVerifyLabel.setStyle("-fx-text-fill: #ff474c;");
            return;
        }
        new Thread(() -> {
            try {
                EmailService.getInstance().sendSimpleEmail(
                        email,
                        "Do not Share",
                        "Your Code is: " + verificationCode
                );


            } catch (Exception e) {
                e.printStackTrace();
                javafx.application.Platform.runLater(() -> {
                    emailVerifyLabel.setText("✖ Failed to send email.");
                    emailVerifyLabel.setStyle("-fx-text-fill: #ff474c;");
                });
            }
        }).start();
        overlayPane.setVisible(true);
    }

    @FXML
    private void verifyCode(javafx.event.ActionEvent event) throws IOException {
        String inputCode = codeField.getText();
        codeMatchLabel.setText("");
        if(Objects.equals(inputCode, verificationCode)){
            String username = usernameField.getText();
            String password = confirmPasswordField.getText();
            String email = emailField.getText();
            registerUser(username,password,email);

            new File(username + "Notifications.txt").createNewFile(); //(Delete this for Admin)
            NotificationDatabase.writeNotification(username, "Welcome!", "Your account has been successfully created."); //(Delete this for Admin)
            NotificationDatabase.writeNotification(username, "Finish Setting Up your Account", "Setup your profile in the profile page.");
            username = "";
            password = "";
            email = "";
            loading(()->{
                    LoginController.SceneSwitcher.switchToLoginPage(event);
                });
        }

        else{
            codeMatchLabel.setText("Invalid Code, Please try again...");
            codeMatchLabel.setStyle("-fx-text-fill: #ff474c;");
            codeField.clear();
        }

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