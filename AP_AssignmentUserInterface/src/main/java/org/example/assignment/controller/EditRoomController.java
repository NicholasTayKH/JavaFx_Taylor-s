package org.example.assignment.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.assignment.FileManager;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class EditRoomController {

    @FXML
    private Button saveButton;

    @FXML
    private Button deleteButton;

    @FXML
    private Button cancelButton;

    @FXML
    private TextField nameField;

    @FXML
    private TextField priceField;

    private BiConsumer<String, String> onSaveCallback;
    private Runnable onDeleteCallback;
    private Stage stage;

    private String originalName;

    public void setInitialData(String name, String price) {
        nameField.setText(name);
        priceField.setText(price);
        originalName = name;
    }

    public void setCallbacks(BiConsumer<String, String> onSave, Runnable onDelete, Stage stage) {
        this.onSaveCallback = onSave;
        this.onDeleteCallback = onDelete;
        this.stage = stage;
    }

    @FXML
    private void handleSave() {
        String newName = nameField.getText().trim();
        String newPrice = priceField.getText().trim();

        if (newName.isEmpty() || newPrice.isEmpty()) {
            return;
        }

        List<String> roomLines = FileManager.readLines("rooms.txt");
        List<String> updatedRooms = new ArrayList<>();

        for (String line : roomLines) {
            String[] parts = line.split(",", 2);
            if (parts.length == 2 && parts[0].trim().equalsIgnoreCase(originalName)) {
                updatedRooms.add(newName + "," + newPrice);
            } else {
                updatedRooms.add(line);
            }
        }
        FileManager.writeLines("rooms.txt", updatedRooms);

        List<String> availableLines = FileManager.readLines("available_rooms.txt");
        List<String> updatedAvailable = new ArrayList<>();

        for (String line : availableLines) {
            String[] parts = line.split(",", 5);
            if (parts.length == 5 && parts[1].trim().equalsIgnoreCase(originalName)) {
                String newLine = parts[0] + "," + newName + "," + parts[2] + "," + parts[3] + "," + newPrice;
                updatedAvailable.add(newLine);
            } else {
                updatedAvailable.add(line);
            }
        }
        FileManager.writeLines("available_rooms.txt", updatedAvailable);

        if (onSaveCallback != null) {
            onSaveCallback.accept(newName, newPrice);
        }
        stage.close();
    }

    @FXML
    private void handleDelete() {
        List<String> roomLines = FileManager.readLines("rooms.txt");
        roomLines.removeIf(line -> line.startsWith(originalName + ","));
        FileManager.writeLines("rooms.txt", roomLines);

        List<String> availableLines = FileManager.readLines("available_rooms.txt");
        availableLines.removeIf(line -> {
            String[] parts = line.split(",", 5);
            return parts.length >= 2 && parts[1].trim().equalsIgnoreCase(originalName);
        });
        FileManager.writeLines("available_rooms.txt", availableLines);

        if (onDeleteCallback != null) {
            onDeleteCallback.run();
        }
        stage.close();
    }

    @FXML
    private void handleCancel() {
        Stage currentStage = (Stage) cancelButton.getScene().getWindow();
        currentStage.close();
    }
}
