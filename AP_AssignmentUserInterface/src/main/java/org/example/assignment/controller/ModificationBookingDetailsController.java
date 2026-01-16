package org.example.assignment.controller;

import Classes.NotificationDatabase;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.example.assignment.FileManager;

import java.util.ArrayList;
import java.util.List;

public class ModificationBookingDetailsController {

    @FXML
    private ImageView roomImage;

    @FXML
    private Label roomTypeLabel;
    @FXML
    private Label startDateLabel;
    @FXML
    private Label endDateLabel;

    @FXML
    private TextField nameField;
    @FXML
    private TextField passportField;
    @FXML
    private TextField phoneField;



    @FXML
    private Button approveButton;
    @FXML
    private Button backButton;

    private String currentUserId;
    private String currentOldStartDate;
    private String currentOldRoomType;
    private String currentNewName;
    private String currentNewIC;
    private String currentNewPhone;
    private String currentNewStartDate;
    private String currentNewReturnDate;
    private String currentUsername;
    private String currentPackageType;
    @FXML private Label packageTypeLabel;

    private String currentNewRoomType; // ✅ Add this new variable

    @FXML
    public void initialize() {
        // No initialization needed yet
    }

    public void setBookingDetails(String userId, String oldStartDate, String oldRoomType,
                                  String newName, String newIC, String newPhone,
                                  String newStartDate, String newReturnDate, String username,
                                  String newRoomType, String travelPackage) {

        this.currentUserId = userId;
        this.currentOldStartDate = oldStartDate;
        this.currentOldRoomType = oldRoomType;
        this.currentNewName = newName;
        this.currentNewIC = newIC;
        this.currentNewPhone = newPhone;
        this.currentNewStartDate = newStartDate;
        this.currentNewReturnDate = newReturnDate;
        this.currentUsername = username;
        this.currentNewRoomType = newRoomType;
        this.currentPackageType = travelPackage;  // ✅ Store passed travel package

        // Set room image
        String imagePath = getImagePathForRoomType(newRoomType != null ? newRoomType : oldRoomType);
        try {
            Image image = new Image(getClass().getResourceAsStream(imagePath));
            roomImage.setImage(image);
        } catch (Exception e) {
            roomImage.setImage(new Image(getClass().getResourceAsStream("/org/example/assignment/default_room.png")));
        }

        // Set basic labels
        roomTypeLabel.setText(newRoomType != null ? newRoomType : oldRoomType);
        startDateLabel.setText(newStartDate);
        endDateLabel.setText(newReturnDate);
        nameField.setText(newName);

        // By default, use the passed travelPackage
        String finalPackageType = (travelPackage != null && !travelPackage.isEmpty()) ? travelPackage : "N/A";

        // Retrieve Travel Package from BookingInfo.txt based on Name (overwrite if found)
        String passport = "N/A";
        String phone = "N/A";
        String foundTravelPackage = finalPackageType;   // ✅ Avoid same name

        List<String> bookingLines = FileManager.readLines("BookingInfo.txt");
        List<String> currentBlock = new ArrayList<>();

        for (String line : bookingLines) {
            if (line.startsWith("UserID:")) {
                currentBlock.clear();
                currentBlock.add(line);
            } else if (line.equals("--------------------------------------------------")) {
                boolean nameMatch = false;
                for (String blkLine : currentBlock) {
                    if (blkLine.startsWith("Name:")) {
                        String nameInBlock = blkLine.substring(5).trim();
                        if (nameInBlock.equalsIgnoreCase(newName)) {
                            nameMatch = true;
                            break;
                        }
                    }
                }

                if (nameMatch) {
                    for (String dataLine : currentBlock) {
                        if (dataLine.startsWith("IC/Passport:")) {
                            passport = dataLine.substring(12).trim();
                        } else if (dataLine.startsWith("Phone Number:")) {
                            phone = dataLine.substring(13).trim();
                        } else if (dataLine.startsWith("Travel Package:")) {
                            foundTravelPackage = dataLine.substring(15).trim();
                        }
                    }
                }

                currentBlock.clear();
            } else {
                currentBlock.add(line);
            }
        }

        passportField.setText(!passport.isEmpty() ? passport : "N/A");
        phoneField.setText(!phone.isEmpty() ? phone : "N/A");

        // ✅ Set Package Type Label
        packageTypeLabel.setText(foundTravelPackage != null && !foundTravelPackage.isEmpty() ? foundTravelPackage : "N/A");
    }



    private String getImagePathForRoomType(String roomType) {
        String lowerType = roomType.toLowerCase().trim();

        switch (lowerType) {
            case "single room":
                return "/org/example/assignment/single_room.png";
            case "2 person room":
                return "/org/example/assignment/two_person_room.png";
            case "3 person room":
                return "/org/example/assignment/three_person_room.png";
            case "deluxe king":
                return "/org/example/assignment/deluxe_king.png";
            case "royal estate":
                return "/org/example/assignment/royale_estate.png";
            case "divine bloodline":
                return "/org/example/assignment/divide_bloodline.png";
            default:
                return "/org/example/assignment/default_room.png";
        }
    }

    @FXML
    private void onApproveClicked() {
        List<String> modLines = FileManager.readLines("modification_request.txt");
        List<String> updatedModLines = new ArrayList<>();
        List<String> approvedBlock = new ArrayList<>();

        boolean isTargetBlock = false;
        boolean blockMatched = false;

        for (String line : modLines) {
            if (line.startsWith("UserID:")) {
                isTargetBlock = false;
                approvedBlock.clear();
            }

            approvedBlock.add(line);

            if (line.startsWith("New Name:")) {
                String nameInBlock = line.substring(9).trim();
                if (nameInBlock.equalsIgnoreCase(currentNewName)) {
                    isTargetBlock = true;
                }
            }

            if (line.startsWith("--------------------------------------------------")) {
                if (isTargetBlock) {
                    blockMatched = true;
                    isTargetBlock = false;
                } else {
                    updatedModLines.addAll(approvedBlock);
                }
                approvedBlock.clear();
            }
        }

        if (blockMatched) {
            System.out.println("DEBUG: currentUsername = " + currentUsername);
            FileManager.writeLines("modification_request.txt", updatedModLines);

            updateExistingBooking(currentUserId, currentOldStartDate, currentOldRoomType,
                    currentNewName, currentNewIC, currentNewPhone,
                    currentNewStartDate, currentNewReturnDate, currentNewRoomType);

            if (currentUsername != null && !currentUsername.isEmpty()) {
                NotificationDatabase.writeNotification(currentUsername,
                        "Booking Modification Approved",
                        "Your booking modification has been approved and updated.");
            }

            showAlert("Success", "Booking updated successfully.");
            closeWindow();
        } else {
            showAlert("Error", "No matching modification request found.");
        }
    }

    private void updateExistingBooking(String userId, String oldStartDate, String oldRoomType,
                                       String newName, String newIC, String newPhone,
                                       String newStartDate, String newReturnDate, String newRoomType) {

        List<String> bookingLines = FileManager.readLines("BookingInfo.txt");
        List<String> updatedBookingLines = new ArrayList<>();

        boolean bookingFound = false;
        List<String> currentBlock = new ArrayList<>();

        for (String line : bookingLines) {
            if (line.startsWith("UserID:")) {
                currentBlock.clear();
            }

            currentBlock.add(line);

            if (line.startsWith("--------------------------------------------------")) {
                String blockUserId = "";
                String blockStartDate = "";
                String blockRoomType = "";

                for (String blockLine : currentBlock) {
                    if (blockLine.startsWith("UserID:")) {
                        blockUserId = blockLine.substring(7).trim();
                    } else if (blockLine.startsWith("Start Date:")) {
                        blockStartDate = blockLine.substring(11).trim();
                    } else if (blockLine.startsWith("Room Type:")) {
                        blockRoomType = blockLine.substring(10).trim();
                    }
                }

                if (blockUserId.equals(userId) &&
                        blockStartDate.equals(oldStartDate) &&
                        blockRoomType.equalsIgnoreCase(oldRoomType)) {

                    List<String> modifiedBlock = new ArrayList<>();
                    for (String blockLine : currentBlock) {
                        if (blockLine.startsWith("Name:")) {
                            modifiedBlock.add("Name: " + newName);
                        } else if (blockLine.startsWith("IC/Passport:")) {
                            modifiedBlock.add("IC/Passport: " + newIC);
                        } else if (blockLine.startsWith("Phone Number:")) {
                            modifiedBlock.add("Phone Number: " + newPhone);
                        } else if (blockLine.startsWith("Start Date:")) {
                            modifiedBlock.add("Start Date: " + newStartDate);
                        } else if (blockLine.startsWith("Return Date:")) {
                            modifiedBlock.add("Return Date: " + newReturnDate);
                        } else if (blockLine.startsWith("Room Type:")) {
                            modifiedBlock.add("Room Type: " + newRoomType);
                        } else {
                            modifiedBlock.add(blockLine);
                        }
                    }

                    updatedBookingLines.addAll(modifiedBlock);
                    bookingFound = true;

                } else {
                    updatedBookingLines.addAll(currentBlock);
                }

                currentBlock.clear();
            }
        }

        if (bookingFound) {
            FileManager.writeLines("BookingInfo.txt", updatedBookingLines);
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void onBackButtonClicked() {
        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.close();
    }

    private void closeWindow() {
        Stage stage = (Stage) approveButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void onRejectClicked() {
        List<String> modLines = FileManager.readLines("modification_request.txt");
        List<String> updatedModLines = new ArrayList<>();
        List<String> currentBlock = new ArrayList<>();

        boolean isTargetBlock = false;
        boolean blockMatched = false;

        for (String line : modLines) {
            if (line.startsWith("UserID:")) {
                isTargetBlock = false;
                currentBlock.clear();
            }

            currentBlock.add(line);

            if (line.startsWith("New Name:")) {
                String nameInBlock = line.substring(9).trim();
                if (nameInBlock.equalsIgnoreCase(currentNewName)) {
                    isTargetBlock = true;
                }
            }

            if (line.startsWith("--------------------------------------------------")) {
                if (!isTargetBlock) {
                    updatedModLines.addAll(currentBlock);
                } else {
                    blockMatched = true;
                }
                currentBlock.clear();
            }
        }

        if (blockMatched) {
            FileManager.writeLines("modification_request.txt", updatedModLines);

            if (currentUsername != null && !currentUsername.isEmpty()) {
                NotificationDatabase.writeNotification(currentUsername,
                        "Booking Modification Rejected",
                        "Your booking modification request has been rejected.");
            }

            showAlert("Rejected", "Modification request has been rejected.");
            closeWindow();
        } else {
            showAlert("Error", "No matching modification request found.");
        }
    }

}