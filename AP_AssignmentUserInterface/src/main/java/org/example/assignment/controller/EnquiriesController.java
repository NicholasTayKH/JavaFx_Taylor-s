package org.example.assignment.controller;

import Classes.NotificationDatabase;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.example.assignment.FileManager;
import org.example.assignment.UserItem;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EnquiriesController {

    @FXML private ListView<UserItem> enquiriesListView;
    @FXML private TextArea replyTextArea;
    @FXML private Button sendReplyButton;
    @FXML private Label feedbackLabel;
    @FXML private Button backButton;

    private String selectedQuestion = "";
    private String selectedUsername = "";
    @FXML
    public void initialize() {
        loadUnansweredFaqs();
        setupCellFactory();

        enquiriesListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                String fullText = newVal.getText();
                int qIndex = fullText.indexOf("Q:");
                int aIndex = fullText.indexOf("\nA:");

                if (qIndex != -1 && aIndex != -1) {
                    selectedQuestion = fullText.substring(qIndex + 2, aIndex).trim();
                }

                selectedUsername = newVal.getUsername();
            }
        });

        sendReplyButton.setOnAction(e -> handleSendReply());
    }

    private void loadUnansweredFaqs() {
        enquiriesListView.getItems().clear();
        List<String> faqs = FileManager.readLines("faq.txt");

        String question = "";
        String answer = "";

        // ❗ 假設你把用戶名存在 Q: 前面 例： [1234] Q: xxx
        for (String line : faqs) {
            line = line.trim();
            if (line.startsWith("Q:") || line.startsWith("[") ) {
                question = line.substring(line.indexOf("Q:") + 2).trim();
            } else if (line.startsWith("A:")) {
                answer = line.substring(2).trim();
            } else if (line.equals("---")) {
                if (!question.isEmpty() && answer.isEmpty()) {
                    // ❗ 假設問題格式是：[1234] Q: xxx
                    String username = "1234";  // 預設
                    if (line.contains("[")) {
                        int start = line.indexOf("[") + 1;
                        int end = line.indexOf("]");
                        if (start >= 0 && end >= 0 && end > start) {
                            username = line.substring(start, end);
                        }
                    }
                    String displayText = "Q: " + question + "\nA: (No answer yet)";
                    enquiriesListView.getItems().add(new UserItem(displayText, "/org/example/assignment/default_user.png", username));
                }
                question = "";
                answer = "";
            }
        }
    }

    private void setupCellFactory() {
        enquiriesListView.setCellFactory(list -> new ListCell<>() {
            private final ImageView imageView = new ImageView();
            private final Label label = new Label();
            private final HBox container = new HBox(10, imageView, label);

            {
                imageView.setFitWidth(60);
                imageView.setFitHeight(60);
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

    private void handleSendReply() {
        String reply = replyTextArea.getText().trim();

        if (selectedQuestion.isEmpty()) {
            feedbackLabel.setText("❗ Please select a question first.");
            return;
        }

        if (reply.isEmpty()) {
            feedbackLabel.setText("❗ Reply cannot be empty.");
            return;
        }


        saveToFAQ(selectedQuestion, reply);


        List<String> allUsers = FileManager.readLines("userDetails.txt");
        for (String line : allUsers) {
            String[] parts = line.split(",");
            if (parts.length >= 2) {
                String username = parts[1].trim();
                if (!username.isEmpty()) {

                    NotificationDatabase.writeNotification(username, "FAQ Updated",
                            "A new FAQ has been answered. Please check the FAQ section.");
                }
            }
        }

        feedbackLabel.setText("✔ Reply saved successfully.");
        replyTextArea.clear();
        selectedQuestion = "";
        loadUnansweredFaqs();
    }




    private void saveToFAQ(String question, String answer) {
        List<String> existingFaqs = FileManager.readLines("faq.txt");
        List<String> updatedFaqs = new ArrayList<>();

        String currentQ = "";
        String currentA = "";

        for (String line : existingFaqs) {
            if (line.startsWith("Q:")) {
                currentQ = line;
            } else if (line.startsWith("A:")) {
                currentA = line;
            } else if (line.equals("---")) {
                if (currentQ.equals("Q: " + question)) {
                    updatedFaqs.add(currentQ);
                    updatedFaqs.add("A: " + answer);
                    updatedFaqs.add("---");
                } else {
                    updatedFaqs.add(currentQ);
                    updatedFaqs.add(currentA);
                    updatedFaqs.add("---");
                }
                currentQ = "";
                currentA = "";
            }
        }

        FileManager.writeLines("faq.txt", updatedFaqs);
    }

    public void handleBackButton() {
        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.close();
    }
}
