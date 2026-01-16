package org.example.assignment.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import org.example.assignment.FileManager;

import java.util.ArrayList;
import java.util.List;

public class FaqManagerController {

    @FXML private ListView<VBox> faqListView;
    @FXML private TextField faqQuestionField;
    @FXML private TextField faqAnswerField;
    @FXML private Button addButton, updateButton, deleteButton, backButton, clearSelectionButton;

    private List<String> faqLines = new ArrayList<>();
    private List<Integer> faqIndices = new ArrayList<>();  // ✅ Add this to map selection to file lines

    @FXML
    public void initialize() {
        loadFaqs();

        addButton.setOnAction(e -> {
            String q = faqQuestionField.getText().trim();
            String a = faqAnswerField.getText().trim();
            if (q.isEmpty() || a.isEmpty()) return;

            faqLines.add("Q: " + q);
            faqLines.add("A: " + a);
            faqLines.add("---");

            saveFaqs();
            loadFaqs();
            clearFields();
        });

        updateButton.setOnAction(e -> {
            int selectedIndex = faqListView.getSelectionModel().getSelectedIndex();
            if (selectedIndex == -1) return;

            String q = faqQuestionField.getText().trim();
            String a = faqAnswerField.getText().trim();
            if (q.isEmpty() || a.isEmpty()) return;

            int lineIndex = faqIndices.get(selectedIndex);
            faqLines.set(lineIndex, "Q: " + q);
            faqLines.set(lineIndex + 1, "A: " + a);

            saveFaqs();
            loadFaqs();
            clearFields();
        });

        deleteButton.setOnAction(e -> {
            int selectedIndex = faqListView.getSelectionModel().getSelectedIndex();
            if (selectedIndex == -1) return;

            int lineIndex = faqIndices.get(selectedIndex);
            // Remove Q, A, ---
            faqLines.remove(lineIndex);      // Q
            faqLines.remove(lineIndex);      // A (same index because previous line was removed)
            faqLines.remove(lineIndex);      // ---
            saveFaqs();
            loadFaqs();
            clearFields();
        });

        clearSelectionButton.setOnAction(e -> {
            faqListView.getSelectionModel().clearSelection();
            clearFields();
        });

        faqListView.getSelectionModel().selectedIndexProperty().addListener((obs, oldVal, newVal) -> {
            int selectedIndex = newVal.intValue();
            if (selectedIndex >= 0 && selectedIndex < faqIndices.size()) {
                int lineIndex = faqIndices.get(selectedIndex);
                String qLine = faqLines.get(lineIndex);
                String aLine = faqLines.get(lineIndex + 1);

                if (qLine.startsWith("Q:") && aLine.startsWith("A:")) {
                    faqQuestionField.setText(qLine.substring(2).trim());
                    faqAnswerField.setText(aLine.substring(2).trim());
                }
            }
        });

        backButton.setOnAction(e -> ((Stage) backButton.getScene().getWindow()).close());
    }

    private void loadFaqs() {
        faqListView.getItems().clear();
        faqIndices.clear();
        faqLines = new ArrayList<>(FileManager.readLines("faq.txt"));

        String question = "";
        String answer = "";

        for (int i = 0; i < faqLines.size(); i++) {
            String line = faqLines.get(i).trim();
            if (line.startsWith("Q:")) {
                question = line.substring(2).trim();
            } else if (line.startsWith("A:")) {
                answer = line.substring(2).trim();
            } else if (line.equals("---")) {
                if (!question.isEmpty()) {
                    String displayText = "Q: " + question + "\nA: " + (answer.isEmpty() ? "No answer provided yet." : answer);
                    VBox box = new VBox(new TextFlow(new Text(displayText)));
                    faqListView.getItems().add(box);
                    faqIndices.add(i - 2);  // Q is i-2
                }
                question = "";
                answer = "";
            }
        }
    }

    private void saveFaqs() {
        FileManager.writeLines("faq.txt", faqLines);
    }

    private void clearFields() {
        faqQuestionField.clear();
        faqAnswerField.clear();
    }
}
