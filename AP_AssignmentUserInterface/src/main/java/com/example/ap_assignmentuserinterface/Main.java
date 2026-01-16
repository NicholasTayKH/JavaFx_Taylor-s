package com.example.ap_assignmentuserinterface;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Admin Dashboard (Controller 1)
        FXMLLoader adminLoader = new FXMLLoader(getClass().getResource("/org/example/assignment/Adminloginpage.fxml"));
        Scene adminScene = new Scene(adminLoader.load(), 800, 600);
        Stage adminStage = new Stage();
        adminStage.setTitle("Admin Dashboard");
        adminStage.setScene(adminScene);
        adminStage.show();

        // User Dashboard (Controller 2)
        FXMLLoader userLoader = new FXMLLoader(getClass().getResource("login-page.fxml"));
        Scene userScene = new Scene(userLoader.load(), 800, 600);
        Stage userStage = new Stage();
        userStage.setTitle("User Dashboard");
        userStage.setScene(userScene);
        userStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}