package org.example.assignment.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.assignment.FileManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RoomController {

    @FXML private Button logoutButton;
    @FXML private Button addRoomButton;
    @FXML private Button addPackageButton;
    @FXML private VBox roomsContainer;
    @FXML private VBox packagesContainer;
    @FXML private Button homeButton;
    @FXML private Button viewProfileButton;
    @FXML private Button cancelButton;

    private static final int MAX_CARDS_PER_ROW = 3;

    @FXML
    public void initialize() {
        loadRoomsFromFile();
        loadPackagesFromFile();
    }

    private void loadRoomsFromFile() {
        roomsContainer.getChildren().clear();
        List<String> lines = FileManager.readLines("rooms.txt");

        for (String line : lines) {
            String[] parts = line.split(",", 2);
            if (parts.length == 2) {
                String name = parts[0].trim();
                String price = parts[1].trim();
                addRoomToUI(name, price);
            }
        }
    }

    private void loadPackagesFromFile() {
        packagesContainer.getChildren().clear();
        List<String> lines = FileManager.readLines("packages.txt");

        for (String line : lines) {
            String[] parts = line.split(",", 2);
            if (parts.length == 2) {
                String name = parts[0].trim();
                String price = parts[1].trim();
                addPackageToUI(name, price, "#95a5a6");
            }
        }
    }

    @FXML
    private void handleAddRoom() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/example/assignment/Addnewroom.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 600, 400);

            Stage stage = new Stage();
            stage.setTitle("Add New Room");
            stage.setScene(scene);
            stage.setResizable(false);

            AddRoomController controller = fxmlLoader.getController();
            controller.setDataReceiver((name, price) -> {
                loadRoomsFromFile();
            });

            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAddPackage() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/example/assignment/Addnewpackage.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 600, 400);

            Stage stage = new Stage();
            stage.setTitle("Add New Package");
            stage.setScene(scene);
            stage.setResizable(false);

            AddPackagesController controller = fxmlLoader.getController();
            controller.setDataReceiver(packageItem -> {
                loadPackagesFromFile();
            });

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleHome() {
        openPage("AdminDashboard.fxml", homeButton);
    }

    private void addRoomToUI(String name, String price) {
        VBox card = new VBox(10);
        card.setStyle("-fx-background-color: #3498db; -fx-padding: 20; -fx-background-radius: 8;");
        card.setPrefWidth(234);
        card.setPrefHeight(138);
        card.setAlignment(Pos.TOP_LEFT);

        Label nameLabel = new Label(name);
        nameLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");

        Label priceLabel = new Label("$" + price);
        priceLabel.setStyle("-fx-text-fill: white; -fx-font-size: 20px;");

        Button editButton = new Button("Edit");
        editButton.setOnAction(e -> openEditRoomDialog(nameLabel, priceLabel));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox editRow = new HBox(spacer, editButton);
        editRow.setAlignment(Pos.TOP_RIGHT);

        card.getChildren().addAll(nameLabel, editRow, priceLabel);

        if (roomsContainer.getChildren().isEmpty()
                || ((HBox) roomsContainer.getChildren().get(roomsContainer.getChildren().size() - 1)).getChildren().size() >= MAX_CARDS_PER_ROW) {
            HBox newRow = new HBox(40);
            newRow.setAlignment(Pos.CENTER);
            roomsContainer.getChildren().add(newRow);
        }

        HBox lastRow = (HBox) roomsContainer.getChildren().get(roomsContainer.getChildren().size() - 1);
        lastRow.getChildren().add(card);
    }

    private void openEditRoomDialog(Label nameLabel, Label priceLabel) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/assignment/EditRooms.fxml"));
            Parent root = loader.load();

            EditRoomController controller = loader.getController();
            controller.setInitialData(nameLabel.getText(), priceLabel.getText().replace("$", ""));
            Stage dialogStage = new Stage();
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setScene(new Scene(root));

            controller.setCallbacks(
                    (newName, newPrice) -> {
                        nameLabel.setText(newName);
                        priceLabel.setText("$" + newPrice);
                        updateRoomInFile(newName, newPrice);
                    },
                    () -> {
                        deleteRoomFromFiles(nameLabel.getText());
                        loadRoomsFromFile();
                    },
                    dialogStage
            );

            dialogStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateRoomInFile(String name, String price) {
        List<String> lines = FileManager.readLines("rooms.txt");
        List<String> updated = new ArrayList<>();

        for (String line : lines) {
            String[] parts = line.split(",", 2);
            if (parts.length == 2 && parts[0].trim().equalsIgnoreCase(name)) {
                updated.add(name + "," + price);
            } else {
                updated.add(line);
            }
        }

        FileManager.writeLines("rooms.txt", updated);
    }

    private void deleteRoomFromFiles(String roomName) {
        List<String> roomLines = FileManager.readLines("rooms.txt");
        List<String> updatedRoomLines = new ArrayList<>();
        for (String line : roomLines) {
            String[] parts = line.split(",", 2);
            if (parts.length == 2 && !parts[0].trim().equalsIgnoreCase(roomName)) {
                updatedRoomLines.add(line);
            }
        }
        FileManager.writeLines("rooms.txt", updatedRoomLines);

        List<String> availableLines = FileManager.readLines("available_rooms.txt");
        List<String> updatedAvailableLines = new ArrayList<>();
        for (String line : availableLines) {
            String[] parts = line.split(",", 5);
            if (parts.length == 5 && !parts[1].trim().equalsIgnoreCase(roomName)) {
                updatedAvailableLines.add(line);
            }
        }
        FileManager.writeLines("available_rooms.txt", updatedAvailableLines);
    }

    private void addPackageToUI(String name, String price, String color) {
        VBox card = new VBox(10);
        card.setStyle("-fx-background-color: " + color + "; -fx-padding: 20; -fx-background-radius: 8;");
        card.setPrefWidth(234);
        card.setPrefHeight(138);
        card.setAlignment(Pos.TOP_LEFT);

        Label nameLabel = new Label(name);
        nameLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");

        Label priceLabel = new Label("$" + price);
        priceLabel.setStyle("-fx-text-fill: white; -fx-font-size: 20px;");

        Button editButton = new Button("Edit");
        editButton.setOnAction(e -> openEditPackageDialog(nameLabel, priceLabel));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox editRow = new HBox(spacer, editButton);
        editRow.setAlignment(Pos.TOP_RIGHT);

        card.getChildren().addAll(nameLabel, editRow, priceLabel);

        if (packagesContainer.getChildren().isEmpty()
                || ((HBox) packagesContainer.getChildren().get(packagesContainer.getChildren().size() - 1)).getChildren().size() >= MAX_CARDS_PER_ROW) {
            HBox newRow = new HBox(40);
            newRow.setAlignment(Pos.CENTER);
            packagesContainer.getChildren().add(newRow);
        }

        HBox lastRow = (HBox) packagesContainer.getChildren().get(packagesContainer.getChildren().size() - 1);
        lastRow.getChildren().add(card);
    }

    private void openEditPackageDialog(Label nameLabel, Label priceLabel) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/assignment/EditPackages.fxml"));
            Parent root = loader.load();

            EditPackagesController controller = loader.getController();
            controller.setInitialData(nameLabel.getText(), priceLabel.getText().replace("$", ""));

            Stage dialogStage = new Stage();
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setScene(new Scene(root));

            controller.setCallbacks(
                    (newName, newPrice) -> {
                        loadPackagesFromFile();
                    },
                    () -> {
                        loadPackagesFromFile();
                    },
                    dialogStage
            );

            dialogStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleViewProfile() {
        openPage("AdminViewProfile.fxml", viewProfileButton);
    }

    private void openPage(String fxml, Button sourceButton) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/assignment/" + fxml));
            Scene scene = new Scene(loader.load());
            Stage stage = new Stage();
            stage.setTitle(fxml.replace(".fxml", ""));
            stage.setScene(scene);
            stage.show();

            Stage currentStage = (Stage) sourceButton.getScene().getWindow();
            currentStage.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogout() {
        openPage("Adminloginpage.fxml", logoutButton);
    }

    @FXML
    private void handleCancel() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
}
