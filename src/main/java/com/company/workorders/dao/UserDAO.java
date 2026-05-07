package com.company.workorders.dao;

import com.company.workorders.model.User;
import com.company.workorders.model.UserRole;
import com.company.workorders.service.AuthService;
import com.company.workorders.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    public static User getUserById(long userId) {
        String query = "SELECT id, name, email, role, COALESCE(is_active, TRUE) AS is_active FROM users WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setLong(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String roleStr = rs.getString("role");
                    UserRole role = UserRole.RECEPTIONIST; // default
                    try {
                        role = UserRole.valueOf(roleStr.toUpperCase());
                    } catch (IllegalArgumentException e) {
                        System.err.println("[UserDAO] Unknown role: " + roleStr);
                    }
                    
                    return new User(
                            rs.getLong("id"),
                            rs.getString("name"),
                            rs.getString("email"),
                            role
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("[UserDAO] Error getting user by ID: " + e.getMessage());
        }

        return null;
    }

    public static List<Object[]> getAllUsers() {
        List<Object[]> users = new ArrayList<>();
        String query = "SELECT id, name, email, role, COALESCE(is_active, TRUE) AS is_active FROM users ORDER BY id DESC";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                users.add(new Object[]{
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("role"),
                        rs.getBoolean("is_active")
                });
            }
        } catch (SQLException e) {
            System.err.println("[UserDAO] Error loading users: " + e.getMessage());
        }

        return users;
    }

    public static List<Object[]> searchUsers(String term) {
        List<Object[]> users = new ArrayList<>();
        String query = "SELECT id, name, email, role, COALESCE(is_active, TRUE) AS is_active FROM users " +
                "WHERE LOWER(name) LIKE LOWER(?) OR LOWER(email) LIKE LOWER(?) OR LOWER(role) LIKE LOWER(?) ORDER BY id DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            String pattern = "%" + term + "%";
            stmt.setString(1, pattern);
            stmt.setString(2, pattern);
            stmt.setString(3, pattern);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    users.add(new Object[]{
                            rs.getLong("id"),
                            rs.getString("name"),
                            rs.getString("email"),
                            rs.getString("role"),
                            rs.getBoolean("is_active")
                    });
                }
            }
        } catch (SQLException e) {
            System.err.println("[UserDAO] Error searching users: " + e.getMessage());
        }

        return users;
    }

    public static long createUser(String name, String email, String rawPassword, String role) {
        String query = "INSERT INTO users (name, email, password, role, is_active, created_at, updated_at) VALUES (?, ?, ?, ?, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.setString(3, AuthService.hashPassword(rawPassword));
            stmt.setString(4, role);

            int affected = stmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet keys = stmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        return keys.getLong(1);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("[UserDAO] Error creating user: " + e.getMessage());
        }

        return -1;
    }

    public static boolean updateUser(long id, String name, String email, String role, String newPassword) {
        boolean updatePassword = newPassword != null && !newPassword.isBlank();
        String query = updatePassword
                ? "UPDATE users SET name = ?, email = ?, role = ?, password = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?"
                : "UPDATE users SET name = ?, email = ?, role = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.setString(3, role);
            if (updatePassword) {
                stmt.setString(4, AuthService.hashPassword(newPassword));
                stmt.setLong(5, id);
            } else {
                stmt.setLong(4, id);
            }

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[UserDAO] Error updating user: " + e.getMessage());
            return false;
        }
    }

    public static boolean deleteUser(long id) {
        String query = "DELETE FROM users WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setLong(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[UserDAO] Error deleting user: " + e.getMessage());
            return false;
        }
    }

    public static boolean setUserActive(long id, boolean active) {
        String query = "UPDATE users SET is_active = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setBoolean(1, active);
            stmt.setLong(2, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[UserDAO] Error toggling user status: " + e.getMessage());
            return false;
        }
    }
}
