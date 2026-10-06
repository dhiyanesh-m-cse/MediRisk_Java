package com.medirisk.model;

/**
 * Represents a patient registered in the MediRisk system.
 * Demonstrates Encapsulation: private fields + public getters/setters.
 * File format: P001|name|age|gender|contact
 */
public class Patient {

    private String id;       // P001, P002, ...
    private String name;
    private int age;
    private String gender;
    private String contact;

    // --- Constructors ---

    public Patient() {
    }

    public Patient(String id, String name, int age, String gender, String contact) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.contact = contact;
    }

    // --- Getters and Setters ---

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    /**
     * Converts the Patient object into a pipe-delimited string for file storage.
     */
    public String toFileString() {
        return id + "|" + name + "|" + age + "|" + gender + "|" + contact;
    }

    /**
     * Constructs a Patient object from a pipe-delimited string.
     * Returns null if the line is invalid or malformed.
     */
    public static Patient fromFileString(String line) {
        if (line == null || line.trim().isEmpty()) {
            return null;
        }
        try {
            String[] parts = line.split("\\|", -1);
            if (parts.length < 5) {
                System.err.println("[Patient] Skipping malformed record: " + line);
                return null;
            }
            int age = Integer.parseInt(parts[2].trim());
            return new Patient(parts[0].trim(), parts[1].trim(), age, parts[3].trim(), parts[4].trim());
        } catch (NumberFormatException e) {
            System.err.println("[Patient] Invalid age in record: " + line);
            return null;
        } catch (Exception e) {
            System.err.println("[Patient] Error parsing record: " + e.getMessage());
            return null;
        }
    }

    @Override
    public String toString() {
        return "Patient{id='" + id + "', name='" + name + "', age=" + age + ", gender='" + gender + "'}";
    }
}
