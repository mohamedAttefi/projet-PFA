package com.company.workorders.service;

import com.company.workorders.model.User;
import com.company.workorders.model.UserRole;
import com.company.workorders.util.DBConnection;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Authentication service that validates users against PostgreSQL.
 */
public class AuthService {
    private static final String USER_QUERY = "SELECT id, name, email, password, role FROM users WHERE email = ?";

    public AuthResult authenticate(String email, String password) {
        System.out.println("[AUTH] Tentative de connexion: " + email);
        
        if (email == null || password == null || email.isBlank() || password.isBlank()) {
            System.out.println("[AUTH] Email ou mot de passe vide");
            return AuthResult.failure("Veuillez renseigner votre email et votre mot de passe.");
        }

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(USER_QUERY)) {

            System.out.println("[AUTH] Connexion BD réussie");
            statement.setString(1, email.trim());

            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    System.out.println("[AUTH] Utilisateur introuvable: " + email);
                    System.out.println("[AUTH] Vérifier que l'email exact existe dans la base de données");
                    System.out.println("[AUTH] SELECT * FROM users; pour lister tous les utilisateurs");
                    return AuthResult.failure("Identifiants invalides.");
                }

                String storedPassword = resultSet.getString("password");
                String storedRole = resultSet.getString("role");
                System.out.println("[AUTH] Utilisateur trouvé: " + email + " | Role: " + storedRole);
                System.out.println("[AUTH] Mot de passe stocké (premiers 20 chars): " + (storedPassword != null ? storedPassword.substring(0, Math.min(20, storedPassword.length())) : "NULL"));
                System.out.println("[AUTH] Mot de passe saisi (premiers 20 chars): " + password.substring(0, Math.min(20, password.length())));
                
                boolean passwordMatches = isBCryptHash(storedPassword)
                        ? BCrypt.checkpw(password, storedPassword)
                        : storedPassword != null && storedPassword.equals(password);

                System.out.println("[AUTH] Mot de passe valide: " + passwordMatches);

                if (!passwordMatches) {
                    System.out.println("[AUTH] Mot de passe incorrect pour: " + email);
                    return AuthResult.failure("Identifiants invalides.");
                }

                User user = new User(
                        resultSet.getLong("id"),
                        resultSet.getString("name"),
                        resultSet.getString("email"),
                        UserRole.fromDatabaseValue(storedRole)
                );

                System.out.println("[AUTH] ✓ Authentification réussie pour: " + email);
                return AuthResult.success(user);
            }
        } catch (SQLException exception) {
            System.err.println("[AUTH] Erreur SQL: " + exception.getMessage());
            exception.printStackTrace();
            return AuthResult.failure("Impossible de contacter la base de données.");
        }
    }

    public static String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt(12));
    }

    private boolean isBCryptHash(String value) {
        return value != null && value.startsWith("$2");
    }
}