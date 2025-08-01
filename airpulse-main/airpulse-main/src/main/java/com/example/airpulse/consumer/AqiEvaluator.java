package com.example.airpulse.consumer;

import com.example.airpulse.consumer.model.AqiSeverity;
import com.example.airpulse.model.SensorType;
import org.springframework.stereotype.Component;

@Component
public class AqiEvaluator {

    // These are simplified thresholds for demonstration.
    // Real AQI calculations are more complex and pollutant-specific.
    public AqiSeverity evaluateAqi(SensorType sensorType, double value) {
        switch (sensorType) {
            case PM25: // µg/m³
                if (value <= 12.0) return AqiSeverity.GOOD;
                if (value <= 35.4) return AqiSeverity.MODERATE;
                if (value <= 55.4) return AqiSeverity.UNHEALTHY_FOR_SENSITIVE_GROUPS;
                if (value <= 150.4) return AqiSeverity.UNHEALTHY;
                if (value <= 250.4) return AqiSeverity.VERY_UNHEALTHY;
                return AqiSeverity.HAZARDOUS;
            case PM10: // µg/m³
                if (value <= 54) return AqiSeverity.GOOD;
                if (value <= 154) return AqiSeverity.MODERATE;
                if (value <= 254) return AqiSeverity.UNHEALTHY_FOR_SENSITIVE_GROUPS;
                if (value <= 354) return AqiSeverity.UNHEALTHY;
                if (value <= 424) return AqiSeverity.VERY_UNHEALTHY;
                return AqiSeverity.HAZARDOUS;
            case CO: // ppm
                if (value <= 4.4) return AqiSeverity.GOOD;
                if (value <= 9.4) return AqiSeverity.MODERATE;
                if (value <= 12.4) return AqiSeverity.UNHEALTHY_FOR_SENSITIVE_GROUPS;
                if (value <= 15.4) return AqiSeverity.UNHEALTHY;
                if (value <= 30.4) return AqiSeverity.VERY_UNHEALTHY;
                return AqiSeverity.HAZARDOUS;
            case NO2: // ppb - Using EPA standards for 1-hour NO2
                if (value <= 53) return AqiSeverity.GOOD;
                if (value <= 100) return AqiSeverity.MODERATE;
                if (value <= 360) return AqiSeverity.UNHEALTHY_FOR_SENSITIVE_GROUPS; // This band is wide for NO2
                if (value <= 649) return AqiSeverity.UNHEALTHY;
                if (value <= 1249) return AqiSeverity.VERY_UNHEALTHY;
                return AqiSeverity.HAZARDOUS;
            case SO2: // ppb - Using EPA standards for 1-hour SO2
                if (value <= 35) return AqiSeverity.GOOD;
                if (value <= 75) return AqiSeverity.MODERATE;
                if (value <= 185) return AqiSeverity.UNHEALTHY_FOR_SENSITIVE_GROUPS;
                if (value <= 304) return AqiSeverity.UNHEALTHY; // Only up to UNHEALTHY for SO2 in this example
                 // For VERY_UNHEALTHY and HAZARDOUS, specific exposure times and higher concentrations are typically considered.
                 // To keep it simple, we'll cap SO2 alerting at UNHEALTHY here.
                return AqiSeverity.UNHEALTHY;
            case O3: // ppb - Based on 8-hour ozone AQI
                if (value <= 54) return AqiSeverity.GOOD;
                if (value <= 70) return AqiSeverity.MODERATE;
                if (value <= 85) return AqiSeverity.UNHEALTHY_FOR_SENSITIVE_GROUPS;
                if (value <= 105) return AqiSeverity.UNHEALTHY;
                if (value <= 200) return AqiSeverity.VERY_UNHEALTHY;
                return AqiSeverity.HAZARDOUS;
            case TEMPERATURE: // Not typically part of AQI, but we can define pragmatic levels
            case HUMIDITY:    // Not typically part of AQI
            default:
                return AqiSeverity.GOOD; // Default to GOOD if not a primary pollutant or unhandled
        }
    }

    public boolean isAlertWorthy(AqiSeverity severity) {
        return severity.ordinal() >= AqiSeverity.UNHEALTHY.ordinal();
    }
} 