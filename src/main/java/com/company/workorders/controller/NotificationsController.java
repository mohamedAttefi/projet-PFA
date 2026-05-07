package com.company.workorders.controller;

import com.company.workorders.dao.NotificationDAO;
import com.company.workorders.service.SessionContext;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;

public class NotificationsController {

    @FXML private ListView<String> notificationList;

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

    private void reloadNotifications() {
        long userId = SessionContext.getCurrentUser() != null ? SessionContext.getCurrentUser().getId() : 0;
        notificationList.getItems().setAll(NotificationDAO.getNotificationsForUser(userId));
        if (notificationList.getItems().isEmpty()) {
            notificationList.getItems().add("Aucune notification pour le moment.");
        }
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
