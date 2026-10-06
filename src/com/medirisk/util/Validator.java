package com.medirisk.util;

/**
 * Utility class for validating patient vital signs and user input.
 * Provides clear error checking before creating objects or saving records.
 */
public class Validator {

    /**
     * Validates vital signs parameters.
     * Returns an error message string if invalid, or null if all inputs are valid.
     */
    public static String validateVitals(int age, int heartRate, int systolicBP, int diastolicBP, int oxygenLevel, double temperature) {
        if (age < 0 || age > 120) {
            return "Invalid Age: Age must be between 0 and 120 years.";
        }
        if (heartRate < 30 || heartRate > 250) {
            return "Invalid Heart Rate: Heart rate must be between 30 and 250 bpm.";
        }
        if (systolicBP < 40 || systolicBP > 260) {
            return "Invalid Systolic BP: Systolic pressure must be between 40 and 260 mmHg.";
        }
        if (diastolicBP < 20 || diastolicBP > 180) {
            return "Invalid Diastolic BP: Diastolic pressure must be between 20 and 180 mmHg.";
        }
        if (diastolicBP >= systolicBP) {
            return "Invalid Blood Pressure: Diastolic BP cannot be equal to or higher than Systolic BP.";
        }
        if (oxygenLevel < 50 || oxygenLevel > 100) {
            return "Invalid Oxygen Level (SpO2): Oxygen saturation must be between 50% and 100%.";
        }
        if (temperature < 30.0 || temperature > 45.0) {
            return "Invalid Temperature: Body temperature must be between 30.0°C and 45.0°C.";
        }
        return null; // Valid
    }

    /**
     * Checks if a string is null or empty.
     */
    public static boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
}
