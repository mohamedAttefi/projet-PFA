package com.company.workorders.dao;

import com.company.workorders.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class NotificationDAO {

    public static List<String> getNotificationsForUser(long userId) {
        List<String> rows = new ArrayList<>();
        String query = "SELECT TO_CHAR(created_at, 'DD/MM/YYYY HH24:MI') AS dt, message, is_read FROM notifications WHERE user_id = ? OR user_id IS NULL ORDER BY created_at DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setLong(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String prefix = rs.getBoolean("is_read") ? "[LU] " : "[NOUVEAU] ";
                    rows.add(prefix + rs.getString("dt") + " - " + rs.getString("message"));
                }
            }
        } catch (SQLException e) {
            System.err.println("[NotificationDAO] Error loading notifications: " + e.getMessage());
        }

        return rows;
    }

    public static void createNotification(Long userId, String message) {
        String query = "INSERT INTO notifications (user_id, message, is_read, created_at) VALUES (?, ?, FALSE, CURRENT_TIMESTAMP)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            if (userId == null || userId <= 0) {
                stmt.setNull(1, java.sql.Types.INTEGER);
            } else {
                stmt.setLong(1, userId);
            }
            stmt.setString(2, message);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[NotificationDAO] Error creating notification: " + e.getMessage());
        }
    }

    public static boolean markAllAsRead(long userId) {
        String query = "UPDATE notifications SET is_read = TRUE WHERE user_id = ? OR user_id IS NULL";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setLong(1, userId);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("[NotificationDAO] Error marking notifications as read: " + e.getMessage());
            return false;
        }
    }
}
