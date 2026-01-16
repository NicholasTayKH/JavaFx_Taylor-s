package org.example.assignment.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.example.assignment.FileManager;
import org.example.assignment.RoomItem;

import java.io.InputStream;
import java.util.*;

public class BookingDetailsController {

    @FXML
    private ImageView roomImage;
    @FXML
    private Label roomTypeLabel;
    @FXML
    private Label startDateLabel;
    @FXML
    private Label endDateLabel;
    @FXML
    private Label packageTypeLabel;

    @FXML
    private TextField nameField;
    @FXML
    private TextField ageField;
    @FXML
    private TextField passportField;
    @FXML
    private TextField phoneField;


    @FXML
    private Button backButton;

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
        backButton.setOnAction(e -> {
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.close();
        });
    }

    public void setBookingDetails(RoomItem booking) {
        if (booking == null) return;

        String roomType = booking.getRoomType() != null ? booking.getRoomType().trim() : "";
        String packageType = booking.getPackageType() != null ? booking.getPackageType().trim() : "";

        String imagePath = roomTypeImages.getOrDefault(roomType, "/org/example/assignment/default_room.png");

        InputStream imgStream = getClass().getResourceAsStream(imagePath);
        if (imgStream != null) {
            roomImage.setImage(new Image(imgStream));
        } else {
            roomImage.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/org/example/assignment/default_room.png"))));
        }

        roomTypeLabel.setText(roomType);
        startDateLabel.setText(booking.getStartDate() != null ? booking.getStartDate() : "");
        endDateLabel.setText(booking.getEndDate() != null ? booking.getEndDate() : "");
        packageTypeLabel.setText(packageType.isEmpty() ? "N/A" : packageType);
        nameField.setText(booking.getCustomerName() != null ? booking.getCustomerName() : "");

        String customerName = booking.getCustomerName() != null ? booking.getCustomerName().trim() : "";

        String passport = "N/A";
        String phone = "N/A";

        List<String> userLines = FileManager.readLines("BookingInfo.txt");
        List<String> currentBlock = new ArrayList<>();

        for (String line : userLines) {
            if (line.startsWith("UserID:")) {
                currentBlock.clear();
                currentBlock.add(line);
            } else if (line.equals("--------------------------------------------------")) {
                // End of block — process it
                boolean nameMatch = false;
                for (String blkLine : currentBlock) {
                    if (blkLine.startsWith("Name:")) {
                        String nameInBlock = blkLine.substring(5).trim();
                        if (nameInBlock.equalsIgnoreCase(customerName)) {
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
                        }
                    }
                }

                currentBlock.clear();
            } else {
                currentBlock.add(line);
            }
        }


        passportField.setText(passport.isEmpty() ? "N/A" : passport);
        phoneField.setText(phone.isEmpty() ? "N/A" : phone);
        ageField.setText("N/A");
        }
    }
