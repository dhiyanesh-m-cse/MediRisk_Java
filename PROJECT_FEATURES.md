# MediRisk AI - Project Features Map

This document maps the main features of the MediRisk Patient Health Risk Assessment System to the specific Java classes and concepts used to implement them.

## 1. Patient Registration & Management
- **Description:** Registering, retrieving, updating, and deleting patient records.
- **Java Classes:** 
  - `Patient.java` (Model)
  - `PatientService.java` (Service Logic)
  - `FileManager.java` (Storage layer)
  - `MediRiskServer.java` (HTTP API routes)
- **Core Java Concepts:** OOP (Encapsulation, Objects), File Handling, String Parsing, Exception Handling.
- **Files Used:** `data/patients.txt`

## 2. Vital Signs Collection & Validation
- **Description:** Receiving physiological readings (Age, Heart Rate, BP, SpO2, Temperature, Respiratory Rate) and ensuring their mathematical validity before assessment.
- **Java Classes:**
  - `VitalSigns.java` (Value Object Model)
  - `Validator.java` (Validation Utility)
- **Core Java Concepts:** OOP, Mathematical boundaries, Static utility methods, Type conversion (String to int/double).
- **Files Used:** None (In-memory object during HTTP request).

## 3. Health Risk Calculation Engine
- **Description:** The core algorithm applying predefined weighted scores to the patient's vital signs to generate a final Risk Score (0-100), Risk Level (LOW to CRITICAL), and recommendation.
- **Java Classes:**
  - `RiskCalculator.java`
- **Core Java Concepts:** OOP (Methods, Business Logic), Conditional logic (if-else), Math.round.
- **Files Used:** None (Calculation happens in memory).

## 4. Multi-threaded Assessment Processing
- **Description:** Performing the risk calculation and file saving in a separate thread to demonstrate basic concurrency.
- **Java Classes:**
  - `AssessmentThread.java`
- **Core Java Concepts:** Multi-threading (extending `Thread`), `run()` method override, `Thread.join()` for synchronization.
- **Files Used:** None (Manages workflow flow).

## 5. Assessment Record Persistence
- **Description:** Storing the final assessment result persistently so it can be retrieved for patient history.
- **Java Classes:**
  - `Assessment.java` (Model)
  - `AssessmentService.java` (Service Logic)
  - `FileManager.java` (Storage layer)
- **Core Java Concepts:** OOP, File I/O (`BufferedWriter`, `BufferedReader`), Exception Handling, Pipe-delimited strings (`|`).
- **Files Used:** `data/assessments.txt`

## 6. User Authentication & Session
- **Description:** Securing the application with basic login and registration using file-based storage.
- **Java Classes:**
  - `User.java` (Model)
  - `UserService.java` (Service Logic)
- **Core Java Concepts:** Collections (`List`, `ArrayList`), Iteration (for-each loops), String comparison.
- **Files Used:** `data/users.txt`

## 7. HTTP API & Static File Serving
- **Description:** Providing an interface for the frontend browser to communicate with the Java backend logic via HTTP, and serving HTML/CSS/JS.
- **Java Classes:**
  - `MediRiskServer.java`
- **Core Java Concepts:** `com.sun.net.httpserver.*`, inner classes, custom HTTP routing, JSON string manipulation, stream reading/writing.
- **Files Used:** `frontend/*` (HTML/CSS/JS)
