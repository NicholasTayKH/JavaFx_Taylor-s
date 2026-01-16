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
import java.util.Random;

import Classes.UserDatabase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

import javax.swing.*;

public class ForgotpasswordController {
    @FXML private AnchorPane loadingOverlay;
    @FXML private ImageView loadingView;
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

    public void hideOverlay() {
        overlayPane.setVisible(false);
    }

    @FXML
    private void handleSubmit() {
        String username = userField.getText();
        if(UserDatabase.findUser(username) != null){
            // Normally you'd validate input or register user here...
            String email = UserDatabase.findEmailByUsername(username);
            // Show the overlay
            userMatchLabel.setText("");
            loading(()->{sendEmail(email);});
        }else{
            userMatchLabel.setText("✖ Username Not Found");
            userMatchLabel.setStyle("-fx-text-fill: #ff474c;");
        }
    }
    private void sendEmail(String email){
        Random random = new Random();
        int number = 100000 + random.nextInt(900000);
        verificationCode = String.valueOf(number);
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
                    userMatchLabel.setText("✖ Failed to send email.");
                    userMatchLabel.setStyle("-fx-text-fill: #ff474c;");
                });
            }
        }).start();
        overlayPane.setVisible(true);
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
    @FXML
    private void verifyCode(ActionEvent event) {
        String inputCode = codeField.getText();
        codeMatchLabel.setText("");
        if(Objects.equals(inputCode, verificationCode)){
            loading(()->{
                try {
                    goToNewPassword(event);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
        else{
            codeMatchLabel.setText("Invalid Code, Please try again...");
            codeMatchLabel.setStyle("-fx-text-fill: #ff474c;");
            codeField.clear();
        }
    }
    private void goToNewPassword(ActionEvent event)throws IOException {
        String username = userField.getText();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("newpassword-page.fxml"));
        Parent root = loader.load();

        // Get the controller and pass the variable
        NewpasswordController controller = loader.getController();
        controller.setUsername(username);  // <- Pass your variable here

        // Load the scene
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    @FXML
    private Button verifyCode;

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
    }

    @FXML
    private void handleBack(ActionEvent event){
        loading(()->{LoginController.SceneSwitcher.switchToLoginPage(event);});
    }
}
