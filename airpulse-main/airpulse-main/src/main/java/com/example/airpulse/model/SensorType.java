package com.example.airpulse.model;

public enum SensorType {
    PM25("Particulate Matter 2.5 micrometers"),
    PM10("Particulate Matter 10 micrometers"),
    CO("Carbon Monoxide"),
    NO2("Nitrogen Dioxide"),
    SO2("Sulfur Dioxide"),
    O3("Ozone"),
    TEMPERATURE("Temperature"),
    HUMIDITY("Humidity");

    private final String description;

    SensorType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
} 