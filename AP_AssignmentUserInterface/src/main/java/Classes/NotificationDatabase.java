package Classes;

import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class NotificationDatabase {

    // Safely wrap text in quotes to allow commas
    private static String quote(String text) {
        return "\"" + text.replace("\"", "\"\"") + "\"";
    }

    // Parse a CSV line into String parts, respecting quotes
    private static String[] parseCSVLine(String line) {
        List<String> result = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder sb = new StringBuilder();

        for (char c : line.toCharArray()) {
            if (c == '\"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                result.add(sb.toString());
                sb.setLength(0);
            } else {
                sb.append(c);
            }
        }
        result.add(sb.toString()); // add last part
        return result.toArray(new String[0]);
    }

    // Create a new notification (unread by default)
    public static void writeNotification(String username, String subject, String content) {
        int newId = getNextId(username);
        LocalDateTime now = LocalDateTime.now();
        String fileName = username + "Notifications.txt";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true))) {
            writer.write(newId + "," +
                    quote(subject) + "," +
                    quote(content) + "," +
                    quote(now.toString()) + "," +
                    "false");
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Get next available ID for that user
    public static int getNextId(String username) {
        int maxId = 0;
        for (Notification n : readAllNotifications(username)) {
            if (n.getId() > maxId) {
                maxId = n.getId();
            }
        }
        return maxId + 1;
    }

    // Read ALL notifications
    public static List<Notification> readAllNotifications(String username) {
        List<Notification> notifications = new ArrayList<>();
        String fileName = username + "Notifications.txt";
        File file = new File(fileName);

        if (!file.exists()) return notifications;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;

            while ((line = reader.readLine()) != null) {
                String[] parts = parseCSVLine(line);
                if (parts.length == 5) {
                    int id = Integer.parseInt(parts[0]);
                    String subject = parts[1];
                    String content = parts[2];
                    LocalDateTime timestamp = LocalDateTime.parse(parts[3]);
                    boolean read = Boolean.parseBoolean(parts[4]);
                    notifications.add(new Notification(id, subject, content, timestamp, read));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return notifications;
    }

    // Read ONLY unread notifications
    public static List<Notification> readUnreadNotifications(String username) {
        List<Notification> all = readAllNotifications(username);
        List<Notification> unread = new ArrayList<>();
        for (Notification n : all) {
            if (!n.isRead()) {
                unread.add(n);
            }
        }
        return unread;
    }



    // Overwrite the file with updated notifications
    public static void updateNotifications(String username, List<Notification> notifications) {
        String fileName = username + "Notifications.txt";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (Notification n : notifications) {
                writer.write(n.getId() + "," +
                        quote(n.getSubject()) + "," +
                        quote(n.getContent()) + "," +
                        quote(n.getTimestamp().toString()) + "," +
                        n.isRead());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
