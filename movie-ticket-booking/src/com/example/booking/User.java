package com.example.booking;

public class User {
    private final String userId;
    private final String name;
    private final String email;

    public User(String userId, String name, String email) {
        this.userId = userId; this.name = name; this.email = email;
    }

    public String userId() { return userId; }
    public String name() { return name; }
    public String email() { return email; }

    @Override
    public String toString() { return name + " (" + email + ")"; }
}
