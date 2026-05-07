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
    private String createdAt;

    public Intervention(long id, String title, String description, String priority, String status,
                       String location, long clientId, long assignedTo, String createdAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.status = status;
        this.location = location;
        this.clientId = clientId;
        this.assignedTo = assignedTo;
        this.createdAt = createdAt;
    }

    // Constructor with client and technician names
    public Intervention(long id, String title, String description, String priority, String status,
                       String location, long clientId, long assignedTo, String clientName, String technicianName, String createdAt) {
        this(id, title, description, priority, status, location, clientId, assignedTo,createdAt);
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
    public String getCreatedAt(){
        if (createdAt == null || createdAt.isBlank()) {
            return "N/A";
        }
        return createdAt;
    }

    public String getFormattedCreatedAt() {
        if (createdAt == null || createdAt.isBlank()) {
            return "N/A";
        }
        try {
            java.time.LocalDateTime dateTime = java.time.LocalDateTime.parse(createdAt, 
                java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            return dateTime.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        } catch (Exception e) {
            return createdAt;
        }
    }

    // Setters
    public void setId(long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setClientId(long clientId) {
        this.clientId = clientId;
    }

    public void setAssignedTo(long assignedTo) {
        this.assignedTo = assignedTo;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public void setTechnicianName(String technicianName) {
        this.technicianName = technicianName;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return title;
    }
}
