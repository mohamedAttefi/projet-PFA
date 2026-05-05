package com.company;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.DriverManager;

import org.junit.jupiter.api.Test;

/**
 * Unit test for simple App.
 */
public class AppTest {

    private static final String URL = "jdbc:postgresql://localhost:5432/test";
    private static final String USER = "postgres";
    private static final String PASSWORD = "ME551234";

    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue() {
        assertTrue(true);
    }

    /**
     * Test database connection
     */
    @Test
    public void testDatabaseConnection() {
        try {
            // Load PostgreSQL driver
            Class.forName("org.postgresql.Driver");
            
            // Get connection
            Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
            
            // Assert connection is valid
            assertNotNull(connection, "Database connection should not be null");
            assertTrue(connection.isValid(5), "Database connection should be valid");
            
            System.out.println("✓ Database connection test passed!");
            connection.close();
            
        } catch (Exception e) {
            System.err.println("✗ Database connection failed: " + e.getMessage());
            throw new RuntimeException("Database connection test failed", e);
        }
    }
}
