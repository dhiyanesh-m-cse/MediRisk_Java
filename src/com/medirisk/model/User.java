package com.medirisk.model;

/**
 * Represents a registered user in the MediRisk system.
 * Demonstrates Encapsulation: private fields + public getters/setters.
 */
public class User {

    private String id;         // U001, U002, ...
    private String username;
    private String password;
    private String name;

    // --- Constructors ---

    public User() {
    }

    public User(String id, String username, String password, String name) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.name = name;
    }

    // --- Getters and Setters ---

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Converts the User object into a pipe-delimited string for file storage.
     * Format: id|username|password|name
     */
    public String toFileString() {
        return id + "|" + username + "|" + password + "|" + name;
    }

    /**
     * Constructs a User object from a pipe-delimited string.
     * Returns null if the line is invalid or malformed.
     */
    public static User fromFileString(String line) {
        if (line == null || line.trim().isEmpty()) {
            return null;
        }
        try {
            String[] parts = line.split("\\|", -1);
            if (parts.length < 4) {
                System.err.println("[User] Skipping malformed record: " + line);
                return null;
            }
            return new User(parts[0].trim(), parts[1].trim(), parts[2].trim(), parts[3].trim());
        } catch (Exception e) {
            System.err.println("[User] Error parsing record: " + e.getMessage());
            return null;
        }
    }

    @Override
    public String toString() {
        return "User{id='" + id + "', username='" + username + "', name='" + name + "'}";
    }
}
