package com.example.ap_assignmentuserinterface;

import Ai.AnswerService;
import Ai.CustomStreamingResponseHandler;
import Ai.SearchAction;
import Classes.ChatHistoryDatabase;
import Classes.Notification;
import Classes.NotificationDatabase;
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
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.util.Duration;
import Classes.Room;
import Classes.RoomManager;

import java.io.*;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

import Classes.BookingData;
import Classes.RoomOption;
import javafx.scene.control.RadioButton;

import java.io.IOException;
import java.nio.file.Paths;



public class BookingController {
    public Button startBookingButton;
    @FXML
    private Button homeButton;
    @FXML
    private Button bookingButton;
    @FXML
    private Button profileButton;
    @FXML
    private Button notificationButton;
    @FXML
    private Button chatbotButton;
    @FXML
    private AnchorPane notificationPanel;
    @FXML
    private AnchorPane chatbotPanel;
    @FXML
    private Button collapseNotificationButton;
    @FXML
    private Button collapseChatBotButton;
    @FXML
    private VBox chatContainer;
    @FXML
    private TextField userInputField;
    @FXML
    private ScrollPane chatScrollPane;
    @FXML
    private Button sendButton;
    @FXML
    private VBox notificationContainer;
    @FXML
    private ScrollPane notificationScrollPane;
    @FXML
    private AnchorPane bookingStepPane;
    @FXML
    private VBox startPane;
    @FXML
    private DatePicker startDatePicker;
    @FXML
    private DatePicker returnDatePicker;
    @FXML
    private Label errorLabel;
    @FXML
    private AnchorPane roomSelectionPane;
    @FXML
    private Spinner<Integer> roomAmountSpinner;
    @FXML
    private Label roomErrorLabel;
    @FXML
    private RadioButton singleRoomBtn;
    @FXML
    private RadioButton twoPersonBtn;
    @FXML
    private RadioButton threePersonBtn;
    @FXML
    private RadioButton deluxeKingBtn;
    @FXML
    private RadioButton royalEstateBtn;
    @FXML
    private RadioButton divineBloodlineBtn;
    @FXML
    private AnchorPane bookingPackagePane;
    @FXML
    private CheckBox freeCancelCheck, breakfastCheck, standardCheck;
    @FXML
    private Label packageErrorLabel;
    @FXML
    private AnchorPane individualDetailsPane;
    @FXML
    private TextField nameField;
    @FXML
    private TextField icField;
    @FXML
    private TextField phoneField;
    @FXML
    private Label detailsErrorLabel;
    @FXML
    private AnchorPane PaymentMethodPane;
    @FXML
    private AnchorPane bookingConfirmationPane;
    @FXML
    private VBox packageContainer;



    private final ToggleGroup roomToggleGroup = new ToggleGroup();
    private final BookingData bookingData = new BookingData();
    private final Map<RadioButton, RoomOption> roomOptions = new HashMap<>();
    private final AnswerService answerService = new AnswerService();
    private boolean initialized = false;
    private String username;
    private String UserID;
    @FXML
    private Label confirmNameLabel, confirmStartDateLabel, confirmReturnDateLabel;
    @FXML
    private Label confirmRoomTypeLabel, confirmRoomAmountLabel, confirmTravelPackageLabel;
    @FXML
    private Label confirmPhoneLabel, confirmICLabel, confirmPaymentMethodLabel, confirmPaymentAmountLabel;


    public void setUsername(String username) {
        this.username = username;
    }

    public void setUserID(String UserID) {
        this.UserID = UserID;
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


    @FXML
    private void handleStartBooking(ActionEvent event) {
        startPane.setVisible(false); // Hide start screen
        bookingStepPane.setVisible(true); // Show booking step
    }

    @FXML
    private void handleCancel() {
        startDatePicker.setValue(null);
        returnDatePicker.setValue(null);
        bookingStepPane.setVisible(false);
        startPane.setVisible(true);// Go back to Start page
    }

    @FXML
    private void handleBack() {
        startDatePicker.setValue(null);
        returnDatePicker.setValue(null);
        bookingStepPane.setVisible(false);
        startPane.setVisible(true);
    }

    @FXML
    private void handleCancelRoom() {
        startDatePicker.setValue(null);
        returnDatePicker.setValue(null);
        bookingData.setRoomType(null);
        bookingData.setPaymentAmount(0);
        roomSelectionPane.setVisible(false);
        startPane.setVisible(true);// Go back to Start page
    }

    @FXML
    private void handleBackRoom() {
        bookingData.setRoomType(null);
        bookingData.setPaymentAmount(0);
        roomSelectionPane.setVisible(false);
        bookingStepPane.setVisible(true);

    }

    @FXML
    private void handleBackPackage() {
        bookingData.setTravelPackage(null);
        bookingData.setPaymentAmount(0);
        bookingPackagePane.setVisible(false);
        roomSelectionPane.setVisible(true);

    }

    @FXML
    private void handleCancelPackage() {

        bookingPackagePane.setVisible(false);
        startPane.setVisible(true);

    }

    @FXML
    private void handleBackIndividualDetails() {
        bookingData.setName(null);
        bookingData.setIcOrPassport(null);
        bookingData.setPhone_number(null);
        individualDetailsPane.setVisible(false);
        bookingPackagePane.setVisible(true);

    }

    @FXML
    private void handleCancelIndividualDetails() {
        individualDetailsPane.setVisible(false);
        startPane.setVisible(true);
    }

    @FXML
    private void handleBackPaymentMethodDetails() {
        bookingData.setPaymentMethod(null);
        individualDetailsPane.setVisible(true);
        PaymentMethodPane.setVisible(false);

    }

    @FXML
    private void handleCancelPaymentMethodDetails() {
        PaymentMethodPane.setVisible(false);
        startPane.setVisible(true);
    }

    @FXML
    private void handleBackConfirmationPage() {

        PaymentMethodPane.setVisible(true);
        bookingConfirmationPane.setVisible(false);

    }

    @FXML
    private void handleCancelConfirmationPage() {
        bookingConfirmationPane.setVisible(false);
        startPane.setVisible(true);
    }

    // First page booking date
    @FXML
    private void handleNext() {
        UserID = getUserIdByUsername(username);
        bookingData.setUserID(UserID);
        System.out.println("User" + UserID);
        LocalDate today = LocalDate.now();
        LocalDate startDate = startDatePicker.getValue();
        LocalDate returnDate = returnDatePicker.getValue();
        bookingData.setUsername(username);


        if (startDate == null || returnDate == null) {
            errorLabel.setText("Please select both start and return dates.");
            errorLabel.setVisible(true);
            return;
        }

        if (startDate.isBefore(today)) {
            errorLabel.setText("Start date cannot be in the past.");
            errorLabel.setVisible(true);
            return;
        }

        if (returnDate.isBefore(today)) {
            errorLabel.setText("Return date cannot be in the past.");
            errorLabel.setVisible(true);
            return;
        }

        if (returnDate.isBefore(startDate)) {
            errorLabel.setText("Return date cannot be before start date.");
            errorLabel.setVisible(true);
            return;
        }

        if (returnDate.isEqual(startDate)) {
            errorLabel.setText("Return date cannot be the same as starting date!");
            errorLabel.setVisible(true);
            return;
        }

        // If all valid
        errorLabel.setVisible(false);
        bookingData.setStartDate(startDate.toString());
        bookingData.setReturnDate(returnDate.toString());

        System.out.println("Booking Info Saved: " + bookingData);
        bookingStepPane.setVisible(false);
        roomSelectionPane.setVisible(true);
        // Proceed to next step
    }

    @FXML
    private void handleRoomNext() {
        RadioButton selected = (RadioButton) roomToggleGroup.getSelectedToggle();

        if (selected == null) {
            roomErrorLabel.setText("Please select a room type.");
            roomErrorLabel.setVisible(true);
            return;
        }

        RoomOption option = roomOptions.get(selected);
        String roomType = option.getRoomType();

        LocalDate startDate = LocalDate.parse(bookingData.getStartDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        LocalDate returnDate = LocalDate.parse(bookingData.getReturnDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        int requestedRooms = roomAmountSpinner.getValue();

        // ✅ 1. Count total physical rooms of selected type
        long totalRooms = 0;
        try {
            totalRooms = Files.lines(Paths.get("available_rooms.txt"))
                    .filter(line -> {
                        String[] parts = line.split(",", 5);
                        return parts.length >= 2 && parts[1].trim().equalsIgnoreCase(roomType);
                    })
                    .count();
        } catch (IOException e) {
            e.printStackTrace();
            roomErrorLabel.setText("Error reading room availability.");
            roomErrorLabel.setVisible(true);
            return;
        }

        // ✅ 2. Build a map<Date, BookedCount> for overlapping bookings
        Map<LocalDate, Integer> dailyBookingMap = new HashMap<>();
        try {
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
                    String bookedStartStr = "";
                    String bookedEndStr = "";
                    int bookedQty = 1;

                    for (String bLine : block) {
                        if (bLine.startsWith("Room Type:")) {
                            bookedRoomType = bLine.substring(10).trim();
                        } else if (bLine.startsWith("Start Date:")) {
                            bookedStartStr = bLine.substring(11).trim();
                        } else if (bLine.startsWith("Return Date:")) {
                            bookedEndStr = bLine.substring(12).trim();
                        } else if (bLine.startsWith("Number of Rooms:")) {
                            try {
                                bookedQty = Integer.parseInt(bLine.substring(17).trim());
                            } catch (NumberFormatException e) {
                                bookedQty = 1;
                            }
                        }
                    }

                    if (bookedRoomType.equalsIgnoreCase(roomType) && !bookedStartStr.isEmpty() && !bookedEndStr.isEmpty()) {
                        LocalDate bookedStart = LocalDate.parse(bookedStartStr);
                        LocalDate bookedEnd = LocalDate.parse(bookedEndStr);

                        // Calculate overlapping dates
                        LocalDate overlapStart = startDate.isAfter(bookedStart) ? startDate : bookedStart;
                        LocalDate overlapEnd = returnDate.isBefore(bookedEnd) ? returnDate : bookedEnd;

                        while (!overlapStart.isAfter(overlapEnd.minusDays(1))) {
                            dailyBookingMap.put(overlapStart, dailyBookingMap.getOrDefault(overlapStart, 0) + bookedQty);
                            overlapStart = overlapStart.plusDays(1);
                        }
                    }

                    inBlock = false;
                } else if (inBlock) {
                    block.add(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            roomErrorLabel.setText("Error reading existing bookings.");
            roomErrorLabel.setVisible(true);
            return;
        }

        // ✅ 3. Check availability for each day
        for (LocalDate date = startDate; date.isBefore(returnDate); date = date.plusDays(1)) {
            int booked = dailyBookingMap.getOrDefault(date, 0);
            if ((booked + requestedRooms) > totalRooms) {
                roomErrorLabel.setText("Not enough rooms on " + date + ". Available: " + (totalRooms - booked));
                roomErrorLabel.setVisible(true);
                return;
            }
        }

        // ✅ 4. Save data and continue
        bookingData.setRoomType(roomType);
        bookingData.setRoomAmount(requestedRooms);
        bookingData.setTotalNight((int) ChronoUnit.DAYS.between(startDate, returnDate));
        bookingData.setPaymentAmount(option.getPrice() * requestedRooms * bookingData.getTotalNight());

        roomErrorLabel.setVisible(false);
        roomSelectionPane.setVisible(false);
        bookingPackagePane.setVisible(true);
    }



    @FXML
    private void handleStandardCheck() {
        if (standardCheck.isSelected()) {
            freeCancelCheck.setSelected(false);
            breakfastCheck.setSelected(false);
        }
    }

    @FXML
    private void handlePackageNext() {
        StringBuilder selectedPackages = new StringBuilder();
        double additionalCost = 0;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate startDate = LocalDate.parse(bookingData.getStartDate(), formatter);
        LocalDate returnDate = LocalDate.parse(bookingData.getReturnDate(), formatter);
        long daysBetween = ChronoUnit.DAYS.between(startDate, returnDate);

        boolean standardSelected = false;
        int selectedCount = 0;

        for (Map.Entry<String, CheckBox> entry : packageCheckboxes.entrySet()) {
            if (entry.getValue().isSelected()) {
                selectedCount++;
                if (entry.getKey().equalsIgnoreCase("Standard Package")) {
                    standardSelected = true;
                }
            }
        }

        // Restriction: If "Standard Package" is selected, it must be the only selection
        if (standardSelected && selectedCount > 1) {
            packageErrorLabel.setText("Standard Package cannot be combined with other packages.");
            packageErrorLabel.setVisible(true);
            return;
        }

        for (Map.Entry<String, CheckBox> entry : packageCheckboxes.entrySet()) {
            if (entry.getValue().isSelected()) {
                String packageName = entry.getKey();
                if (!selectedPackages.isEmpty()) selectedPackages.append(", ");
                selectedPackages.append(packageName);

                try {
                    List<String> lines = Files.readAllLines(Paths.get("packages.txt"));
                    for (String line : lines) {
                        String[] parts = line.split(",", 3);
                        if (parts.length >= 2 && parts[0].trim().equalsIgnoreCase(packageName)) {
                            String priceStr = parts[1].trim();

                            // Skip non-integer values
                            if (priceStr.contains(".")) continue;

                            int cost = Integer.parseInt(priceStr);

                            if (packageName.equalsIgnoreCase("Complimentary Breakfast")) {
                                additionalCost += (cost * daysBetween * bookingData.getRoomAmount());
                            } else {
                                additionalCost += cost;
                            }
                            break;
                        }
                    }
                } catch (IOException | NumberFormatException e) {
                    e.printStackTrace(); // Optional: show alert
                }
            }
        }

        if (selectedPackages.isEmpty()) {
            packageErrorLabel.setText("Please select at least one package.");
            packageErrorLabel.setVisible(true);
            return;
        }

        bookingData.setTravelPackage(selectedPackages.toString());
        bookingData.setPaymentAmount(bookingData.getPaymentAmount() + additionalCost);
        packageErrorLabel.setVisible(false);

        bookingPackagePane.setVisible(false);
        individualDetailsPane.setVisible(true);
    }


    @FXML
    private void handleIndividualNext() {
        String name = nameField.getText().trim();
        String ic = icField.getText().trim();
        String phone = phoneField.getText().trim();

        if (name.isEmpty() || ic.isEmpty() || phone.isEmpty()) {
            detailsErrorLabel.setVisible(true);
            return;
        }

        detailsErrorLabel.setVisible(false);

        bookingData.setName(name);
        bookingData.setIcOrPassport(ic);
        bookingData.setPhone_number(phone);


        System.out.println("Individual Details Collected: " + bookingData);

        individualDetailsPane.setVisible(false);
        PaymentMethodPane.setVisible(true);
    }

    @FXML
    private void handleOnlineBankingSelected(ActionEvent event) {
        bookingData.setPaymentMethod("Online Banking");
        System.out.println("All : " + bookingData);

        PaymentMethodPane.setVisible(false);
        showConfirmationPane();

    }

    public void showConfirmationPane() {
        confirmNameLabel.setText("Name: " + bookingData.getName());
        confirmStartDateLabel.setText("Start Date: " + bookingData.getStartDate());
        confirmReturnDateLabel.setText("Return Date: " + bookingData.getReturnDate());
        confirmRoomTypeLabel.setText("Room Type: " + bookingData.getRoomType());
        confirmRoomAmountLabel.setText("Number of Rooms: " + bookingData.getRoomAmount());
        confirmTravelPackageLabel.setText("Travel Package: " + bookingData.getTravelPackage());
        confirmPhoneLabel.setText("Phone Number: " + bookingData.getPhone_number());
        confirmICLabel.setText("IC/Passport: " + bookingData.getIcOrPassport());
        confirmPaymentMethodLabel.setText("Payment Method: " + bookingData.getPaymentMethod());
        confirmPaymentAmountLabel.setText("Payment Amount: $" + bookingData.getPaymentAmount());

        // Show confirmation pane
        startPane.setVisible(false);
        bookingStepPane.setVisible(false);
        roomSelectionPane.setVisible(false);
        bookingPackagePane.setVisible(false);
        individualDetailsPane.setVisible(false);
        PaymentMethodPane.setVisible(false);
        bookingConfirmationPane.setVisible(true);
    }

    public void handleConfirmBooking() {

        try {
            File file = new File("BookingInfo.txt");
            FileWriter writer = new FileWriter(file, true);

            writer.write("UserID: " + bookingData.getUserID() + "\n");
            writer.write("Username: " + bookingData.getUsername() + "\n");
            writer.write("Name: " + bookingData.getName() + "\n");
            writer.write("IC/Passport: " + bookingData.getIcOrPassport() + "\n");
            writer.write("Phone Number: " + bookingData.getPhone_number() + "\n");
            writer.write("Start Date: " + bookingData.getStartDate() + "\n");
            writer.write("Return Date: " + bookingData.getReturnDate() + "\n");
            writer.write("Total Night: " + bookingData.getTotalNight() + "\n");
            writer.write("Room Type: " + bookingData.getRoomType() + "\n");
            writer.write("Number of Rooms: " + bookingData.getRoomAmount() + "\n");
            writer.write("Travel Package: " + bookingData.getTravelPackage() + "\n");
            writer.write("Payment Method: " + bookingData.getPaymentMethod() + "\n");
            writer.write("Payment Amount: $" + bookingData.getPaymentAmount() + "\n");
            writer.write("--------------------------------------------------\n");
            NotificationDatabase.writeNotification(username, "Booking Comfirmed !", "Start date:" + bookingData.getStartDate() + " | " + bookingData.getTotalNight() + "Nights | " + bookingData.getRoomAmount() + " " + bookingData.getRoomType());
            loadNotifications();
            writer.close();

            // Return to startPane
            bookingConfirmationPane.setVisible(false);
            startPane.setVisible(true);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public void loadNotifications() {
        notificationContainer.getChildren().clear(); // Clear previous ones to avoid duplicates
        List<Notification> unread = NotificationDatabase.readUnreadNotifications(username);
        Collections.reverse(unread);
        for (Notification n : unread) {
            addNotification(n.getSubject(), n.getContent(), n.getTimestamp(), n.getId());
        }
    }


    @FXML
    private void goProfile(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("profile-page.fxml"));
        Parent root = loader.load();
        ProfileController controller = loader.getController();
        controller.setUsername(username);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    private void applyEffects(Button button) {
        applyClickScaleEffect(button);
        applyWaterHoverEffect(button);
    }

    @FXML
    private Button nextButton;
    @FXML
    private Button overlayCancel;
    @FXML
    private Button overlayBack;
    @FXML
    private Button overlayCancel2;
    @FXML
    private Button overlayBack2;
    @FXML
    private Button nextButton2;

    @FXML
    private Button overlayCancel3;
    @FXML
    private Button overlayBack3;
    @FXML
    private Button nextButton3;

    @FXML
    private Button overlayCancel4;
    @FXML
    private Button overlayBack4;
    @FXML
    private Button nextButton4;

    @FXML
    private Button overlayCancel5;
    @FXML
    private Button overlayBack5;

    @FXML
    private Button overlayCancel6;
    @FXML
    private Button overlayBack6;
    @FXML
    private Button confirmButton; // For "Confirm Booking"
    @FXML
    private Button onlineBankingButton;
    @FXML
    private VBox roomOptionsContainer;

    private Map<String, CheckBox> packageCheckboxes = new HashMap<>();

    public void initialize() {
        if (!initialized) {
            SearchAction initAction = new SearchAction("Initializing chatbot...");
            answerService.init(initAction);
            initialized = true;
        }

        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10, 1);
        roomAmountSpinner.setValueFactory(valueFactory);
        notificationPanel.setTranslateY(-650);
        chatbotPanel.setTranslateX(850);
        applyEffects(overlayCancel2);
        applyEffects(overlayBack2);
        applyEffects(nextButton2);
        applyEffects(onlineBankingButton);
        applyEffects(overlayCancel3);
        applyEffects(overlayBack3);
        applyEffects(nextButton3);
        applyEffects(overlayCancel4);
        applyEffects(overlayBack4);
        applyEffects(nextButton4);
        applyEffects(overlayCancel5);
        applyEffects(overlayBack5);
        applyEffects(overlayCancel6);
        applyEffects(overlayBack6);
        applyEffects(confirmButton);
        applyEffects(overlayCancel);
        applyEffects(overlayBack);
        applyEffects(nextButton);
        applyEffects(startBookingButton);
        applyEffects(homeButton);
        applyEffects(profileButton);
        applyEffects(notificationButton);
        applyEffects(chatbotButton);
        applyEffects(collapseNotificationButton);
        applyEffects(collapseChatBotButton);
        applyEffects(sendButton);
        loadAvailableRooms();

        try {
            List<String> lines = Files.readAllLines(Paths.get("packages.txt"));
            for (String line : lines) {
                String[] parts = line.split(",", 3);
                if (parts.length < 2) continue;

                String name = parts[0].trim();
                String priceText = parts[1].trim();
                double price = Double.parseDouble(priceText);

                CheckBox cb = new CheckBox(name + " $" + priceText);
                cb.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
                packageContainer.getChildren().add(cb);
                packageCheckboxes.put(name, cb);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Platform.runLater(() -> {
            List<Notification> unread = NotificationDatabase.readUnreadNotifications(username);
            Collections.reverse(unread);
            for (Notification n : unread) {
                addNotification(n.getSubject(), n.getContent(), n.getTimestamp(), n.getId());
            }
        });

        Platform.runLater(() -> {
            chatScrollPane.lookupAll(".scroll-bar").forEach(node -> {
                if (node instanceof ScrollBar scrollBar && scrollBar.getOrientation() == javafx.geometry.Orientation.VERTICAL) {
                    scrollBar.setTranslateX(20);
                }
            });
            for (Node node : chatScrollPane.lookupAll(".scroll-bar")) {
                if (node instanceof ScrollBar scrollBar && scrollBar.getOrientation() == Orientation.VERTICAL) {
                    scrollBar.setStyle("-fx-background-color: transparent;");
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
                if (node instanceof ScrollBar scrollBar && scrollBar.getOrientation() == javafx.geometry.Orientation.VERTICAL) {
                    scrollBar.setTranslateX(20);
                }
            });
            for (Node node : notificationScrollPane.lookupAll(".scroll-bar")) {
                if (node instanceof ScrollBar scrollBar && scrollBar.getOrientation() == Orientation.VERTICAL) {
                    scrollBar.setStyle("-fx-background-color: transparent;");
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
    }

    @FXML
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
            ChatHistoryDatabase.saveMessage("user", userMessage); // ✅ Save user message
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
                        ChatHistoryDatabase.saveMessage("ai", aiText.getText()); // ✅ Save AI response
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

    public class RoomOption {
        private String roomType;
        private double price;

        public RoomOption(String roomType, double price) {
            this.roomType = roomType;
            this.price = price;
        }

        public String getRoomType() {
            return roomType;
        }

        public double getPrice() {
            return price;
        }
    }

    private void loadAvailableRooms() {
        Map<String, Integer> priceMap = new HashMap<>();
        Set<String> uniqueRoomTypes = new HashSet<>();

        try {
            List<String> lines = Files.readAllLines(Paths.get("available_rooms.txt"));
            for (String line : lines) {
                String[] parts = line.split(",");
                if (parts.length >= 5) {  // ✅ Now we need at least 5 parts (index 0-4)
                    String roomType = parts[1].trim();
                    String status = parts[2].trim();
                    String priceStr = parts[4].trim();

                    if (status.equalsIgnoreCase("Available")) {
                        uniqueRoomTypes.add(roomType);

                        // ✅ Add price only if not already present
                        if (!priceMap.containsKey(roomType)) {
                            try {
                                int price = Integer.parseInt(priceStr);
                                priceMap.put(roomType, price);
                            } catch (NumberFormatException e) {
                                priceMap.put(roomType, 100);  // Default if error
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        roomOptionsContainer.getChildren().clear();
        roomOptions.clear();
        roomToggleGroup.getToggles().clear();

        for (String type : uniqueRoomTypes) {
            int price = priceMap.getOrDefault(type, 100);

            RadioButton rb = new RadioButton(type + " - RM" + price);
            rb.setStyle("-fx-font-size: 16px;");
            rb.setToggleGroup(roomToggleGroup);

            roomOptions.put(rb, new RoomOption(type, price));
            roomOptionsContainer.getChildren().add(rb);
        }
    }

}
