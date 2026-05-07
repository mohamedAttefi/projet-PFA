package com.company.workorders.controller;

import com.company.workorders.dao.HistoryDAO;
import com.company.workorders.dao.UserDAO;
import com.company.workorders.service.PermissionService;
import com.company.workorders.service.SessionContext;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class UsersController {

    @FXML private TextField searchField;
    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private ComboBox<String> roleBox;
    @FXML private TableView<UserRow> userTable;
    @FXML private TableColumn<UserRow, String> nameColumn;
    @FXML private TableColumn<UserRow, String> emailColumn;
    @FXML private TableColumn<UserRow, String> roleColumn;
    @FXML private TableColumn<UserRow, String> statusColumn;
    @FXML private Button saveButton;
    @FXML private Button deleteButton;
    @FXML private Button toggleActiveButton;

    private UserRow selectedUser;

    @FXML
    public void initialize() {
        roleBox.getItems().addAll("RECEPTIONIST", "TECHNICIAN", "ADMINISTRATOR");

        nameColumn.setCellValueFactory(cell -> cell.getValue().nameProperty());
        emailColumn.setCellValueFactory(cell -> cell.getValue().emailProperty());
        roleColumn.setCellValueFactory(cell -> cell.getValue().roleProperty());
        statusColumn.setCellValueFactory(cell -> cell.getValue().statusProperty());

        userTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            selectedUser = newVal;
            if (newVal != null) {
                nameField.setText(newVal.getName());
                emailField.setText(newVal.getEmail());
                roleBox.setValue(newVal.getRole());
                passwordField.clear();
                toggleActiveButton.setText(newVal.isActive() ? "Désactiver" : "Activer");
            }
        });

        boolean canManage = PermissionService.canManageUsers();
        saveButton.setDisable(!canManage);
        deleteButton.setDisable(!canManage);
        toggleActiveButton.setDisable(!canManage);

        if (!canManage) {
            nameField.setEditable(false);
            emailField.setEditable(false);
            passwordField.setEditable(false);
            roleBox.setDisable(true);
            searchField.setEditable(false);
        }

        loadUsers();
    }

    @FXML
    private void handleSaveUser() {
        if (!PermissionService.canManageUsers()) {
            showAlert(Alert.AlertType.WARNING, "Permission refusée", "Vous n'avez pas les droits pour gérer les utilisateurs.");
            return;
        }

        String name = value(nameField);
        String email = value(emailField);
        String role = roleBox.getValue();
        String password = value(passwordField);

        if (name.isEmpty() || email.isEmpty() || role == null || role.isBlank()) {
            showAlert(Alert.AlertType.WARNING, "Validation", "Nom, email et rôle sont obligatoires.");
            return;
        }

        try {
            if (selectedUser == null) {
                if (password.isEmpty()) {
                    showAlert(Alert.AlertType.WARNING, "Validation", "Le mot de passe est obligatoire pour un nouvel utilisateur.");
                    return;
                }

                long id = UserDAO.createUser(name, email, password, role);
                if (id > 0) {
                    addHistory("Création utilisateur", "Utilisateur", id, "Création de " + email);
                    showAlert(Alert.AlertType.INFORMATION, "Succès", "Utilisateur créé avec succès.");
                    resetForm();
                    loadUsers();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de créer l'utilisateur.");
                }
            } else {
                boolean updated = UserDAO.updateUser(selectedUser.getId(), name, email, role, password);
                if (updated) {
                    addHistory("Mise à jour utilisateur", "Utilisateur", selectedUser.getId(), "Mise à jour de " + email);
                    showAlert(Alert.AlertType.INFORMATION, "Succès", "Utilisateur mis à jour avec succès.");
                    resetForm();
                    loadUsers();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de mettre à jour l'utilisateur.");
                }
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la sauvegarde: " + e.getMessage());
        }
    }

    @FXML
    private void handleDeleteUser() {
        if (!PermissionService.canManageUsers()) {
            showAlert(Alert.AlertType.WARNING, "Permission refusée", "Vous n'avez pas les droits pour supprimer les utilisateurs.");
            return;
        }

        if (selectedUser == null) {
            showAlert(Alert.AlertType.WARNING, "Validation", "Sélectionnez un utilisateur à supprimer.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText(null);
        confirm.setContentText("Supprimer l'utilisateur sélectionné ?");

        if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            if (UserDAO.deleteUser(selectedUser.getId())) {
                addHistory("Suppression utilisateur", "Utilisateur", selectedUser.getId(), "Suppression de " + selectedUser.getEmail());
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Utilisateur supprimé.");
                resetForm();
                loadUsers();
            } else {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Suppression impossible.");
            }
        }
    }

    @FXML
    private void handleToggleActive() {
        if (selectedUser == null) {
            showAlert(Alert.AlertType.WARNING, "Validation", "Sélectionnez un utilisateur.");
            return;
        }

        boolean target = !selectedUser.isActive();
        if (UserDAO.setUserActive(selectedUser.getId(), target)) {
            addHistory(target ? "Activation compte" : "Désactivation compte", "Utilisateur", selectedUser.getId(), selectedUser.getEmail());
            loadUsers();
            resetForm();
        } else {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de changer l'état du compte.");
        }
    }

    @FXML
    private void handleSearchUsers() {
        String term = value(searchField);
        if (term.isEmpty()) {
            loadUsers();
            return;
        }

        ObservableList<UserRow> rows = FXCollections.observableArrayList();
        for (Object[] row : UserDAO.searchUsers(term)) {
            rows.add(toRow(row));
        }
        userTable.setItems(rows);
    }

    @FXML
    private void handleResetForm() {
        resetForm();
        loadUsers();
    }

    private void loadUsers() {
        ObservableList<UserRow> rows = FXCollections.observableArrayList();
        for (Object[] row : UserDAO.getAllUsers()) {
            rows.add(toRow(row));
        }
        userTable.setItems(rows);
    }

    private UserRow toRow(Object[] data) {
        long id = (long) data[0];
        String name = (String) data[1];
        String email = (String) data[2];
        String role = (String) data[3];
        boolean active = (boolean) data[4];
        return new UserRow(id, name, email, role, active);
    }

    private void addHistory(String action, String entityType, long entityId, String details) {
        long actorId = SessionContext.getCurrentUser() != null ? SessionContext.getCurrentUser().getId() : 0;
        HistoryDAO.addHistory(actorId, action, entityType, entityId, details);
    }

    private String value(TextInputControl control) {
        return control.getText() == null ? "" : control.getText().trim();
    }

    private void resetForm() {
        selectedUser = null;
        nameField.clear();
        emailField.clear();
        passwordField.clear();
        roleBox.getSelectionModel().clearSelection();
        userTable.getSelectionModel().clearSelection();
        toggleActiveButton.setText("Activer/Désactiver");
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static final class UserRow {
        private final SimpleLongProperty id;
        private final SimpleStringProperty name;
        private final SimpleStringProperty email;
        private final SimpleStringProperty role;
        private final SimpleBooleanProperty active;

        public UserRow(long id, String name, String email, String role, boolean active) {
            this.id = new SimpleLongProperty(id);
            this.name = new SimpleStringProperty(name);
            this.email = new SimpleStringProperty(email);
            this.role = new SimpleStringProperty(role);
            this.active = new SimpleBooleanProperty(active);
        }

        public long getId() { return id.get(); }
        public String getName() { return name.get(); }
        public String getEmail() { return email.get(); }
        public String getRole() { return role.get(); }
        public boolean isActive() { return active.get(); }

        public SimpleStringProperty nameProperty() { return name; }
        public SimpleStringProperty emailProperty() { return email; }
        public SimpleStringProperty roleProperty() { return role; }
        public SimpleStringProperty statusProperty() { return new SimpleStringProperty(active.get() ? "Actif" : "Désactivé"); }
    }
}
