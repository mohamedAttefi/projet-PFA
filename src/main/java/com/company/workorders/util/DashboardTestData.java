package com.company.workorders.util;

import com.company.workorders.dao.InterventionDAO;
import com.company.workorders.model.Intervention;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

/**
 * Utility class to create sample intervention data for dashboard testing
 */
public class DashboardTestData {
    
    private static final String[] CLIENTS = {
        "BNP Paribas", "Société Générale", "Crédit Agricole", "LCL", "AXA Banque",
        "Mairie de Lille", "Cabinet Médical Artois", "Logisimmo", "Cabinet d'Avocats Dubois",
        "Ecole Primaire Jean Jaurès", "Centre Commercial Euralille", "Hôpital Claude Huriez"
    };
    
    private static final String[] TITLES = {
        "Panne serveur base de données", "Maintenance préventive parc informatique", 
        "Remplacement postes de travail", "Mise à jour logiciel de gestion",
        "Problème réseau connexion", "Installation nouveau système de sauvegarde",
        "Dépannage urgent imprimante", "Configuration VPN accès distant",
        "Mise à niveau système d'exploitation", "Réparation poste de travail",
        "Installation logiciel comptabilité", "Dépannage connexion internet"
    };
    
    private static final String[] PRIORITIES = {"Critique", "Haute", "Normale", "Basse"};
    private static final String[] STATUSES = {"Nouvelle", "Assignée", "En cours", "Terminée"};
    private static final String[] LOCATIONS = {
        "Siège social", "Direction", "Service comptabilité", "Secrétariat",
        "Service commercial", "Informatique", "Salle de réunion", "Accueil"
    };
    
    private static final Random random = new Random();
    
    /**
     * Create sample interventions for testing dashboard statistics
     */
    public static void createSampleInterventions() {
        System.out.println("[DashboardTestData] Creating sample interventions...");
        
        try {
            // Create 20 sample interventions with varied data
            for (int i = 0; i < 20; i++) {
                Intervention intervention = generateRandomIntervention(i + 1);
                
                // Insert into database
                long id = InterventionDAO.createIntervention(
                    intervention.getTitle(),
                    intervention.getDescription(),
                    intervention.getPriority(),
                    intervention.getStatus(),
                    intervention.getLocation(),
                    intervention.getClientId(),
                    intervention.getAssignedTo()
                );
                
                System.out.println("[DashboardTestData] Created intervention #" + id + ": " + intervention.getTitle());
            }
            
            System.out.println("[DashboardTestData] Sample interventions created successfully");
            
        } catch (Exception e) {
            System.err.println("[DashboardTestData] Error creating sample interventions: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Clear all interventions (for testing purposes)
     */
    public static void clearAllInterventions() {
        try {
            java.sql.Connection conn = com.company.workorders.util.DBConnection.getConnection();
            java.sql.PreparedStatement stmt = conn.prepareStatement("DELETE FROM interventions");
            stmt.executeUpdate();
            stmt.close();
            conn.close();
            System.out.println("[DashboardTestData] All interventions cleared");
        } catch (Exception e) {
            System.err.println("[DashboardTestData] Error clearing interventions: " + e.getMessage());
        }
    }
    
    /**
     * Generate a random intervention for testing
     */
    private static Intervention generateRandomIntervention(int id) {
        String title = TITLES[random.nextInt(TITLES.length)];
        String clientName = CLIENTS[random.nextInt(CLIENTS.length)];
        String priority = PRIORITIES[random.nextInt(PRIORITIES.length)];
        String status = STATUSES[random.nextInt(STATUSES.length)];
        String location = LOCATIONS[random.nextInt(LOCATIONS.length)];
        
        // Create description based on title
        String description = generateDescription(title, clientName);
        
        // Generate timestamps
        String createdAt = generateRandomTimestamp();
        
        // Random client and technician IDs (1-5 for testing)
        long clientId = random.nextInt(5) + 1;
        long assignedTo = random.nextInt(5) + 1;
        
        return new Intervention(
            id, title, description, priority, status, location,
            clientId, assignedTo, clientName, "Technicien " + assignedTo, createdAt
        );
    }
    
    /**
     * Generate a description based on the title and client
     */
    private static String generateDescription(String title, String clientName) {
        StringBuilder desc = new StringBuilder();
        desc.append("Intervention pour ").append(clientName).append(": ");
        
        if (title.toLowerCase().contains("serveur")) {
            desc.append("Le serveur principal présente des symptômes de défaillance. ");
            desc.append("Diagnostic nécessaire pour identifier la cause exacte et prévenir la perte de données.");
        } else if (title.toLowerCase().contains("maintenance")) {
            desc.append("Maintenance préventive planifiée pour garantir le bon fonctionnement des équipements. ");
            desc.append("Vérification complète des systèmes et mise à jour des logiciels.");
        } else if (title.toLowerCase().contains("remplacement")) {
            desc.append("Les postes de travail actuels sont obsolètes ou défectueux. ");
            desc.append("Installation de nouveaux équipements et migration des données.");
        } else if (title.toLowerCase().contains("mise à jour")) {
            desc.append("Mise à jour nécessaire pour bénéficier des dernières fonctionnalités et corrections de sécurité. ");
            desc.append("Sauvegarde préalable des données importante.");
        } else {
            desc.append("Intervention requise pour résoudre le problème signalé. ");
            desc.append("Analyse complète et solution adaptée prévues.");
        }
        
        return desc.toString();
    }
    
    /**
     * Generate a random timestamp within the last 30 days
     */
    private static String generateRandomTimestamp() {
        LocalDateTime now = LocalDateTime.now();
        int daysAgo = random.nextInt(30);
        LocalDateTime randomDate = now.minusDays(daysAgo)
            .minusHours(random.nextInt(24))
            .minusMinutes(random.nextInt(60));
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return randomDate.format(formatter);
    }
    
    /**
     * Check if interventions table has data
     */
    public static boolean hasInterventions() {
        try {
            java.sql.Connection conn = com.company.workorders.util.DBConnection.getConnection();
            java.sql.Statement stmt = conn.createStatement();
            java.sql.ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as count FROM interventions");
            
            boolean hasData = false;
            if (rs.next()) {
                hasData = rs.getInt("count") > 0;
            }
            
            rs.close();
            stmt.close();
            conn.close();
            
            return hasData;
            
        } catch (Exception e) {
            System.err.println("[DashboardTestData] Error checking interventions: " + e.getMessage());
            return false;
        }
    }
}
