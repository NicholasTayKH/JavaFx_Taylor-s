package Classes;

import java.io.*;
import java.util.*;

public class UserDatabase {
    private static final String FILE_NAME = "user.txt";
    private static final String FILE_DETAIL_NAME = "userDetails.txt";

    public static List<User> readUsers() {
        List<User> users = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 4) {
                    int id = Integer.parseInt(parts[0]);
                    users.add(new User(id, parts[1], parts[2], parts[3]));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return users;
    }

    public static void writeUser(String username, String password, String email) {
        int newId = getNextId();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME, true))) {
            writer.write(newId + "," + username + "," + password + "," + email);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_DETAIL_NAME, true))) {
            writer.write(newId + "," + username + "," + password + "," + email);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static int getNextId() {
        int maxId = 0;
        for (User user : readUsers()) {
            if (user.getId() > maxId) {
                maxId = user.getId();
            }
        }
        return maxId + 1;
    }

    public static User findUser(String username) {
        for (User user : readUsers()) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }

    public static String findEmailByUsername(String username) {
        String line;
        for (User user : readUsers()) {
            if (user.getUsername().equals(username)) {
                return user.getEmail();
            }
        }
        return null; // not found
    }

    public static void updatePassword(String username, String newPassword) {
        List<User> users = readUsers();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (User user : users) {
                if (user.getUsername().equals(username)) {
                    user = new User(user.getId(), user.getUsername(), newPassword, user.getEmail());
                }
                writer.write(user.getId() + "," + user.getUsername() + "," + user.getPassword() + "," + user.getEmail());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean validateLogin(String username, String password) {
        for (User user : readUsers()) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                return true; // Valid login
            }
        }
        return false; // Invalid username or password
    }
}
