package com.company.workorders.controller;

import com.company.workorders.dao.*;
import com.company.workorders.model.*;
import com.company.workorders.service.NotificationService;
import com.company.workorders.util.AppNavigator;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class DashboardController {

    @FXML private Label performanceLabel;
    @FXML private Label critiquesLabel;
    @FXML private Label chargeLabel;
    @FXML private Label slaLabel;
    @FXML private Label totalInterventionsLabel;
    @FXML private VBox recentInterventionsList;

    @FXML
    public void initialize() {
        // Start notification service for urgent interventions
        NotificationService.startNotificationService();
        
        // Create sample notifications for testing (only if no notifications exist)
        createSampleNotificationsIfNeeded();
        
        refreshDashboard();
    }
    
    /**
     * Create sample notifications if the database is empty
     */
    private void createSampleNotificationsIfNeeded() {
        try {
            // Check if notifications table exists and has data
            java.sql.Connection conn = com.company.workorders.util.DBConnection.getConnection();
            java.sql.Statement stmt = conn.createStatement();
            
            // Check if table exists
            java.sql.ResultSet rs = stmt.executeQuery(
                "SELECT COUNT(*) as count FROM information_schema.tables WHERE table_name = 'notifications'");
            
            if (rs.next() && rs.getInt("count") > 0) {
                // Check if table has data
                rs.close();
                rs = stmt.executeQuery("SELECT COUNT(*) as count FROM notifications");
                
                if (rs.next() && rs.getInt("count") == 0) {
                    // Table exists but is empty, create sample data
                    com.company.workorders.util.NotificationTestData.createSampleNotifications();
                }
            }
            
            rs.close();
            stmt.close();
            conn.close();
            
        } catch (Exception e) {
            System.err.println("[DashboardController] Error checking notifications: " + e.getMessage());
            // If there's an error, create sample data anyway
            com.company.workorders.util.NotificationTestData.createSampleNotifications();
        }
    }

    private void refreshDashboard() {
        try {
            // Update performance metrics with real data
            int totalInterventions = (int) DashboardDAO.countInterventions();
            int urgentInterventions = (int) DashboardDAO.countUrgentes();
            int inProgressInterventions = (int) DashboardDAO.countEnCours();
            int completedInterventions = (int) DashboardDAO.countTerminees();
            
            totalInterventionsLabel.setText(String.valueOf(totalInterventions));
            critiquesLabel.setText(String.format("%02d", urgentInterventions));
            
            // Calculate average workload per technician
            int totalTechnicians = getTotalTechnicians();
            double avgWorkload = totalTechnicians > 0 ? (double) inProgressInterventions / totalTechnicians : 0;
            chargeLabel.setText(String.format("%.1f", avgWorkload));
            
            // Calculate SLA compliance rate (placeholder calculation)
            double slaRate = totalInterventions > 0 ? ((double) completedInterventions / totalInterventions) * 100 : 0;
            slaLabel.setText(String.format("%.1f%%", slaRate));
            
            // Calculate performance score (placeholder)
            double performance = calculatePerformanceScore(urgentInterventions, inProgressInterventions, completedInterventions, totalInterventions);
            performanceLabel.setText(String.format("%.1f%%", performance));
            
            // Load recent interventions into the VBox
            loadRecentInterventions();
            
        } catch (Exception e) {
            System.err.println("Error refreshing dashboard: " + e.getMessage());
            // Fallback to sample data if database fails
            performanceLabel.setText("92.4%");
            critiquesLabel.setText("08");
            chargeLabel.setText("6.2");
            slaLabel.setText("98.1%");
            totalInterventionsLabel.setText("124");
        }
    }
    
    private int getTotalTechnicians() {
        try {
            java.util.List<Object[]> technicians = com.company.workorders.dao.InterventionDAO.getAllTechnicians();
            return technicians.size();
        } catch (Exception e) {
            return 5; // Fallback value
        }
    }
    
    private double calculatePerformanceScore(int urgent, int inProgress, int completed, int total) {
        if (total == 0) return 100.0;
        
        // Simple performance calculation based on completion rate and urgency handling
        double completionRate = (double) completed / total * 100;
        double urgencyPenalty = urgent > 0 ? (double) urgent / total * 20 : 0;
        
        return Math.max(0, Math.min(100, completionRate - urgencyPenalty));
    }
    
    private void loadRecentInterventions() {
        try {
            java.util.List<Intervention> recentInterventions = DashboardDAO.getRecentInterventions(5);
            
            // Clear existing content
            recentInterventionsList.getChildren().clear();
            
            // Add recent interventions to the VBox
            for (Intervention intervention : recentInterventions) {
                recentInterventionsList.getChildren().add(createInterventionRow(intervention));
            }
            
            // If no interventions, show placeholder
            if (recentInterventions.isEmpty()) {
                recentInterventionsList.getChildren().add(createPlaceholderRow());
            }
            
        } catch (Exception e) {
            System.err.println("Error loading recent interventions: " + e.getMessage());
            // Keep the sample data from FXML as fallback
        }
    }
    
    private javafx.scene.layout.HBox createInterventionRow(Intervention intervention) {
        javafx.scene.layout.HBox row = new javafx.scene.layout.HBox(8);
        row.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        row.setStyle("-fx-padding: 12 0;");
        
        // Alternate row background
        if (recentInterventionsList.getChildren().size() % 2 == 0) {
            row.setStyle("-fx-padding: 12 0; -fx-background-color: #faf7f6; -fx-background-radius: 6;");
        }
        
        // ID/Client column
        javafx.scene.layout.VBox idClientBox = new javafx.scene.layout.VBox(2);
        idClientBox.setPrefWidth(180);
        javafx.scene.control.Label idLabel = new javafx.scene.control.Label("#" + intervention.getId());
        idLabel.setStyle("-fx-font-size: 11; -fx-font-weight: bold; -fx-text-fill: #2d2d2d;");
        javafx.scene.control.Label clientLabel = new javafx.scene.control.Label(
            intervention.getClientName() != null ? intervention.getClientName() : "N/A");
        clientLabel.setStyle("-fx-font-size: 10; -fx-text-fill: #7d6d68;");
        idClientBox.getChildren().addAll(idLabel, clientLabel);
        
        // Title column
        javafx.scene.control.Label titleLabel = new javafx.scene.control.Label(intervention.getTitle());
        titleLabel.setStyle("-fx-font-size: 11; -fx-text-fill: #2d2d2d;");
        javafx.scene.layout.HBox.setHgrow(titleLabel, javafx.scene.layout.Priority.ALWAYS);
        
        // Priority column
        javafx.scene.control.Label priorityLabel = new javafx.scene.control.Label(intervention.getPriority());
        priorityLabel.setStyle("-fx-font-size: 11; -fx-font-weight: bold; -fx-pref-width: 100;");
        priorityLabel.setTextFill(getPriorityColor(intervention.getPriority()));
        
        // Technician column
        javafx.scene.control.Label techLabel = new javafx.scene.control.Label(
            intervention.getTechnicianName() != null ? intervention.getTechnicianName() : "Non assigné");
        techLabel.setStyle("-fx-font-size: 11; -fx-pref-width: 140;");
        
        // SLA column
        javafx.scene.control.Label slaLabel = new javafx.scene.control.Label("En cours");
        slaLabel.setStyle("-fx-font-size: 11; -fx-pref-width: 120;");
        
        // Status column
        javafx.scene.control.Label statusLabel = new javafx.scene.control.Label(intervention.getStatus());
        statusLabel.setStyle("-fx-font-size: 11; -fx-font-weight: bold; -fx-pref-width: 100;");
        statusLabel.setTextFill(getStatusColor(intervention.getStatus()));
        
        row.getChildren().addAll(idClientBox, titleLabel, priorityLabel, techLabel, slaLabel, statusLabel);
        return row;
    }
    
    private javafx.scene.layout.HBox createPlaceholderRow() {
        javafx.scene.layout.HBox row = new javafx.scene.layout.HBox();
        row.setAlignment(javafx.geometry.Pos.CENTER);
        row.setStyle("-fx-padding: 20 0;");
        
        javafx.scene.control.Label placeholder = new javafx.scene.control.Label("Aucune intervention récente");
        placeholder.setStyle("-fx-font-size: 12; -fx-text-fill: #7d6d68; -fx-font-style: italic;");
        
        row.getChildren().add(placeholder);
        return row;
    }
    
    private javafx.scene.paint.Color getPriorityColor(String priority) {
        switch (priority) {
            case "Critique": return javafx.scene.paint.Color.web("#c8102e");
            case "Haute": return javafx.scene.paint.Color.web("#f39c12");
            case "Normale": return javafx.scene.paint.Color.web("#27ae60");
            case "Basse": return javafx.scene.paint.Color.web("#3b5b93");
            default: return javafx.scene.paint.Color.web("#7d6d68");
        }
    }
    
    private javafx.scene.paint.Color getStatusColor(String status) {
        switch (status) {
            case "Nouvelle": return javafx.scene.paint.Color.web("#2a7cff");
            case "Assignée": return javafx.scene.paint.Color.web("#3b5b93");
            case "En cours": return javafx.scene.paint.Color.web("#f39c12");
            case "Terminée": return javafx.scene.paint.Color.web("#27ae60");
            case "Fermée": return javafx.scene.paint.Color.web("#8a7b76");
            default: return javafx.scene.paint.Color.web("#7d6d68");
        }
    }

    @FXML
    public void createNewIntervention() {
        try {
            AppNavigator.loadView("/views/intervention-detail-view.fxml");
        } catch (Exception e) {
            System.err.println("Error creating new intervention: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void viewAllInterventions() {
        try {
            AppNavigator.loadView("/views/interventions-view.fxml");
        } catch (Exception e) {
            System.err.println("Error viewing all interventions: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
