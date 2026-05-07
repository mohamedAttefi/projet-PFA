package com.company.workorders.dao;

import com.company.workorders.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class HistoryDAO {

    public static List<Object[]> getAllHistory() {
        List<Object[]> rows = new ArrayList<>();
        String query = "SELECT TO_CHAR(h.created_at, 'DD/MM/YYYY HH24:MI') AS dt, h.action, COALESCE(u.name, 'Système') AS uname, COALESCE(h.details, '') AS details " +
                "FROM history h LEFT JOIN users u ON h.user_id = u.id ORDER BY h.created_at DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                rows.add(new Object[]{
                        rs.getString("dt"),
                        rs.getString("action"),
                        rs.getString("uname"),
                        rs.getString("details")
                });
            }
        } catch (SQLException e) {
            System.err.println("[HistoryDAO] Error loading history: " + e.getMessage());
        }

        return rows;
    }

    public static void addHistory(long userId, String action, String entityType, long entityId, String details) {
        String query = "INSERT INTO history (user_id, action, entity_type, entity_id, details, created_at) VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setLong(1, userId > 0 ? userId : 0);
            stmt.setString(2, action);
            stmt.setString(3, entityType);
            stmt.setLong(4, entityId);
            stmt.setString(5, details);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[HistoryDAO] Error inserting history: " + e.getMessage());
        }
    }
}
