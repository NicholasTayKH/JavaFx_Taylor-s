package org.example.assignment;

import java.io.*;
import java.util.*;

public class AdminDatabase {
    private static final String FILE_PATH = "Admin.txt";

    // Read all admins from file
    public static List<Admin> readAdmins() {
        List<Admin> admins = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 4) {
                    int id = Integer.parseInt(parts[0].trim());
                    String username = parts[1].trim();
                    String password = parts[2].trim();
                    String email = parts[3].trim();
                    admins.add(new Admin(id, username, password, email));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return admins;
    }

    // Write a new admin to file
    public static void writeAdmin(String username, String password, String email) {
        int newId = getNextId();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            writer.write(newId + "," + username + "," + password + "," + email);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Get next admin ID
    public static int getNextId() {
        int maxId = 0;
        for (Admin admin : readAdmins()) {
            if (admin.getId() > maxId) {
                maxId = admin.getId();
            }
        }
        return maxId + 1;
    }

    // Find an admin by username
    public static Admin findAdmin(String username) {
        for (Admin admin : readAdmins()) {
            if (admin.getUsername().equals(username)) {
                return admin;
            }
        }
        return null;
    }

    // Find admin's email by username
    public static String findEmailByUsername(String username) {
        for (Admin admin : readAdmins()) {
            if (admin.getUsername().equals(username)) {
                return admin.getEmail();
            }
        }
        return null;
    }

    // Update password for an admin
    public static void updatePassword(String username, String newPassword) {
        List<Admin> admins = readAdmins();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Admin admin : admins) {
                if (admin.getUsername().equals(username)) {
                    admin = new Admin(admin.getId(), admin.getUsername(), newPassword, admin.getEmail());
                }
                writer.write(admin.getId() + "," + admin.getUsername() + "," + admin.getPassword() + "," + admin.getEmail());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Validate admin login
    public static boolean validateLogin(String username, String password) {
        List<Admin> admins = readAdmins();
        for (Admin admin : admins) {
            if (admin.getUsername().equals(username) && admin.getPassword().equals(password)) {
                return true;
            }
        }
        return false;
    }
}
