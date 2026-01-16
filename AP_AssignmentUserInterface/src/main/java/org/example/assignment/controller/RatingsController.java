package org.example.assignment.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.assignment.FileManager;

import java.util.ArrayList;
import java.util.List;

public class RatingsController {

    @FXML private ListView<VBox> ratingsListView;
    @FXML private Button backButton;

    private final List<Integer> ratingIndices = new ArrayList<>();

    @FXML
    public void initialize() {
        loadRatings();

        backButton.setOnAction(e -> {
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.close();
        });
    }

    private void loadRatings() {
        ratingsListView.getItems().clear();
        ratingIndices.clear();
        List<String> ratingLines = new ArrayList<>(FileManager.readLines("rating.txt"));

        String username = "";
        String stars = "";
        String feedback = "";
        String date = "";

        for (int i = 0; i < ratingLines.size(); i++) {
            String line = ratingLines.get(i).trim();
            if (line.startsWith("Username:")) {
                username = line.substring(9).trim();
            } else if (line.startsWith("Rating:")) {
                stars = "★".repeat(Integer.parseInt(line.substring(7).trim()));
            } else if (line.startsWith("Feedback:")) {
                feedback = line.substring(9).trim();
            } else if (line.startsWith("Date:")) {
                date = line.substring(5).trim();
            } else if (line.equals("---")) {
                String display = username + " " + stars + "\n" +
                        "Feedback: " + (feedback.isEmpty() ? "No feedback" : feedback) + "\n" +
                        "Date: " + date;
                VBox box = new VBox(new Label(display));
                ratingsListView.getItems().add(box);
                ratingIndices.add(i - 4);  // index of Username line
                username = stars = feedback = date = "";
            }
        }
    }
}

