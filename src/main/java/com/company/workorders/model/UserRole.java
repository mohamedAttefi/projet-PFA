package com.company.workorders.model;

/**
 * Application roles supported by the login flow.
 */
public enum UserRole {
    RECEPTIONIST,
    TECHNICIAN,
    ADMINISTRATOR;

    public static UserRole fromDatabaseValue(String value) {
        if (value == null || value.isBlank()) {
            return RECEPTIONIST;
        }

        return switch (value.trim().toUpperCase()) {
            case "RECEPTIONIST", "RECEPTIONNISTE", "RECEPTEUR" -> RECEPTIONIST;
            case "TECHNICIAN", "TECHNICIEN" -> TECHNICIAN;
            case "ADMINISTRATOR", "ADMIN", "RESPONSABLE" -> ADMINISTRATOR;
            default -> RECEPTIONIST;
        };
    }
}