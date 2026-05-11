package com.company.workorders.dao;

import com.company.workorders.model.Intervention;
import com.company.workorders.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DashboardDAO {

    public static long countInterventions() {
        return singleCount("SELECT COUNT(*) FROM interventions");
    }

    public static long countUrgentes() {
        return singleCount("SELECT COUNT(*) FROM interventions WHERE LOWER(priority) IN ('haute', 'critique', 'urgente') AND LOWER(status) NOT IN ('terminee', 'terminée', 'fermee', 'fermée', 'annulee', 'annulée')");
    }

    public static long countEnCours() {
        return singleCount("SELECT COUNT(*) FROM interventions WHERE LOWER(status) = 'en cours'");
    }

    public static long countTerminees() {
        return singleCount("SELECT COUNT(*) FROM interventions WHERE LOWER(status) IN ('terminee', 'terminée', 'fermee', 'fermée')");
    }

    public static List<String> loadRecentActivity(int limit) {
        List<String> items = new ArrayList<>();
        String query = "SELECT TO_CHAR(h.created_at, 'DD/MM/YYYY HH24:MI') AS dt, COALESCE(u.name, 'Système') AS uname, h.action, COALESCE(h.details, '') AS details " +
                "FROM history h LEFT JOIN users u ON h.user_id = u.id ORDER BY h.created_at DESC LIMIT ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, limit);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    items.add(rs.getString("dt") + " - " + rs.getString("uname") + " - " + rs.getString("action") + " - " + rs.getString("details"));
                }
            }
        } catch (SQLException e) {
            System.err.println("[DashboardDAO] Error loading recent activity: " + e.getMessage());
        }

        return items;
    }

    public static List<String> loadPriorityAlerts() {
        List<String> alerts = new ArrayList<>();
        alerts.add(countUrgentes() + " interventions urgentes en attente");
        alerts.add(singleCount("SELECT COUNT(*) FROM interventions WHERE assigned_to IS NULL") + " interventions non assignées");
        alerts.add(countEnCours() + " interventions actuellement en cours");
        String topTech = loadTopTechnician();
        if (!topTech.isBlank()) {
            alerts.add("Technicien le plus actif: " + topTech);
        }
        return alerts;
    }

    private static String loadTopTechnician() {
        String query = "SELECT COALESCE(u.name, 'N/A') AS uname, COUNT(*) AS total FROM interventions i " +
                "LEFT JOIN users u ON i.assigned_to = u.id WHERE i.assigned_to IS NOT NULL " +
                "GROUP BY u.name ORDER BY total DESC LIMIT 1";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) {
                return rs.getString("uname") + " (" + rs.getLong("total") + " interventions)";
            }
        } catch (SQLException e) {
            System.err.println("[DashboardDAO] Top technician error: " + e.getMessage());
        }
        return "";
    }

    public static List<Intervention> getRecentInterventions(int limit) {
        List<Intervention> interventions = new ArrayList<>();
        String query = "SELECT i.id, i.title, i.description, i.priority, i.status, " +
                "i.location, i.client_id, i.assigned_to, " +
                "COALESCE(c.company_name, 'Client inconnu') as client_name, " +
                "COALESCE(u.name, 'Non assigné') as technician_name, " +
                "i.created_at " +
                "FROM interventions i " +
                "LEFT JOIN clients c ON i.client_id = c.id " +
                "LEFT JOIN users u ON i.assigned_to = u.id " +
                "ORDER BY i.created_at DESC LIMIT ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, limit);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Intervention intervention = new Intervention(
                        rs.getLong("id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getString("priority"),
                        rs.getString("status"),
                        rs.getString("location"),
                        rs.getLong("client_id"),
                        rs.getLong("assigned_to"),
                        rs.getString("client_name"),
                        rs.getString("technician_name"),
                        rs.getString("created_at")
                    );
                    interventions.add(intervention);
                }
            }
        } catch (SQLException e) {
            System.err.println("[DashboardDAO] Error loading recent interventions: " + e.getMessage());
        }

        return interventions;
    }

    public static double calculateSLACompliance() {
        String query = "SELECT " +
                "CASE " +
                "WHEN COUNT(*) = 0 THEN 100.0 " +
                "ELSE (COUNT(CASE WHEN status IN ('Terminée', 'Fermée') AND " +
                "EXTRACT(EPOCH FROM (updated_at - created_at)) <= 86400 THEN 1 END) * 100.0 / COUNT(*)) " +
                "END as sla_rate " +
                "FROM interventions WHERE created_at >= CURRENT_DATE - INTERVAL '30 days'";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) {
                return rs.getDouble("sla_rate");
            }
        } catch (SQLException e) {
            System.err.println("[DashboardDAO] SLA calculation error: " + e.getMessage());
        }
        return 0.0;
    }
    
    public static double calculateAverageResolutionTime() {
        String query = "SELECT AVG(EXTRACT(EPOCH FROM (updated_at - created_at)) / 3600) as avg_hours " +
                "FROM interventions WHERE status IN ('Terminée', 'Fermée') AND updated_at IS NOT NULL";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next() && rs.getObject("avg_hours") != null) {
                return rs.getDouble("avg_hours");
            }
        } catch (SQLException e) {
            System.err.println("[DashboardDAO] Average resolution time error: " + e.getMessage());
        }
        return 0.0;
    }
    
    public static int getActiveTechniciansCount() {
        String query = "SELECT COUNT(DISTINCT assigned_to) as count " +
                "FROM interventions WHERE assigned_to IS NOT NULL AND status IN ('Assignée', 'En cours')";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) {
                return rs.getInt("count");
            }
        } catch (SQLException e) {
            System.err.println("[DashboardDAO] Active technicians count error: " + e.getMessage());
        }
        return 0;
    }
    
    public static double calculateWorkloadScore() {
        String query = "SELECT " +
                "CASE " +
                "WHEN tech_count = 0 THEN 0.0 " +
                "ELSE (active_interventions * 1.0 / tech_count) " +
                "END as workload " +
                "FROM (SELECT COUNT(DISTINCT assigned_to) as tech_count, " +
                "COUNT(*) as active_interventions " +
                "FROM interventions WHERE assigned_to IS NOT NULL AND status = 'En cours') t";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) {
                return rs.getDouble("workload");
            }
        } catch (SQLException e) {
            System.err.println("[DashboardDAO] Workload calculation error: " + e.getMessage());
        }
        return 0.0;
    }
    
    public static double calculatePerformanceScore() {
        String query = "SELECT " +
                "CASE " +
                "WHEN total_count = 0 THEN 100.0 " +
                "ELSE (completed_rate * 0.6 + sla_rate * 0.4) " +
                "END as performance " +
                "FROM (SELECT " +
                "(COUNT(CASE WHEN status IN ('Terminée', 'Fermée') THEN 1 END) * 100.0 / COUNT(*)) as completed_rate, " +
                "(COUNT(CASE WHEN status IN ('Terminée', 'Fermée') AND " +
                "EXTRACT(EPOCH FROM (updated_at - created_at)) <= 86400 THEN 1 END) * 100.0 / " +
                "CASE WHEN COUNT(*) = 0 THEN 1 ELSE COUNT(*) END) as sla_rate, " +
                "COUNT(*) as total_count " +
                "FROM interventions WHERE created_at >= CURRENT_DATE - INTERVAL '30 days') t";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) {
                return rs.getDouble("performance");
            }
        } catch (SQLException e) {
            System.err.println("[DashboardDAO] Performance score error: " + e.getMessage());
        }
        return 0.0;
    }
    
    public static long countUnassignedInterventions() {
        return singleCount("SELECT COUNT(*) FROM interventions WHERE assigned_to IS NULL AND status NOT IN ('Terminée', 'Fermée', 'Annulée')");
    }
    
    public static long countOverdueInterventions() {
        String query = "SELECT COUNT(*) FROM interventions " +
                "WHERE status NOT IN ('Terminée', 'Fermée', 'Annulée') " +
                "AND created_at < CURRENT_DATE - INTERVAL '3 days'";
        return singleCount(query);
    }
    
    public static List<Object[]> getTopTechnicians(int limit) {
        List<Object[]> technicians = new ArrayList<>();
        String query = "SELECT COALESCE(u.name, 'Non assigné') as name, " +
                "COUNT(*) as total, " +
                "COUNT(CASE WHEN i.status IN ('Terminée', 'Fermée') THEN 1 END) as completed, " +
                "AVG(EXTRACT(EPOCH FROM (COALESCE(i.updated_at, CURRENT_TIMESTAMP) - i.created_at)) / 3600) as avg_hours " +
                "FROM interventions i " +
                "LEFT JOIN users u ON i.assigned_to = u.id " +
                "WHERE i.assigned_to IS NOT NULL " +
                "GROUP BY u.name " +
                "ORDER BY total DESC " +
                "LIMIT ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, limit);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    technicians.add(new Object[]{
                        rs.getString("name"),
                        rs.getLong("total"),
                        rs.getLong("completed"),
                        rs.getDouble("avg_hours")
                    });
                }
            }
        } catch (SQLException e) {
            System.err.println("[DashboardDAO] Top technicians error: " + e.getMessage());
        }
        
        return technicians;
    }

    private static long singleCount(String sql) {
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            System.err.println("[DashboardDAO] Count error: " + e.getMessage());
        }
        return 0;
    }
}
