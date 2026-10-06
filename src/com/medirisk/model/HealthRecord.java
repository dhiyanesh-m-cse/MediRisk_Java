package com.medirisk.model;

/**
 * Represents a stored health record file (e.g. uploaded lab report, prescription).
 * File format: R001|patientId|fileName|filePath|date|time|fileType
 */
public class HealthRecord {

    private String id;           // R001, R002, ...
    private String patientId;    // P001
    private String fileName;
    private String filePath;
    private String date;         // yyyy-MM-dd
    private String time;         // HH:mm:ss
    private String fileType;     // pdf, jpg, png, txt

    // --- Constructors ---

    public HealthRecord() {
    }

    public HealthRecord(String id, String patientId, String fileName,
                        String filePath, String date, String time, String fileType) {
        this.id = id;
        this.patientId = patientId;
        this.fileName = fileName;
        this.filePath = filePath;
        this.date = date;
        this.time = time;
        this.fileType = fileType;
    }

    // --- Getters and Setters ---

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public String getFileType() { return fileType; }
    public void setFileType(String fileType) { this.fileType = fileType; }

    /**
     * Converts the HealthRecord object into a pipe-delimited string for file storage.
     */
    public String toFileString() {
        return id + "|" + patientId + "|" + fileName + "|" + filePath + "|"
                + date + "|" + time + "|" + fileType;
    }

    /**
     * Constructs a HealthRecord from a pipe-delimited string.
     */
    public static HealthRecord fromFileString(String line) {
        if (line == null || line.trim().isEmpty()) {
            return null;
        }
        try {
            String[] parts = line.split("\\|", -1);
            if (parts.length < 7) {
                System.err.println("[HealthRecord] Skipping malformed record: " + line);
                return null;
            }
            return new HealthRecord(
                parts[0].trim(), parts[1].trim(), parts[2].trim(),
                parts[3].trim(), parts[4].trim(), parts[5].trim(), parts[6].trim()
            );
        } catch (Exception e) {
            System.err.println("[HealthRecord] Error parsing record: " + e.getMessage());
            return null;
        }
    }

    @Override
    public String toString() {
        return "HealthRecord{id='" + id + "', patient='" + patientId + "', file='" + fileName + "'}";
    }
}
