package org.example.assignment.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.assignment.FileManager;
import org.example.assignment.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AdminViewProfileController {

    @FXML
    private VBox profileContentBox;

    @FXML private Button homeButton;
    @FXML private Button manageRoomsButton;
    @FXML private Button profileButton;
    @FXML private Button logoutButton;

    @FXML
    private TextField searchField;

    private List<User> users = new ArrayList<>();

    @FXML
    public void initialize() {
        loadUserDataFromFile();
        generateUserCards(users);

        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            List<User> filtered = users.stream()
                    .filter(u -> u.getName().toLowerCase().contains(newVal.toLowerCase()) || u.getPhone().contains(newVal))
                    .collect(Collectors.toList());
            generateUserCards(filtered);
        });
    }

    private void loadUserDataFromFile() {
        users.clear();

        List<String> lines = FileManager.readLines("userDetails.txt");

        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("#")) continue;

            String[] parts = line.split(",");
            if (parts.length >= 4) {
                String id = parts[0].trim();
                String name = parts[1].trim();
                String username = parts[2].trim();
                String email = parts[3].trim();
                String age = parts.length > 4 ? parts[4].trim() : "";
                String ic = parts.length > 5 ? parts[5].trim() : "";
                String phone = parts.length > 6 ? parts[6].trim() : "";

                users.add(new User(id, name, username, Integer.parseInt(age.isEmpty() ? "0" : age), ic, phone, email));
            }
        }
    }

    @FXML
    private void handleRefresh() {
        loadUserDataFromFile();
        generateUserCards(users);
    }

    private void generateUserCards(List<User> userList) {
        profileContentBox.getChildren().clear();

        for (User user : userList) {
            HBox card = createUserCard(user);
            profileContentBox.getChildren().add(card);
        }

        if (userList.isEmpty()) {
            Label noResult = new Label("No users found.");
            noResult.setStyle("-fx-text-fill: grey;");
            profileContentBox.getChildren().add(noResult);
        }
    }

    private HBox createUserCard(User user) {
        HBox card = new HBox(20);
        card.setStyle("-fx-background-color: #eeeeee; -fx-padding: 15; -fx-background-radius: 10;");
        card.setPrefHeight(200);

        ImageView profileImage = new ImageView(new Image(getClass().getResourceAsStream("/org/example/assignment/profile.png")));
        profileImage.setFitWidth(100);
        profileImage.setFitHeight(100);

        GridPane grid = new GridPane();
        grid.setVgap(10);
        grid.setHgap(10);

        Label nameLabel = new Label("Full Name:");
        TextField nameField = new TextField(user.getName());
        nameField.setEditable(false);

        Label phoneLabel = new Label("Phone:");
        TextField phoneField = new TextField(user.getPhone());
        phoneField.setEditable(false);

        Label dobLabel = new Label("DOB:");
        TextField dobField = new TextField(user.getDob());
        dobField.setEditable(false);

        grid.add(nameLabel, 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(phoneLabel, 0, 1);
        grid.add(phoneField, 1, 1);
        grid.add(dobLabel, 0, 2);
        grid.add(dobField, 1, 2);

        Button viewButton = new Button("View");
        viewButton.setStyle("-fx-background-color: #9ea1e4; -fx-text-fill: white;");
        viewButton.setOnAction(e -> openUserProfile(user, e));

        card.getChildren().addAll(profileImage, grid, viewButton);
        return card;
    }

    private void openUserProfile(User user, ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/assignment/ViewProfileBookings.fxml"));
            Parent root = loader.load();

            ViewProfileBookingController controller = loader.getController();
            controller.setUser(user);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("User Profile & Bookings");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openPage(String fxml) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/assignment/" + fxml));
            Scene scene = new Scene(loader.load());
            Stage stage = new Stage();
            stage.setTitle(fxml.replace(".fxml", ""));
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleHome() {
        openPage("AdminDashboard.fxml");
    }

    @FXML
    private void handleManageRooms() {
        openPage("Room.fxml");
    }

    @FXML
    private void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/assignment/Adminloginpage.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Admin Login");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


