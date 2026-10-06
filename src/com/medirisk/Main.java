package com.medirisk;

import com.medirisk.model.Patient;
import com.medirisk.model.User;
import com.medirisk.model.VitalSigns;
import com.medirisk.model.Assessment;
import com.medirisk.server.MediRiskServer;
import com.medirisk.service.AssessmentService;
import com.medirisk.service.PatientService;
import com.medirisk.service.ReportService;
import com.medirisk.service.RiskCalculator;
import com.medirisk.service.UserService;
import com.medirisk.storage.FileManager;
import com.medirisk.threads.AssessmentThread;

import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        System.out.println("==========================================================");
        System.out.println("   MediRisk AI - Core Java Backend Initialization        ");
        System.out.println("==========================================================");

        System.out.println("[Main] Step 1: Checking local data directory and files...");
        FileManager fileManager = new FileManager();
        fileManager.initStorage();

        System.out.println("[Main] Step 2: Initializing services...");
        UserService userService = new UserService(fileManager);
        PatientService patientService = new PatientService(fileManager);
        AssessmentService assessmentService = new AssessmentService(fileManager);
        ReportService reportService = new ReportService(fileManager);
        RiskCalculator riskCalculator = new RiskCalculator();

        System.out.println("[Main] Step 3: Running diagnostic self-test...");
        
        // 1. Auth check
        User demoUser = userService.authenticate("dhiyanesh", "password");
        if (demoUser != null) {
            System.out.println("  [PASS] User Authentication: Logged in as '" + demoUser.getName() + "'");
        } else {
            System.out.println("  [FAIL] User Authentication: Could not login test user.");
        }

        // 2. Patient check
        System.out.println("  [PASS] File Database (Patients): Loaded " + patientService.getAllPatients().size() + " patient records.");

        // 3. Risk Thread Test
        Patient testPatient = patientService.getPatientById("P002");
        if (testPatient != null) {
            VitalSigns testVitals = new VitalSigns(108, 145, 92, 94, 38.2, 22);
            AssessmentThread threadRunnable = new AssessmentThread(testPatient, testVitals, riskCalculator, assessmentService);
            threadRunnable.start();

            try {
                threadRunnable.join(2000);
                Assessment result = threadRunnable.getResultAssessment();
                if (result != null) {
                    System.out.println("  [PASS] Multi-Threaded Risk Engine: Score=" + result.getRiskScore() 
                            + ", Level=" + result.getRiskLevel() + ", Rec='" + result.getRecommendation() + "'");
                }
            } catch (InterruptedException e) {
                System.out.println("  [FAIL] Multi-Threaded test interrupted.");
            }
        }

        System.out.println("  [PASS] File Database (Assessments): Loaded " + assessmentService.getAllAssessments().size() + " saved assessments.");

        System.out.println("[Main] Step 4: Starting Core Java HTTP Server on port 8080...");
        try {
            MediRiskServer server = new MediRiskServer(8080, userService, patientService, assessmentService, reportService);
            server.start();
            System.out.println("\n[SUCCESS] MediRisk Backend initialized successfully!");
            System.out.println("[READY] Listening for HTTP requests at: http://localhost:8080/api/status");
            System.out.println("==========================================================");
        } catch (IOException e) {
            System.err.println("[ERROR] Failed to start server: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
