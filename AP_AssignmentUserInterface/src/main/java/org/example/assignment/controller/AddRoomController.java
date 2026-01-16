package org.example.assignment.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.assignment.FileManager;
import org.example.assignment.RoomDataReceiver;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AddRoomController {

    @FXML
    private TextField nameField;

    @FXML
    private TextField priceField;

    @FXML
    private Button cancelButton;

    private RoomDataReceiver dataReceiver;

    public void setDataReceiver(RoomDataReceiver receiver) {
        this.dataReceiver = receiver;
    }

    @FXML
    private void initialize() {
        if (cancelButton != null) {
            cancelButton.setOnAction(e -> closeWindow());
        }
    }

    @FXML
    private void handleSave() {
        String name = nameField.getText().trim();
        String price = priceField.getText().trim();

        if (name.isEmpty() || price.isEmpty()) {
            showAlert("Error", "Please fill in all fields.");
            return;
        }

        // Save to files
        saveNewRoomToFile(name, price);

        // Call back to RoomController to update UI only (not open AvailableRooms)
        if (dataReceiver != null) {
            dataReceiver.onRoomDataReceived(name, price);
        }

        // Just close the Add Room window (don’t open AvailableRooms!)
        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.close();
    }



    private void saveNewRoomToFile(String name, String price) {
        List<String> existing = FileManager.readLines("available_rooms.txt");

        // 1️⃣ 把所有房型分开：相同房型 vs 其他房型
        List<String> otherRooms = new ArrayList<>();
        List<String> sameTypeRooms = new ArrayList<>();

        for (String line : existing) {
            String[] parts = line.split(",", 5);
            if (parts.length >= 2 && parts[1].trim().equalsIgnoreCase(name)) {
                sameTypeRooms.add(line);
            } else {
                otherRooms.add(line);
            }
        }

        // 2️⃣ 如果已经有3间或以上，直接弹窗，不添加
        if (sameTypeRooms.size() >= 3) {
            showAlert("Limit Reached", "Room type '" + name + "' already has 3 rooms.");
            return;
        }

        // 3️⃣ 计算需要添加几间才能达到3间
        int roomsToAdd = 3 - sameTypeRooms.size();

        // 4️⃣ 找最大房号
        int maxNumber = existing.stream()
                .map(line -> line.split(",", 2)[0].trim())
                .filter(num -> num.matches("\\d+"))
                .mapToInt(Integer::parseInt)
                .max()
                .orElse(100);

        String status = "Available";

        String imagePath = switch (name) {
            case "Single Room" -> "single_room.png";
            case "3 Person Room" -> "three_person_room.png";
            case "2 Person Room" -> "two_person_room.png";
            case "Deluxe King" -> "deluxe_king.png";
            case "Royal Estate" -> "royale_estate.png";
            case "Divine Bloodline" -> "divide_bloodline.png";
            default -> "default_room.png";
        };

        // 5️⃣ 补足到3间
        for (int i = 1; i <= roomsToAdd; i++) {
            int roomNumber = maxNumber + i;
            String record = roomNumber + "," + name + "," + status + "," + imagePath + "," + price;
            sameTypeRooms.add(record);
        }

        // 6️⃣ 合并其他房型 + 当前房型 ➔ 写回 available_rooms.txt
        List<String> finalRooms = new ArrayList<>(otherRooms);
        finalRooms.addAll(sameTypeRooms);
        FileManager.writeLines("available_rooms.txt", finalRooms);

        // 7️⃣ rooms.txt ➔ 只加一次
        List<String> roomTypes = FileManager.readLines("rooms.txt");
        boolean exists = roomTypes.stream()
                .anyMatch(line -> line.split(",", 2)[0].trim().equalsIgnoreCase(name));
        if (!exists) {
            roomTypes.add(name + "," + price);
            FileManager.writeLines("rooms.txt", roomTypes);
        }
    }



    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.close();
    }
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}


