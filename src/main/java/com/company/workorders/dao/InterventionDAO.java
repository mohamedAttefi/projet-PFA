package com.company.workorders.dao;

import com.company.workorders.model.Intervention;
import com.company.workorders.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Intervention operations
 */
public class InterventionDAO {

    /**
     * Get all interventions
     */
    public static List<Intervention> getAllInterventions() {
        List<Intervention> interventions = new ArrayList<>();
        String query = "SELECT i.id, i.title, i.description, i.priority, i.status, i.location, " +
                   "i.client_id, i.assigned_to, i.created_at, c.company_name, u.name " +
                       "FROM interventions i " +
                       "LEFT JOIN clients c ON i.client_id = c.id " +
                       "LEFT JOIN users u ON i.assigned_to = u.id " +
                       "ORDER BY i.id DESC";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

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
                        rs.getString("company_name"),
                        rs.getString("name"),
                        rs.getString("created_at")
                );
                interventions.add(intervention);
            }
        } catch (SQLException e) {
            System.err.println("[InterventionDAO] Error getting all interventions: " + e.getMessage());
        }

        return interventions;
    }

    /**
     * Get interventions assigned to a specific technician
     */
    public static List<Intervention> getInterventionsByTechnician(long technicianId) {
        List<Intervention> interventions = new ArrayList<>();
        String query = "SELECT i.id, i.title, i.description, i.priority, i.status, i.location, " +
                       "i.client_id, i.assigned_to, i.created_at, c.company_name, u.name " +
                       "FROM interventions i " +
                       "LEFT JOIN clients c ON i.client_id = c.id " +
                       "LEFT JOIN users u ON i.assigned_to = u.id " +
                       "WHERE i.assigned_to = ? " +
                       "ORDER BY i.id DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setLong(1, technicianId);

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
                            rs.getString("company_name"),
                            rs.getString("name"),
                            rs.getString("created_at")
                    );
                    interventions.add(intervention);
                }
            }
        } catch (SQLException e) {
            System.err.println("[InterventionDAO] Error getting interventions for technician: " + e.getMessage());
        }

        return interventions;
    }

    /**
     * Get intervention by ID
     */
    public static Intervention getInterventionById(long interventionId) {
        String query = "SELECT i.id, i.title, i.description, i.priority, i.status, i.location, " +
                       "i.client_id, i.assigned_to, i.created_at, c.company_name, u.name " +
                       "FROM interventions i " +
                       "LEFT JOIN clients c ON i.client_id = c.id " +
                       "LEFT JOIN users u ON i.assigned_to = u.id " +
                       "WHERE i.id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setLong(1, interventionId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Intervention(
                            rs.getLong("id"),
                            rs.getString("title"),
                            rs.getString("description"),
                            rs.getString("priority"),
                            rs.getString("status"),
                            rs.getString("location"),
                            rs.getLong("client_id"),
                            rs.getLong("assigned_to"),
                            rs.getString("company_name"),
                            rs.getString("name"),
                            rs.getString("created_at")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("[InterventionDAO] Error getting intervention by ID: " + e.getMessage());
        }

        return null;
    }

    /**
     * Create a new intervention
     */
    public static long createIntervention(String title, String description, String priority,
                                         String status, String location, long clientId, long assignedTo) {
        String query = "INSERT INTO interventions (title, description, priority, status, location, " +
                       "client_id, assigned_to, created_at, updated_at) " +
                       "VALUES (?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, title);
            stmt.setString(2, description);
            stmt.setString(3, priority);
            stmt.setString(4, status);
            stmt.setString(5, location);
            stmt.setLong(6, clientId);
            stmt.setLong(7, assignedTo);

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        long id = generatedKeys.getLong(1);
                        System.out.println("[InterventionDAO] Intervention created with ID: " + id);
                        return id;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("[InterventionDAO] Error creating intervention: " + e.getMessage());
        }

        return -1;
    }

    /**
     * Update an existing intervention
     */
    public static boolean updateIntervention(long interventionId, String title, String description,
                                            String priority, String status, String location, long assignedTo) {
        String query = "UPDATE interventions SET title = ?, description = ?, priority = ?, " +
                       "status = ?, location = ?, assigned_to = ?, updated_at = CURRENT_TIMESTAMP " +
                       "WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, title);
            stmt.setString(2, description);
            stmt.setString(3, priority);
            stmt.setString(4, status);
            stmt.setString(5, location);
            stmt.setLong(6, assignedTo);
            stmt.setLong(7, interventionId);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("[InterventionDAO] Intervention " + interventionId + " updated");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("[InterventionDAO] Error updating intervention: " + e.getMessage());
        }

        return false;
    }

    /**
     * Update only the status of an intervention
     */
    public static boolean updateInterventionStatus(long interventionId, String newStatus) {
        String query = "UPDATE interventions SET status = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, newStatus);
            stmt.setLong(2, interventionId);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("[InterventionDAO] Intervention " + interventionId + " status updated to: " + newStatus);
                return true;
            }
        } catch (SQLException e) {
            System.err.println("[InterventionDAO] Error updating intervention status: " + e.getMessage());
        }

        return false;
    }

    /**
     * Delete an intervention
     */
    public static boolean deleteIntervention(long interventionId) {
        String query = "DELETE FROM interventions WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setLong(1, interventionId);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("[InterventionDAO] Intervention " + interventionId + " deleted");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("[InterventionDAO] Error deleting intervention: " + e.getMessage());
        }

        return false;
    }

    /**
     * Get interventions by status
     */
    public static List<Intervention> getInterventionsByStatus(String status) {
        List<Intervention> interventions = new ArrayList<>();
        String query = "SELECT i.id, i.title, i.description, i.priority, i.status, i.location, " +
                       "i.client_id, i.assigned_to, i.created_at, c.company_name, u.name " +
                       "FROM interventions i " +
                       "LEFT JOIN clients c ON i.client_id = c.id " +
                       "LEFT JOIN users u ON i.assigned_to = u.id " +
                       "WHERE i.status = ? " +
                       "ORDER BY i.id DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, status);

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
                            rs.getString("company_name"),
                            rs.getString("name"),
                            rs.getString("created_at")
                    );
                    interventions.add(intervention);
                }
            }
        } catch (SQLException e) {
            System.err.println("[InterventionDAO] Error getting interventions by status: " + e.getMessage());
        }

        return interventions;
    }

    /**
     * Get interventions by priority
     */
    public static List<Intervention> getInterventionsByPriority(String priority) {
        List<Intervention> interventions = new ArrayList<>();
        String query = "SELECT i.id, i.title, i.description, i.priority, i.status, i.location, " +
                       "i.client_id, i.assigned_to, i.created_at, c.company_name, u.name " +
                       "FROM interventions i " +
                       "LEFT JOIN clients c ON i.client_id = c.id " +
                       "LEFT JOIN users u ON i.assigned_to = u.id " +
                       "WHERE i.priority = ? " +
                       "ORDER BY i.id DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, priority);

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
                            rs.getString("company_name"),
                            rs.getString("name"),
                            rs.getString("created_at")
                    );
                    interventions.add(intervention);
                }
            }
        } catch (SQLException e) {
            System.err.println("[InterventionDAO] Error getting interventions by priority: " + e.getMessage());
        }

        return interventions;
    }

    /**
     * Get all available technicians (users with TECHNICIAN role)
     */
    public static List<Object[]> getAllTechnicians() {
        List<Object[]> technicians = new ArrayList<>();
        String query = "SELECT id, name FROM users WHERE role = 'TECHNICIAN' ORDER BY name";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                technicians.add(new Object[]{rs.getLong("id"), rs.getString("name")});
            }
        } catch (SQLException e) {
            System.err.println("[InterventionDAO] Error getting technicians: " + e.getMessage());
        }

        return technicians;
    }

    public static List<Intervention> searchInterventions(String term, String status, String priority, Long technicianId, boolean onlyAssignedToCurrentUser, long currentUserId) {
        List<Intervention> interventions = new ArrayList<>();
        StringBuilder query = new StringBuilder(
            "SELECT i.id, i.title, i.description, i.priority, i.status, i.location, i.client_id, i.assigned_to, i.created_at, c.company_name, u.name " +
                "FROM interventions i " +
                "LEFT JOIN clients c ON i.client_id = c.id " +
                "LEFT JOIN users u ON i.assigned_to = u.id WHERE 1=1"
        );

        List<Object> params = new ArrayList<>();

        if (term != null && !term.isBlank()) {
            query.append(" AND (LOWER(i.title) LIKE LOWER(?) OR LOWER(COALESCE(i.description, '')) LIKE LOWER(?) OR LOWER(COALESCE(c.company_name, '')) LIKE LOWER(?) OR LOWER(COALESCE(u.name, '')) LIKE LOWER(?))");
            String pattern = "%" + term.trim() + "%";
            params.add(pattern);
            params.add(pattern);
            params.add(pattern);
            params.add(pattern);
        }
        if (status != null && !status.isBlank() && !"Tous".equalsIgnoreCase(status)) {
            query.append(" AND i.status = ?");
            params.add(status);
        }
        if (priority != null && !priority.isBlank() && !"Toutes".equalsIgnoreCase(priority)) {
            query.append(" AND i.priority = ?");
            params.add(priority);
        }
        if (technicianId != null && technicianId > 0) {
            query.append(" AND i.assigned_to = ?");
            params.add(technicianId);
        }
        if (onlyAssignedToCurrentUser && currentUserId > 0) {
            query.append(" AND i.assigned_to = ?");
            params.add(currentUserId);
        }

        query.append(" ORDER BY i.id DESC");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query.toString())) {

            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    interventions.add(new Intervention(
                            rs.getLong("id"),
                            rs.getString("title"),
                            rs.getString("description"),
                            rs.getString("priority"),
                            rs.getString("status"),
                            rs.getString("location"),
                            rs.getLong("client_id"),
                            rs.getLong("assigned_to"),
                            rs.getString("company_name"),
                            rs.getString("name"),
                            rs.getString("created_at")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("[InterventionDAO] Error searching interventions: " + e.getMessage());
        }

        return interventions;
    }

    /**
     * Count interventions matching search criteria
     */
    public static int countSearchInterventions(String term, String status, String priority, Long technicianId, boolean onlyAssignedToCurrentUser, long currentUserId) {
        StringBuilder query = new StringBuilder(
            "SELECT COUNT(*) FROM interventions i " +
                "LEFT JOIN clients c ON i.client_id = c.id " +
                "LEFT JOIN users u ON i.assigned_to = u.id WHERE 1=1"
        );

        List<Object> params = new ArrayList<>();

        if (term != null && !term.isBlank()) {
            query.append(" AND (LOWER(i.title) LIKE LOWER(?) OR LOWER(COALESCE(i.description, '')) LIKE LOWER(?) OR LOWER(COALESCE(c.company_name, '')) LIKE LOWER(?) OR LOWER(COALESCE(u.name, '')) LIKE LOWER(?))");
            String pattern = "%" + term.trim() + "%";
            params.add(pattern);
            params.add(pattern);
            params.add(pattern);
            params.add(pattern);
        }
        if (status != null && !status.isBlank() && !"Tous".equalsIgnoreCase(status)) {
            query.append(" AND i.status = ?");
            params.add(status);
        }
        if (priority != null && !priority.isBlank() && !"Toutes".equalsIgnoreCase(priority)) {
            query.append(" AND i.priority = ?");
            params.add(priority);
        }
        if (technicianId != null && technicianId > 0) {
            query.append(" AND i.assigned_to = ?");
            params.add(technicianId);
        }
        if (onlyAssignedToCurrentUser && currentUserId > 0) {
            query.append(" AND i.assigned_to = ?");
            params.add(currentUserId);
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query.toString())) {

            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("[InterventionDAO] Error counting search interventions: " + e.getMessage());
        }

        return 0;
    }

    /**
     * Search interventions with pagination
     */
    public static List<Intervention> searchInterventionsPaginated(String term, String status, String priority, Long technicianId, boolean onlyAssignedToCurrentUser, long currentUserId, int page, int itemsPerPage) {
        List<Intervention> interventions = new ArrayList<>();
        StringBuilder query = new StringBuilder(
            "SELECT i.id, i.title, i.description, i.priority, i.status, i.location, i.client_id, i.assigned_to, i.created_at, c.company_name, u.name " +
                "FROM interventions i " +
                "LEFT JOIN clients c ON i.client_id = c.id " +
                "LEFT JOIN users u ON i.assigned_to = u.id WHERE 1=1"
        );

        List<Object> params = new ArrayList<>();

        if (term != null && !term.isBlank()) {
            query.append(" AND (LOWER(i.title) LIKE LOWER(?) OR LOWER(COALESCE(i.description, '')) LIKE LOWER(?) OR LOWER(COALESCE(c.company_name, '')) LIKE LOWER(?) OR LOWER(COALESCE(u.name, '')) LIKE LOWER(?))");
            String pattern = "%" + term.trim() + "%";
            params.add(pattern);
            params.add(pattern);
            params.add(pattern);
            params.add(pattern);
        }
        if (status != null && !status.isBlank() && !"Tous".equalsIgnoreCase(status)) {
            query.append(" AND i.status = ?");
            params.add(status);
        }
        if (priority != null && !priority.isBlank() && !"Toutes".equalsIgnoreCase(priority)) {
            query.append(" AND i.priority = ?");
            params.add(priority);
        }
        if (technicianId != null && technicianId > 0) {
            query.append(" AND i.assigned_to = ?");
            params.add(technicianId);
        }
        if (onlyAssignedToCurrentUser && currentUserId > 0) {
            query.append(" AND i.assigned_to = ?");
            params.add(currentUserId);
        }

        query.append(" ORDER BY i.id DESC LIMIT ? OFFSET ?");
        params.add(itemsPerPage);
        params.add((page - 1) * itemsPerPage);

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query.toString())) {

            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    interventions.add(new Intervention(
                            rs.getLong("id"),
                            rs.getString("title"),
                            rs.getString("description"),
                            rs.getString("priority"),
                            rs.getString("status"),
                            rs.getString("location"),
                            rs.getLong("client_id"),
                            rs.getLong("assigned_to"),
                            rs.getString("company_name"),
                            rs.getString("name"),
                            rs.getString("created_at")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("[InterventionDAO] Error searching paginated interventions: " + e.getMessage());
        }

        return interventions;
    }

    /**
     * Count interventions matching search criteria with date filtering
     */
    public static int countSearchInterventionsWithDates(String term, String status, String priority, Long technicianId, boolean onlyAssignedToCurrentUser, long currentUserId, java.time.LocalDate startDate, java.time.LocalDate endDate) {
        StringBuilder query = new StringBuilder(
            "SELECT COUNT(*) FROM interventions i " +
                "LEFT JOIN clients c ON i.client_id = c.id " +
                "LEFT JOIN users u ON i.assigned_to = u.id WHERE 1=1"
        );

        List<Object> params = new ArrayList<>();

        if (term != null && !term.isBlank()) {
            query.append(" AND (LOWER(i.title) LIKE LOWER(?) OR LOWER(COALESCE(i.description, '')) LIKE LOWER(?) OR LOWER(COALESCE(c.company_name, '')) LIKE LOWER(?) OR LOWER(COALESCE(u.name, '')) LIKE LOWER(?))");
            String pattern = "%" + term.trim() + "%";
            params.add(pattern);
            params.add(pattern);
            params.add(pattern);
            params.add(pattern);
        }
        if (status != null && !status.isBlank() && !"Tous".equalsIgnoreCase(status)) {
            query.append(" AND i.status = ?");
            params.add(status);
        }
        if (priority != null && !priority.isBlank() && !"Toutes".equalsIgnoreCase(priority)) {
            query.append(" AND i.priority = ?");
            params.add(priority);
        }
        if (technicianId != null && technicianId > 0) {
            query.append(" AND i.assigned_to = ?");
            params.add(technicianId);
        }
        if (onlyAssignedToCurrentUser && currentUserId > 0) {
            query.append(" AND i.assigned_to = ?");
            params.add(currentUserId);
        }
        if (startDate != null) {
            query.append(" AND DATE(i.created_at) >= ?");
            params.add(startDate.toString());
        }
        if (endDate != null) {
            query.append(" AND DATE(i.created_at) <= ?");
            params.add(endDate.toString());
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query.toString())) {

            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("[InterventionDAO] Error counting search interventions with dates: " + e.getMessage());
        }

        return 0;
    }

    /**
     * Search interventions with pagination and date filtering
     */
    public static List<Intervention> searchInterventionsPaginatedWithDates(String term, String status, String priority, Long technicianId, boolean onlyAssignedToCurrentUser, long currentUserId, int page, int itemsPerPage, java.time.LocalDate startDate, java.time.LocalDate endDate) {
        List<Intervention> interventions = new ArrayList<>();
        StringBuilder query = new StringBuilder(
            "SELECT i.id, i.title, i.description, i.priority, i.status, i.location, i.client_id, i.assigned_to, i.created_at, c.company_name, u.name " +
                "FROM interventions i " +
                "LEFT JOIN clients c ON i.client_id = c.id " +
                "LEFT JOIN users u ON i.assigned_to = u.id WHERE 1=1"
        );

        List<Object> params = new ArrayList<>();

        if (term != null && !term.isBlank()) {
            query.append(" AND (LOWER(i.title) LIKE LOWER(?) OR LOWER(COALESCE(i.description, '')) LIKE LOWER(?) OR LOWER(COALESCE(c.company_name, '')) LIKE LOWER(?) OR LOWER(COALESCE(u.name, '')) LIKE LOWER(?))");
            String pattern = "%" + term.trim() + "%";
            params.add(pattern);
            params.add(pattern);
            params.add(pattern);
            params.add(pattern);
        }
        if (status != null && !status.isBlank() && !"Tous".equalsIgnoreCase(status)) {
            query.append(" AND i.status = ?");
            params.add(status);
        }
        if (priority != null && !priority.isBlank() && !"Toutes".equalsIgnoreCase(priority)) {
            query.append(" AND i.priority = ?");
            params.add(priority);
        }
        if (technicianId != null && technicianId > 0) {
            query.append(" AND i.assigned_to = ?");
            params.add(technicianId);
        }
        if (onlyAssignedToCurrentUser && currentUserId > 0) {
            query.append(" AND i.assigned_to = ?");
            params.add(currentUserId);
        }
        if (startDate != null) {
            query.append(" AND DATE(i.created_at) >= ?");
            params.add(startDate.toString());
        }
        if (endDate != null) {
            query.append(" AND DATE(i.created_at) <= ?");
            params.add(endDate.toString());
        }

        query.append(" ORDER BY i.id DESC LIMIT ? OFFSET ?");
        params.add(itemsPerPage);
        params.add((page - 1) * itemsPerPage);

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query.toString())) {

            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    interventions.add(new Intervention(
                            rs.getLong("id"),
                            rs.getString("title"),
                            rs.getString("description"),
                            rs.getString("priority"),
                            rs.getString("status"),
                            rs.getString("location"),
                            rs.getLong("client_id"),
                            rs.getLong("assigned_to"),
                            rs.getString("company_name"),
                            rs.getString("name"),
                            rs.getString("created_at")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("[InterventionDAO] Error searching paginated interventions with dates: " + e.getMessage());
        }

        return interventions;
    }
}
