package com.medirisk.storage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Persistence layer managing file storage for MediRisk.
 * Demonstrates Core Java File I/O: File, FileReader, FileWriter, BufferedReader, BufferedWriter,
 * try-catch-finally exception handling, and thread-safe synchronized file operations.
 */
public class FileManager {

    public static final String DATA_DIR = "data";
    public static final String FILE_USERS = DATA_DIR + File.separator + "users.txt";
    public static final String FILE_PATIENTS = DATA_DIR + File.separator + "patients.txt";
    public static final String FILE_ASSESSMENTS = DATA_DIR + File.separator + "assessments.txt";
    public static final String FILE_RECORDS = DATA_DIR + File.separator + "health_records.txt";

    /**
     * Initializes the data directory and data files on startup.
     * Seeds initial demo records only if files are newly created or empty.
     */
    public synchronized void initStorage() {
        File dir = new File(DATA_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
            System.out.println("[FileManager] Created data directory: " + dir.getAbsolutePath());
        }

        ensureFileExistsWithSampleData(FILE_USERS, getSampleUsers());
        ensureFileExistsWithSampleData(FILE_PATIENTS, getSamplePatients());
        ensureFileExistsWithSampleData(FILE_ASSESSMENTS, getSampleAssessments());
        ensureFileExistsWithSampleData(FILE_RECORDS, null);
    }

    private void ensureFileExistsWithSampleData(String filePath, String[] sampleData) {
        File file = new File(filePath);
        try {
            if (!file.exists() || file.length() == 0) {
                file.createNewFile();
                if (sampleData != null && sampleData.length > 0) {
                    BufferedWriter writer = null;
                    try {
                        writer = new BufferedWriter(new FileWriter(file, false));
                        for (String line : sampleData) {
                            writer.write(line);
                            writer.newLine();
                        }
                    } finally {
                        if (writer != null) {
                            writer.close();
                        }
                    }
                    System.out.println("[FileManager] Initialized sample data for: " + file.getName());
                }
            }
        } catch (IOException e) {
            System.err.println("[FileManager] Error initializing file " + filePath + ": " + e.getMessage());
        }
    }

    /**
     * Reads all non-empty lines from a specified text file.
     */
    public synchronized List<String> readAllLines(String filePath) {
        List<String> lines = new ArrayList<String>();
        File file = new File(filePath);
        if (!file.exists()) {
            return lines;
        }

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    lines.add(line.trim());
                }
            }
        } catch (IOException e) {
            System.err.println("[FileManager] Error reading file " + filePath + ": " + e.getMessage());
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ignored) {
                }
            }
        }
        return lines;
    }

    /**
     * Appends a single record line to a specified text file.
     */
    public synchronized boolean appendLine(String filePath, String line) {
        BufferedWriter writer = null;
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                file.createNewFile();
            }
            writer = new BufferedWriter(new FileWriter(file, true));
            writer.write(line);
            writer.newLine();
            return true;
        } catch (IOException e) {
            System.err.println("[FileManager] Error appending to file " + filePath + ": " + e.getMessage());
            return false;
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException ignored) {
                }
            }
        }
    }

    /**
     * Overwrites the whole file with a list of lines.
     */
    public synchronized boolean writeAllLines(String filePath, List<String> lines) {
        BufferedWriter writer = null;
        try {
            File file = new File(filePath);
            writer = new BufferedWriter(new FileWriter(file, false));
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
            return true;
        } catch (IOException e) {
            System.err.println("[FileManager] Error writing lines to file " + filePath + ": " + e.getMessage());
            return false;
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException ignored) {
                }
            }
        }
    }

    /**
     * Deletes a record from a file based on its ID (first field before '|').
     */
    public synchronized boolean deleteLineById(String filePath, String targetId) {
        List<String> lines = readAllLines(filePath);
        List<String> updatedLines = new ArrayList<String>();
        boolean found = false;

        for (String line : lines) {
            String[] parts = line.split("\\|", 2);
            if (parts.length > 0 && parts[0].equalsIgnoreCase(targetId)) {
                found = true;
            } else {
                updatedLines.add(line);
            }
        }

        if (found) {
            writeAllLines(filePath, updatedLines);
        }
        return found;
    }

    /**
     * Replaces a record line matching the target ID with a new line.
     */
    public synchronized boolean updateLineById(String filePath, String targetId, String newLine) {
        List<String> lines = readAllLines(filePath);
        List<String> updatedLines = new ArrayList<String>();
        boolean updated = false;

        for (String line : lines) {
            String[] parts = line.split("\\|", 2);
            if (parts.length > 0 && parts[0].equalsIgnoreCase(targetId)) {
                updatedLines.add(newLine);
                updated = true;
            } else {
                updatedLines.add(line);
            }
        }

        if (updated) {
            writeAllLines(filePath, updatedLines);
        }
        return updated;
    }

    /**
     * Searches lines containing the keyword (case-insensitive).
     */
    public synchronized List<String> searchLines(String filePath, String keyword) {
        List<String> lines = readAllLines(filePath);
        List<String> results = new ArrayList<String>();
        String lowerKeyword = keyword.toLowerCase();

        for (String line : lines) {
            if (line.toLowerCase().contains(lowerKeyword)) {
                results.add(line);
            }
        }
        return results;
    }

    /**
     * Generates the next sequential ID for a given file and prefix.
     * Reads all lines, finds the highest numeric suffix, and increments by 1.
     * Example: prefix="P", existing IDs P001..P005 → returns "P006".
     */
    public synchronized String generateNextId(String filePath, String prefix) {
        List<String> lines = readAllLines(filePath);
        int maxNum = 0;
        for (String line : lines) {
            String[] parts = line.split("\\|", 2);
            if (parts.length > 0 && parts[0].startsWith(prefix)) {
                try {
                    int num = Integer.parseInt(parts[0].substring(prefix.length()));
                    if (num > maxNum) {
                        maxNum = num;
                    }
                } catch (NumberFormatException ignored) {
                }
            }
        }
        return String.format("%s%03d", prefix, maxNum + 1);
    }

    // --- Sample data seeders for academic demonstration ---

    private String[] getSampleUsers() {
        return new String[] {
            "U001|dhiyanesh|password|Dhiyanesh M",
            "U002|alexandra|password|Alexandra Reed",
            "U003|admin|admin123|Dr. Smith"
        };
    }

    private String[] getSamplePatients() {
        return new String[] {
            "P001|Dhiyanesh M|21|Male|+91-9876543210",
            "P002|Alexandra Reed|45|Female|+1-555-0192",
            "P003|Robert Johnson|68|Male|+1-555-0144",
            "P004|Maria Garcia|32|Female|+1-555-0288"
        };
    }

    private String[] getSampleAssessments() {
        return new String[] {
            "A001|P001|Dhiyanesh M|21|72|118|78|98|36.6|16|10|LOW|Continue regular monitoring.|2026-10-06|10:15:00",
            "A002|P002|Alexandra Reed|45|108|145|92|94|38.2|22|45|MODERATE|Monitor vital signs and consider medical consultation.|2026-10-06|14:30:00",
            "A003|P003|Robert Johnson|68|125|168|102|88|38.8|24|78|HIGH|Medical evaluation is recommended.|2026-10-06|16:45:00",
            "A004|P004|Maria Garcia|32|140|175|110|82|39.5|28|85|CRITICAL|Immediate medical attention is recommended.|2026-10-06|18:00:00"
        };
    }
}
