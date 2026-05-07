package com.company.workorders.dao;

import com.company.workorders.model.InterventionComment;
import com.company.workorders.util.DBConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for InterventionComment operations
 */
public class InterventionCommentDAO {

    /**
     * Create a new comment for an intervention
     */
    public static long createComment(long interventionId, long userId, String content, String commentType) {
        String query = "INSERT INTO intervention_comments (intervention_id, user_id, content, comment_type, created_at, updated_at) " +
                      "VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setLong(1, interventionId);
            stmt.setLong(2, userId);
            stmt.setString(3, content);
            stmt.setString(4, commentType);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getLong(1);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("[InterventionCommentDAO] Error creating comment: " + e.getMessage());
        }

        return -1;
    }

    /**
     * Get all comments for a specific intervention
     */
    public static List<InterventionComment> getCommentsByInterventionId(long interventionId) {
        List<InterventionComment> comments = new ArrayList<>();
        String query = "SELECT ic.id, ic.intervention_id, ic.user_id, ic.content, ic.comment_type, " +
                      "ic.created_at, ic.updated_at, u.name as user_name " +
                      "FROM intervention_comments ic " +
                      "LEFT JOIN users u ON ic.user_id = u.id " +
                      "WHERE ic.intervention_id = ? " +
                      "ORDER BY ic.created_at ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setLong(1, interventionId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    InterventionComment comment = new InterventionComment(
                        rs.getLong("id"),
                        rs.getLong("intervention_id"),
                        rs.getLong("user_id"),
                        rs.getString("user_name"),
                        rs.getString("content"),
                        rs.getTimestamp("created_at").toLocalDateTime(),
                        rs.getTimestamp("updated_at").toLocalDateTime(),
                        rs.getString("comment_type")
                    );
                    comments.add(comment);
                }
            }
        } catch (SQLException e) {
            System.err.println("[InterventionCommentDAO] Error getting comments: " + e.getMessage());
        }

        return comments;
    }

    /**
     * Update an existing comment
     */
    public static boolean updateComment(long commentId, String content) {
        String query = "UPDATE intervention_comments SET content = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, content);
            stmt.setLong(2, commentId);

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("[InterventionCommentDAO] Error updating comment: " + e.getMessage());
        }

        return false;
    }

    /**
     * Delete a comment
     */
    public static boolean deleteComment(long commentId) {
        String query = "DELETE FROM intervention_comments WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setLong(1, commentId);

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("[InterventionCommentDAO] Error deleting comment: " + e.getMessage());
        }

        return false;
    }

    /**
     * Get comment count for an intervention
     */
    public static int getCommentCount(long interventionId) {
        String query = "SELECT COUNT(*) FROM intervention_comments WHERE intervention_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setLong(1, interventionId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("[InterventionCommentDAO] Error getting comment count: " + e.getMessage());
        }

        return 0;
    }

    /**
     * Get recent comments across all interventions (for dashboard)
     */
    public static List<InterventionComment> getRecentComments(int limit) {
        List<InterventionComment> comments = new ArrayList<>();
        String query = "SELECT ic.id, ic.intervention_id, ic.user_id, ic.content, ic.comment_type, " +
                      "ic.created_at, ic.updated_at, u.name as user_name, i.title as intervention_title " +
                      "FROM intervention_comments ic " +
                      "LEFT JOIN users u ON ic.user_id = u.id " +
                      "LEFT JOIN interventions i ON ic.intervention_id = i.id " +
                      "ORDER BY ic.created_at DESC LIMIT ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, limit);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    InterventionComment comment = new InterventionComment(
                        rs.getLong("id"),
                        rs.getLong("intervention_id"),
                        rs.getLong("user_id"),
                        rs.getString("user_name"),
                        rs.getString("content"),
                        rs.getTimestamp("created_at").toLocalDateTime(),
                        rs.getTimestamp("updated_at").toLocalDateTime(),
                        rs.getString("comment_type")
                    );
                    comments.add(comment);
                }
            }
        } catch (SQLException e) {
            System.err.println("[InterventionCommentDAO] Error getting recent comments: " + e.getMessage());
        }

        return comments;
    }

    /**
     * Create the intervention_comments table if it doesn't exist
     */
    public static void createTableIfNotExists() {
        String query = "CREATE TABLE IF NOT EXISTS intervention_comments (" +
                      "id BIGSERIAL PRIMARY KEY, " +
                      "intervention_id BIGINT NOT NULL, " +
                      "user_id BIGINT NOT NULL, " +
                      "content TEXT NOT NULL, " +
                      "comment_type VARCHAR(20) DEFAULT 'INTERNAL', " +
                      "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                      "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                      "FOREIGN KEY (intervention_id) REFERENCES interventions(id) ON DELETE CASCADE, " +
                      "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE " +
                      ")";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute(query);
            System.out.println("[InterventionCommentDAO] Table intervention_comments ensured to exist");
        } catch (SQLException e) {
            System.err.println("[InterventionCommentDAO] Error creating table: " + e.getMessage());
        }
    }
}
