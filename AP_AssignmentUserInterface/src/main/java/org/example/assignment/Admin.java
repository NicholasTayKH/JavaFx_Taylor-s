package org.example.assignment;

public class Admin {
    private int id;
    private String username;
    private String password;
    private String email;

    public Admin(int id, String username, String password, String email) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
    }

    // Getters and setters if needed
    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getEmail() { return email; }
}
