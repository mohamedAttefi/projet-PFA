package com.company;

import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseConnectionTest {
    
    private static final String URL = "jdbc:postgresql://localhost:5432/test";
    private static final String USER = "postgres";
    private static final String PASSWORD = "ME551234";
    
    public static void main(String[] args) {
        testConnection();
    }
    
    public static void testConnection() {
        System.out.println("Testing database connection...");
        
        try {
            // Test PostgreSQL driver availability
            Class.forName("org.postgresql.Driver");
            System.out.println("✓ PostgreSQL driver loaded successfully");
            
            // Test connection
            Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("✓ Database connection established successfully");
            
            if (connection.isValid(5)) {
                System.out.println("✓ Connection is valid");
            } else {
                System.out.println("✗ Connection is not valid");
            }
            
            // Get database metadata
            System.out.println("✓ Database metadata:");
            System.out.println("  - Database name: " + connection.getMetaData().getDatabaseProductName());
            System.out.println("  - Database version: " + connection.getMetaData().getDatabaseProductVersion());
            System.out.println("  - URL: " + connection.getMetaData().getURL());
            
            connection.close();
            System.out.println("✓ Connection closed successfully");
            System.out.println("\n🎉 All database connection tests passed!");
            
        } catch (ClassNotFoundException e) {
            System.err.println("✗ PostgreSQL driver not found: " + e.getMessage());
            System.err.println("  Make sure postgresql dependency is in your pom.xml");
        } catch (Exception e) {
            System.err.println("✗ Database connection failed: " + e.getMessage());
            System.err.println("  Check if PostgreSQL is running on localhost:5432");
            System.err.println("  Check if database 'test' exists");
            System.err.println("  Check username and password");
        }
    }
}
