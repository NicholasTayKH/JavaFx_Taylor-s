package org.example.assignment.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.example.assignment.FileManager;
import org.example.assignment.UserItem;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BookingModRequestsController {

    @FXML
    private ListView<UserItem> modRequestListView;

    @FXML
    private Button backButton;

    private final List<String[]> modRequests = new ArrayList<>();

    @FXML
    public void initialize() {
        List<String> lines = FileManager.readLines("modification_request.txt");

        String userId = "";
        String username = "";
        String oldStartDate = "";
        String oldRoomType = "";
        String newName = "";
        String newIC = "";
        String newPhone = "";
        String newStartDate = "";
        String newReturnDate = "";
        String newRoomType = "";
        String travelPackage = "";

        for (String line : lines) {
            line = line.trim();
            if (line.startsWith("UserID:")) {
                userId = line.substring(7).trim();
            } else if (line.startsWith("Username:")) {
                username = line.substring(9).trim();
            } else if (line.startsWith("Old Start Date:")) {
                oldStartDate = line.substring(15).trim();
            } else if (line.startsWith("Old Room Type:")) {
                oldRoomType = line.substring(15).trim();
            } else if (line.startsWith("New Name:")) {
                newName = line.substring(9).trim();
            } else if (line.startsWith("New IC/Passport:")) {
                newIC = line.substring(16).trim();
            } else if (line.startsWith("New Phone Number:")) {
                newPhone = line.substring(17).trim();
            } else if (line.startsWith("New Start Date:")) {
                newStartDate = line.substring(15).trim();
            } else if (line.startsWith("New Return Date:")) {
                newReturnDate = line.substring(16).trim();
            } else if (line.startsWith("New Room Type:")) {
                newRoomType = line.substring(15).trim();
            } else if (line.startsWith("Travel Package:")) {
                travelPackage = line.substring(15).trim();
            } else if (line.equals("--------------------------------------------------")) {

                String displayText = newName + " requested: " + newStartDate + " to " + newReturnDate;
                String profile = "/org/example/assignment/default_user.png";
                modRequestListView.getItems().add(new UserItem(displayText, profile));

                modRequests.add(new String[]{userId, username, oldStartDate, oldRoomType, newName, newIC, newPhone, newStartDate, newReturnDate, newRoomType, travelPackage});

                userId = username = oldStartDate = oldRoomType = newName = newIC = newPhone = newStartDate = newReturnDate = newRoomType = travelPackage = "";
            }
        }

        modRequestListView.setCellFactory(list -> new ListCell<>() {
            private final ImageView imageView = new ImageView();
            private final Label label = new Label();
            private final HBox container = new HBox(10, imageView, label);

            {
                imageView.setFitWidth(40);
                imageView.setFitHeight(40);
            }

            @Override
            protected void updateItem(UserItem item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    InputStream imgStream = getClass().getResourceAsStream(item.getProfileImage());
                    if (imgStream != null) {
                        imageView.setImage(new Image(imgStream));
                    } else {
                        imageView.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/org/example/assignment/default_user.png"))));
                    }
                    label.setText(item.getText());
                    setGraphic(container);
                }
            }
        });

        modRequestListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                int index = modRequestListView.getSelectionModel().getSelectedIndex();
                if (index >= 0 && index < modRequests.size()) {
                    try {
                        openBookingDetails(modRequests.get(index));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        backButton.setOnAction(e -> {
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.close();
        });
    }

    private void openBookingDetails(String[] details) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/assignment/Modificationbookingdetails.fxml"));
        Scene scene = new Scene(loader.load());
        Stage stage = new Stage();
        stage.setTitle("Modification Details");
        stage.setScene(scene);

        ModificationBookingDetailsController controller = loader.getController();
        controller.setBookingDetails(
                details[0],  // userId
                details[2],  // oldStartDate
                details[3],  // oldRoomType
                details[4],  // newName
                details[5],  // newIC
                details[6],  // newPhone
                details[7],  // newStartDate
                details[8],  // newReturnDate
                details[1],  // username
                details[9],  // newRoomType
                details[10]  // travelPackage
        );

        stage.show();
    }
}