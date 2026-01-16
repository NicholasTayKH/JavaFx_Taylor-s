package org.example.assignment.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.assignment.FileManager;
import org.example.assignment.User;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ViewProfileBookingController {

    @FXML
    private VBox detailsSection;
    @FXML
    private VBox bookingSection;
    @FXML
    private VBox historySection;

    @FXML
    private VBox bookingSectionContent;
    @FXML
    private VBox historySectionContent;

    @FXML
    private Button detailsBtn;
    @FXML
    private Button bookingBtn;
    @FXML
    private Button historyBtn;

    @FXML
    private Label nameLabel;
    @FXML
    private Label phoneLabel;
    @FXML
    private Label dobLabel;
    @FXML
    private Label emailLabel;
    @FXML
    private Button homeButton;
    @FXML
    private Button manageRoomsButton;
    @FXML
    private Button logoutButton;

    private User user;

    @FXML
    public void initialize() {
        showSection("details");

        detailsBtn.setOnAction(e -> showSection("details"));
        bookingBtn.setOnAction(e -> showSection("bookings"));
        historyBtn.setOnAction(e -> showSection("history"));
    }

    public void setUser(User user) {
        this.user = user;
        showUserDetails();
        loadUserBookingsAndHistory();
    }

    private void showUserDetails() {
        List<String> userLines = FileManager.readLines("userDetails.txt");

        for (String line : userLines) {
            String[] parts = line.split(",");
            if (parts.length >= 4 && parts[0].trim().equals(user.getId().trim())) {
                nameLabel.setText(parts[1]);
                phoneLabel.setText(parts.length >= 6 ? parts[5].trim() : "N/A");
                dobLabel.setText(parts.length >= 5 ? parts[4].trim() : "N/A");
                emailLabel.setText(parts[3]);
                break;
            }
        }
    }

    private void showSection(String section) {
        boolean isDetails = section.equals("details");
        boolean isBookings = section.equals("bookings");
        boolean isHistory = section.equals("history");

        detailsSection.setVisible(isDetails);
        detailsSection.setManaged(isDetails);

        bookingSection.setVisible(isBookings);
        bookingSection.setManaged(isBookings);

        historySection.setVisible(isHistory);
        historySection.setManaged(isHistory);

        if (isBookings || isHistory) {
            loadUserBookingsAndHistory();
        }
    }

    private void loadUserBookingsAndHistory() {
        bookingSectionContent.getChildren().clear();
        historySectionContent.getChildren().clear();

        if (user == null) return;

        String targetUserId = user.getId().trim();
        List<String> bookingLines = FileManager.readLines("BookingInfo.txt");

        boolean match = false;
        String startDate = "", returnDate = "", roomType = "", totalNight = "", paymentAmount = "", name = "", phoneNumber = "", icPassport = "", travelPackage = "", numberOfRoom = "";

        for (String line : bookingLines) {
            if (line.startsWith("UserID:")) {
                String currentId = line.substring(8).trim();
                match = currentId.equals(targetUserId);
            }

            if (match) {
                if (line.startsWith("Start Date:")) startDate = line.substring(11).trim();
                else if (line.startsWith("Return Date:")) returnDate = line.substring(12).trim();
                else if (line.startsWith("Room Type:")) roomType = line.substring(10).trim();
                else if (line.startsWith("Total Night:")) totalNight = line.substring(12).trim();
                else if (line.startsWith("Payment Amount:")) paymentAmount = line.substring(15).trim();
                else if (line.startsWith("Name:")) name = line.substring(5).trim();
                else if (line.startsWith("Phone Number:")) phoneNumber = line.substring(13).trim();
                else if (line.startsWith("Number of Rooms:")) numberOfRoom = line.substring(16).trim();
                else if (line.startsWith("IC/Passport:")) icPassport = line.substring(12).trim();
                else if (line.startsWith("Travel Package:")) travelPackage = line.substring(15).trim();
            }

            if (match && line.startsWith("----")) {
                try {
                    LocalDate returnLocalDate = LocalDate.parse(returnDate);
                    HBox card = createBookingCard(numberOfRoom, startDate, returnDate, roomType, totalNight, paymentAmount, name, phoneNumber, icPassport, travelPackage);

                    if (!returnLocalDate.isBefore(LocalDate.now())) {
                        bookingSectionContent.getChildren().add(card);
                    } else {
                        historySectionContent.getChildren().add(card);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                match = false;
                startDate = returnDate = roomType = totalNight = paymentAmount = name = phoneNumber = icPassport = travelPackage = numberOfRoom = "";
            }
        }
    }

    private HBox createBookingCard(String numberOfRoom, String startDate, String returnDate, String roomType, String totalNight, String paymentAmount, String name, String phoneNumber, String icPassport, String travelPackage) {
        HBox card = new HBox(10);
        card.setStyle("-fx-background-color: #E0E0E0; -fx-background-radius: 15; -fx-padding: 10;");
        card.setPrefHeight(100);
        card.setPrefWidth(480);

        VBox textBox = new VBox(5);
        textBox.getChildren().addAll(
                new Label("Start: " + startDate + " | End: " + returnDate),
                new Label(roomType + " | " + numberOfRoom + " room | " + totalNight + " nights"),
                new Label("Package: " + travelPackage),
                new Label("Payment: " + paymentAmount)
        );

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        card.getChildren().addAll(textBox, spacer);
        return card;
    }

    @FXML
    private void handleDelete(ActionEvent event) {
        if (user == null) return;

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete this user and their bookings?", ButtonType.YES, ButtonType.NO);
        alert.setHeaderText("Confirm Deletion");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                deleteUserData();
                handleBack(event);
            }
        });
    }

    private void deleteUserData() {
        String userId = user.getId().trim();

        List<String> userLines = FileManager.readLines("userDetails.txt");
        List<String> updatedUserLines = new ArrayList<>();
        for (String line : userLines) {
            if (!line.startsWith(userId + ",")) {
                updatedUserLines.add(line);
            }
        }
        FileManager.writeLines("userDetails.txt", updatedUserLines);

        List<String> bookingLines = FileManager.readLines("BookingInfo.txt");
        List<String> updatedBookingLines = new ArrayList<>();
        boolean skip = false;

        for (String line : bookingLines) {
            if (line.startsWith("UserID:") && line.contains("UserID: " + userId)) {
                skip = true;
                continue;
            }
            if (skip && line.startsWith("----")) {
                skip = false;
                continue;
            }
            if (!skip) {
                updatedBookingLines.add(line);
            }
        }
        FileManager.writeLines("BookingInfo.txt", updatedBookingLines);
    }

    @FXML
    private void handleBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/assignment/AdminViewProfile.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Admin View Profile");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleHome() throws IOException {
        openPage("AdminDashboard.fxml");
    }

    @FXML
    private void handleManageRooms() throws IOException {
        openPage("Room.fxml");
    }


    private void openPage(String fxml) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/assignment/" + fxml));
            Scene scene = new Scene(loader.load());
            Stage stage = new Stage();
            stage.setTitle(fxml.replace(".fxml", ""));
            stage.setScene(scene);
            stage.setOnHidden(e -> initialize());
            stage.show();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
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
}