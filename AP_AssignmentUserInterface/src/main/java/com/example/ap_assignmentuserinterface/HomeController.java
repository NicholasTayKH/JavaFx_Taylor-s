package com.example.ap_assignmentuserinterface;
import Classes.*;
import dev.langchain4j.model.chat.response.ChatResponse;
import javafx.animation.PauseTransition;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.*;

import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

import javafx.event.ActionEvent;
import javafx.util.Duration;
import javafx.animation.TranslateTransition;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import javafx.scene.text.Font;
import javafx.scene.paint.Color;

import java.io.IOException;
import Ai.AnswerService;
import Ai.SearchAction;
import Ai.CustomStreamingResponseHandler;
import Classes.FAQDatabase;

public class HomeController {
    @FXML private Button faqButton;
    @FXML private Button rateButton;
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
    @FXML private AnchorPane rateUsOverlay;
    @FXML private AnchorPane successRatingOverlay;
    @FXML private Label feedbackCheckerLabel;
    @FXML private AnchorPane existingRatingOverlay;
    @FXML private AnchorPane faqPanel;
    @FXML private TextField faqSearchField;
    @FXML private VBox faqContainer;
    @FXML private Button newFaq;
    @FXML private AnchorPane newFaqOverlay;
    @FXML private TextArea newQuestionArea;
    @FXML private Label newQuestionCheckerLabel;
    @FXML private AnchorPane successFaqOverlay;
    @FXML private Button feedbackCancel;
    @FXML private Button feedbackSubmit;
    @FXML private Button questionCancel;
    @FXML private Button questionSubmit;
    @FXML private Button continue1;
    @FXML private Button continue2;
    @FXML private Button continue3;
    private final AnswerService answerService = new AnswerService();
    private boolean initialized = false;
    private String username;

    public void setUsername(String username) {
        this.username = username;
    }
    @FXML
    private void goBooking(ActionEvent event) throws IOException {
        TranslateTransition transition = new TranslateTransition(Duration.millis(400), faqPanel);
        transition.setToY(600);
        transition.setOnFinished(e -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("booking-page.fxml"));
                Parent root = loader.load();
                BookingController controller = loader.getController();
                controller.setUsername(username);
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.show();
            } catch (IOException ex) {
                ex.printStackTrace(); // Optional: show error message to user
            }
        });

        transition.play();
    }
    @FXML
    private void goProfile(ActionEvent event) throws IOException {
        TranslateTransition transition = new TranslateTransition(Duration.millis(400), faqPanel);
        transition.setToY(600);
        transition.setOnFinished(e -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("profile-page.fxml"));
                Parent root = loader.load();
                ProfileController controller = loader.getController();
                controller.setUsername(username);
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.show();
            } catch (IOException ex) {
                ex.printStackTrace(); // Optional: show error message to user
            }
        });

        transition.play(); // Start the animation
    }

    public void initialize() {

        feedbackCheckerLabel.setText("");
        if (!initialized) {
            SearchAction initAction = new SearchAction("Initializing chatbot...");
            answerService.init(initAction);
            initialized = true;
        }
        faqPanel.setTranslateY(650);
        notificationPanel.setTranslateY(-650);
        chatbotPanel.setTranslateX(850);
        //Water Effect on hover
        applyWaterHoverEffect(homeButton);
        applyWaterHoverEffect(bookingButton);
        applyWaterHoverEffect(profileButton);
        applyWaterHoverEffect(chatbotButton);
        applyWaterHoverEffect(notificationButton);
        applyWaterHoverEffect(collapseNotificationButton);
        applyWaterHoverEffect(collapseChatBotButton);
        applyWaterHoverEffect(sendButton);
        applyWaterHoverEffect(newFaq);
        applyWaterHoverEffect(faqButton);
        applyWaterHoverEffect(rateButton);
        applyWaterHoverEffect(feedbackCancel);
        applyWaterHoverEffect(feedbackSubmit);
        applyWaterHoverEffect(questionCancel);
        applyWaterHoverEffect(questionSubmit);
        applyWaterHoverEffect(continue1);
        applyWaterHoverEffect(continue2);
        applyWaterHoverEffect(continue3);

        //On clicked downscaled effect
        applyClickScaleEffect(continue3);
        applyClickScaleEffect(continue2);
        applyClickScaleEffect(continue1);
        applyClickScaleEffect(questionSubmit);
        applyClickScaleEffect(questionCancel);
        applyClickScaleEffect(feedbackSubmit);
        applyClickScaleEffect(feedbackCancel);
        applyClickScaleEffect(homeButton);
        applyClickScaleEffect(bookingButton);
        applyClickScaleEffect(profileButton);
        applyClickScaleEffect(chatbotButton);
        applyClickScaleEffect(notificationButton);
        applyClickScaleEffect(collapseNotificationButton);
        applyClickScaleEffect(collapseChatBotButton);
        applyClickScaleEffect(sendButton);
        applyClickScaleEffect(newFaq);
        applyClickScaleEffect(rateButton);
        applyClickScaleEffect(faqButton);
        faqSearchField.textProperty().addListener((obs, oldVal, newVal) -> handleFAQSearch());
        displayFAQs(FAQDatabase.loadAllFAQs());
        faqSearchField.setStyle("""
            -fx-background-color: white;
            -fx-background-radius: 20;
            -fx-border-radius: 20;
            -fx-border-color: #6CA8E6;
            -fx-border-width: 2;
            -fx-padding: 8 12;
            -fx-font-size: 14px;
        """);
        Platform.runLater(() ->{
            // Load the user's notifications here
            List<Notification> unread = NotificationDatabase.readUnreadNotifications(username);
            Collections.reverse(unread);
            for (Notification n : unread) {
                addNotification(n.getSubject(), n.getContent(), n.getTimestamp(), n.getId());
            }
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
        chatScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        chatScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

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
        stars = new Text[]{star1, star2, star3, star4, star5};
        updateStars();

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
    @FXML
    private void hideNotificationPanel(ActionEvent event) {
        TranslateTransition transition = new TranslateTransition(Duration.millis(400), notificationPanel);
        transition.setToY(-650); // move it back up off-screen
        transition.setOnFinished(e -> notificationPanel.setVisible(false)); // optional: hide after animation
        transition.play();
    }
    @FXML
    private void hideChatbotPanel(ActionEvent event) {
        TranslateTransition transition = new TranslateTransition(Duration.millis(400), chatbotPanel);
        transition.setToX(850); // move it off-screen right
        transition.setOnFinished(e -> chatbotPanel.setVisible(false));
        transition.play();
    }@FXML
    private void showNotificationPanel(ActionEvent event) {
        if (faqPanel.getTranslateY() < 600) {
            TranslateTransition hideFAQ = new TranslateTransition(Duration.millis(400), faqPanel);
            hideFAQ.setToY(600); // Collapse FAQ panel

            // After collapsing FAQ, show notification panel
            hideFAQ.setOnFinished(e -> {
                notificationPanel.setVisible(true);
                TranslateTransition showNotification = new TranslateTransition(Duration.millis(400), notificationPanel);
                showNotification.setToY(0); // slide it into place
                showNotification.play();
            });

            hideFAQ.play();
        } else {
            notificationPanel.setVisible(true);
            TranslateTransition showNotification = new TranslateTransition(Duration.millis(400), notificationPanel);
            showNotification.setToY(0);
            showNotification.play();
        }
    }
    @FXML
    private void showFAQPanel(ActionEvent event){
        TranslateTransition transition = new TranslateTransition(Duration.millis(400), faqPanel);
        transition.setToY(100);
        transition.setOnFinished(e -> chatbotPanel.setVisible(true));
        transition.play();
    }
    @FXML
    private void hideFAQPanel(ActionEvent event){
        TranslateTransition transition = new TranslateTransition(Duration.millis(400), faqPanel);
        transition.setToY(600);
        transition.setOnFinished(e -> chatbotPanel.setVisible(false));
        transition.play();
    }
    @FXML
    private void showChatbotPanel(ActionEvent event) {
        chatbotPanel.setVisible(true);
        TranslateTransition transition = new TranslateTransition(Duration.millis(400), chatbotPanel);
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
        notificationRow.setPrefWidth(600);
        notificationRow.setMaxWidth(600);
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
    @FXML private Text star1;
    @FXML private Text star2;
    @FXML private Text star3;
    @FXML private Text star4;
    @FXML private Text star5;
    @FXML private TextArea feedbackArea;
    private Text[] stars;
    private int rating = 0;
    void hideOverlay(){rateUsOverlay.setVisible(false); newFaqOverlay.setVisible(false);successRatingOverlay.setVisible(false);existingRatingOverlay.setVisible(false);successFaqOverlay.setVisible(false);}
    @FXML private void openRateUsOverlay(){

        if(RatingDatabase.findRatingByUsername(username)==null){
            rateUsOverlay.setVisible(true);
        }
        else{
            existingRatingOverlay.setVisible(true);
        }
    }
    @FXML private void handleOverlayCancel(){rateUsOverlay.setVisible(false);setRating(0);feedbackArea.clear();}
    @FXML
    private void handleStar1(){
        setRating(1);
    }
    @FXML
    private void handleStar2(){
        setRating(2);
    }
    @FXML
    private void handleStar3(){
        setRating(3);
    }
    @FXML
    private void handleStar4(){
        setRating(4);
    }
    @FXML
    private void handleStar5(){
        setRating(5);
    }
    @FXML
    private void handleOverlaySubmit(){

        String feedback = feedbackArea.getText();
        LocalDateTime date = LocalDateTime.now();
        // Check if rating is selected and feedback is not empty
        if (rating == 0 || feedback.isEmpty()) {
            feedbackCheckerLabel.setText("Please enter rating and feedback");
            feedbackCheckerLabel.setStyle("-fx-text-fill: #b30000; -fx-font-weight: bold;");
        }
        else{
            Rating newRating = new Rating(username, rating, feedback, date);
            RatingDatabase.saveRating(newRating);
            rateUsOverlay.setVisible(false);
            successRatingOverlay.setVisible(true);
            NotificationDatabase.writeNotification(username, "Thank You!", "You had provided a feedback.");
            loadNotifications();
        }
    }
    private void updateStars() {
        for (int i = 0; i < stars.length; i++) {
            Text star = stars[i];
            star.setText(i < rating ? "★" : "☆");
            star.setStyle("-fx-font-size: 30px;");
            if (i < rating) {
                star.setFill(Color.GOLD);
                star.setStroke(Color.DARKGOLDENROD);
                star.setStrokeWidth(1);
            } else {
                star.setFill(Color.GRAY);
                star.setStroke(null);
            }
        }
    }
    private void setRating(int r) {
        rating = r;
        updateStars();
    }

    public void handleCloseSuccessOverlay(ActionEvent event) {successRatingOverlay.setVisible(false);}
    public void handleCloseExistingOverlay(ActionEvent event){existingRatingOverlay.setVisible(false);}
    private void displayFAQs(List<FAQ> faqs) {
        faqContainer.getChildren().clear();

        for (FAQ faq : faqs) {
            TitledPane pane = new TitledPane();
            pane.setText(faq.getQuestion());

            String answerText = faq.getAnswer().isEmpty()
                    ? "Awaiting response from admin."
                    : faq.getAnswer();

            Label answerLabel = new Label(answerText);
            answerLabel.setWrapText(true);
            answerLabel.setStyle("-fx-padding: 10; -fx-font-size: 14px;");

            pane.setContent(answerLabel);
            pane.setExpanded(false);

            String backgroundColor = faq.getAnswer().isEmpty() ? "#ffcccc" : "#ccffcc";
            String borderColor = faq.getAnswer().isEmpty() ? "#cc0000" : "#009900";

            pane.setStyle(String.format("""
            -fx-background-color: %s;
            -fx-border-color: %s;
            -fx-background-radius: 20;
            -fx-border-radius: 20;
            -fx-border-width: 1.2;
            -fx-padding: 3;
        """, backgroundColor, borderColor));

            faqContainer.getChildren().add(pane);

            PauseTransition pause = new PauseTransition(Duration.millis(10));
            pause.setOnFinished(e -> {
                Node titleRegion = pane.lookup(".title");
                if (titleRegion != null) {
                    titleRegion.setStyle("""
            -fx-background-radius: 20;
            -fx-border-radius: 20;
            -fx-padding: 8;
        """);
                }

                Node contentRegion = pane.lookup(".content");
                if (contentRegion != null) {
                    contentRegion.setStyle("""
            -fx-background-radius: 20;
            -fx-border-radius: 20;
            -fx-padding: 8;
        """);
                }
            });
            pause.play();

        }
    }


    @FXML
    private void handleFAQSearch() {
        String query = faqSearchField.getText().trim();
        List<FAQ> results;
        if (query.isEmpty()) {
            results = FAQDatabase.loadAllFAQs(); // show all if empty
        } else {
            results = FAQDatabase.searchFAQs(query);
        }
        displayFAQs(results);
    }


    public void showNewFaqOverlay(ActionEvent event) {
        newFaqOverlay.setVisible(true);
    }

    public void handleCancelQuestion(ActionEvent event) {
        newQuestionArea.clear();
        newFaqOverlay.setVisible(false);
    }

    public void handleSendQuestion(ActionEvent event) {
        String newQuestion = newQuestionArea.getText();
        if(newQuestion.isEmpty()){
            newQuestionCheckerLabel.setText("Please enter question to submit.");
            newQuestionCheckerLabel.setStyle("-fx-text-fill: #b30000; -fx-font-weight: bold;");
        }
        else{
            FAQ newfaq = new FAQ(newQuestion,"");
            FAQDatabase.saveFAQ(newfaq);
            newFaqOverlay.setVisible(false);
            successFaqOverlay.setVisible(true);
            NotificationDatabase.writeNotification(username, "Question Uploaded!", "You had provided a question, you can check your question in the Faq section!");
            loadNotifications();
            loadFaqs();
            newQuestionArea.clear();
        }
    }

    public void handleCloseSuccessFaqOverlay(ActionEvent event) {
        successFaqOverlay.setVisible(false);
    }
    public void loadNotifications() {
        notificationContainer.getChildren().clear(); // Clear previous ones to avoid duplicates
        List<Notification> unread = NotificationDatabase.readUnreadNotifications(username);
        Collections.reverse(unread);
        for (Notification n : unread) {
            addNotification(n.getSubject(), n.getContent(), n.getTimestamp(), n.getId());
        }
    }

    public void loadFaqs() {
        displayFAQs(FAQDatabase.loadAllFAQs());
    }
}
