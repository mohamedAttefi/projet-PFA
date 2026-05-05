package com.company.workorders.util;

import com.company.workorders.model.UserRole;
import com.company.workorders.service.AuthResult;
import com.company.workorders.service.AuthService;
import com.company.workorders.util.AppNavigator;
import com.company.workorders.service.SessionContext;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/**
 * Controller for the login screen.
 */
public class LoginController {

    private final AuthService authService = new AuthService();

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private CheckBox rememberMeCheckBox;

    @FXML
    public void initialize() {
        if (rememberMeCheckBox != null) {
            rememberMeCheckBox.setSelected(false);
        }
    }

    @FXML
    private void handleLogin() {
        AuthResult authResult = authService.authenticate(emailField.getText(), passwordField.getText());

        if (authResult.isSuccess()) {
            SessionContext.setCurrentUser(authResult.getUser());
            AppNavigator.showAppShell();
            showAlert(
                    Alert.AlertType.INFORMATION,
                    "Connexion réussie",
                    buildWelcomeMessage(authResult.getUser().getRole())
            );
            clearForm();
            return;
        }

        showAlert(Alert.AlertType.ERROR, "Connexion refusée", authResult.getMessage());
        clearPassword();
    }

    private String buildWelcomeMessage(UserRole role) {
        String roleLabel = switch (role) {
            case RECEPTIONIST -> "Réceptionniste";
            case TECHNICIAN -> "Technicien";
            case ADMINISTRATOR -> "Administrateur";
        };

        return "Bienvenue dans l'application.\nRôle: " + roleLabel;
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void clearForm() {
        emailField.clear();
        passwordField.clear();
        if (rememberMeCheckBox != null) {
            rememberMeCheckBox.setSelected(false);
        }
    }

    private void clearPassword() {
        passwordField.clear();
        passwordField.requestFocus();
    }
}
    
