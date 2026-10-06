# MediRisk AI — Java-Based Patient Health Risk Assessment and Monitoring System

MediRisk AI is an academic **Patient Health Risk Assessment and Monitoring System** built using **Core Java** and a **local file-based persistence layer**. It provides physiological risk scoring, multi-threaded assessment processing, and a built-in Core Java HTTP API server to interact with modern web frontends.

---

## 📌 Project Objective

The goal of MediRisk AI is to demonstrate foundational Computer Science & Engineering (CSE) concepts—specifically Object-Oriented Programming (OOP), Java File I/O, Collections, Multi-Threading, Exception Handling, and HTTP Networking—in a clean, beginner-friendly architecture suitable for college practical exams and viva presentations.

---

## 🛠️ Technology Stack

* **Language**: Java (JDK 8+ / OpenJDK 21 / OpenJDK 26)
* **Backend Architecture**: Core Java (Standard JDK only — **No Spring Boot, No Hibernate, No External Database**)
* **HTTP Server**: `com.sun.net.httpserver.HttpServer` (Built-in JDK server listening on Port 8080)
* **Persistence Layer**: Local Delimiter-Separated Text Files (`data/*.txt`) using `BufferedReader` and `BufferedWriter`
* **Concurrency**: Multi-threading via standard Java `Runnable` and `Thread`
* **Frontend**: HTML5, CSS3, Vanilla JavaScript (Fetch API)

---

## 🏗️ Architecture & Data Flow

```
+---------------------------------------------------------------------------------+
|                       FRONTEND LAYER (HTML5 / CSS3 / Vanilla JS)                |
|  - Login & Dashboard Interface                                                  |
|  - Vitals Assessment Form                                                       |
|  - Health History & File Records                                                |
+---------------------------------------------------------------------------------+
                                        |
                                        v (HTTP / JSON over Port 8080)
+---------------------------------------------------------------------------------+
|                       CORE JAVA BACKEND (com.medirisk)                          |
|                                                                                 |
|  [Server Layer]                                                                 |
|   ├── Main.java (Application Entry Point & Diagnostic Self-Test)                |
|   └── server/MediRiskServer.java (HttpServer on port 8080 & CORS Router)        |
|                                                                                 |
|  [Service & Core Logic Layer]                                                   |
|   ├── service/UserService.java (Authentication & Account Management)           |
|   ├── service/PatientService.java (Patient Profile CRUD)                        |
|   ├── service/AssessmentService.java (Risk Engine Coordinator)                  |
|   ├── service/ReportService.java (Health Record Metadata Management)            |
|   └── service/RiskCalculator.java (Weighted Physiological Risk Engine)          |
|                                                                                 |
|  [Concurrency Layer]                                                            |
|   └── threads/AssessmentThread.java (Background Thread execution)               |
|                                                                                 |
|  [Model Layer]                                                                  |
|   ├── model/User.java                                                           |
|   ├── model/Patient.java                                                        |
|   ├── model/VitalSigns.java                                                     |
|   ├── model/Assessment.java                                                     |
|   └── model/HealthRecord.java                                                   |
|                                                                                 |
|  [Data Storage Layer]                                                           |
|   └── storage/FileManager.java (Reads/Writes text database files)               |
+---------------------------------------------------------------------------------+
                                        |
                                        v (Core Java File I/O)
+---------------------------------------------------------------------------------+
|                         LOCAL FILE PERSISTENCE (data/)                          |
|  - data/users.txt           (User credentials & profiles)                       |
|  - data/patients.txt        (Patient record index)                              |
|  - data/assessments.txt     (Assessment history & vitals data)                  |
|  - data/health_records.txt  (Uploaded health document metadata)                 |
+---------------------------------------------------------------------------------+
```

---

## 📂 Folder Structure

```
MediRisk/
│
├── src/                          # Core Java Backend Source Code
│   └── com/
│       └── medirisk/
│           ├── Main.java         # Application Entry Point
│           ├── model/            # OOP Domain Models
│           │   ├── User.java
│           │   ├── Patient.java
│           │   ├── VitalSigns.java
│           │   ├── Assessment.java
│           │   └── HealthRecord.java
│           ├── service/          # Business Logic Services
│           │   ├── UserService.java
│           │   ├── PatientService.java
│           │   ├── AssessmentService.java
│           │   ├── ReportService.java
│           │   └── RiskCalculator.java
│           ├── storage/          # File Database Persistence
│           │   └── FileManager.java
│           ├── threads/          # Multi-Threading Components
│           │   └── AssessmentThread.java
│           ├── server/           # Built-in HTTP API Server
│           │   └── MediRiskServer.java
│           └── util/             # Utility Classes
│               ├── DateUtil.java
│               └── Validator.java
│
├── data/                         # Local Text File Database
│   ├── users.txt
│   ├── patients.txt
│   ├── assessments.txt
│   └── health_records.txt
│
└── README.md                     # Project Documentation
```

---

## 🎓 Java Concepts Demonstrated (Viva Guide)

1. **Object-Oriented Programming (OOP)**:
   - **Encapsulation**: Private fields, constructors, public getters/setters, data validation.
   - **Domain Modeling**: `User`, `Patient`, `VitalSigns`, `Assessment`, `HealthRecord`.
2. **File Handling & Persistence**:
   - Uses `File`, `FileReader`, `FileWriter`, `BufferedReader`, and `BufferedWriter` to maintain data persistence without SQL database servers.
3. **Multi-Threading**:
   - `AssessmentThread implements Runnable`: Performs asynchronous risk score computation and file logging in a background worker thread.
4. **Collections Framework**:
   - Uses `List` and `ArrayList` for dynamic record searching, filtering, and data manipulation.
5. **Exception Handling**:
   - `try-catch-finally` blocks to handle file I/O exceptions (`IOException`), bad number inputs (`NumberFormatException`), and thread interruptions.
6. **Thread Synchronization**:
   - `synchronized` methods in `FileManager.java` prevent file corruption during concurrent read/write operations.
7. **HTTP Networking**:
   - Built-in `com.sun.net.httpserver.HttpServer` listening on Port 8080, handling REST API requests with JSON parsing and CORS support.

---

## 🧮 Physiological Risk Calculator Engine

The risk calculator (`RiskCalculator.java`) evaluates 6 core physiological parameters using weighted scoring:

| Vital Sign Parameter | Weight | Sub-score Scale |
| :--- | :--- | :--- |
| **Age** | 15% | 5 (Young) – 100 (Elderly $\ge 85$) |
| **Heart Rate (bpm)** | 20% | 10 (Normal 60-100) – 95 (Extreme Tachycardia/Bradycardia) |
| **Systolic BP (mmHg)** | 20% | 5 (Normal 90-120) – 95 (Severe Hypertension $>180$) |
| **Diastolic BP (mmHg)** | 15% | 5 (Normal 60-80) – 90 (Hypertension $>110$) |
| **Oxygen Saturation ($SpO_2$)** | 15% | 5 ($\ge 95\%$) – 95 (Hypoxia $<80\%$) |
| **Body Temperature (°C)** | 15% | 5 (Normal 36.1-37.2°C) – 95 (High Fever $>40°C$) |

### Risk Level Classification:
* **0 – 30**: **LOW RISK** ("Continue regular monitoring.")
* **31 – 60**: **MODERATE RISK** ("Monitor vital signs and consider medical consultation.")
* **61 – 80**: **HIGH RISK** ("Medical evaluation is recommended.")
* **81 – 100**: **CRITICAL RISK** ("Immediate medical attention is recommended.")

---

## 🗄️ File Database Format (`data/*.txt`)

Delimiter: Pipe character (`|`)

### `users.txt`
```
userID|username|password|fullName
U001|dhiyanesh|password|Dhiyanesh M
```

### `patients.txt`
```
patientID|name|age|gender|contact
P001|Dhiyanesh M|21|Male|+91-9876543210
```

### `assessments.txt`
```
assessmentID|patientID|patientName|age|heartRate|systolicBP|diastolicBP|oxygenLevel|temperature|respiratoryRate|riskScore|riskLevel|recommendation|date|time
A001|P001|Dhiyanesh M|21|72|118|78|98|36.6|16|10|LOW|Continue regular monitoring.|2026-10-06|10:15:00
```

---

## ⚡ How to Compile and Run

### 1. Compile Java Backend
From the root directory of the project, run:
```bash
javac -d bin src/com/medirisk/util/*.java src/com/medirisk/model/*.java src/com/medirisk/storage/*.java src/com/medirisk/service/*.java src/com/medirisk/threads/*.java src/com/medirisk/server/*.java src/com/medirisk/Main.java
```

### 2. Run Java Backend
```bash
java -cp bin com.medirisk.Main
```

### 3. Verify HTTP API Server
Open browser or terminal and test:
```http
GET http://localhost:8080/api/status
```

---

## 📡 Sample HTTP Endpoints

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/api/status` | Server status check |
| `POST` | `/api/login` | User login (`{"username":"dhiyanesh","password":"password"}`) |
| `POST` | `/api/register` | User registration |
| `GET` | `/api/patients` | Retrieve list of patients |
| `POST` | `/api/patients` | Create patient profile |
| `GET` | `/api/assessments` | Retrieve assessment history |
| `POST` | `/api/assessments` | Submit vitals & calculate risk |
| `DELETE` | `/api/assessments?id=A001` | Delete an assessment record |

---

## ⚠️ Academic Disclaimer

MediRisk AI is an **educational academic prototype** designed for Computer Science coursework and viva demonstration. It does NOT provide certified clinical diagnosis, treatment advice, or replace medical professionals.
