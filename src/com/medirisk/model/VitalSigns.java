package com.medirisk.model;

/**
 * Represents a set of vital sign readings for a patient.
 * This is a value object used during risk assessment.
 * Demonstrates Encapsulation and input validation.
 */
public class VitalSigns {

    private int heartRate;         // beats per minute
    private int systolicBP;        // mmHg
    private int diastolicBP;       // mmHg
    private int oxygenLevel;       // SpO2 percentage (0-100)
    private double temperature;    // degrees Celsius
    private int respiratoryRate;   // breaths per minute

    // --- Constructors ---

    public VitalSigns() {
    }

    public VitalSigns(int heartRate, int systolicBP, int diastolicBP,
                      int oxygenLevel, double temperature, int respiratoryRate) {
        this.heartRate = heartRate;
        this.systolicBP = systolicBP;
        this.diastolicBP = diastolicBP;
        this.oxygenLevel = oxygenLevel;
        this.temperature = temperature;
        this.respiratoryRate = respiratoryRate;
    }

    // --- Getters and Setters ---

    public int getHeartRate() {
        return heartRate;
    }

    public void setHeartRate(int heartRate) {
        this.heartRate = heartRate;
    }

    public int getSystolicBP() {
        return systolicBP;
    }

    public void setSystolicBP(int systolicBP) {
        this.systolicBP = systolicBP;
    }

    public int getDiastolicBP() {
        return diastolicBP;
    }

    public void setDiastolicBP(int diastolicBP) {
        this.diastolicBP = diastolicBP;
    }

    public int getOxygenLevel() {
        return oxygenLevel;
    }

    public void setOxygenLevel(int oxygenLevel) {
        this.oxygenLevel = oxygenLevel;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public int getRespiratoryRate() {
        return respiratoryRate;
    }

    public void setRespiratoryRate(int respiratoryRate) {
        this.respiratoryRate = respiratoryRate;
    }

    /**
     * Estimates R-R interval from heart rate: 60000 / HR (in milliseconds).
     * This is a mathematical estimation, NOT a real ECG measurement.
     */
    public double getEstimatedRRInterval() {
        if (heartRate <= 0) return 0;
        return 60000.0 / heartRate;
    }

    @Override
    public String toString() {
        return "VitalSigns{HR=" + heartRate + ", BP=" + systolicBP + "/" + diastolicBP
                + ", SpO2=" + oxygenLevel + "%, Temp=" + temperature + "°C, RR=" + respiratoryRate + "}";
    }
}
