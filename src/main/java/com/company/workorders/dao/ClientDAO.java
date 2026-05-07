package com.company.workorders.dao;

import com.company.workorders.model.Client;
import com.company.workorders.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Client operations
 */
public class ClientDAO {

    /**
     * Get all clients
     */
    public static List<Client> getAllClients() {
        List<Client> clients = new ArrayList<>();
        String query = "SELECT id, company_name, phone, address, COALESCE(email, '') AS email FROM clients ORDER BY company_name";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Client client = new Client(
                        rs.getLong("id"),
                        rs.getString("company_name"),
                        rs.getString("phone"),
                        rs.getString("address"),
                    rs.getString("email")
                );
                clients.add(client);
            }
        } catch (SQLException e) {
            System.err.println("[ClientDAO] Error getting all clients: " + e.getMessage());
        }

        return clients;
    }

    /**
     * Get client by ID
     */
    public static Client getClientById(long clientId) {
        String query = "SELECT id, company_name, phone, address, COALESCE(email, '') AS email FROM clients WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setLong(1, clientId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Client(
                            rs.getLong("id"),
                            rs.getString("company_name"),
                            rs.getString("phone"),
                            rs.getString("address"),
                            rs.getString("email")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("[ClientDAO] Error getting client by ID: " + e.getMessage());
        }

        return null;
    }

    /**
     * Create a new client
     */
    public static long createClient(String companyName, String phone, String address, String email) {
        String query = "INSERT INTO clients (company_name, phone, address, email) " +
                       "VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, companyName);
            stmt.setString(2, phone);
            stmt.setString(3, address);
            stmt.setString(4, email);

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        long id = generatedKeys.getLong(1);
                        System.out.println("[ClientDAO] Client created with ID: " + id);
                        return id;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("[ClientDAO] Error creating client: " + e.getMessage());
        }

        return -1;
    }

    /**
     * Update an existing client
     */
    public static boolean updateClient(long clientId, String companyName, String phone, String address, String email) {
        String query = "UPDATE clients SET company_name = ?, phone = ?, address = ?, email = ?" +
                       "WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, companyName);
            stmt.setString(2, phone);
            stmt.setString(3, address);
            stmt.setString(4, email);
            stmt.setLong(5, clientId);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("[ClientDAO] Client " + clientId + " updated");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("[ClientDAO] Error updating client: " + e.getMessage());
        }

        return false;
    }

    /**
     * Delete a client
     */
    public static boolean deleteClient(long clientId) {
        String query = "DELETE FROM clients WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setLong(1, clientId);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("[ClientDAO] Client " + clientId + " deleted");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("[ClientDAO] Error deleting client: " + e.getMessage());
        }

        return false;
    }

    /**
     * Search clients by company name
     */
    public static List<Client> searchClients(String searchTerm) {
        List<Client> clients = new ArrayList<>();
        String query = "SELECT id, company_name, phone, address, COALESCE(email, '') AS email FROM clients " +
                       "WHERE LOWER(company_name) LIKE LOWER(?) OR LOWER(phone) LIKE LOWER(?) OR LOWER(COALESCE(email, '')) LIKE LOWER(?) " +
                       "ORDER BY company_name";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            String searchPattern = "%" + searchTerm + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            stmt.setString(3, searchPattern);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Client client = new Client(
                            rs.getLong("id"),
                            rs.getString("company_name"),
                            rs.getString("phone"),
                            rs.getString("address"),
                            rs.getString("email")
                    );
                    clients.add(client);
                }
            }
        } catch (SQLException e) {
            System.err.println("[ClientDAO] Error searching clients: " + e.getMessage());
        }

        return clients;
    }

    public static List<String> getInterventionHistoryForClient(long clientId) {
        List<String> rows = new ArrayList<>();
        String query = "SELECT id, title, status, priority " +
                "FROM interventions WHERE client_id = ? ORDER BY id DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    rows.add("#" + rs.getLong("id") + " - " + rs.getString("title") + " | " +
                            rs.getString("status") + " | Priorité: " + rs.getString("priority"));
                }
            }
        } catch (SQLException e) {
            System.err.println("[ClientDAO] Error loading intervention history for client: " + e.getMessage());
        }

        return rows;
    }
}
