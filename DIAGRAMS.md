# MediRisk AI - System Diagrams

## 1. Simple Class Diagram

This shows the fundamental relationships between the Core Java classes.

```text
+-------------------+       +-----------------------+       +-------------------+
|  MediRiskServer   | ----> |   PatientService      | ----> |   FileManager     |
| (Handles HTTP)    |       | (Manages Patient CRUD)|       | (File DB Access)  |
+-------------------+       +-----------------------+       +-------------------+
          |                             |                             ^
          v                             v                             |
+-------------------+       +-----------------------+                 |
| AssessmentThread  |       |     Patient (Model)   |                 |
| (Background Task) |       +-----------------------+                 |
+-------------------+                                                 |
          |                                                           |
          |         +-----------------------+                         |
          +-------> |    RiskCalculator     |                         |
          |         | (Scoring Algorithm)   |                         |
          |         +-----------------------+                         |
          v                                                           |
+-------------------+       +-----------------------+                 |
| VitalSigns(Model) |       | AssessmentService     | ----------------+
| (Value Object)    |       | (Manages Assess CRUD) |
+-------------------+       +-----------------------+
                                        |
                                        v
                            +-----------------------+
                            |   Assessment (Model)  |
                            +-----------------------+
```

## 2. Data Flow Diagram (DFD)

This diagram outlines how a patient's vital signs are transmitted from the browser, processed, and stored as a final assessment.

```text
[ Browser (HTML/JS) ]
         |
         | 1. User submits Patient Vital Signs Form via fetch() POST
         v
[ MediRiskServer ]
         |
         | 2. Server parses JSON payload into Java strings/ints
         | 3. Server instantiates VitalSigns object
         | 4. Server spawns AssessmentThread
         v
[ AssessmentThread ]
         |
         | 5. Thread passes Patient Age and VitalSigns object to RiskCalculator
         v
[ RiskCalculator ]
         |
         | 6. Applies weights to Heart Rate, BP, SpO2, Temperature
         | 7. Returns final Risk Score, Level, and Recommendation
         v
[ AssessmentThread ]
         |
         | 8. Instantiates Assessment object combining patient data + risk result
         | 9. Passes Assessment object to AssessmentService
         v
[ AssessmentService ]
         |
         | 10. Calls FileManager to save the object
         v
[ FileManager ]
         |
         | 11. Synchronized write operation converting object to pipe-delimited string
         | 12. Appends string to data/assessments.txt
         v
[ data/assessments.txt ] (Persistent Storage)
         |
         | 13. File operation completes successfully
         v
[ MediRiskServer ]
         |
         | 14. Server formats Assessment object into JSON response
         v
[ Browser (HTML/JS) ]
         |
         | 15. UI updates dynamically to show the Patient's Risk Score and Level
```
