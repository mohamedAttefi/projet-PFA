package com.company.workorders.controller;

import com.company.workorders.dao.NotificationDAO;
import com.company.workorders.dao.InterventionDAO;
import com.company.workorders.service.SessionContext;
import com.company.workorders.model.Intervention;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Region;
import javafx.geometry.Insets;

import java.util.List;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

public class NotificationsController {

    @FXML private VBox notificationList;
    @FXML private Label urgentCountLabel;
    @FXML private Label inProgressCountLabel;
    @FXML private Label totalNotificationsLabel;

    @FXML
    public void initialize() {
        reloadNotifications();
        updateStatistics();
    }

    @FXML
    private void handleMarkAllRead() {
        long userId = SessionContext.getCurrentUser() != null ? SessionContext.getCurrentUser().getId() : 0;
        if (NotificationDAO.markAllAsRead(userId)) {
            reloadNotifications();
            updateStatistics();
            showInfo("Notifications", "Toutes les notifications ont été marquées comme lues.");
        }
    }

    @FXML
    private void handleSettings() {
        showInfo("Paramètres", "Paramètres de notification - Fonctionnalité à implémenter");
    }

    private void reloadNotifications() {
        if (notificationList == null) return;
        
        long userId = SessionContext.getCurrentUser() != null ? SessionContext.getCurrentUser().getId() : 0;
        
        // Clear existing notifications
        notificationList.getChildren().clear();
        
        // Load real notifications from database
        List<String> notifications = NotificationDAO.getNotificationsForUser(userId);
        
        if (notifications.isEmpty()) {
            // Show empty state
            VBox emptyState = createEmptyStateCard();
            notificationList.getChildren().add(emptyState);
        } else {
            // Display real notifications
            for (String notification : notifications) {
                VBox notificationCard = createNotificationCard(notification);
                notificationList.getChildren().add(notificationCard);
            }
        }
        
        // Add load more button if there are many notifications
        if (notifications.size() > 10) {
            HBox loadMoreButton = createLoadMoreButton();
            notificationList.getChildren().add(loadMoreButton);
        }
    }

    private void updateStatistics() {
        try {
            // Get real statistics from database
            List<Intervention> urgentInterventions = InterventionDAO.searchInterventions(
                "", "Nouvelle,Assignée,En cours", "Urgente,Critique", null, false, 0
            );
            
            List<Intervention> inProgressInterventions = InterventionDAO.searchInterventions(
                "", "En cours", "Toutes", null, false, 0
            );
            
            // Update statistics labels
            if (urgentCountLabel != null) {
                urgentCountLabel.setText(String.format("%02d", urgentInterventions.size()));
            }
            
            if (inProgressCountLabel != null) {
                inProgressCountLabel.setText(String.format("%02d", inProgressInterventions.size()));
            }
            
            if (totalNotificationsLabel != null) {
                long userId = SessionContext.getCurrentUser() != null ? SessionContext.getCurrentUser().getId() : 0;
                List<String> allNotifications = NotificationDAO.getNotificationsForUser(userId);
                int unreadCount = (int) allNotifications.stream()
                    .filter(n -> n.startsWith("[NOUVEAU]"))
                    .count();
                totalNotificationsLabel.setText(String.format("%02d", unreadCount));
            }
            
        } catch (Exception e) {
            System.err.println("[NotificationsController] Error updating statistics: " + e.getMessage());
        }
    }

    private VBox createNotificationCard(String notificationText) {
        VBox card = new VBox(12);
        card.setStyle("-fx-background-color: white; -fx-border-color: #ebe2de; -fx-border-width: 2 0 0 0; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 16;");
        
        // Parse notification text
        boolean isRead = notificationText.startsWith("[LU]");
        String type = isRead ? "LU" : "NOUVEAU";
        String content = notificationText.replaceFirst("\\[(LU|NOUVEAU)\\] ", "");
        
        // Extract timestamp and message
        String[] parts = content.split(" - ", 2);
        String timestamp = parts.length > 1 ? parts[0] : "Date inconnue";
        String message = parts.length > 1 ? parts[1] : content;
        
        // Determine color based on content
        String borderColor = "#8a7b76"; // Default gray
        String typeColor = "#8a7b76";
        String icon = "⚪";
        
        if (message.toLowerCase().contains("urgent") || message.toLowerCase().contains("critique")) {
            borderColor = "#c8102e";
            typeColor = "#c8102e";
            icon = "🔴";
        } else if (message.toLowerCase().contains("en cours") || message.toLowerCase().contains("mise à jour")) {
            borderColor = "#f39c12";
            typeColor = "#f39c12";
            icon = "🟡";
        } else if (message.toLowerCase().contains("nouveau") || message.toLowerCase().contains("créée")) {
            borderColor = "#27ae60";
            typeColor = "#27ae60";
            icon = "🟢";
        }
        
        card.setStyle("-fx-background-color: white; -fx-border-color: " + borderColor + "; -fx-border-width: 2 0 0 0; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 16;");
        
        // Header
        HBox header = new HBox(8);
        header.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        
        Label typeLabel = new Label(type);
        typeLabel.setStyle("-fx-font-size: 10; -fx-text-fill: " + typeColor + "; -fx-font-weight: bold;");
        
        Label timeLabel = new Label(formatTimestamp(timestamp));
        timeLabel.setStyle("-fx-font-size: 10; -fx-text-fill: #7d6d68;");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
        
        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size: 12;");
        
        header.getChildren().addAll(typeLabel, timeLabel, spacer, iconLabel);
        
        // Content
        Label messageLabel = new Label(message);
        messageLabel.setStyle("-fx-font-size: 14; -fx-font-weight: bold; -fx-text-fill: #2d2d2d;");
        messageLabel.setWrapText(true);
        
        // Add action buttons for unread notifications
        if (!isRead) {
            HBox actionButtons = new HBox(8);
            Button markReadButton = new Button("Marquer comme lu");
            markReadButton.setStyle("-fx-background-color: #c8102e; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
            markReadButton.setOnAction(e -> markNotificationAsRead(notificationText));
            
            Button detailsButton = new Button("Voir détails");
            detailsButton.setStyle("-fx-background-color: white; -fx-border-color: #ebe2de; -fx-text-fill: #2d2d2d; -fx-cursor: hand;");
            detailsButton.setOnAction(e -> showNotificationDetails(notificationText));
            
            actionButtons.getChildren().addAll(markReadButton, detailsButton);
            card.getChildren().addAll(header, messageLabel, actionButtons);
        } else {
            card.getChildren().addAll(header, messageLabel);
        }
        
        return card;
    }

    private VBox createEmptyStateCard() {
        VBox card = new VBox(16);
        card.setAlignment(javafx.geometry.Pos.CENTER);
        card.setStyle("-fx-background-color: white; -fx-border-color: #ebe2de; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 40;");
        
        Label emptyIcon = new Label("📭");
        emptyIcon.setStyle("-fx-font-size: 48;");
        
        Label emptyTitle = new Label("Aucune notification");
        emptyTitle.setStyle("-fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: #2d2d2d;");
        
        Label emptyMessage = new Label("Vous n'avez aucune notification pour le moment.\nLes nouvelles notifications apparaîtront ici.");
        emptyMessage.setStyle("-fx-font-size: 14; -fx-text-fill: #7d6d68;");
        emptyMessage.setWrapText(true);
        
        card.getChildren().addAll(emptyIcon, emptyTitle, emptyMessage);
        return card;
    }

    private HBox createLoadMoreButton() {
        HBox buttonContainer = new HBox();
        buttonContainer.setAlignment(javafx.geometry.Pos.CENTER);
        
        Button loadMoreButton = new Button("Charger les notifications précédentes");
        loadMoreButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #7d6d68; -fx-cursor: hand;");
        loadMoreButton.setOnAction(e -> showInfo("Historique", "Fonctionnalité de chargement d'historique à implémenter"));
        
        buttonContainer.getChildren().add(loadMoreButton);
        return buttonContainer;
    }

    private String formatTimestamp(String timestamp) {
        try {
            // Parse the timestamp from database format and format it nicely
            if (timestamp.matches("\\d{2}/\\d{2}/\\d{4} \\d{2}:\\d{2}")) {
                DateTimeFormatter inputFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                DateTimeFormatter outputFormat = DateTimeFormatter.ofPattern("dd MMM HH:mm");
                
                LocalDateTime dateTime = LocalDateTime.parse(timestamp, inputFormat);
                return dateTime.format(outputFormat);
            }
        } catch (Exception e) {
            // If parsing fails, return original timestamp
        }
        return timestamp;
    }

    private void markNotificationAsRead(String notificationText) {
        // This would typically update the specific notification in the database
        // For now, we'll mark all as read
        handleMarkAllRead();
    }

    private void showNotificationDetails(String notificationText) {
        showInfo("Détails de la notification", notificationText);
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
