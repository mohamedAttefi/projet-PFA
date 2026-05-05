package com.company.workorders.model;

/**
 * Intervention domain model
 */
public class Intervention {
    private long id;
    private String title;
    private String description;
    private String priority; // "Haute", "Normale", "Basse"
    private String status;   // "Nouvelle", "En cours", "Terminée", "Fermée"
    private String location;
    private long clientId;
    private long assignedTo; // User ID
    private String clientName;
    private String technicianName;

    public Intervention(long id, String title, String description, String priority, String status,
                       String location, long clientId, long assignedTo) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.status = status;
        this.location = location;
        this.clientId = clientId;
        this.assignedTo = assignedTo;
    }

    // Constructor with client and technician names
    public Intervention(long id, String title, String description, String priority, String status,
                       String location, long clientId, long assignedTo, String clientName, String technicianName) {
        this(id, title, description, priority, status, location, clientId, assignedTo);
        this.clientName = clientName;
        this.technicianName = technicianName;
    }

    // Getters
    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getPriority() {
        return priority;
    }

    public String getStatus() {
        return status;
    }

    public String getLocation() {
        return location;
    }

    public long getClientId() {
        return clientId;
    }

    public long getAssignedTo() {
        return assignedTo;
    }

    public String getClientName() {
        return clientName;
    }

    public String getTechnicianName() {
        return technicianName;
    }

    @Override
    public String toString() {
        return title;
    }
}
