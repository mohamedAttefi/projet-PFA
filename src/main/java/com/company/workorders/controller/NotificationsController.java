package com.company.workorders.controller;

import com.company.workorders.dao.NotificationDAO;
import com.company.workorders.service.SessionContext;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.layout.VBox;

public class NotificationsController {

    @FXML private VBox notificationList;

    @FXML
    public void initialize() {
        reloadNotifications();
    }

    @FXML
    private void handleMarkAllRead() {
        long userId = SessionContext.getCurrentUser() != null ? SessionContext.getCurrentUser().getId() : 0;
        if (NotificationDAO.markAllAsRead(userId)) {
            reloadNotifications();
            showInfo("Notifications", "Toutes les notifications ont été marquées comme lues.");
        }
    }

    @FXML
    private void handleSettings() {
        showInfo("Paramètres", "Paramètres de notification - Fonctionnalité à implémenter");
    }

    private void reloadNotifications() {
        // The notifications are now static in the FXML
        // This method can be used to dynamically load notifications in the future
        // For now, the FXML contains the sample notification cards
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
