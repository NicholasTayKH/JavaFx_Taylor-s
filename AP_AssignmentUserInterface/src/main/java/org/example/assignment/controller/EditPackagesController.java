package org.example.assignment.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class EditPackagesController {

    @FXML
    private TextField nameField;
    @FXML
    private TextField priceField;
    @FXML
    private Button deleteButton;
    @FXML
    private Button cancelButton;
    @FXML
    private Button saveButton;

    private BiConsumer<String, String> onSaveCallback;
    private Runnable onDeleteCallback;
    private Stage stage;

    private String originalName;

    public void setInitialData(String name, String price) {
        originalName = name;
        nameField.setText(name);
        priceField.setText(price);
    }

    public void setCallbacks(BiConsumer<String, String> onSave, Runnable onDelete, Stage stage) {
        this.onSaveCallback = onSave;
        this.onDeleteCallback = onDelete;
        this.stage = stage;
    }

    @FXML
    private void handleSave() {
        String newName = nameField.getText().trim();
        String priceText = priceField.getText().trim();

        if (newName.isEmpty() || priceText.isEmpty()) return;

        try {
            double price = Double.parseDouble(priceText);
            updatePackageInFile(originalName, newName, price);
        } catch (NumberFormatException | IOException e) {
            e.printStackTrace(); // Optional: show alert
            return;
        }

        if (onSaveCallback != null) {
            onSaveCallback.accept(newName, priceText);
        }
        stage.close();
    }

    @FXML
    private void handleDelete() {
        try {
            deletePackageFromFile(originalName);
        } catch (IOException e) {
            e.printStackTrace(); // Optional: show alert
        }

        if (onDeleteCallback != null) {
            onDeleteCallback.run();
        }

        stage.close();
    }

    @FXML
    private void handleCancel() {
        stage.close();
    }

    private void updatePackageInFile(String originalName, String newName, double newPrice) throws IOException {
        Path path = Paths.get("packages.txt");
        List<String> lines = Files.exists(path) ? Files.readAllLines(path) : new ArrayList<>();
        List<String> updated = new ArrayList<>();

        for (String line : lines) {
            String[] parts = line.split(",", 3);
            if (parts.length >= 2 && parts[0].trim().equalsIgnoreCase(originalName)) {
                updated.add(newName + "," + (newPrice % 1 == 0 ? String.valueOf((int) newPrice) : String.valueOf(newPrice)));
            } else {
                updated.add(line);
            }
        }

        Files.write(path, updated, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    private void deletePackageFromFile(String nameToDelete) throws IOException {
        Path path = Paths.get("packages.txt");
        if (!Files.exists(path)) return;

        List<String> lines = Files.readAllLines(path);
        List<String> filtered = new ArrayList<>();

        for (String line : lines) {
            String[] parts = line.split(",", 2);
            if (parts.length >= 2) {
                String existingName = parts[0].trim();
                if (!existingName.equalsIgnoreCase(nameToDelete.trim())) {
                    filtered.add(line); // Keep the line if not matching
                }
            }
        }

        Files.write(path, filtered, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }
}
