package com.medirisk.service;

import com.medirisk.model.Patient;
import com.medirisk.storage.FileManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Service for managing Patient records.
 * Provides CRUD operations backed by FileManager.
 */
public class PatientService {

    private FileManager fileManager;

    public PatientService(FileManager fileManager) {
        this.fileManager = fileManager;
    }

    /**
     * Reads all patients from file.
     */
    public List<Patient> getAllPatients() {
        List<String> lines = fileManager.readAllLines(FileManager.FILE_PATIENTS);
        List<Patient> patients = new ArrayList<Patient>();
        for (String line : lines) {
            Patient p = Patient.fromFileString(line);
            if (p != null) {
                patients.add(p);
            }
        }
        return patients;
    }

    /**
     * Creates a new patient with an auto-generated ID.
     */
    public Patient createPatient(String name, int age, String gender, String contact) {
        String newId = fileManager.generateNextId(FileManager.FILE_PATIENTS, "P");
        Patient newPatient = new Patient(newId, name, age, gender, contact);
        boolean success = fileManager.appendLine(FileManager.FILE_PATIENTS, newPatient.toFileString());
        if (success) {
            return newPatient;
        }
        return null; // Failed to write
    }

    /**
     * Finds a patient by their exact ID.
     */
    public Patient getPatientById(String id) {
        List<Patient> patients = getAllPatients();
        for (Patient p : patients) {
            if (p.getId().equalsIgnoreCase(id)) {
                return p;
            }
        }
        return null;
    }

    /**
     * Updates an existing patient.
     * Returns true if successful and the patient was found.
     */
    public boolean updatePatient(Patient patient) {
        if (patient == null || patient.getId() == null) {
            return false;
        }
        // Validate patient still exists before overwriting
        Patient existing = getPatientById(patient.getId());
        if (existing == null) {
            return false;
        }
        return fileManager.updateLineById(FileManager.FILE_PATIENTS, patient.getId(), patient.toFileString());
    }

    /**
     * Deletes a patient by ID.
     * Note: This does NOT cascade delete assessments in this basic academic implementation,
     * though that could be mentioned in the viva as a future enhancement.
     */
    public boolean deletePatient(String id) {
        if (id == null) return false;
        return fileManager.deleteLineById(FileManager.FILE_PATIENTS, id);
    }
}
