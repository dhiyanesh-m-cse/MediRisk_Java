package com.medirisk.service;

import com.medirisk.model.User;
import com.medirisk.storage.FileManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages user authentication and registration.
 * Demonstrates: Collections (ArrayList), File I/O via FileManager, exception handling.
 */
public class UserService {

    private FileManager fileManager;

    public UserService(FileManager fileManager) {
        this.fileManager = fileManager;
    }

    /**
     * Authenticates a user by username and password.
     * Returns the User object if credentials match, null otherwise.
     */
    public User authenticate(String username, String password) {
        if (username == null || password == null) {
            return null;
        }
        List<User> users = getAllUsers();
        for (User user : users) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                return user;
            }
        }
        return null;
    }

    /**
     * Registers a new user with auto-generated ID.
     * Returns the created User, or null if the username already exists.
     */
    public User register(String username, String password, String name) {
        if (username == null || password == null || name == null) {
            return null;
        }
        // Check for duplicate username
        List<User> users = getAllUsers();
        for (User user : users) {
            if (user.getUsername().equalsIgnoreCase(username)) {
                return null; // Username already taken
            }
        }

        String newId = fileManager.generateNextId(FileManager.FILE_USERS, "U");
        User newUser = new User(newId, username, password, name);
        fileManager.appendLine(FileManager.FILE_USERS, newUser.toFileString());
        System.out.println("[UserService] Registered new user: " + newUser);
        return newUser;
    }

    /**
     * Reads all users from users.txt, skipping any malformed records.
     */
    public List<User> getAllUsers() {
        List<String> lines = fileManager.readAllLines(FileManager.FILE_USERS);
        List<User> users = new ArrayList<User>();
        for (String line : lines) {
            User user = User.fromFileString(line);
            if (user != null) {
                users.add(user);
            }
        }
        return users;
    }

    /**
     * Finds a user by their ID.
     */
    public User getUserById(String userId) {
        List<User> users = getAllUsers();
        for (User user : users) {
            if (user.getId().equalsIgnoreCase(userId)) {
                return user;
            }
        }
        return null;
    }
}
