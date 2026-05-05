package com.company;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class DatabaseSetup {
    public static void main(String[] args) throws Exception {
        String url = "jdbc:postgresql://localhost:5432/test";
        String user = "postgres";
        String password = "ME551234";
        String sqlFile = "src/main/resources/setup.sql";

        // Read SQL file
        String sql = new String(Files.readAllBytes(Paths.get(sqlFile)));

        // Connect and execute
        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt = conn.createStatement()) {

            System.out.println("✓ Connected to PostgreSQL");

            // Split and execute statements
            String[] statements = sql.split(";");
            int count = 0;
            for (String statement : statements) {
                String trimmed = statement.trim();
                if (!trimmed.isEmpty() && !trimmed.startsWith("--")) {
                    try {
                        stmt.execute(trimmed);
                        count++;
                    } catch (Exception e) {
                        // Skip conflicts
                        if (!e.getMessage().contains("CONFLICT") && !e.getMessage().contains("already exists")) {
                            System.err.println("Error: " + e.getMessage());
                        }
                    }
                }
            }

            System.out.println("✓ Executed " + count + " SQL statements");
            System.out.println("✓ Database setup complete!");
            System.out.println("\nTest credentials:");
            System.out.println("  admin@canal-info.fr / admin123");
            System.out.println("  receptionist@canal-info.fr / recept123");
            System.out.println("  technician@canal-info.fr / tech123");
            System.out.println("  technician2@canal-info.fr / tech456");
        }
    }
}
