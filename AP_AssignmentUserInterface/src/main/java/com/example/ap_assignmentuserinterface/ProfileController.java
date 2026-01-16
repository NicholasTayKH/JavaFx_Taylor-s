package com.example.ap_assignmentuserinterface;

import Ai.AnswerService;
import Ai.CustomStreamingResponseHandler;
import Ai.SearchAction;
import Classes.ChatHistoryDatabase;
import Classes.Notification;
import Classes.NotificationDatabase;
import Classes.User;
import dev.langchain4j.model.chat.response.ChatResponse;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class ProfileController {
    @FXML private Button homeButton;
    @FXML private Button bookingButton;
    @FXML private Button profileButton;
    @FXML private Button notificationButton;
    @FXML private Button chatbotButton;
    @FXML private AnchorPane notificationPanel;
    @FXML private AnchorPane chatbotPanel;
    @FXML private Button collapseNotificationButton;
    @FXML private Button collapseChatBotButton;
    @FXML private VBox chatContainer;
    @FXML private TextField userInputField;
    @FXML private ScrollPane chatScrollPane;
    @FXML private Button sendButton;
    @FXML private VBox notificationContainer;
    @FXML private ScrollPane notificationScrollPane;
    @FXML private ScrollPane detailsPane, bookingsPane, historyPane;
    @FXML private ImageView profileImageView;
    @FXML private AnchorPane settingsPane;
    @FXML private TextField usernameField;
    @FXML private TextField emailField;
    @FXML private VBox detailsContent;
    @FXML private Label userIdLabel;
    @FXML private Label usernameLabel;
    @FXML private Label emailLabel;
    @FXML private TextField ageField;
    @FXML private TextField phoneField;
    @FXML private TextField icPassportField;
    @FXML private Label ageLabel;
    @FXML private Label icPassportLabel;
    @FXML private Label phoneLabel;
    @FXML private VBox bookingsContent;
    @FXML private VBox historyContent;
    @FXML private Button logoutButton;

    private final AnswerService answerService = new AnswerService();
    private boolean initialized = false;
    private String username ;
    private String currentEmail = "";
    private String backupUsername;
    private String backupEmail;
    private String UserID;
    private String backupAge;
    private String backupIC;
    private String backupPhone;



    public void setUsername(String username) {
        this.username = username; populateDetailsPane();
    }

    public String getUsername(){return username;}

    public void initialize(URL location, ResourceBundle resources) {
        Circle clip = new Circle(65, 65, 65); // x, y, radius — centered in image
        profileImageView.setClip(clip);
    }

    private void showDetailedBookingPopup(
            String userId,
            String username,
            String roomType,
            String startDate,
            String returnDate,
            String travelPackage,
            String name,
            String phoneNumber,
            String icPassport
    ) {
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("Modify Booking");

        TextField nameField = new TextField();
        TextField icField = new TextField();
        TextField phoneField = new TextField();
        DatePicker startDatePicker = new DatePicker(LocalDate.parse(startDate));
        DatePicker returnDatePicker = new DatePicker();
        ComboBox<String> roomTypeBox = new ComboBox<>();

        try {
            List<String> availableRooms = Files.readAllLines(Paths.get("available_rooms.txt"));
            Set<String> uniqueRoomTypes = new LinkedHashSet<>();
            for (String line : availableRooms) {
                String[] parts = line.split(",", 5);
                if (parts.length >= 2) {
                    uniqueRoomTypes.add(parts[1].trim());
                }
            }
            roomTypeBox.getItems().addAll(uniqueRoomTypes);
        } catch (IOException e) {
            e.printStackTrace();
        }

        roomTypeBox.setValue(roomType);

        GridPane grid = new GridPane();
        grid.setVgap(10);
        grid.setHgap(10);
        grid.setPadding(new Insets(20));

        grid.addRow(0, new Label("Name:"), nameField);
        grid.addRow(1, new Label("IC/Passport:"), icField);
        grid.addRow(2, new Label("Phone Number:"), phoneField);
        grid.addRow(3, new Label("Start Date:"), startDatePicker);
        grid.addRow(4, new Label("Return Date:"), returnDatePicker);
        grid.addRow(5, new Label("Room Type:"), roomTypeBox);

        HBox buttons = new HBox(10);
        Button saveBtn = new Button("Submit Request");
        Button cancelBtn = new Button("Cancel");
        buttons.getChildren().addAll(saveBtn, cancelBtn);

        VBox root = new VBox(15, grid, buttons);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #f0f0f0; -fx-background-radius: 15;");

        Scene scene = new Scene(root);
        popup.setScene(scene);
        popup.setResizable(false);

        try {
            List<String> lines = Files.readAllLines(Paths.get("BookingInfo.txt"));
            boolean isTarget = false;
            for (String line : lines) {
                if (line.startsWith("UserID:") && line.substring(7).trim().equals(userId)) {
                    isTarget = true;
                }
                if (isTarget && line.startsWith("Start Date:") && !line.substring(11).trim().equals(startDate)) {
                    isTarget = false;
                }
                if (isTarget && line.startsWith("Room Type:") && !line.substring(10).trim().equalsIgnoreCase(roomType)) {
                    isTarget = false;
                }
                if (isTarget) {
                    if (line.startsWith("Name:")) nameField.setText(line.substring(5).trim());
                    else if (line.startsWith("IC/Passport:")) icField.setText(line.substring(12).trim());
                    else if (line.startsWith("Phone Number:")) phoneField.setText(line.substring(13).trim());
                    else if (line.startsWith("Return Date:")) returnDatePicker.setValue(LocalDate.parse(line.substring(12).trim()));
                }
                if (line.startsWith("--------------------------------------------------")) {
                    if (isTarget) break;
                    isTarget = false;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        saveBtn.setOnAction(event -> {
            String selectedRoomType = roomTypeBox.getValue();
            LocalDate selectedStart = startDatePicker.getValue();
            LocalDate selectedReturn = returnDatePicker.getValue();

            if (selectedStart == null || selectedReturn == null) {
                showAlert("Invalid Date", "Please select both start and return dates.");
                return;
            }

            if (!selectedReturn.isAfter(selectedStart)) {
                showAlert("Invalid Date", "Return date must be after start date.");
                return;
            }

            boolean isChangingRoomOrDate = !selectedStart.toString().equals(startDate)
                    || !selectedRoomType.equalsIgnoreCase(roomType);

            if (isChangingRoomOrDate) {
                try {
                    List<String> availableRooms = Files.readAllLines(Paths.get("available_rooms.txt"));
                    int totalRoomsForType = 0;

                    for (String line : availableRooms) {
                        String[] parts = line.split(",", 5);
                        if (parts.length >= 2 && parts[1].trim().equalsIgnoreCase(selectedRoomType)) {
                            totalRoomsForType++;
                        }
                    }

                    Map<LocalDate, Integer> roomBookingPerDay = new HashMap<>();
                    List<String> bookingLines = Files.readAllLines(Paths.get("BookingInfo.txt"));
                    List<String> block = new ArrayList<>();
                    boolean inBlock = false;

                    for (String line : bookingLines) {
                        if (line.startsWith("UserID:")) {
                            block.clear();
                            block.add(line);
                            inBlock = true;
                        } else if (inBlock && line.equals("--------------------------------------------------")) {
                            String bookedRoomType = "";
                            LocalDate bookedStart = null;
                            LocalDate bookedEnd = null;
                            int bookedRoomCount = 1;

                            for (String bLine : block) {
                                if (bLine.startsWith("Room Type:")) {
                                    bookedRoomType = bLine.substring(10).trim();
                                } else if (bLine.startsWith("Start Date:")) {
                                    bookedStart = LocalDate.parse(bLine.substring(11).trim());
                                } else if (bLine.startsWith("Return Date:")) {
                                    bookedEnd = LocalDate.parse(bLine.substring(12).trim());
                                } else if (bLine.startsWith("Number of Rooms:")) {
                                    try {
                                        bookedRoomCount = Integer.parseInt(bLine.substring(17).trim());
                                    } catch (NumberFormatException e) {
                                        bookedRoomCount = 1;
                                    }
                                }
                            }

                            if (bookedRoomType.equalsIgnoreCase(selectedRoomType) && bookedStart != null && bookedEnd != null) {
                                for (LocalDate d = bookedStart; !d.isAfter(bookedEnd.minusDays(1)); d = d.plusDays(1)) {
                                    roomBookingPerDay.put(d, roomBookingPerDay.getOrDefault(d, 0) + bookedRoomCount);
                                }
                            }

                            inBlock = false;
                        } else if (inBlock) {
                            block.add(line);
                        }
                    }

                    // Check selected range for full rooms
                    for (LocalDate d = selectedStart; !d.isAfter(selectedReturn.minusDays(1)); d = d.plusDays(1)) {
                        int booked = roomBookingPerDay.getOrDefault(d, 0);
                        if (booked >= totalRoomsForType) {
                            showAlert("Room Unavailable", "Room fully booked on " + d + " for type: " + selectedRoomType);
                            return;
                        }
                    }

                } catch (IOException ex) {
                    ex.printStackTrace();
                    showAlert("Error", "Unable to check room availability.");
                    return;
                }
            }

            // ✅ 生成修改请求
            try {
                List<String> requestLines = new ArrayList<>();
                requestLines.add("UserID: " + userId);
                requestLines.add("Username: " + username);
                requestLines.add("Old Start Date: " + startDate);
                requestLines.add("Old Room Type: " + roomType);
                requestLines.add("New Name: " + nameField.getText());
                requestLines.add("New IC/Passport: " + icField.getText());
                requestLines.add("New Phone Number: " + phoneField.getText());
                requestLines.add("New Start Date: " + selectedStart.toString());
                requestLines.add("New Return Date: " + selectedReturn.toString());
                requestLines.add("New Room Type: " + selectedRoomType);
                requestLines.add("requestApprove: false");
                requestLines.add("--------------------------------------------------");

                Files.write(Paths.get("modification_request.txt"), requestLines,
                        StandardOpenOption.CREATE, StandardOpenOption.APPEND);

                showAlert("Request Submitted", "Your booking modification request has been submitted for admin approval.");
                popup.close();

            } catch (IOException ex) {
                ex.printStackTrace();
                showAlert("Error", "Failed to submit your request.");
            }
        });


        cancelBtn.setOnAction(event -> popup.close());
        popup.showAndWait();
    }



    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void updateBooking(String userId, String oldStartDate, String oldRoomType,
                               String newName, String newIC, String newPhone,
                               String newStartDate, String newReturnDate, String newRoomType,String totalNight) {
        try {
            Path filePath = Paths.get("BookingInfo.txt");
            List<String> lines = Files.readAllLines(filePath);

            List<String> updatedLines = new ArrayList<>();
            boolean insideBooking = false;
            boolean isTargetBooking = false;
            List<String> currentBooking = new ArrayList<>();

            for (String line : lines) {
                if (line.startsWith("UserID:")) {
                    insideBooking = true;
                    isTargetBooking = false;
                    currentBooking.clear();
                }

                if (insideBooking) {
                    currentBooking.add(line);

                    if (line.startsWith("UserID:") && line.substring(8).trim().equals(userId))
                        isTargetBooking = true;
                    if (line.startsWith("Start Date:") && !line.substring(12).trim().equals(oldStartDate))
                        isTargetBooking = false;
                    if (line.startsWith("Room Type:") && !line.substring(11).trim().equals(oldRoomType))
                        isTargetBooking = false;

                    if (line.startsWith("--------------------------------------------------")) {
                        insideBooking = false;

                        if (isTargetBooking) {
                            List<String> modified = new ArrayList<>();
                            for (String l : currentBooking) {
                                if (l.startsWith("Name:")) modified.add("Name: " + newName);
                                else if (l.startsWith("IC/Passport:")) modified.add("IC/Passport: " + newIC);
                                else if (l.startsWith("Phone Number:")) modified.add("Phone Number: " + newPhone);
                                else if (l.startsWith("Start Date:")) modified.add("Start Date: " + newStartDate);
                                else if (l.startsWith("Return Date:")) modified.add("Return Date: " + newReturnDate);
                                else if (l.startsWith("Room Type:")) modified.add("Room Type: " + newRoomType);
                                else if (l.startsWith("Total Night:")) modified.add("Total Night: " + totalNight);
                                else modified.add(l);
                            }
                            updatedLines.addAll(modified);
                        } else {
                            updatedLines.addAll(currentBooking);
                        }

                        currentBooking.clear();
                    }
                } else {
                    updatedLines.add(line);
                }
            }

            Files.write(filePath, updatedLines);
            populateBookings(userId); // refresh UI
            NotificationDatabase.writeNotification(username, "Booking Modify!","A booking has been deleted");
            loadNotifications();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Booking Updated");
            alert.setContentText("Booking updated successfully.");
            alert.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void requestBooking(String userId, String oldStartDate, String oldRoomType,
                                String newName, String newIC, String newPhone,
                                String newStartDate, String newReturnDate, String newRoomType, String totalNight) {
        try {
            // Define the output file path
            Path requestPath = Paths.get("modification_request.txt");

            // Prepare the content to write
            List<String> requestLines = new ArrayList<>();
            requestLines.add("UserID: " + userId);
            requestLines.add("Username: " + username);
            requestLines.add("Old Start Date: " + oldStartDate);
            requestLines.add("Old Room Type: " + oldRoomType);
            requestLines.add("New Name: " + newName);
            requestLines.add("New IC/Passport: " + newIC);
            requestLines.add("New Phone Number: " + newPhone);
            requestLines.add("New Start Date: " + newStartDate);
            requestLines.add("New Return Date: " + newReturnDate);
            requestLines.add("New Room Type: " + newRoomType);
            requestLines.add("Total Night: " + totalNight);
            requestLines.add("requestApprove: false");
            requestLines.add("--------------------------------------------------");


            // Append to the file
            Files.write(requestPath, requestLines, StandardOpenOption.CREATE, StandardOpenOption.APPEND);

            // Optional notifications
            NotificationDatabase.writeNotification(username, "Booking Modification Requested", "Your booking change request has been submitted.");
            loadNotifications();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Modification Request Submitted");
            alert.setContentText("Your request has been saved for admin approval.");
            alert.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    private void deleteBooking(String userId, String startDate, String roomType) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm Delete");
        confirmation.setHeaderText("Are you sure you want to delete this booking?");
        confirmation.setContentText("This action cannot be undone.");

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                Path filePath = Paths.get("BookingInfo.txt");
                List<String> lines = Files.readAllLines(filePath);

                List<String> updatedLines = new ArrayList<>();
                boolean insideBooking = false;
                boolean isTargetBooking = false;
                List<String> currentBooking = new ArrayList<>();

                for (String line : lines) {
                    if (line.startsWith("UserID:")) {
                        insideBooking = true;
                        isTargetBooking = false;
                        currentBooking.clear(); // Start capturing a new booking
                    }

                    if (insideBooking) {
                        currentBooking.add(line);

                        if (line.startsWith("UserID:") && line.substring(8).trim().equals(userId)) {
                            isTargetBooking = true;
                        }

                        if (line.startsWith("Start Date:") && !line.substring(12).trim().equals(startDate)) {
                            isTargetBooking = false;
                        }

                        if (line.startsWith("Room Type:") && !line.substring(11).trim().equals(roomType)) {
                            isTargetBooking = false;
                        }

                        if (line.startsWith("--------------------------------------------------")) {
                            insideBooking = false;

                            // If it's not the target booking, keep it
                            if (!isTargetBooking) {
                                updatedLines.addAll(currentBooking);
                            }

                            currentBooking.clear(); // Reset
                        }
                    } else {
                        // Outside a booking block (e.g., blank lines), just keep it
                        updatedLines.add(line);
                    }
                }

                // Write back the updated bookings
                Files.write(filePath, updatedLines);

                Alert success = new Alert(Alert.AlertType.INFORMATION);
                success.setHeaderText(null);
                success.setContentText("Booking deleted successfully.");
                NotificationDatabase.writeNotification(username, "Booking Deleted!","A booking has been deleted");
                loadNotifications();
                success.showAndWait();

                // Refresh booking list (optional)
                populateBookings(userId);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



    private void populateHistory(String userId) {
        historyContent.getChildren().clear(); // Clear existing history cards

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate today = LocalDate.now();
        boolean hasBooking = false;


        try {
            List<String> lines = Files.readAllLines(Paths.get("BookingInfo.txt"));

            boolean match = false;
            String startDate = "", roomType = "", totalNight = "", paymentAmount = "", returnDate = "",
                    name="",phoneNumber="", icPassport="", travelPackage="",NumberOfRoom="";

            for (String line : lines) {
                if (line.startsWith("UserID:")) {
                    match = line.substring(8).trim().equals(userId);
                }

                if (match) {
                    if (line.startsWith("Start Date:")) {
                        startDate = line.substring(12).trim();
                    } else if (line.startsWith("Return Date:")) {
                        returnDate = line.substring(13).trim(); // Capture return date
                    } else if (line.startsWith("Room Type:")) {
                        roomType = line.substring(11).trim();
                    } else if (line.startsWith("Total Night:")) {
                        totalNight = line.substring(13).trim();
                    } else if (line.startsWith("Payment Amount:")) {
                        paymentAmount = line.substring(16).trim();
                    } else if (line.startsWith("Name:")) {
                        name = line.substring(6).trim();
                    } else if (line.startsWith("Number of Rooms:")) {
                        NumberOfRoom = line.substring(17).trim();
                    }else if (line.startsWith("Phone Number:")) {
                        phoneNumber = line.substring(14).trim();
                    } else if (line.startsWith("IC/Passport:")) {
                        icPassport = line.substring(13).trim();
                    } else if (line.startsWith("Travel Package:")) {
                        travelPackage = line.substring(16).trim();
                    }else if (line.startsWith("--------------------------------------------------")) {
                        LocalDate returnDateObj = LocalDate.parse(returnDate, formatter);
                        if (returnDateObj.isBefore(today)) {
                            HBox card = createHistoryCard(   userId,             // Add this ✅
                                    name,               // Or replace with actual username if you have one ✅
                                    NumberOfRoom,
                                    startDate,
                                    returnDate,
                                    roomType,
                                    totalNight,
                                    paymentAmount,
                                    name,
                                    phoneNumber,
                                    icPassport,
                                    travelPackage);
                            historyContent.getChildren().add(card);
                            hasBooking = true;
                        }
                        match = false; // Reset for next record
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (!hasBooking) {
            Label noBookingLabel = new Label("     No bookings \n     found.");
            noBookingLabel.setStyle("-fx-font-size: 54px; -fx-text-fill: red; -fx-font-weight: bold;");
            noBookingLabel.setTextAlignment(TextAlignment.CENTER);
            historyContent.getChildren().add(noBookingLabel);
        }
    }

    private HBox createHistoryCard(        String userId,
                                           String username,
                                           String numberOfRoom,
                                           String startDate,
                                           String returnDate,
                                           String roomType,
                                           String totalNight,
                                           String paymentAmount,
                                           String name,
                                           String phoneNumber,
                                           String icPassport,
                                           String travelPackage) {
        HBox card = new HBox(10);
        card.setStyle("-fx-background-color: #E0E0E0; -fx-background-radius: 15; -fx-padding: 10;");
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPrefHeight(100);
        card.setPrefWidth(480);

        ImageView imageView = new ImageView(new Image(getClass().getResourceAsStream("/com/example/ap_assignmentuserinterface/images/Booking.png")));
        imageView.setFitWidth(80);
        imageView.setFitHeight(80);

        VBox textBox = new VBox(10);
        textBox.getChildren().addAll(
                new Label("Start date: " + startDate),
                new Label(roomType + " | " +numberOfRoom+" room | "+ totalNight + " nights"),
                new Label("Payment: " + paymentAmount)
        );

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button viewBtn = new Button("View");
        viewBtn.setStyle("-fx-background-color: white;");
        viewBtn.setOnAction(e -> showDetailedBookingPopup(
                userId,
                username,
                roomType,
                startDate,
                returnDate,
                travelPackage,
                name,
                phoneNumber,
                icPassport
        ));

        VBox buttonBox = new VBox(viewBtn);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);

        card.getChildren().addAll(imageView, textBox, spacer, buttonBox);
        return card;
    }

    // "C:\\Users\\jxlor\\IdeaProjects\\AP_AssignmentUserInterface\\src\\main\\resources\\documents\\BookingInfo.txt"
    private void populateBookings(String userId) {
        bookingsContent.getChildren().clear();
        boolean hasBooking = false; // Clear previous cards

        try {
            List<String> lines = Files.readAllLines(Paths.get("BookingInfo.txt"));
            boolean match = false;
            String startDate = "", roomType = "", totalNight = "", paymentAmount = "", returnDate = "",
                    name="",phoneNumber="", icPassport="", travelPackage="",NumberOfRoom="";

            for (String line : lines) {
                if (line.startsWith("UserID:")) {
                    String currentId = line.substring(8).trim();
                    match = currentId.equals(userId); // String comparison
                }

                if (match) {
                    if (line.startsWith("Start Date:")) {
                        startDate = line.substring(12).trim();
                    } else if (line.startsWith("Return Date:")) {
                        returnDate = line.substring(13).trim(); // Capture return date
                    } else if (line.startsWith("Room Type:")) {
                        roomType = line.substring(11).trim();
                    } else if (line.startsWith("Total Night:")) {
                        totalNight = line.substring(13).trim();
                    } else if (line.startsWith("Payment Amount:")) {
                        paymentAmount = line.substring(16).trim();
                    } else if (line.startsWith("Name:")) {
                        name = line.substring(6).trim();
                    } else if (line.startsWith("Phone Number:")) {
                        phoneNumber = line.substring(14).trim();
                    } else if (line.startsWith("Number of Rooms:")) {
                        NumberOfRoom = line.substring(17).trim();
                    } else if (line.startsWith("IC/Passport:")) {
                        icPassport = line.substring(13).trim();
                    } else if (line.startsWith("Travel Package:")) {
                        travelPackage = line.substring(16).trim();
                    }else if (line.startsWith("--------------------------------------------------")) {
                        // Parse return date and compare with today
                        try {
                            LocalDate returnLocalDate = LocalDate.parse(returnDate); // Format: yyyy-MM-dd
                            if (!returnLocalDate.isBefore(LocalDate.now())) {
                                // Date is today or after
                                HBox card = createBookingCard(userId,username,NumberOfRoom,startDate, returnDate, roomType, totalNight, paymentAmount, name, phoneNumber,icPassport, travelPackage);
                                bookingsContent.getChildren().add(card);
                                hasBooking = true;
                            }
                        } catch (DateTimeParseException e) {
                            System.err.println("Invalid return date format: " + returnDate);
                        }

                        // Reset flags/data
                        match = false;
                        startDate = roomType = totalNight = paymentAmount = returnDate = "";
                    }
                }
            }

            // Show fallback message if no bookings
            if (!hasBooking) {
                Label noBookingLabel = new Label("     No bookings \n     found.");
                noBookingLabel.setStyle("-fx-font-size: 54px; -fx-text-fill: red; -fx-font-weight: bold;");
                noBookingLabel.setTextAlignment(TextAlignment.CENTER);
                bookingsContent.getChildren().add(noBookingLabel);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private HBox createBookingCard(
            String userId,           // ✅ Added userId
            String username,         // ✅ Added username
            String NumberOfRoom,
            String startDate,
            String returnDate,
            String roomType,
            String totalNight,
            String paymentAmount,
            String name,
            String phoneNumber,
            String icPassport,
            String travelPackage
    ) {
        HBox card = new HBox(10);
        card.setStyle("-fx-background-color: #D3D3D3; -fx-background-radius: 15; -fx-padding: 10;");
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPrefHeight(100);
        card.setPrefWidth(480);

        // Image
        ImageView imageView = new ImageView(new Image(getClass().getResourceAsStream("/com/example/ap_assignmentuserinterface/images/Booking.png")));
        imageView.setFitWidth(80);
        imageView.setFitHeight(80);

        // Text Section
        VBox textBox = new VBox(10);
        textBox.getChildren().addAll(
                new Label("Start date: " + startDate),
                new Label(roomType + " | " + NumberOfRoom + " room | " + totalNight + " nights"),
                new Label("Payment: " + paymentAmount)
        );

        // Buttons Section
        VBox buttonsBox = new VBox(5);
        buttonsBox.setAlignment(Pos.CENTER_RIGHT);

        Button viewBtn = new Button("View");
        Button modifyBtn = new Button("Modify");
        Button cancelBtn = new Button("Delete");

        // ====== VIEW BUTTON ======
        viewBtn.setStyle("-fx-background-color: white;");
        viewBtn.setOnAction(e -> showViewOnlyBookingPopup(
                userId,           // ✅ Fixed
                username,         // ✅ Fixed
                roomType,
                startDate,
                returnDate,
                travelPackage,
                name,
                phoneNumber,
                icPassport
        ));

        // ====== MODIFY BUTTON ======
        modifyBtn.setStyle("-fx-background-color: white;");
        modifyBtn.setOnAction(e -> showDetailedBookingPopup(
                userId,
                username,
                roomType,
                startDate,
                returnDate,
                travelPackage,
                name,
                phoneNumber,
                icPassport
        ));

        // ====== DELETE BUTTON ======
        cancelBtn.setStyle("-fx-background-color: white;");
        cancelBtn.setOnAction(e -> deleteBooking(userId, startDate, roomType));    // ✅ Fixed

        buttonsBox.getChildren().addAll(viewBtn, modifyBtn, cancelBtn);

        // Spacer
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Add all to Card
        card.getChildren().addAll(imageView, textBox, spacer, buttonsBox);
        return card;
    }
    private void showViewOnlyBookingPopup(
            String userId,
            String username,
            String roomType,
            String startDate,
            String returnDate,
            String travelPackage,
            String name,
            String phoneNumber,
            String icPassport
    ) {
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("Booking Details");

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER_LEFT);

        layout.getChildren().addAll(
                new Label("Name: " + name),
                new Label("IC/Passport: " + icPassport),
                new Label("Phone Number: " + phoneNumber),
                new Label("Start Date: " + startDate),
                new Label("Return Date: " + returnDate),
                new Label("Room Type: " + roomType),
                new Label("Travel Package: " + travelPackage)
        );

        Button closeButton = new Button("Close");
        closeButton.setOnAction(e -> popup.close());

        layout.getChildren().add(closeButton);

        Scene scene = new Scene(layout, 350, 400);
        popup.setScene(scene);
        popup.setResizable(false);
        popup.showAndWait();
    }



    @FXML
    private void showDetailsPane() {
        detailsPane.toFront();
        detailsPane.setVisible(true);
        bookingsPane.setVisible(false);
        historyPane.setVisible(false);
    }

    private void populateDetailsPane() {
        try {
            UserID = getUserIdByUsername(username);
            System.out.println("User ID: " + UserID);

            List<String> lines = Files.readAllLines(Paths.get("userDetails.txt"));
            for (String line : lines) {
                String[] parts = line.split(",");

                if (parts.length >= 4 && parts[0].trim().equals(UserID)) {
                    userIdLabel.setText(parts[0].trim());
                    usernameLabel.setText(parts[1].trim());
                    emailLabel.setText(getSafeField(parts, 3));
                    ageLabel.setText(getSafeField(parts, 4));
                    icPassportLabel.setText(getSafeField(parts, 5));
                    phoneLabel.setText(getSafeField(parts, 6));
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getSafeField(String[] parts, int index) {
        if (parts.length > index && !parts[index].trim().isEmpty()) {
            return parts[index].trim();
        } else {
            return "-";
        }
    }


    @FXML
    private void showBookingsPane() {
        UserID = getUserIdByUsername(username);
        System.out.println("User ID: " + UserID);

        detailsPane.setVisible(false);
        bookingsPane.setVisible(true);
        historyPane.setVisible(false);
        populateBookings(UserID);
    }

    @FXML
    private void showHistoryPane() {
        UserID = getUserIdByUsername(username);
        System.out.println("User ID: " + UserID);
        detailsPane.setVisible(false);
        bookingsPane.setVisible(false);
        historyPane.setVisible(true);
        populateHistory(UserID);
    }

    @FXML
    private void handleLogout(ActionEvent event) throws IOException
    {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Logout Confirmation");
        alert.setHeaderText(null);
        alert.setContentText("Confirm Logout?");

        ButtonType yesButton = new ButtonType("Yes", ButtonBar.ButtonData.YES);
        ButtonType noButton = new ButtonType("No", ButtonBar.ButtonData.NO);

        alert.getButtonTypes().setAll(yesButton, noButton);

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == yesButton) {
            try {
                FXMLLoader loader = new FXMLLoader(LoginController.class.getResource("login-page.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();
                System.out.println("Logout");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Logout cancelled.");
        }
    }


    @FXML
    private void handleOpenSettings() {
        try {
            UserID = getUserIdByUsername(username);
            System.out.println("user ID:" + UserID);

            List<String> lines = Files.readAllLines(Paths.get("userDetails.txt"));
            for (String line : lines) {
                String[] parts = line.split(",");
                if (parts.length >= 4 && parts[0].equals(UserID)) {
                    backupUsername = parts[1];
                    backupEmail = parts[3];
                    backupAge = parts.length > 4 ? parts[4].trim() : "";
                    backupIC = parts.length > 5 ? parts[5].trim() : "";
                    backupPhone = parts.length > 6 ? parts[6].trim() : "";

                    usernameField.setText(backupUsername);
                    emailField.setText(backupEmail);
                    ageField.setText(backupAge);
                    icPassportField.setText(backupIC);
                    phoneField.setText(backupPhone);
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        settingsPane.setVisible(true);
        settingsPane.setMouseTransparent(false);
        settingsPane.toFront();
    }

    @FXML
    private void handleSaveSettings() {
        String newUsername = usernameField.getText();
        String newEmail = emailField.getText();
        String newAge = ageField.getText();
        String newIC = icPassportField.getText();
        String newPhone = phoneField.getText();

        Path path = Paths.get("userDetails.txt");
        Path path2 = Paths.get("user.txt");

        try {
            List<String> lines = Files.readAllLines(path);

            for (int i = 0; i < lines.size(); i++) {
                String[] parts = lines.get(i).split(",");
                if (parts.length >= 4 && parts[0].equals(UserID))
                {
                    // Update line with additional fields
                    String updatedLine = parts[0] + "," + newUsername + "," + parts[2] + "," + newEmail
                            + "," + newAge + "," + newIC + "," + newPhone;
                    lines.set(i, updatedLine);

                    // Update runtime references
                    username = newUsername;
                    currentEmail = newEmail;
                    break;
                }
            }

            Files.write(path, lines);

        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            List<String> lines = Files.readAllLines(path2);

            for (int i = 0; i < lines.size(); i++) {
                String[] parts = lines.get(i).split(",");
                if (parts.length >= 4 && parts[0].equals(UserID))
                {
                    // Update line with additional fields
                    String updatedLine = parts[0] + "," + newUsername + "," + parts[2] + "," + newEmail;
                    lines.set(i, updatedLine);

                    // Update runtime references
                    username = newUsername;
                    currentEmail = newEmail;
                    break;
                }
            }

            Files.write(path2, lines);

        } catch (IOException e) {
            e.printStackTrace();
        }
        NotificationDatabase.writeNotification(username, "User Details Updated!","Your new user details are updated");
        loadNotifications();
        settingsPane.setVisible(false);
        settingsPane.setMouseTransparent(true);
        System.out.println("Saved Username: " + username);
        System.out.println("Saved Email: " + currentEmail);
    }

    @FXML
    private void handleCancelSettings() {
        usernameField.setText(backupUsername);
        emailField.setText(backupEmail);
        ageField.setText(backupAge);
        icPassportField.setText(backupIC);
        phoneField.setText(backupPhone);

        settingsPane.setVisible(false);
        settingsPane.setMouseTransparent(true);
        System.out.println("Settings edit cancelled");
    }


    @FXML
    private void goBooking(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("booking-page.fxml"));
        Parent root = loader.load();
        BookingController controller = loader.getController();
        controller.setUsername(username);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root,800,600);
        stage.setScene(scene);
        stage.show();
    }
    @FXML
    private void goHome(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("home-page.fxml"));
        Parent root = loader.load();
        HomeController controller = loader.getController();
        controller.setUsername(username);


        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }



    public void loadNotifications() {
        notificationContainer.getChildren().clear(); // Clear previous ones to avoid duplicates
        List<Notification> unread = NotificationDatabase.readUnreadNotifications(username);
        Collections.reverse(unread);
        for (Notification n : unread) {
            addNotification(n.getSubject(), n.getContent(), n.getTimestamp(), n.getId());
        }
    }



    public String getUserIdByUsername(String username) {
        try {
            List<String> lines = Files.readAllLines(Paths.get("user.txt"));
            for (String line : lines) {
                String[] parts = line.split(",");
                if (parts.length >= 2 && parts[1].equals(username)) {
                    return parts[0]; // return the User ID
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null; // if user not found
    }
    private void applyClickScaleEffect(Button button) {
        button.setOnMousePressed(e -> {
            button.setScaleX(0.9);
            button.setScaleY(0.9);
        });

        button.setOnMouseReleased(e -> {
            button.setScaleX(1.0);
            button.setScaleY(1.0);
        });
    }
    private void applyEffects(Button button) {
        applyClickScaleEffect(button);
        applyWaterHoverEffect(button);
    }
    @FXML private Button settingsButton;
    @FXML private Button settingsSave;
    @FXML private Button settingsCancel;
    @FXML private Button detailsTab;
    @FXML private Button historyTab;
    @FXML private Button bookingsTab;
    public void initialize() {
        if (!initialized)
        {
            SearchAction initAction = new SearchAction("Initializing chatbot...");
            answerService.init(initAction);
            initialized = true;
        }

        System.out.println("detailsContent: " + detailsContent);
        System.out.println("Username on init: " + username); // Debug print
        populateDetailsPane();
        notificationPanel.setTranslateY(-650);
        chatbotPanel.setTranslateX(850);
        applyEffects(homeButton);
        applyEffects(bookingButton);
        applyEffects(notificationButton);
        applyEffects(chatbotButton);
        applyEffects(collapseNotificationButton);
        applyEffects(collapseChatBotButton);
        applyEffects(sendButton);
        applyEffects(settingsButton);
        applyEffects(settingsSave);
        applyEffects(settingsCancel);
        applyEffects(detailsTab);
        applyEffects(historyTab);
        applyEffects(bookingsTab);
        applyEffects(logoutButton);
        Platform.runLater(() ->{
            // Load the user's notifications here
            List<Notification> unread = NotificationDatabase.readUnreadNotifications(username);
            for (Notification n : unread) {
                addNotification(n.getSubject(), n.getContent(), n.getTimestamp(), n.getId());
            }
        });
        Platform.runLater(() -> {
            chatScrollPane.lookupAll(".scroll-bar").forEach(node -> {
                if (node instanceof ScrollBar scrollBar &&
                        scrollBar.getOrientation() == javafx.geometry.Orientation.VERTICAL) {

                    scrollBar.setTranslateX(20);
                }
            });
            for (Node node : chatScrollPane.lookupAll(".scroll-bar")) {
                if (node instanceof ScrollBar scrollBar && scrollBar.getOrientation() == Orientation.VERTICAL) {
                    // Make the track transparent
                    scrollBar.setStyle("-fx-background-color: transparent;");

                    // Make the thumb (inner bar) black
                    Node thumb = scrollBar.lookup(".thumb");
                    if (thumb != null) {
                        thumb.setStyle("-fx-background-color: black;");
                    }
                }
            }
        });
        chatScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        chatScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        Platform.runLater(() -> {
            notificationScrollPane.lookupAll(".scroll-bar").forEach(node -> {
                if (node instanceof ScrollBar scrollBar &&
                        scrollBar.getOrientation() == javafx.geometry.Orientation.VERTICAL) {

                    scrollBar.setTranslateX(20);
                }
            });
            for (Node node : notificationScrollPane.lookupAll(".scroll-bar")) {
                if (node instanceof ScrollBar scrollBar && scrollBar.getOrientation() == Orientation.VERTICAL) {
                    // Make the track transparent
                    scrollBar.setStyle("-fx-background-color: transparent;");

                    // Make the thumb (inner bar) black
                    Node thumb = scrollBar.lookup(".thumb");
                    if (thumb != null) {
                        thumb.setStyle("-fx-background-color: black;");
                    }
                }
            }
        });
        notificationScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        notificationScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
    }
    private void stylePrettyButton(Button button, String borderColor) {
        String baseStyle = "-fx-background-color: linear-gradient(#ffffff, #e0e0e0);"
                + "-fx-border-color: " + borderColor + ";"
                + "-fx-border-radius: 20;"
                + "-fx-background-radius: 20;"
                + "-fx-font-weight: bold;"
                + "-fx-text-fill: #333333;"
                + "-fx-cursor: hand;";
        String hoverStyle = "-fx-background-color: linear-gradient(#d0f0ff, #b0e0ff);"
                + "-fx-border-color: " + borderColor + ";"
                + "-fx-border-radius: 20;"
                + "-fx-background-radius: 20;"
                + "-fx-font-weight: bold;"
                + "-fx-text-fill: black;";

        // Set base style
        button.setStyle(baseStyle);

        // Add hover effects
        button.setOnMouseEntered(e -> button.setStyle(hoverStyle));
        button.setOnMouseExited(e -> button.setStyle(baseStyle));
    }
    private void applyWaterHoverEffect(Button button) {
        DropShadow shadow = new DropShadow();
        shadow.setRadius(0);
        shadow.setSpread(0.1);
        shadow.setColor(javafx.scene.paint.Color.LIGHTBLUE);

        ScaleTransition scaleUp = new ScaleTransition(Duration.millis(300), button);
        scaleUp.setToX(1.15);
        scaleUp.setToY(1.15);

        ScaleTransition scaleDown = new ScaleTransition(Duration.millis(300), button);
        scaleDown.setToX(1.0);
        scaleDown.setToY(1.0);

        button.setOnMouseEntered(e -> {
            button.setEffect(shadow);
            scaleUp.playFromStart();
        });

        button.setOnMouseExited(e -> {
            button.setEffect(null);
            scaleDown.playFromStart();
        });
    }
    @FXML
    private void hideNotificationPanel(ActionEvent event) {
        TranslateTransition transition = new TranslateTransition(Duration.millis(300), notificationPanel);
        transition.setToY(-650); // move it back up off-screen
        transition.setOnFinished(e -> notificationPanel.setVisible(false)); // optional: hide after animation
        transition.play();
    }
    @FXML
    private void hideChatbotPanel(ActionEvent event) {
        TranslateTransition transition = new TranslateTransition(Duration.millis(300), chatbotPanel);
        transition.setToX(850); // move it off-screen right
        transition.setOnFinished(e -> chatbotPanel.setVisible(false));
        transition.play();
    }@FXML
    private void showNotificationPanel(ActionEvent event) {
        notificationPanel.setVisible(true);  // make it visible again
        TranslateTransition transition = new TranslateTransition(Duration.millis(300), notificationPanel);
        transition.setToY(0); // slide it into place
        transition.play();
    }
    @FXML
    private void showChatbotPanel(ActionEvent event) {
        chatbotPanel.setVisible(true);
        TranslateTransition transition = new TranslateTransition(Duration.millis(300), chatbotPanel);
        transition.setToX(0); // slide it back to visible area
        transition.play();

        if (chatContainer.getChildren().size() > 1) {
            chatContainer.getChildren().remove(1, chatContainer.getChildren().size());
        }

        List<String[]> history = ChatHistoryDatabase.loadHistory();
        for (String[] entry : history) {
            String sender = entry[0];
            String message = entry[1];
            if (sender.equals("user")) {
                addUserMessage(message);
            } else if (sender.equals("ai")) {
                Text aiText = addStreamingAiMessage();
                aiText.setText(message);
            }
        }
        Platform.runLater(() -> chatScrollPane.setVvalue(1.0));
    }
    public Text addStreamingAiMessage() {
        HBox aiChatLayer = new HBox();
        aiChatLayer.setAlignment(Pos.CENTER_LEFT);
        aiChatLayer.setStyle("-fx-background-color: transparent;");

        HBox aiChatBubble = new HBox();
        aiChatBubble.setAlignment(Pos.CENTER_LEFT);
        aiChatBubble.setStyle("-fx-background-color: #90EE90; -fx-padding: 12; -fx-background-radius: 30;");
        aiChatBubble.setMaxWidth(500); // Prevents growing too wide on long messages

        Text aiText = new Text();
        aiText.setStyle("-fx-fill: black; -fx-font-size: 14px;");

        TextFlow textFlow = new TextFlow(aiText);
        textFlow.setMaxWidth(500); // Set max width to limit stretching
        textFlow.setPadding(new Insets(0));
        textFlow.setLineSpacing(2);

        aiChatBubble.getChildren().add(textFlow);
        aiChatLayer.getChildren().add(aiChatBubble);

        chatContainer.getChildren().add(aiChatLayer);
        return aiText;
    }

    public void addUserMessage(String message) {
        HBox userChatLayer = new HBox();
        userChatLayer.setAlignment(Pos.CENTER_RIGHT);
        userChatLayer.setSpacing(10); // space between bubble and image
        userChatLayer.setStyle("-fx-background-color: transparent;");

        HBox userChatBubble = new HBox();
        userChatBubble.setAlignment(Pos.CENTER_RIGHT);
        userChatBubble.setStyle("-fx-background-color: #6CA8E6; -fx-padding: 12; -fx-background-radius: 30;");
        userChatBubble.setMaxWidth(500);

        Text userText = new Text(message);
        userText.setStyle("-fx-fill: white; -fx-font-size: 14px;");

        TextFlow textFlow = new TextFlow(userText);
        textFlow.setMaxWidth(500);
        textFlow.setLineSpacing(2);

        userChatBubble.getChildren().add(textFlow);

        // 🔹 Create profile icon
        Image profileImage = new Image(getClass().getResource("/com/example/ap_assignmentuserinterface/images/ProfileAvatar.png").toExternalForm());
        ImageView profileView = new ImageView(profileImage);
        profileView.setFitWidth(32);
        profileView.setFitHeight(32);
        profileView.setSmooth(true);
        profileView.setPreserveRatio(true);

        // 🔹 Add to layer: bubble first, then profile icon
        userChatLayer.getChildren().addAll(userChatBubble, profileView);

        chatContainer.getChildren().add(userChatLayer);
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Invalid Input");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    public void addNotification(String subject, String content, LocalDateTime timestamp, int id) {
        HBox notificationRow = new HBox();
        notificationRow.setAlignment(Pos.TOP_LEFT);
        notificationRow.setPrefWidth(660);
        notificationRow.setMaxWidth(660);
        notificationRow.setPadding(new Insets(15));
        notificationRow.setSpacing(10);
        notificationRow.setStyle("""
        -fx-background-color: radial-gradient(center 50% 50%, radius 80%, #999999, #444444);
        -fx-background-radius: 10;
        -fx-border-radius: 10;
        -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 5, 0.2, 0, 2);
    """);

        VBox contentBox = new VBox(5);
        contentBox.setAlignment(Pos.TOP_LEFT);

        Text subjectText = new Text(subject);
        subjectText.setFont(Font.font("System", FontWeight.BOLD, 16));
        subjectText.setFill(Color.WHITE);

        Text contentText = new Text(content);
        contentText.setFont(Font.font("System", 14));
        contentText.setFill(Color.WHITE);

        TextFlow contentFlow = new TextFlow(contentText);
        contentFlow.setMaxWidth(500);

        Label timeLabel = new Label(timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        timeLabel.setTextFill(Color.WHITE);
        timeLabel.setStyle("-fx-font-size: 12px;");

        contentBox.getChildren().addAll(subjectText, contentFlow, timeLabel);

        // Hover-only Mark as Read button
        Button markAsReadButton = new Button("Mark as Read");
        markAsReadButton.setStyle("""
        -fx-background-color: #6CA8E6;
        -fx-text-fill: white;
        -fx-background-radius: 15;
        -fx-padding: 5 10;
        -fx-font-size: 10px;
    """);
        markAsReadButton.setVisible(false);

        // Add hover effect to show button
        notificationRow.setOnMouseEntered(e -> markAsReadButton.setVisible(true));
        notificationRow.setOnMouseExited(e -> markAsReadButton.setVisible(false));

        // Mark as read and remove
        markAsReadButton.setOnAction(e -> {
            List<Notification> notifications = NotificationDatabase.readAllNotifications(username);
            for (Notification n : notifications) {
                if (n.getId() == id) {
                    n.setRead(true);
                    break;
                }
            }
            NotificationDatabase.updateNotifications(username, notifications);
            notificationContainer.getChildren().remove(notificationRow); // remove from UI
        });

        // Button container at top-right
        VBox buttonBox = new VBox(markAsReadButton);
        buttonBox.setAlignment(Pos.TOP_RIGHT);
        buttonBox.setPrefWidth(120);

        HBox.setHgrow(contentBox, Priority.ALWAYS);
        notificationRow.getChildren().addAll(contentBox, buttonBox);
        notificationContainer.getChildren().add(notificationRow);
    }

    @FXML
    private void handleSendMessage() {
        String userMessage = userInputField.getText().trim();
        if (!userMessage.isEmpty()) {
            String userLabel = userMessage;
            addUserMessage(userLabel);
            ChatHistoryDatabase.saveMessage("user", userMessage);
            userInputField.clear();

            SearchAction action = new SearchAction(userMessage);
            Text aiText = addStreamingAiMessage();

            CustomStreamingResponseHandler handler = new CustomStreamingResponseHandler(action) {
                @Override
                public void onNext(String token) {
                    Platform.runLater(() -> aiText.setText(aiText.getText() + token));
                    Platform.runLater(() -> chatScrollPane.setVvalue(1.0));
                }

                @Override
                public void onComplete(ChatResponse response) {
                    super.onComplete(response);
                    Platform.runLater(() -> {
                        chatScrollPane.setVvalue(1.0);
                        ChatHistoryDatabase.saveMessage("ai", aiText.getText());
                    });
                }

                @Override
                public void onError(Throwable error) {
                    super.onError(error);
                    Platform.runLater(() -> aiText.setText("Error: " + error.getMessage()));
                }
            };

            answerService.getAssistant().chat(userMessage)
                    .onPartialResponse(handler::onNext)
                    .onCompleteResponse(handler::onComplete)
                    .onError(handler::onError)
                    .start();
        }
    }
}

