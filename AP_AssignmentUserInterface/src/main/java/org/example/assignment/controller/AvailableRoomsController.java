package org.example.assignment.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.example.assignment.DashboardRoomItem;
import org.example.assignment.FileManager;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class AvailableRoomsController {

    @FXML private ListView<DashboardRoomItem> roomListView;
    @FXML private ListView<DashboardRoomItem> bookedListView;
    @FXML private Button backButton;
    @FXML private Button filterButton;
    @FXML private Button resetButton;
    @FXML private DatePicker selectedDatePicker;
    @FXML private Button logoutButton;
    @FXML private Button homeButton;

    private final List<DashboardRoomItem> allRooms = new ArrayList<>();

    private final Map<String, String> roomTypeImages = Map.of(
            "Single Room", "/org/example/assignment/single_room.png",
            "3 Person Room", "/org/example/assignment/three_person_room.png",
            "2 Person Room", "/org/example/assignment/two_person_room.png",
            "Deluxe King", "/org/example/assignment/deluxe_king.png",
            "Royal Estate", "/org/example/assignment/royale_estate.png",
            "Divine Bloodline", "/org/example/assignment/divide_bloodline.png"
    );

    @FXML
    public void initialize() {
        filterButton.disableProperty().bind(selectedDatePicker.valueProperty().isNull());

        loadAllRooms();

        limitRoomsToThreePerType();
        roomListView.getItems().setAll(allRooms);
        setupRoomCellFactory(roomListView);

        selectedDatePicker.setOnAction(e -> updateAvailableRooms());
        filterButton.setOnAction(e -> updateAvailableRooms());

        resetButton.setOnAction(e -> {
            selectedDatePicker.setValue(null);
            limitRoomsToThreePerType();
            roomListView.getItems().setAll(allRooms);
            if (bookedListView != null) bookedListView.getItems().clear();
        });

        backButton.setOnAction(e -> ((Stage) backButton.getScene().getWindow()).close());
    }

    private void loadAllRooms() {
        allRooms.clear();
        List<String> lines = FileManager.readLines("available_rooms.txt");

        for (String line : lines) {
            String[] parts = line.split(",", 4);
            if (parts.length == 4) {
                String roomNumber = parts[0].trim();
                String roomType = parts[1].trim();
                String availability = parts[2].trim();
                String imagePath = roomTypeImages.getOrDefault(roomType, "/org/example/assignment/default_room.png");

                if (!imagePath.contains("default_room.png") || isNewRoomType(roomType)) {
                    String displayName = roomType + " (Room " + roomNumber + ")";
                    allRooms.add(new DashboardRoomItem(roomNumber, displayName, availability, imagePath));
                }
            }
        }
    }

    private boolean isNewRoomType(String roomType) {
        return !roomTypeImages.containsKey(roomType);
    }

    private void limitRoomsToThreePerType() {
        Map<String, List<DashboardRoomItem>> grouped = allRooms.stream()
                .collect(Collectors.groupingBy(item -> extractRoomType(item.getName())));

        List<DashboardRoomItem> limitedRooms = new ArrayList<>();
        for (Map.Entry<String, List<DashboardRoomItem>> entry : grouped.entrySet()) {
            List<DashboardRoomItem> rooms = entry.getValue();
            limitedRooms.addAll(rooms.stream().limit(3).collect(Collectors.toList()));
        }

        allRooms.clear();
        allRooms.addAll(limitedRooms);
    }

    private void updateAvailableRooms() {
        LocalDate selectedDate = selectedDatePicker.getValue();
        if (selectedDate == null || selectedDate.isBefore(LocalDate.now())) {
            roomListView.getItems().clear();
            if (bookedListView != null) bookedListView.getItems().clear();
            return;
        }

        List<String> bookings = FileManager.readLines("BookingInfo.txt");

        // 👉 统计每种房型在这一天已被预订的数量
        Map<String, Long> bookedRoomTypeCount = new HashMap<>();

        String currentRoomType = "";
        String currentStartDate = "";
        String currentReturnDate = "";

        for (String line : bookings) {
            line = line.trim();
            if (line.startsWith("Room Type:")) {
                currentRoomType = line.substring(10).trim();
            } else if (line.startsWith("Start Date:")) {
                currentStartDate = line.substring(11).trim();
            } else if (line.startsWith("Return Date:")) {
                currentReturnDate = line.substring(12).trim();
            } else if (line.startsWith("--------------------------------------------------")) {
                try {
                    LocalDate start = LocalDate.parse(currentStartDate);
                    LocalDate end = LocalDate.parse(currentReturnDate);
                    if (!selectedDate.isBefore(start) && !selectedDate.isAfter(end)) {
                        bookedRoomTypeCount.put(currentRoomType, bookedRoomTypeCount.getOrDefault(currentRoomType, 0L) + 1);
                    }
                } catch (Exception ignored) {}
                currentRoomType = "";
                currentStartDate = "";
                currentReturnDate = "";
            }
        }

        // 👉 每种房型最多3间 => 3 - 已预订 = 剩余可用
        Map<String, List<DashboardRoomItem>> grouped = allRooms.stream()
                .collect(Collectors.groupingBy(item -> extractRoomType(item.getName())));

        List<DashboardRoomItem> availableRooms = new ArrayList<>();
        List<DashboardRoomItem> bookedRooms = new ArrayList<>();

        for (Map.Entry<String, List<DashboardRoomItem>> entry : grouped.entrySet()) {
            String roomType = entry.getKey();
            List<DashboardRoomItem> rooms = entry.getValue();

            long bookedCount = bookedRoomTypeCount.getOrDefault(roomType, 0L);
            long remaining = 3 - bookedCount;

            for (int i = 0; i < rooms.size(); i++) {
                if (i < bookedCount) {
                    bookedRooms.add(rooms.get(i));
                } else if (remaining > 0) {
                    availableRooms.add(rooms.get(i));
                    remaining--;
                } else {
                    bookedRooms.add(rooms.get(i));  // Even if not really booked, exceed = show as full
                }
            }
        }

        roomListView.getItems().setAll(availableRooms);
        if (bookedListView != null) bookedListView.getItems().setAll(bookedRooms);

        if (availableRooms.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "No rooms are available on " + selectedDate + ".");
            alert.showAndWait();
        }
    }


    private String extractRoomType(String displayName) {
        int index = displayName.indexOf(" (");
        return index > 0 ? displayName.substring(0, index).trim() : displayName.trim();
    }

    private void setupRoomCellFactory(ListView<DashboardRoomItem> listView) {
        listView.setCellFactory(list -> new ListCell<>() {
            private final ImageView imageView = new ImageView();
            private final Label label = new Label();
            private final HBox container = new HBox(10, imageView, label);

            {
                imageView.setFitWidth(100);
                imageView.setFitHeight(100);
            }

            @Override
            protected void updateItem(DashboardRoomItem item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    InputStream imgStream = getClass().getResourceAsStream(item.getImagePath());
                    if (imgStream != null) {
                        imageView.setImage(new Image(imgStream));
                    }
                    label.setText(item.getName() + " - " + item.getStatus());
                    setGraphic(container);
                }
            }
        });
    }
    @FXML
    private void handleHome() {
        openPage("/org/example/assignment/AdminDashboard.fxml");
    }

    @FXML
    private void handleManageRooms() {
        openPage("/org/example/assignment/Room.fxml");
    }

    @FXML
    private void handleViewProfile() {
        openPage("AdminViewProfile.fxml");
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

    private void openPage(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Scene scene = new Scene(loader.load());
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Admin Page");
            stage.show();

            // Optional: Close current window
            Stage currentStage = (Stage) homeButton.getScene().getWindow();
            currentStage.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
