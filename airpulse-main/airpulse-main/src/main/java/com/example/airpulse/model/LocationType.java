package com.example.airpulse.model;

public enum LocationType {
    APARTMENT("00"),
    HOUSE("11"),
    HOSPITAL("22");

    private final String codePrefix;

    LocationType(String codePrefix) {
        this.codePrefix = codePrefix;
    }

    public String getCodePrefix() {
        return codePrefix;
    }
} 