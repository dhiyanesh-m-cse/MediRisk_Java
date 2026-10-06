# MediRisk AI - Viva Preparation Notes

This document contains simple, direct answers to common questions you might be asked during your academic project presentation (viva).

### 1. What is MediRisk?
MediRisk is a Java-based Patient Health Risk Assessment prototype. It takes a patient's vital signs (like heart rate, blood pressure, oxygen), calculates a risk score using a weighted algorithm in Core Java, and provides a risk level (Low, Moderate, High, Critical). 

### 2. Why did you use Java?
I used Core Java to demonstrate fundamental programming concepts like Object-Oriented Programming (OOP), file handling, exception handling, and multithreading, which are part of our core academic syllabus.

### 3. Why did you not use MySQL or a database?
To strictly demonstrate Core Java File I/O capabilities. Using simple `.txt` files proves that I understand how to read, write, parse, and manipulate data streams manually without relying on external database abstraction layers.

### 4. How are files acting as a database?
I used pipe-delimited strings (`|`). Each line in the text file represents one object. For example, in `patients.txt`, a line looks like `P001|John|45|Male|+123`. My `FileManager` class uses `BufferedReader` to read the lines, `String.split()` to break them apart, and then instantiates `Patient` objects.

### 5. What is OOP and where is it used?
Object-Oriented Programming is a paradigm based on "objects" that contain data (fields) and code (methods). 
- **Example:** I created classes like `Patient`, `User`, and `Assessment` to model real-world entities.

### 6. Where did you use Encapsulation?
In all my model classes (`Patient`, `User`, `VitalSigns`). The data fields (like `age`, `name`) are declared `private` so they cannot be changed directly from outside. They are only modified using `public` getters and setters (e.g., `setAge()`).

### 7. Where did you use ArrayList / Collections?
When the application reads data from the text files, it doesn't just print it. It parses each line into a Java object and stores those objects in an `ArrayList` (e.g., `ArrayList<Patient>`). This allows me to easily search, iterate, and filter the data in memory.

### 8. Where did you use Exception Handling?
Mainly in `FileManager.java` and when parsing strings to numbers.
- I used `try-catch-finally` blocks when opening files. If a `FileNotFoundException` occurs, I catch it and create a new file instead of crashing.
- I use the `finally` block to ensure `reader.close()` is always called to prevent memory leaks.
- I catch `NumberFormatException` so that if a text file is manually corrupted (e.g., letters instead of numbers for Age), the app just skips that record instead of shutting down.

### 9. Where did you use Threads and why?
I used a thread (`AssessmentThread.java`) for the actual risk calculation. 
- **Why?** To demonstrate concurrency. When the HTTP server receives a request to assess a patient, it spawns a background thread to do the heavy math and file writing, ensuring the main server thread isn't blocked.

### 10. What is the role of FileManager?
It is the only class in the project that talks to the hard drive. It handles all `FileReader` and `FileWriter` logic. It uses the `synchronized` keyword to ensure that if two threads try to write data at the exact same time, they don't corrupt the file.

### 11. What is the role of RiskCalculator?
It holds the business logic. It takes the `VitalSigns` object, applies predefined percentage weights to each vital sign (e.g., Heart Rate is 20%, Age is 15%), and calculates a final score out of 100. It is a pure algorithmic class.

### 12. How does the frontend communicate with Java?
I built a custom HTTP Server using Java's built-in `com.sun.net.httpserver`. The JavaScript frontend uses the `fetch()` API to send JSON data to `localhost:8080`. The Java server receives the JSON, parses it, processes the logic, and sends a JSON response back.

### 13. Why is the project not an ML project?
This is a Core Java academic project. While Machine Learning (like Random Forest) could be used to predict risk, implementing it here would require Python dependencies and detract from demonstrating fundamental Java concepts (OOP, File I/O). ML is documented as a "Future Scope" enhancement.

### 14. What are the limitations?
- **Security:** Passwords are saved in plain text, which is bad for production but acceptable for a basic prototype.
- **Concurrency Scale:** File-based databases struggle if thousands of users write simultaneously.
- **Medical Accuracy:** The scoring algorithm is rule-based and educational; it is not a clinically validated diagnostic tool.

### 15. What is the future scope?
- Migrating from file-based storage to a relational database like MySQL using JDBC.
- Encrypting user passwords.
- Integrating Python-based Machine Learning models to provide AI-driven risk predictions instead of static rule-based scoring.
