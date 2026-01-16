package org.example.assignment.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.example.assignment.FileManager;
import org.example.assignment.DashboardRoomItem;
import org.example.assignment.UserItem;

import java.time.LocalDate;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class AdminDashboardController {

    @FXML
    private Button viewProfileButton;
    @FXML
    private ListView<DashboardRoomItem> availableRoomsList;
    @FXML
    private ListView<DashboardRoomItem> bookedRoomsList;
    @FXML
    private ListView<String> ratingsList;
    @FXML
    private ListView<UserItem> enquiriesList;
    @FXML
    private ListView<UserItem> modificationRequestList;
    @FXML
    private ListView<String> faqList;
    @FXML
    private Button logoutButton;
    @FXML
    private Button viewAllRoomsButton;
    @FXML
    private Button viewRatingsButton;
    @FXML
    private Button viewBookingsButton;
    @FXML
    private Button viewEnquiriesButton;
    @FXML
    private Button manageFaqButton;
    @FXML
    private Button viewModRequestsButton;
    @FXML
    private Button manageRoomsButton;

    private final Map<String, String> roomTypeImages = Map.of(
            "Single Room", "/org/example/assignment/single_room.png",
            "3 Person Room", "/org/example/assignment/three_person_room.png",
            "2 Person Room", "/org/example/assignment/two_person_room.png",
            "Deluxe King", "/org/example/assignment/deluxe_king.png",
            "Royal Estate", "/org/example/assignment/royale_estate.png",
            "Divine Bloodline", "/org/example/assignment/divide_bloodline.png"
    );

    @FXML
    private Label welcomeLabel;
    private String adminUsername;

    public void setAdminUsername(String username) {
        this.adminUsername = username;
        if (welcomeLabel != null) {
            welcomeLabel.setText("Welcome, " + username + "!");
        }
    }

    @FXML
    public void initialize() {
        viewAllRoomsButton.setOnAction(e -> openPage("AvailableRooms.fxml"));
        viewRatingsButton.setOnAction(e -> openPage("Ratings.fxml"));
        viewBookingsButton.setOnAction(e -> openPage("Bookings.fxml"));
        viewEnquiriesButton.setOnAction(e -> openPage("Enquiries.fxml"));
        manageFaqButton.setOnAction(e -> openPage("FaqManager.fxml"));
        viewModRequestsButton.setOnAction(e -> openPage("BookingModRequests.fxml"));
        manageRoomsButton.setOnAction(e -> openPage("Room.fxml"));
        viewProfileButton.setOnAction(e -> openPage("AdminViewProfile.fxml"));

        availableRoomsList.getItems().clear();
        bookedRoomsList.getItems().clear();
        ratingsList.getItems().clear();
        enquiriesList.getItems().clear();
        modificationRequestList.getItems().clear();
        faqList.getItems().clear();

        setupRoomCellFactory(availableRoomsList);
        setupRoomCellFactory(bookedRoomsList);
        setupUserCellFactory(enquiriesList);
        setupUserCellFactory(modificationRequestList);
        setupFaqCellFactory(faqList);
        setupRatingsCellFactory(ratingsList);

        loadFaqs();
        loadRatings();
        handleRefresh();

        LocalDate today = LocalDate.now();

        List<String> allRooms = FileManager.readLines("available_rooms.txt");

        List<String> bookingLines = FileManager.readLines("BookingInfo.txt");

        List<String> bookedToday = new ArrayList<>();
        String currentUserId = "";
        String currentStartDate = "", currentReturnDate = "", currentRoomType = "";

        for (String line : bookingLines) {
            line = line.trim();

            if (line.startsWith("UserID:")) {
                currentUserId = line.substring(7).trim();
            } else if (line.startsWith("Start Date:")) {
                currentStartDate = line.substring(11).trim();
            } else if (line.startsWith("Return Date:")) {
                currentReturnDate = line.substring(12).trim();
            } else if (line.startsWith("Room Type:")) {
                currentRoomType = line.substring(10).trim();
            } else if (line.startsWith("--------------------------------------------------")) {
                try {
                    LocalDate start = LocalDate.parse(currentStartDate);

                    // ✅ Only include if UserID == "3" AND start date is today or in the future
                    if (currentUserId.equals("3") && !start.isBefore(today)) {
                        bookedToday.add(currentRoomType);  // Or add room number if you have it
                    }
                } catch (Exception ignored) {
                }

                // Reset for next block
                currentUserId = "";
                currentStartDate = "";
                currentReturnDate = "";
                currentRoomType = "";
            }
        }


        List<String> availableToday = allRooms.stream()
                .filter(line -> {
                    String[] parts = line.split(",");
                    return parts.length >= 5 && !bookedToday.contains(parts[1].trim());
                })
                .limit(5)
                .toList();


        for (String line : availableToday) {
            String[] parts = line.split(",");
            if (parts.length >= 5) {
                String roomNumber = parts[0].trim();
                String roomType = parts[1].trim();
                String status = parts[2].trim();
                String imagePath = parts[3].trim();
                String price = parts[4].trim();  // ✅ new

                String displayText = roomType + " (Room " + roomNumber + ") - RM" + price;

                String finalImagePath = roomTypeImages.getOrDefault(roomType, "/org/example/assignment/default_room.png");

                availableRoomsList.getItems().add(new DashboardRoomItem(
                        roomNumber,
                        displayText,
                        "Available Today",
                        finalImagePath
                ));
            }
        }


        List<String> faqs = FileManager.readLines("faq.txt");
        String question = "";
        String answer = "";

        for (String line : faqs) {
            if (line.startsWith("Q:")) {
                question = line.substring(2).trim();
            } else if (line.startsWith("A:")) {
                answer = line.substring(2).trim();
            } else if (line.startsWith("---")) {
                if (!question.isEmpty()) {
                    String displayText = "Q: " + question + "\nA: " + (answer.isEmpty() ? "No answer provided yet." : answer);
                    faqList.getItems().add(displayText);
                }
                question = "";
                answer = "";
            }
        }

        List<String> bookings = FileManager.readLines("BookingInfo.txt");

        String bookingUserId = "", bookingUsername = "", bookingName = "", bookingIC = "", bookingPhone = "", bookingStartDate = "", bookingReturnDate = "", bookingRoomType = "", bookingPackage = "";

        for (String line : bookings) {
            line = line.trim();
            if (line.startsWith("UserID:")) bookingUserId = line.substring(7).trim();
            else if (line.startsWith("Username:")) bookingUsername = line.substring(9).trim();
            else if (line.startsWith("Name:")) bookingName = line.substring(5).trim();
            else if (line.startsWith("IC/Passport:")) bookingIC = line.substring(12).trim();
            else if (line.startsWith("Phone Number:")) bookingPhone = line.substring(13).trim();
            else if (line.startsWith("Start Date:")) bookingStartDate = line.substring(11).trim();
            else if (line.startsWith("Return Date:")) bookingReturnDate = line.substring(12).trim();
            else if (line.startsWith("Room Type:")) bookingRoomType = line.substring(10).trim();
            else if (line.startsWith("Travel Package:")) bookingPackage = line.substring(15).trim();
            else if (line.startsWith("---")) {

                // 🔑 Add this check before displaying:
                try {
                    LocalDate startDate = LocalDate.parse(bookingStartDate);
                    if (startDate.isBefore(LocalDate.now())) {
                        // Skip past bookings
                        bookingUserId = bookingUsername = bookingName = bookingIC = bookingPhone = bookingStartDate = bookingReturnDate = bookingRoomType = bookingPackage = "";
                        continue;
                    }
                } catch (Exception e) {
                    // Skip invalid dates
                    bookingUserId = bookingUsername = bookingName = bookingIC = bookingPhone = bookingStartDate = bookingReturnDate = bookingRoomType = bookingPackage = "";
                    continue;
                }

                String displayText = bookingRoomType + " (" + bookingPackage + ", Booked by " + bookingName + ") [" + bookingStartDate + " to " + bookingReturnDate + "]";
                String imageFile = roomTypeImages.getOrDefault(bookingRoomType, "/org/example/assignment/default_room.png");

                bookedRoomsList.getItems().add(new DashboardRoomItem(bookingUserId, displayText, "Booked", imageFile));

                bookingUserId = bookingUsername = bookingName = bookingIC = bookingPhone = bookingStartDate = bookingReturnDate = bookingRoomType = bookingPackage = "";
            }
        }


        String currentQuestion = "";
        String currentAnswer = "";

        for (String line : faqs) {
            line = line.trim();
            if (line.startsWith("Q:")) {
                currentQuestion = line.substring(2).trim();
            } else if (line.startsWith("A:")) {
                currentAnswer = line.substring(2).trim();
            } else if (line.equals("---")) {
                if (!currentQuestion.isEmpty() && currentAnswer.isEmpty()) {
                    String profile = "/org/example/assignment/default_user.png";
                    enquiriesList.getItems().add(new UserItem("Q: " + currentQuestion + "\nA: (No answer yet)", profile));
                }
                currentQuestion = "";
                currentAnswer = "";
            }
        }

        List<String> mods = FileManager.readLines("modification_request.txt");

        String userId = "";
        String newName = "";
        String newStartDate = "";
        String newReturnDate = "";

        for (String line : mods) {
            line = line.trim();
            if (line.startsWith("UserID:")) {
                userId = line.substring(7).trim();
            } else if (line.startsWith("New Name:")) {
                newName = line.substring(9).trim();
            } else if (line.startsWith("New Start Date:")) {
                newStartDate = line.substring(15).trim();
            } else if (line.startsWith("New Return Date:")) {
                newReturnDate = line.substring(16).trim();
            } else if (line.equals("--------------------------------------------------")) {
                if (!userId.isEmpty()) {
                    String displayText = newName + " requested: " + newStartDate + " to " + newReturnDate;
                    String profile = "/org/example/assignment/default_user.png";
                    modificationRequestList.getItems().add(new UserItem(displayText, profile));
                }
                userId = "";
                newName = "";
                newStartDate = "";
                newReturnDate = "";
            }
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

    private String getProfilePath(String username) {
        return "/org/example/assignment" + username.toLowerCase() + ".png";
    }

    private void loadRatings() {
        ratingsList.getItems().clear();
        List<String> lines = FileManager.readLines("rating.txt");

        String username = "";
        String stars = "";
        String feedback = "";
        String date = "";

        for (String line : lines) {
            line = line.trim();
            if (line.startsWith("Username:")) {
                username = line.substring(9).trim();
            } else if (line.startsWith("Rating:")) {
                try {
                    int ratingValue = Integer.parseInt(line.substring(7).trim());
                    stars = "★".repeat(Math.max(0, ratingValue));
                } catch (NumberFormatException e) {
                    stars = "";
                }
            } else if (line.startsWith("Feedback:")) {
                feedback = line.substring(9).trim();
            } else if (line.startsWith("Date:")) {
                date = line.substring(5).trim();
            } else if (line.equals("---")) {
                if (!username.isEmpty() && !stars.isEmpty()) {
                    String display = username + " " + stars + "\n" +
                            (feedback.isEmpty() ? "No feedback" : feedback) + "\n" +
                            "📅 " + date;

                    ratingsList.getItems().add(display);
                }
                username = "";
                stars = "";
                feedback = "";
                date = "";
            }
        }
    }

    private void loadFaqs() {
        faqList.getItems().clear();
        List<String> faqs = FileManager.readLines("faq.txt");

        String question = "";
        String answer = "";

        for (String line : faqs) {
            line = line.trim();
            if (line.startsWith("Q:")) {
                question = line.substring(2).trim();
            } else if (line.startsWith("A:")) {
                answer = line.substring(2).trim();
            } else if (line.equals("---")) {
                if (!question.isEmpty()) {
                    String displayText = "Q: " + question + "\nA: " + (answer.isEmpty() ? "No answer provided yet." : answer);
                    faqList.getItems().add(displayText);
                }
                question = "";
                answer = "";
            }
        }
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

    private void setupRatingsCellFactory(ListView<String> listView) {
        listView.setCellFactory(param -> new ListCell<>() {
            private final Label label = new Label();

            {
                label.setWrapText(true);
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    label.setText(item);
                    setGraphic(label);
                }
            }
        });
    }

    private void setupFaqCellFactory(ListView<String> listView) {
        listView.setCellFactory(param -> new ListCell<>() {
            private final Label label = new Label();

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    label.setText(item);
                    setGraphic(label);
                }
            }
        });
    }

    private void setupRoomCellFactory(ListView<DashboardRoomItem> listView) {
        listView.setCellFactory(param -> new ListCell<>() {
            private final ImageView imageView = new ImageView();
            private final Label label = new Label();
            private final HBox container = new HBox(10, imageView, label);

            {
                imageView.setFitWidth(40);
                imageView.setFitHeight(40);
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
                    } else {
                        imageView.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/org/example/assignment/default_room.png"))));
                    }
                    label.setText(item.getAll());
                    setGraphic(container);
                }
            }
        });
    }

    @FXML
    private void handleManageRooms() {
        openPage("Room.fxml");
    }

    @FXML
    private void handleViewProfile() {
        openPage("AdminViewProfile.fxml");
    }

    private void setupUserCellFactory(ListView<UserItem> listView) {
        listView.setCellFactory(param -> new ListCell<>() {
            private final ImageView imageView = new ImageView();
            private final Label label = new Label();
            private final HBox container = new HBox(10, imageView, label);

            {
                imageView.setFitWidth(40);
                imageView.setFitHeight(40);
            }

            @Override
            protected void updateItem(UserItem item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    InputStream imgStream = getClass().getResourceAsStream(item.getProfileImage());
                    if (imgStream != null) {
                        imageView.setImage(new Image(imgStream));
                    } else {
                        imageView.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/org/example/assignment/default_user.png"))));
                    }
                    label.setText(item.getText());
                    setGraphic(container);
                }
            }
        });
    }

    @FXML
    private void handleRefresh() {
        availableRoomsList.getItems().clear();
        bookedRoomsList.getItems().clear();
        ratingsList.getItems().clear();
        enquiriesList.getItems().clear();
        modificationRequestList.getItems().clear();
        faqList.getItems().clear();

        loadAvailableRooms();
        loadBookings();
        loadRatings();
        loadFaqs();
        loadEnquiries();
        loadModRequests();
    }
    private void loadAvailableRooms() {
        LocalDate today = LocalDate.now();
        List<String> allRooms = FileManager.readLines("available_rooms.txt");
        List<String> bookingLines = FileManager.readLines("BookingInfo.txt");

        List<String> bookedToday = new ArrayList<>();
        String currentUserId = "";
        String currentStartDate = "", currentReturnDate = "", currentRoomType = "";

        for (String line : bookingLines) {
            line = line.trim();
            if (line.startsWith("UserID:")) currentUserId = line.substring(7).trim();
            else if (line.startsWith("Start Date:")) currentStartDate = line.substring(11).trim();
            else if (line.startsWith("Return Date:")) currentReturnDate = line.substring(12).trim();
            else if (line.startsWith("Room Type:")) currentRoomType = line.substring(10).trim();
            else if (line.startsWith("--------------------------------------------------")) {
                try {
                    LocalDate start = LocalDate.parse(currentStartDate);
                    if (currentUserId.equals("3") && !start.isBefore(today)) {
                        bookedToday.add(currentRoomType);
                    }
                } catch (Exception ignored) {}

                currentUserId = "";
                currentStartDate = "";
                currentReturnDate = "";
                currentRoomType = "";
            }
        }

        List<String> availableToday = allRooms.stream()
                .filter(line -> {
                    String[] parts = line.split(",");
                    return parts.length >= 5 && !bookedToday.contains(parts[1].trim());
                })
                .limit(5)
                .toList();

        for (String line : availableToday) {
            String[] parts = line.split(",");
            if (parts.length >= 5) {
                String roomNumber = parts[0].trim();
                String roomType = parts[1].trim();
                String price = parts[4].trim();
                String displayText = roomType + " (Room " + roomNumber + ") - RM" + price;
                String finalImagePath = roomTypeImages.getOrDefault(roomType, "/org/example/assignment/default_room.png");

                availableRoomsList.getItems().add(new DashboardRoomItem(
                        roomNumber,
                        displayText,
                        "Available Today",
                        finalImagePath
                ));
            }
        }
    }
    private void loadBookings() {
        List<String> bookings = FileManager.readLines("BookingInfo.txt");

        String userId = "", username = "", name = "", ic = "", phone = "", startDate = "", returnDate = "", roomType = "", travelPackage = "";

        for (String line : bookings) {
            line = line.trim();
            if (line.startsWith("UserID:")) userId = line.substring(7).trim();
            else if (line.startsWith("Username:")) username = line.substring(9).trim();
            else if (line.startsWith("Name:")) name = line.substring(5).trim();
            else if (line.startsWith("IC/Passport:")) ic = line.substring(12).trim();
            else if (line.startsWith("Phone Number:")) phone = line.substring(13).trim();
            else if (line.startsWith("Start Date:")) startDate = line.substring(11).trim();
            else if (line.startsWith("Return Date:")) returnDate = line.substring(12).trim();
            else if (line.startsWith("Room Type:")) roomType = line.substring(10).trim();
            else if (line.startsWith("Travel Package:")) travelPackage = line.substring(15).trim();
            else if (line.startsWith("---")) {
                try {
                    LocalDate start = LocalDate.parse(startDate);
                    if (start.isBefore(LocalDate.now())) {
                        userId = username = name = ic = phone = startDate = returnDate = roomType = travelPackage = "";
                        continue;
                    }
                } catch (Exception e) {
                    userId = username = name = ic = phone = startDate = returnDate = roomType = travelPackage = "";
                    continue;
                }

                String displayText = roomType + " (" + travelPackage + ", Booked by " + name + ") [" + startDate + " to " + returnDate + "]";
                String imageFile = roomTypeImages.getOrDefault(roomType, "/org/example/assignment/default_room.png");

                bookedRoomsList.getItems().add(new DashboardRoomItem(userId, displayText, "Booked", imageFile));
                userId = username = name = ic = phone = startDate = returnDate = roomType = travelPackage = "";
            }
        }
    }
    private void loadEnquiries() {
        enquiriesList.getItems().clear();
        List<String> faqs = FileManager.readLines("faq.txt");

        String currentQuestion = "";
        String currentAnswer = "";

        for (String line : faqs) {
            line = line.trim();
            if (line.startsWith("Q:")) {
                currentQuestion = line.substring(2).trim();
            } else if (line.startsWith("A:")) {
                currentAnswer = line.substring(2).trim();
            } else if (line.equals("---")) {
                if (!currentQuestion.isEmpty() && currentAnswer.isEmpty()) {
                    String profile = "/org/example/assignment/default_user.png";
                    enquiriesList.getItems().add(new UserItem("Q: " + currentQuestion + "\nA: (No answer yet)", profile));
                }
                currentQuestion = "";
                currentAnswer = "";
            }
        }
    }
    private void loadModRequests() {
        modificationRequestList.getItems().clear();
        List<String> mods = FileManager.readLines("modification_request.txt");

        String userId = "";
        String newName = "";
        String newStartDate = "";
        String newReturnDate = "";

        for (String line : mods) {
            line = line.trim();
            if (line.startsWith("UserID:")) {
                userId = line.substring(7).trim();
            } else if (line.startsWith("New Name:")) {
                newName = line.substring(9).trim();
            } else if (line.startsWith("New Start Date:")) {
                newStartDate = line.substring(15).trim();
            } else if (line.startsWith("New Return Date:")) {
                newReturnDate = line.substring(16).trim();
            } else if (line.equals("--------------------------------------------------")) {
                if (!userId.isEmpty()) {
                    String displayText = newName + " requested: " + newStartDate + " to " + newReturnDate;
                    String profile = "/org/example/assignment/default_user.png";
                    modificationRequestList.getItems().add(new UserItem(displayText, profile));
                }
                userId = "";
                newName = "";
                newStartDate = "";
                newReturnDate = "";
            }
        }
    }


}
