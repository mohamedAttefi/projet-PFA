package com.company.workorders.service;

import com.company.workorders.dao.InterventionDAO;
import com.company.workorders.model.Intervention;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Service for handling notifications for urgent interventions
 */
public class NotificationService {
    
    private static Timer notificationTimer;
    private static final AtomicBoolean isRunning = new AtomicBoolean(false);
    private static final long CHECK_INTERVAL = 60000; // Check every minute
    
    /**
     * Start the notification service
     */
    public static void startNotificationService() {
        if (isRunning.get()) {
            return;
        }
        
        isRunning.set(true);
        notificationTimer = new Timer("NotificationTimer", true);
        
        notificationTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                checkForUrgentInterventions();
            }
        }, 0, CHECK_INTERVAL);
        
        System.out.println("[NotificationService] Started - checking for urgent interventions every " + (CHECK_INTERVAL/1000) + " seconds");
    }
    
    /**
     * Stop the notification service
     */
    public static void stopNotificationService() {
        if (notificationTimer != null) {
            notificationTimer.cancel();
            notificationTimer = null;
        }
        isRunning.set(false);
        System.out.println("[NotificationService] Stopped");
    }
    
    /**
     * Check for urgent interventions and show notifications
     */
    private static void checkForUrgentInterventions() {
        try {
            // Get urgent interventions (Urgente and Critique priorities)
            List<Intervention> urgentInterventions = InterventionDAO.searchInterventions(
                "", // No search term
                "Nouvelle,Assignée,En cours", // Only active statuses
                "Urgente,Critique", // Urgent priorities
                null, // All technicians
                PermissionService.canViewOnlyAssignedInterventions(),
                PermissionService.getCurrentUserId()
            );
            
            if (!urgentInterventions.isEmpty()) {
                Platform.runLater(() -> showUrgentInterventionAlert(urgentInterventions));
            }
            
        } catch (Exception e) {
            System.err.println("[NotificationService] Error checking urgent interventions: " + e.getMessage());
        }
    }
    
    /**
     * Show alert for urgent interventions
     */
    private static void showUrgentInterventionAlert(List<Intervention> urgentInterventions) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("⚠️ Interventions Urgentes");
        alert.setHeaderText("Vous avez " + urgentInterventions.size() + " intervention(s) urgente(s) en attente");
        
        StringBuilder content = new StringBuilder();
        content.append("Les interventions suivantes nécessitent une attention immédiate:\n\n");
        
        for (int i = 0; i < Math.min(5, urgentInterventions.size()); i++) {
            Intervention intervention = urgentInterventions.get(i);
            content.append("• ").append(intervention.getTitle())
                   .append(" (").append(intervention.getPriority()).append(")")
                   .append(" - ").append(intervention.getClientName())
                   .append("\n");
        }
        
        if (urgentInterventions.size() > 5) {
            content.append("... et ").append(urgentInterventions.size() - 5).append(" autre(s)");
        }
        
        alert.setContentText(content.toString());
        
        // Add custom buttons
        ButtonType viewButton = new ButtonType("Voir les interventions");
        ButtonType dismissButton = new ButtonType("Ignorer");
        
        alert.getButtonTypes().setAll(viewButton, dismissButton);
        
        alert.showAndWait().ifPresent(buttonType -> {
            if (buttonType == viewButton) {
                // Navigate to interventions view
                try {
                    com.company.workorders.util.AppNavigator.loadView("/views/interventions-view.fxml");
                } catch (Exception e) {
                    System.err.println("[NotificationService] Error navigating to interventions: " + e.getMessage());
                }
            }
        });
    }
    
    /**
     * Check if notification service is running
     */
    public static boolean isRunning() {
        return isRunning.get();
    }
    
    /**
     * Manually trigger a notification check
     */
    public static void checkNow() {
        if (!isRunning.get()) {
            checkForUrgentInterventions();
        }
    }
}
