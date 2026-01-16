package org.example.assignment.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.assignment.PackageDataReceiver;
import org.example.assignment.PackageItem;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class AddPackagesController {

    @FXML
    private TextField nameField;

    @FXML
    private TextField priceField;

    @FXML
    private Button cancelButton;

    private PackageDataReceiver dataReceiver;

    private PackageItem editingPackage;

    public void setDataReceiver(PackageDataReceiver receiver) {
        this.dataReceiver = receiver;
    }

    public void setPackageData(PackageItem packageItem) {
        this.editingPackage = packageItem;
        if (packageItem != null) {
            nameField.setText(packageItem.getName());
            priceField.setText(String.valueOf((int) packageItem.getPrice()));
        }
    }

    @FXML
    private void initialize() {
        if (cancelButton != null) {
            cancelButton.setOnAction(e -> handleCancel());
        }
    }

    @FXML
    private void handleSave() {
        String name = nameField.getText().trim();
        String priceText = priceField.getText().trim();

        if (name.isEmpty() || priceText.isEmpty()) {
            return; // Optional: show an alert here
        }

        int price;
        try {
            // Only allow integer price input (no decimals)
            if (priceText.contains(".")) return;
            price = Integer.parseInt(priceText);
        } catch (NumberFormatException e) {
            return; // Optional: show an alert here
        }

        PackageItem packageItem;
        if (editingPackage != null) {
            editingPackage.setName(name);
            editingPackage.setPrice(price);
            packageItem = editingPackage;
        } else {
            packageItem = new PackageItem(0, name, price);
        }

        try {
            Path filePath = Paths.get("packages.txt");
            List<String> lines = Files.exists(filePath)
                    ? Files.readAllLines(filePath)
                    : new ArrayList<>();

            List<String> updatedLines = new ArrayList<>();
            boolean updated = false;

            for (String line : lines) {
                String[] parts = line.split(",", 3);
                if (parts.length >= 2 && parts[0].trim().equalsIgnoreCase(name)) {
                    updatedLines.add(name + "," + price);
                    updated = true;
                } else {
                    updatedLines.add(line);
                }
            }

            if (!updated) {
                updatedLines.add(name + "," + price);
            }

            Files.write(filePath, updatedLines, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (dataReceiver != null) {
            dataReceiver.onPackageDataReceived(packageItem);
        }

        handleClose();
    }

    @FXML
    private void handleCancel() {
        handleClose();
    }

    private void handleClose() {
        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.close();
    }
}
