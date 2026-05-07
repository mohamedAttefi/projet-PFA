package com.company.workorders.util;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class SchemaManager {

    private SchemaManager() {
    }

    public static void ensureCompatibility() {
        String addUserActive = "ALTER TABLE users ADD COLUMN IF NOT EXISTS is_active BOOLEAN DEFAULT TRUE";
        String addClientEmail = "ALTER TABLE clients ADD COLUMN IF NOT EXISTS email VARCHAR(255)";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(addUserActive);
            stmt.execute(addClientEmail);
        } catch (SQLException e) {
            System.err.println("[SchemaManager] Impossible de mettre à jour le schéma: " + e.getMessage());
        }
    }
}
