package com.medirisk.model;

/**
 * Represents a completed risk assessment result.
 * Contains patient info, vital signs, calculated risk score, classification, and recommendation.
 * File format: A001|P001|name|age|hr|sys|dia|spo2|temp|rr|score|level|recommendation|date|time
 */
public class Assessment {

    private String id;              // A001, A002, ...
    private String patientId;       // P001
    private String patientName;
    private int age;
    private int heartRate;
    private int systolicBP;
    private int diastolicBP;
    private int oxygenLevel;
    private double temperature;
    private int respiratoryRate;
    private int riskScore;          // 0-100
    private String riskLevel;       // LOW, MODERATE, HIGH, CRITICAL
    private String recommendation;
    private String date;            // yyyy-MM-dd
    private String time;            // HH:mm:ss

    // --- Constructors ---

    public Assessment() {
    }

    public Assessment(String id, String patientId, String patientName, int age,
                      int heartRate, int systolicBP, int diastolicBP,
                      int oxygenLevel, double temperature, int respiratoryRate,
                      int riskScore, String riskLevel, String recommendation,
                      String date, String time) {
        this.id = id;
        this.patientId = patientId;
        this.patientName = patientName;
        this.age = age;
        this.heartRate = heartRate;
        this.systolicBP = systolicBP;
        this.diastolicBP = diastolicBP;
        this.oxygenLevel = oxygenLevel;
        this.temperature = temperature;
        this.respiratoryRate = respiratoryRate;
        this.riskScore = riskScore;
        this.riskLevel = riskLevel;
        this.recommendation = recommendation;
        this.date = date;
        this.time = time;
    }

    // --- Getters and Setters ---

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }

    public String getPatientName() { return patientName; }
    public void setPatientName(String patientName) { this.patientName = patientName; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public int getHeartRate() { return heartRate; }
    public void setHeartRate(int heartRate) { this.heartRate = heartRate; }

    public int getSystolicBP() { return systolicBP; }
    public void setSystolicBP(int systolicBP) { this.systolicBP = systolicBP; }

    public int getDiastolicBP() { return diastolicBP; }
    public void setDiastolicBP(int diastolicBP) { this.diastolicBP = diastolicBP; }

    public int getOxygenLevel() { return oxygenLevel; }
    public void setOxygenLevel(int oxygenLevel) { this.oxygenLevel = oxygenLevel; }

    public double getTemperature() { return temperature; }
    public void setTemperature(double temperature) { this.temperature = temperature; }

    public int getRespiratoryRate() { return respiratoryRate; }
    public void setRespiratoryRate(int respiratoryRate) { this.respiratoryRate = respiratoryRate; }

    public int getRiskScore() { return riskScore; }
    public void setRiskScore(int riskScore) { this.riskScore = riskScore; }

    public String getRiskLevel() { return riskLevel; }
    public void setRiskLevel(String riskLevel) { this.riskLevel = riskLevel; }

    public String getRecommendation() { return recommendation; }
    public void setRecommendation(String recommendation) { this.recommendation = recommendation; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    /**
     * Converts the Assessment object into a pipe-delimited string for file storage.
     */
    public String toFileString() {
        return id + "|" + patientId + "|" + patientName + "|" + age + "|"
                + heartRate + "|" + systolicBP + "|" + diastolicBP + "|"
                + oxygenLevel + "|" + temperature + "|" + respiratoryRate + "|"
                + riskScore + "|" + riskLevel + "|" + recommendation + "|"
                + date + "|" + time;
    }

    /**
     * Constructs an Assessment object from a pipe-delimited string.
     * Returns null if the line is invalid or malformed.
     */
    public static Assessment fromFileString(String line) {
        if (line == null || line.trim().isEmpty()) {
            return null;
        }
        try {
            String[] p = line.split("\\|", -1);
            if (p.length < 15) {
                System.err.println("[Assessment] Skipping malformed record (fields=" + p.length + "): " + line);
                return null;
            }
            Assessment a = new Assessment();
            a.setId(p[0].trim());
            a.setPatientId(p[1].trim());
            a.setPatientName(p[2].trim());
            a.setAge(Integer.parseInt(p[3].trim()));
            a.setHeartRate(Integer.parseInt(p[4].trim()));
            a.setSystolicBP(Integer.parseInt(p[5].trim()));
            a.setDiastolicBP(Integer.parseInt(p[6].trim()));
            a.setOxygenLevel(Integer.parseInt(p[7].trim()));
            a.setTemperature(Double.parseDouble(p[8].trim()));
            a.setRespiratoryRate(Integer.parseInt(p[9].trim()));
            a.setRiskScore(Integer.parseInt(p[10].trim()));
            a.setRiskLevel(p[11].trim());
            a.setRecommendation(p[12].trim());
            a.setDate(p[13].trim());
            a.setTime(p[14].trim());
            return a;
        } catch (NumberFormatException e) {
            System.err.println("[Assessment] Invalid number in record: " + e.getMessage());
            return null;
        } catch (Exception e) {
            System.err.println("[Assessment] Error parsing record: " + e.getMessage());
            return null;
        }
    }

    @Override
    public String toString() {
        return "Assessment{id='" + id + "', patient='" + patientName
                + "', score=" + riskScore + ", level='" + riskLevel + "'}";
    }
}
