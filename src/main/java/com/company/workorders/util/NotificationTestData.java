package com.company.workorders.util;

import com.company.workorders.dao.NotificationDAO;
import com.company.workorders.service.NotificationService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utility class to create sample notification data for testing
 */
public class NotificationTestData {
    
    /**
     * Create sample notifications for testing purposes
     */
    public static void createSampleNotifications() {
        System.out.println("[NotificationTestData] Creating sample notifications...");
        
        // Create urgent intervention notification
        NotificationDAO.createNotification(null, 
            "ALERTE URGENTE: Panne critique au siège social de BNP Paribas - Serveur de base de données hors ligne");
        
        // Create status update notification
        NotificationDAO.createNotification(null, 
            "MISE À JOUR STATUT: Intervention en cours - Jean Dupont a commencé la maintenance sur le parc informatique de la Mairie de Lille");
        
        // Create new work order notification
        NotificationDAO.createNotification(null, 
            "NOUVEAU BON: Nouvelle demande d'intervention créée par Logisimmo - Remplacement de 3 postes de travail suite à surtension");
        
        // Create completion notification
        NotificationDAO.createNotification(null, 
            "TERMINÉE: Maintenance effectuée avec succès - Cabinet Médical Artois - Mise à jour logiciel de gestion de patientèle par Marc Lefebvre");
        
        // Create assignment notification
        NotificationDAO.createNotification(null, 
            "ASSIGNATION: Intervention urgente assignée - Data-center Lyon serveur Rack HPE ProLiant DL380 Gen10 assigné à Jean-Luc Moreau");
        
        // Create system notification
        NotificationService.createSystemNotification(
            "SYSTÈME: Sauvegarde automatique des données complétée avec succès");
        
        // Create some older notifications
        NotificationDAO.createNotification(null, 
            "INFORMATION: Rappel - Maintenance préventive planifiée pour le 15 Mai 2024");
        
        NotificationDAO.createNotification(null, 
            "MISE À JOUR: Catalogue de pièces détachées mis à jour avec 15 nouvelles références");
        
        System.out.println("[NotificationTestData] Sample notifications created successfully");
    }
    
    /**
     * Clear all notifications (for testing purposes)
     */
    public static void clearAllNotifications() {
        try {
            java.sql.Connection conn = com.company.workorders.util.DBConnection.getConnection();
            java.sql.PreparedStatement stmt = conn.prepareStatement("DELETE FROM notifications");
            stmt.executeUpdate();
            stmt.close();
            conn.close();
            System.out.println("[NotificationTestData] All notifications cleared");
        } catch (Exception e) {
            System.err.println("[NotificationTestData] Error clearing notifications: " + e.getMessage());
        }
    }
}
