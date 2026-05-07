package com.company.workorders.controller;

import com.company.workorders.dao.UserDAO;
import com.company.workorders.model.User;
import com.company.workorders.model.UserRole;
import com.company.workorders.service.SessionContext;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class ProfileController {

    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField emailField;
    @FXML private ComboBox<String> roleBox;
    @FXML private PasswordField currentPasswordField;
    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private ComboBox<String> languageBox;
    @FXML private CheckBox notificationsToggle;

    private User currentUser;

    @FXML
    public void initialize() {
        roleBox.getItems().addAll("RECEPTIONIST", "TECHNICIAN", "ADMINISTRATOR");
        languageBox.getItems().addAll("Français (France)", "English (United States)");

        currentUser = SessionContext.getCurrentUser();
        if (currentUser != null) {
            loadCurrentUser(currentUser);
        }

        boolean canEditRole = currentUser != null && currentUser.getRole() == UserRole.ADMINISTRATOR;
        roleBox.setDisable(!canEditRole);
    }

    @FXML
    private void handleSaveProfile() {
        if (currentUser == null) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Aucun utilisateur connecté.");
            return;
        }

        String firstName = value(firstNameField);
        String lastName = value(lastNameField);
        String email = value(emailField);
        String role = roleBox.getValue() != null ? roleBox.getValue() : currentUser.getRole().name();
        String newPassword = value(newPasswordField);
        String confirmPassword = value(confirmPasswordField);

        if (firstName.isBlank() || lastName.isBlank() || email.isBlank()) {
            showAlert(Alert.AlertType.WARNING, "Validation", "Le prénom, le nom et l'email sont obligatoires.");
            return;
        }

        if (!newPassword.isBlank() && !newPassword.equals(confirmPassword)) {
            showAlert(Alert.AlertType.WARNING, "Validation", "Les mots de passe ne correspondent pas.");
            return;
        }

        String fullName = (firstName + " " + lastName).trim();
        boolean updated = UserDAO.updateUser(currentUser.getId(), fullName, email, role, newPassword.isBlank() ? null : newPassword);
        if (updated) {
            UserRole roleEnum;
            try {
                roleEnum = UserRole.valueOf(role.toUpperCase());
            } catch (IllegalArgumentException exception) {
                roleEnum = currentUser.getRole();
            }
            SessionContext.setCurrentUser(new User(currentUser.getId(), fullName, email, roleEnum));
            currentUser = SessionContext.getCurrentUser();
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Profil mis à jour avec succès.");
        } else {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de mettre à jour le profil.");
        }
    }

    @FXML
    private void handleResetProfile() {
        if (currentUser != null) {
            loadCurrentUser(currentUser);
        }
    }

    private void loadCurrentUser(User user) {
        String[] parts = splitName(user.getName());
        firstNameField.setText(parts[0]);
        lastNameField.setText(parts[1]);
        emailField.setText(user.getEmail());
        roleBox.setValue(user.getRole().name());
        currentPasswordField.clear();
        newPasswordField.clear();
        confirmPasswordField.clear();
        languageBox.setValue("Français (France)");
        notificationsToggle.setSelected(true);
    }

    private String[] splitName(String name) {
        if (name == null || name.isBlank()) {
            return new String[]{"", ""};
        }

        String trimmed = name.trim();
        int index = trimmed.indexOf(' ');
        if (index < 0) {
            return new String[]{trimmed, ""};
        }

        return new String[]{trimmed.substring(0, index), trimmed.substring(index + 1).trim()};
    }

    private String value(TextInputControl control) {
        return control.getText() == null ? "" : control.getText().trim();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}