package org.example.assignment;

public class User {
    private String id;
    private String name;
    private String phone;
    private int age;
    private String dob;
    private String ic;
    private String email;

    public User(String id, String name, String phone, int age, String dob, String ic, String email) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.age = age;
        this.dob = dob;
        this.ic = ic;
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public int getAge() {
        return age;
    }

    public String getDob() {
        return dob;
    }

    public String getIc() {
        return ic;
    }

    public String getEmail() {
        return email;
    }
}
