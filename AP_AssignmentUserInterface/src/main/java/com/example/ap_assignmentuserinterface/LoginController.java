package com.example.ap_assignmentuserinterface;

import Classes.ChatHistoryDatabase;
import Classes.UserDatabase;
import javafx.animation.ScaleTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.springframework.boot.SpringApplication;
import jakarta.mail.internet.MimeMessage;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

public class LoginController extends Application {
    @FXML private TextField userField;
    @FXML private TextField passwordField;
    @FXML private Label loginValidationLabel;
    @FXML private Button loginButton;
    @FXML private AnchorPane loadingOverlay;
    @FXML private ImageView loadingView;

    public class SceneSwitcher {
        public static void switchToLoginPage(ActionEvent event) {
            try {
                FXMLLoader loader = new FXMLLoader(SceneSwitcher.class.getResource("login-page.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(LoginController.class.getResource("login-page.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);
        stage.setTitle("Titan Resort User Interface");
        stage.setScene(scene);
        stage.show();
    }
    protected Stage stage;
    protected Scene scene;

    @FXML
    private void setToSignUp (ActionEvent event){
        loading(() -> {
            try {
                goToRegisterPage(event);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
    @FXML
    private void forgotPassword (ActionEvent event){
        loading(() -> {
            try {
                goToForgotPassword(event);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
    @FXML
    private void handleLogin(ActionEvent event){
        String username=userField.getText();
        String password=passwordField.getText();
        if(UserDatabase.validateLogin(username,password)) {
            loading(() -> {
                try {
                    goToHome(event, username);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        else{
            loginValidationLabel.setText("Invalid username and password.");
            loginValidationLabel.setStyle("-fx-text-fill: #b30000; -fx-font-weight: bold;");
            passwordField.clear();
        }
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
    private void goToHome(ActionEvent event, String username) throws IOException{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("home-page.fxml"));
            Parent root = loader.load();
            HomeController controller = loader.getController();
            controller.hideOverlay();
            controller.setUsername(username);
            ChatHistoryDatabase.clearHistory();
            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            scene = new Scene(root, 800, 600);
            stage.setScene(scene);
            stage.show();

    }
    private void goToForgotPassword(ActionEvent event) throws IOException{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("forgotpassword-page.fxml"));
        Parent root = loader.load();

        // Use the correct controller class
        ForgotpasswordController controller = loader.getController();
        controller.hideOverlay();  // Make sure this method is defined in ForgotpasswordController

        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    private void goToRegisterPage(ActionEvent event) throws IOException{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("register-page.fxml"));
        Parent root = loader.load();
        RegisterController controller = loader.getController();
        controller.hideOverlay();

        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
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

        loginValidationLabel.setText("");
        loginButton.setStyle("-fx-background-color: #87a8d5;");
        loginButton.setOnMouseEntered(e -> loginButton.setStyle(
                "-fx-background-color: #87a8d5; -fx-translate-y: 2;"));
        loginButton.setOnMouseExited(e -> loginButton.setStyle(
                "-fx-background-color: #87a8d5; -fx-translate-y: 0;"));
        loginButton.setOnMouseEntered(e -> loginButton.setStyle(
                "-fx-background-color: #87a8d5; -fx-translate-y: 2; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 8, 0, 0, 2);"));

    }
    public static void main(String[] args) {
        launch();
    }
} 