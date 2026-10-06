package com.medirisk.service;

import com.medirisk.model.VitalSigns;
import java.util.HashMap;
import java.util.Map;

/**
 * Calculates risk scores from vital sign readings.
 *
 * Weighted scoring:
 *   Age              15%
 *   Heart Rate       20%
 *   Systolic BP      20%
 *   Diastolic BP     15%
 *   SpO2             15%
 *   Temperature      15%
 *
 * Risk Levels:
 *   0-30   LOW
 *   31-60  MODERATE
 *   61-80  HIGH
 *   81-100 CRITICAL
 *
 * This is a rule-based academic scoring engine, NOT a medical diagnostic tool.
 */
public class RiskCalculator {

    // Weights for each vital sign component
    private static final double WEIGHT_AGE = 0.15;
    private static final double WEIGHT_HR = 0.20;
    private static final double WEIGHT_SYS_BP = 0.20;
    private static final double WEIGHT_DIA_BP = 0.15;
    private static final double WEIGHT_SPO2 = 0.15;
    private static final double WEIGHT_TEMP = 0.15;

    /**
     * Calculates the overall risk score (0-100) from age and vital signs.
     * Returns a Map with: score, level, recommendation, and individual component scores.
     */
    public Map<String, Object> calculateRisk(int age, VitalSigns vitals) {
        Map<String, Object> result = new HashMap<String, Object>();

        // Calculate individual component scores (each 0-100)
        int ageScore = calculateAgeScore(age);
        int hrScore = calculateHeartRateScore(vitals.getHeartRate());
        int sysScore = calculateSystolicBPScore(vitals.getSystolicBP());
        int diaScore = calculateDiastolicBPScore(vitals.getDiastolicBP());
        int spo2Score = calculateSpO2Score(vitals.getOxygenLevel());
        int tempScore = calculateTemperatureScore(vitals.getTemperature());

        // Weighted total
        double weightedScore = (ageScore * WEIGHT_AGE)
                + (hrScore * WEIGHT_HR)
                + (sysScore * WEIGHT_SYS_BP)
                + (diaScore * WEIGHT_DIA_BP)
                + (spo2Score * WEIGHT_SPO2)
                + (tempScore * WEIGHT_TEMP);

        // Clamp to 0-100
        int finalScore = (int) Math.round(weightedScore);
        if (finalScore < 0) finalScore = 0;
        if (finalScore > 100) finalScore = 100;

        String level = classifyRiskLevel(finalScore);
        String recommendation = generateRecommendation(level);

        // Build result map
        result.put("score", finalScore);
        result.put("level", level);
        result.put("recommendation", recommendation);
        result.put("ageScore", ageScore);
        result.put("heartRateScore", hrScore);
        result.put("systolicBPScore", sysScore);
        result.put("diastolicBPScore", diaScore);
        result.put("spo2Score", spo2Score);
        result.put("temperatureScore", tempScore);

        return result;
    }

    // --- Component Scoring Methods ---

    /**
     * Age risk: Higher risk for very young (<5) and elderly (>60) patients.
     */
    private int calculateAgeScore(int age) {
        if (age < 0) return 50;
        if (age <= 5) return 40;
        if (age <= 17) return 15;
        if (age <= 40) return 10;
        if (age <= 60) return 30;
        if (age <= 75) return 55;
        return 80;  // >75
    }

    /**
     * Heart Rate risk: Normal adult resting is 60-100 bpm.
     */
    private int calculateHeartRateScore(int hr) {
        if (hr <= 0) return 100;
        if (hr >= 60 && hr <= 100) return 10;     // Normal
        if (hr >= 50 && hr < 60) return 30;        // Mild bradycardia
        if (hr > 100 && hr <= 110) return 35;      // Mild tachycardia
        if (hr > 110 && hr <= 130) return 60;      // Moderate tachycardia
        if (hr > 130 && hr <= 150) return 80;      // Severe tachycardia
        if (hr > 150) return 95;                    // Dangerous tachycardia
        return 70;  // Severe bradycardia (<50)
    }

    /**
     * Systolic BP risk: Normal is 90-120 mmHg.
     */
    private int calculateSystolicBPScore(int sys) {
        if (sys <= 0) return 100;
        if (sys >= 90 && sys <= 120) return 10;     // Normal
        if (sys > 120 && sys <= 130) return 25;     // Elevated
        if (sys > 130 && sys <= 140) return 40;     // Stage 1 hypertension
        if (sys > 140 && sys <= 160) return 60;     // Stage 2 hypertension
        if (sys > 160 && sys <= 180) return 80;     // Severe
        if (sys > 180) return 95;                    // Crisis
        return 50;  // Hypotension (<90)
    }

    /**
     * Diastolic BP risk: Normal is 60-80 mmHg.
     */
    private int calculateDiastolicBPScore(int dia) {
        if (dia <= 0) return 100;
        if (dia >= 60 && dia <= 80) return 10;      // Normal
        if (dia > 80 && dia <= 90) return 30;       // Elevated
        if (dia > 90 && dia <= 100) return 50;      // Stage 1
        if (dia > 100 && dia <= 110) return 70;     // Stage 2
        if (dia > 110) return 90;                    // Crisis
        return 45;  // Hypotension (<60)
    }

    /**
     * SpO2 risk: Normal is 95-100%.
     */
    private int calculateSpO2Score(int spo2) {
        if (spo2 >= 95 && spo2 <= 100) return 5;    // Normal
        if (spo2 >= 90 && spo2 < 95) return 40;     // Mild hypoxemia
        if (spo2 >= 85 && spo2 < 90) return 70;     // Moderate hypoxemia
        if (spo2 >= 80 && spo2 < 85) return 85;     // Severe hypoxemia
        if (spo2 < 80) return 100;                    // Critical
        return 100;
    }

    /**
     * Temperature risk: Normal is 36.1-37.2°C.
     */
    private int calculateTemperatureScore(double temp) {
        if (temp >= 36.1 && temp <= 37.2) return 5;   // Normal
        if (temp > 37.2 && temp <= 37.8) return 25;    // Low-grade fever
        if (temp > 37.8 && temp <= 38.5) return 45;    // Moderate fever
        if (temp > 38.5 && temp <= 39.5) return 70;    // High fever
        if (temp > 39.5 && temp <= 41.0) return 90;    // Very high fever
        if (temp > 41.0) return 100;                    // Hyperthermia
        if (temp >= 35.0 && temp < 36.1) return 30;    // Mild hypothermia
        if (temp < 35.0) return 75;                     // Hypothermia
        return 50;
    }

    // --- Classification ---

    /**
     * Classifies a risk score into a risk level.
     */
    public String classifyRiskLevel(int score) {
        if (score <= 30) return "LOW";
        if (score <= 60) return "MODERATE";
        if (score <= 80) return "HIGH";
        return "CRITICAL";
    }

    /**
     * Generates a recommendation based on risk level.
     */
    public String generateRecommendation(String level) {
        if ("LOW".equals(level)) {
            return "Continue regular monitoring. Vital signs are within acceptable ranges.";
        } else if ("MODERATE".equals(level)) {
            return "Monitor vital signs closely and consider scheduling a medical consultation.";
        } else if ("HIGH".equals(level)) {
            return "Medical evaluation is strongly recommended. Multiple vital signs are outside normal ranges.";
        } else if ("CRITICAL".equals(level)) {
            return "Immediate medical attention is recommended. Vital signs indicate a potentially dangerous condition.";
        }
        return "Unable to generate recommendation.";
    }
}
