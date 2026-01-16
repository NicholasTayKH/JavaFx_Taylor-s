package Classes;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class RatingDatabase {
    private static final String FILE_PATH = "rating.txt";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public static void saveRating(Rating rating) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            writer.write("Username: " + rating.getRatingUsername());
            writer.newLine();
            writer.write("Rating: " + rating.getRating());
            writer.newLine();
            writer.write("Feedback: " + rating.getFeedback());
            writer.newLine();
            writer.write("Date: " + rating.getDate().format(FORMATTER));
            writer.newLine();
            writer.write("---");
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Rating> loadRatings() {
        List<Rating> ratings = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            String username = null, feedback = null;
            int rating = 0;
            LocalDateTime date = null;

            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Username:")) {
                    username = line.substring(9).trim();
                } else if (line.startsWith("Rating:")) {
                    rating = Integer.parseInt(line.substring(7).trim());
                } else if (line.startsWith("Feedback:")) {
                    feedback = line.substring(9).trim();
                } else if (line.startsWith("Date:")) {
                    date = LocalDateTime.parse(line.substring(5).trim(), FORMATTER);
                } else if (line.equals("---")) {
                    ratings.add(new Rating(username, rating, feedback, date));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return ratings;
    }
    public static Rating findRatingByUsername(String usernameToFind) {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            String username = null, feedback = null;
            int rating = 0;
            LocalDateTime date = null;

            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Username:")) {
                    username = line.substring(9).trim();
                } else if (line.startsWith("Rating:")) {
                    rating = Integer.parseInt(line.substring(7).trim());
                } else if (line.startsWith("Feedback:")) {
                    feedback = line.substring(9).trim();
                } else if (line.startsWith("Date:")) {
                    date = LocalDateTime.parse(line.substring(5).trim(), FORMATTER);
                } else if (line.equals("---")) {
                    if (username != null && username.equals(usernameToFind)) {
                        return new Rating(username, rating, feedback, date);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null; // not found
    }
}
