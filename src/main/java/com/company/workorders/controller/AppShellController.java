package com.company.workorders.controller;

import com.company.workorders.model.UserRole;
import com.company.workorders.service.PermissionService;
import com.company.workorders.service.SessionContext;
import com.company.workorders.util.AppNavigator;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

/**
 * Controller for the main enterprise shell with sidebar navigation.
 */
public class AppShellController {

    @FXML
    private StackPane contentPane;

    @FXML
    private Label userLabel;

    @FXML
    private Button dashboardBtn;

    @FXML
    private Button clientsBtn;

    @FXML
    private Button interventionsBtn;

    @FXML
    private Button usersBtn;

    @FXML
    private Button profileBtn;

    @FXML
    private Button notificationsBtn;

    @FXML
    private Button historyBtn;

    @FXML
    private Button logoutBtn;

    @FXML
    public void initialize() {
        var currentUser = SessionContext.getCurrentUser();
        if (userLabel != null && currentUser != null) {
            userLabel.setText(currentUser.getName() == null || currentUser.getName().isBlank()
                    ? currentUser.getEmail()
                    : currentUser.getName());
        }

        // Set visibility based on permissions
        updateMenuVisibility(currentUser != null ? currentUser.getRole() : UserRole.RECEPTIONIST);

        loadDashboard();
    }

    private void updateMenuVisibility(UserRole role) {
        if (dashboardBtn != null) {
            dashboardBtn.setVisible(true);
            dashboardBtn.setManaged(true);
        }

        // Clients: Receptionist + Admin
        boolean canSeeClients = role == UserRole.RECEPTIONIST || role == UserRole.ADMINISTRATOR;
        if (clientsBtn != null) {
            clientsBtn.setVisible(canSeeClients);
            clientsBtn.setManaged(canSeeClients);
        }

        // Interventions: All roles
        if (interventionsBtn != null) {
            interventionsBtn.setVisible(true);
            interventionsBtn.setManaged(true);
        }

        // Users: Admin only
        boolean canSeeUsers = role == UserRole.ADMINISTRATOR;
        if (usersBtn != null) {
            usersBtn.setVisible(canSeeUsers);
            usersBtn.setManaged(canSeeUsers);
        }

        if (profileBtn != null) {
            profileBtn.setVisible(true);
            profileBtn.setManaged(true);
        }

        // Notifications: All roles
        if (notificationsBtn != null) {
            notificationsBtn.setVisible(true);
            notificationsBtn.setManaged(true);
        }

        // History: All roles
        if (historyBtn != null) {
            historyBtn.setVisible(true);
            historyBtn.setManaged(true);
        }

        // Logout: All roles
        if (logoutBtn != null) {
            logoutBtn.setVisible(true);
            logoutBtn.setManaged(true);
        }
    }

    @FXML
    private void loadDashboard() {
        loadView("/views/dashboard-view.fxml");
    }

    @FXML
    private void loadClients() {
        if (!PermissionService.canManageClients()) {
            showAccessDenied("Vous n'avez pas accès aux clients");
            return;
        }
        loadView("/views/clients-view.fxml");
    }

    @FXML
    private void loadInterventions() {
        loadView("/views/interventions-view.fxml");
    }

    @FXML
    private void loadUsers() {
        if (!PermissionService.canManageUsers()) {
            showAccessDenied("Vous n'avez pas accès à la gestion des utilisateurs");
            return;
        }
        loadView("/views/users-view.fxml");
    }

    @FXML
    private void loadNotifications() {
        loadView("/views/notifications-view.fxml");
    }

    @FXML
    private void loadProfile() {
        loadView("/views/profile-view.fxml");
    }

    @FXML
    private void loadHistory() {
        loadView("/views/history-view.fxml");
    }

    @FXML
    private void handleLogout() {
        SessionContext.clear();
        AppNavigator.showLogin();
    }

    /**
     * Load intervention detail view with a specific intervention ID
     */
    public void loadInterventionDetail(long interventionId) {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                    getClass().getResource("/views/intervention-detail-view.fxml")
            );
            javafx.scene.Parent root = loader.load();
            
            // Get controller and initialize with intervention ID
            InterventionDetailController controller = loader.getController();
            controller.initialize(interventionId);
            
            contentPane.getChildren().setAll(root);
        } catch (Exception e) {
            System.err.println("[AppShellController] Error loading intervention detail: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadView(String fxmlPath) {
        contentPane.getChildren().setAll(AppNavigator.loadView(fxmlPath));
    }

    private void showAccessDenied(String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.WARNING);
        alert.setTitle("Accès refusé");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}