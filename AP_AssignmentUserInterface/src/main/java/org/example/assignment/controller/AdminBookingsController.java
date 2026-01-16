package org.example.assignment.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import org.example.assignment.FileManager;
import org.example.assignment.RoomItem;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class AdminBookingsController {

    @FXML private ListView<RoomItem> bookingsListView;
    @FXML private Button backButton;

    private final static Map<String, String> roomTypeImages = Map.of(
            "Single Room", "/org/example/assignment/single_room.png",
            "2 Person Room", "/org/example/assignment/two_person_room.png",
            "3 Person Room", "/org/example/assignment/three_person_room.png",
            "Deluxe King", "/org/example/assignment/deluxe_king.png",
            "Royal Estate", "/org/example/assignment/royale_estate.png",
            "Divine Bloodline", "/org/example/assignment/divide_bloodline.png"
    );

    @FXML
    public void initialize() {
        refreshList();

        backButton.setOnAction(e -> ((Stage) backButton.getScene().getWindow()).close());

        bookingsListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                RoomItem selected = bookingsListView.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    openDetailsPage(selected);
                }
            }
        });
    }

    private void refreshList() {
        if (bookingsListView == null) return;

        bookingsListView.getItems().clear();

        List<String> lines = FileManager.readLines("BookingInfo.txt");
        List<RoomItem> bookingItems = new ArrayList<>();

        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        List<String> currentBlock = new ArrayList<>();

        for (String line : lines) {
            if (!line.trim().isEmpty()) {
                currentBlock.add(line.trim());
            }
            if (line.startsWith("--------------------------------------------------")) {
                String startDate = "";
                String endDate = "";
                String roomType = "";
                String name = "";
                String travelPackage = "";  // Add this line

                for (String entry : currentBlock) {
                    if (entry.startsWith("Start Date:")) startDate = entry.substring(11).trim();
                    if (entry.startsWith("Return Date:")) endDate = entry.substring(12).trim();
                    if (entry.startsWith("Room Type:")) roomType = entry.substring(10).trim();
                    if (entry.startsWith("Name:")) name = entry.substring(5).trim();
                    if (entry.startsWith("Travel Package:")) travelPackage = entry.substring(15).trim();  // Add this line
                }

                try {
                    LocalDate bookingStart = LocalDate.parse(startDate, formatter);
                    if (bookingStart.isAfter(today) || bookingStart.isEqual(today)) {
                        String normalizedRoomType = roomType.trim();

                        String imagePath = roomTypeImages.entrySet().stream()
                                .filter(entry -> entry.getKey().equalsIgnoreCase(normalizedRoomType))
                                .map(Map.Entry::getValue)
                                .findFirst()
                                .orElse("/org/example/assignment/default_room.png");

                        String displayText = normalizedRoomType + " - Booked by " + name + " [" + startDate + " to " + endDate + "]";

                        // Pass travelPackage here 👇
                        bookingItems.add(new RoomItem("", displayText, "-", imagePath, normalizedRoomType, startDate, endDate, name, travelPackage));
                    }
                } catch (Exception ignored) {}
            }
        }

        bookingsListView.getItems().addAll(bookingItems);

        bookingsListView.setCellFactory(list -> new ListCell<>() {
            private final ImageView imageView = new ImageView();
            private final Label label = new Label();
            private final Region spacer = new Region();
            private final HBox container = new HBox(10, imageView, label, spacer);

            {
                imageView.setFitWidth(80);
                imageView.setFitHeight(80);
                label.setWrapText(true);
                HBox.setHgrow(spacer, Priority.ALWAYS);
                container.setStyle("-fx-padding: 10; -fx-alignment: CENTER_LEFT;");
            }

            @Override
            protected void updateItem(RoomItem item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    InputStream imgStream = getClass().getResourceAsStream(item.getImagePath());
                    imageView.setImage(imgStream != null
                            ? new Image(imgStream)
                            : new Image(Objects.requireNonNull(getClass().getResourceAsStream("/org/example/assignment/default_room.png"))));
                    label.setText(item.getName());
                    setGraphic(container);
                }
            }
        });
    }

    private void openDetailsPage(RoomItem booking) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/assignment/BookingDetails.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = new Stage();
            stage.setTitle("Booking Details");
            stage.setScene(scene);

            BookingDetailsController controller = loader.getController();
            controller.setBookingDetails(booking);

            stage.show();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
