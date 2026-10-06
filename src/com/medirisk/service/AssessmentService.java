package com.medirisk.service;

import com.medirisk.model.Assessment;
import com.medirisk.storage.FileManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Service for managing Assessment records.
 * Provides CRUD operations backed by FileManager.
 */
public class AssessmentService {

    private FileManager fileManager;

    public AssessmentService(FileManager fileManager) {
        this.fileManager = fileManager;
    }

    /**
     * Reads all assessments from file.
     */
    public List<Assessment> getAllAssessments() {
        List<String> lines = fileManager.readAllLines(FileManager.FILE_ASSESSMENTS);
        List<Assessment> assessments = new ArrayList<Assessment>();
        for (String line : lines) {
            Assessment a = Assessment.fromFileString(line);
            if (a != null) {
                assessments.add(a);
            }
        }
        return assessments;
    }

    /**
     * Saves a new Assessment object.
     * Generates a new sequential ID (A001, A002, etc.) and appends to the file.
     */
    public Assessment saveAssessment(Assessment assessment) {
        if (assessment == null) return null;
        
        String newId = fileManager.generateNextId(FileManager.FILE_ASSESSMENTS, "A");
        assessment.setId(newId);
        
        boolean success = fileManager.appendLine(FileManager.FILE_ASSESSMENTS, assessment.toFileString());
        if (success) {
            return assessment;
        }
        return null;
    }

    /**
     * Deletes an assessment by ID.
     */
    public boolean deleteAssessment(String id) {
        if (id == null) return false;
        return fileManager.deleteLineById(FileManager.FILE_ASSESSMENTS, id);
    }
}
