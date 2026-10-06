# Core Java Concepts Demonstrated

This document details how the fundamental concepts of Java are applied practically in the MediRisk AI project. It serves as a guide for understanding the code and preparing for a viva voce.

## 1. Object-Oriented Programming (OOP)

OOP is the backbone of the project, structuring the data and logic into understandable components.

### Objects & Classes
The system models real-world entities using Java classes:
- `Patient`: Represents a patient profile.
- `User`: Represents an application user (e.g., doctor or admin).
- `VitalSigns`: A value object holding physiological readings.
- `Assessment`: Represents the final calculated health risk report.

### Encapsulation
All model classes (`Patient`, `Assessment`, etc.) use private instance variables to protect their data state. External access is only allowed through public `get()` and `set()` methods (getters and setters).
*Example:* In `Patient.java`, `private String name;` can only be modified via `public void setName(String name)`.

### Constructors
Constructors are used to properly initialize objects when they are created, ensuring they start in a valid state.
*Example:* `VitalSigns` requires all physiological parameters to be passed in during instantiation.

### Polymorphism & Inheritance
Since this is a simple Core Java project, complex inheritance trees are avoided. However, the project utilizes polymorphism fundamentally through `List<Patient> patients = new ArrayList<Patient>();`, programming to the interface `List` while instantiating the concrete class `ArrayList`. Furthermore, `AssessmentThread` *extends* the base `Thread` class and overrides its `run()` method.

## 2. File Handling (Persistence)

Since no external database (like MySQL) is used, Core Java File I/O is used to persist data across application restarts.

- **`FileManager.java`**: Centralizes all file operations.
- **Reading**: Uses `FileReader` wrapped in a `BufferedReader` for efficient line-by-line reading.
- **Writing**: Uses `FileWriter` wrapped in a `BufferedWriter` to append or overwrite data.
- **File Structure**: Data is stored in plain text files (`patients.txt`, `assessments.txt`) using pipe-delimited format (e.g., `ID|Name|Age`). This makes parsing simple using `String.split("\\|")`.

## 3. Collections Framework

Collections are used extensively to manage multiple objects in memory after they are read from files.

- **`ArrayList`**: The primary collection used. When `PatientService.getAllPatients()` is called, it reads the text file, parses each line into a `Patient` object, and adds it to an `ArrayList<Patient>`.
- **`Map` / `HashMap`**: Used in `RiskCalculator.java` to return multiple pieces of data (score, level, recommendation) from a single method execution, and in `MediRiskServer` to parse simple JSON structures dynamically.

## 4. Exception Handling

Robust exception handling ensures the application doesn't crash from bad input or missing files.

- **Try-Catch-Finally**: Used extensively in `FileManager` to ensure files are always closed in the `finally` block, preventing memory leaks and file locks.
- **NumberFormatException**: Handled gracefully in model parsing (e.g., `Patient.fromFileString()`). If a user tampers with the text file and puts "abc" instead of an age, the system logs the error and skips the corrupted record instead of crashing.
- **IOException**: Handled for all file operations and network stream reading/writing.

## 5. Multi-Threading

The project demonstrates concurrency by executing the core algorithm off the main HTTP request thread.

- **`AssessmentThread.java`**: Extends the `Thread` class.
- **Execution**: When a new assessment is requested, `MediRiskServer` spawns an `AssessmentThread`. The thread performs the risk calculation and calls `FileManager` to save the result.
- **Synchronization**: `FileManager` methods are marked with the `synchronized` keyword. This ensures that if multiple threads try to write to `assessments.txt` simultaneously, they are queued, preventing data corruption. `Thread.join()` is used to wait for the result before returning the HTTP response.

## 6. Networking (Built-in HTTP Server)

The project creates a web server using entirely native Core Java APIs, without using external frameworks like Spring Boot.

- **`com.sun.net.httpserver.HttpServer`**: Binds to port 8080 and listens for incoming browser requests.
- **Context Routing**: Routes like `/api/patients` are mapped to custom `HttpHandler` implementations.
- **Static File Serving**: The server reads HTML/CSS/JS files from the `frontend/` directory using `FileInputStream` and streams them back to the browser.
