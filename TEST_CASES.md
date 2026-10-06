# MediRisk AI - Test Cases & Sample Data

## Sample Assessment Scenarios

Use these inputs to demonstrate the Risk Calculator engine during the viva.

### Scenario 1: LOW RISK (Healthy Patient)
- **Age:** 30
- **Heart Rate:** 72 bpm
- **Systolic BP:** 118 mmHg
- **Diastolic BP:** 78 mmHg
- **SpO2:** 98%
- **Temperature:** 36.6 °C
- **Expected Outcome:** Score < 30. Level: **LOW**.
- **Explanation:** All vitals are within normal adult ranges.

### Scenario 2: MODERATE RISK (Mild Fever & Elevated HR)
- **Age:** 45
- **Heart Rate:** 108 bpm
- **Systolic BP:** 125 mmHg
- **Diastolic BP:** 82 mmHg
- **SpO2:** 96%
- **Temperature:** 38.2 °C
- **Expected Outcome:** Score 31-60. Level: **MODERATE**.
- **Explanation:** Elevated temperature and slight tachycardia trigger a moderate risk flag.

### Scenario 3: HIGH RISK (Hypertension & Hypoxemia)
- **Age:** 68
- **Heart Rate:** 90 bpm
- **Systolic BP:** 168 mmHg
- **Diastolic BP:** 102 mmHg
- **SpO2:** 88%
- **Temperature:** 37.1 °C
- **Expected Outcome:** Score 61-80. Level: **HIGH**.
- **Explanation:** Age factor + Stage 2 Hypertension + low oxygen saturation.

### Scenario 4: CRITICAL RISK (Emergency)
- **Age:** 76
- **Heart Rate:** 145 bpm
- **Systolic BP:** 185 mmHg
- **Diastolic BP:** 115 mmHg
- **SpO2:** 82%
- **Temperature:** 39.5 °C
- **Expected Outcome:** Score > 80. Level: **CRITICAL**.
- **Explanation:** High age factor combined with extreme hypertension, tachycardia, severe hypoxemia, and high fever.

---

## Application Test Cases

| ID | Test Case | Action | Expected Result | Status |
|----|-----------|--------|-----------------|--------|
| TC01 | Login Valid | Login with `dhiyanesh` / `password` | Redirects to Dashboard | Pass |
| TC02 | Login Invalid | Login with `wrong` / `wrong` | Error toast "Invalid credentials" displayed | Pass |
| TC03 | Patient Create | Add a new patient via Patients page | Patient appears in the list immediately | Pass |
| TC04 | Patient Read | Open Patients page | All patients from `patients.txt` are listed | Pass |
| TC05 | Patient Update | Edit an existing patient | Patient details are updated in the file | Pass |
| TC06 | Patient Delete | Click delete on a patient | Patient is removed from the file | Pass |
| TC07 | Patient Search | Type a name in the search bar | List filters to matching patients | Pass |
| TC08 | Assessment Valid | Submit complete vital signs form | Assessment calculates successfully and redirects | Pass |
| TC09 | Assessment Invalid | Submit form with negative Age | Error toast "Age must be between 0 and 120" | Pass |
| TC10 | Assessment History | View History page | All past assessments are displayed | Pass |
| TC11 | Assessment Delete | Click delete on a history record | Record is deleted from `assessments.txt` | Pass |
| TC12 | Server Restart Persistence | Create patient, restart Java server | Created patient still exists | Pass |
| TC13 | Missing File Handling | Delete `data/patients.txt`, restart | System auto-creates the file and runs normally | Pass |
| TC14 | Invalid Record Handling | Add `P001\|BadData` to txt file manually | Corrupt record is skipped; app doesn't crash | Pass |
| TC15 | Thread Execution | Submit an assessment | Console logs show `AssessmentWorkerThread` running | Pass |
