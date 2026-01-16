package Classes;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ChatHistoryDatabase {

    private static final String FILE_NAME = "chatbotHistory.txt";

    public static void saveMessage(String sender, String message) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME, true))) {
            writer.write("--" + sender + "--");
            writer.newLine();
            writer.write(message);
            writer.newLine(); // ensure separation
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<String[]> loadHistory() {
        List<String[]> history = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            String currentSender = null;
            StringBuilder messageBuilder = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                if (line.startsWith("--user--") || line.startsWith("--ai--")) {
                    // Save previous message
                    if (currentSender != null && messageBuilder.length() > 0) {
                        history.add(new String[]{currentSender, messageBuilder.toString().trim()});
                        messageBuilder.setLength(0); // reset
                    }
                    currentSender = line.substring(2, line.length() - 2); // remove -- --
                } else {
                    messageBuilder.append(line).append("\n");
                }
            }

            // Final message
            if (currentSender != null && messageBuilder.length() > 0) {
                history.add(new String[]{currentSender, messageBuilder.toString().trim()});
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return history;
    }

    public static void clearHistory() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            writer.write(""); // Clear file
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
