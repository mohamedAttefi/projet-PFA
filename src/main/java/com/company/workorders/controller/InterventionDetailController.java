package com.company.workorders.controller;

import com.company.workorders.model.Intervention;
import com.company.workorders.model.User;
import com.company.workorders.dao.InterventionDAO;
import com.company.workorders.dao.UserDAO;
import com.company.workorders.dao.ClientDAO;
import com.company.workorders.util.AppNavigator;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.awt.Desktop;
import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Controller for the Intervention Detail View
 */
public class InterventionDetailController {

    @FXML
    private Label interventionRefLabel;
    @FXML
    private Label interventionTitleLabel;
    @FXML
    private Label interventionDateLabel;
    @FXML
    private Label priorityLabel;
    @FXML
    private Label clientNameLabel;
    @FXML
    private Label clientCodeLabel;
    @FXML
    private Label locationLabel;
    @FXML
    private Label technicianNameLabel;
    @FXML
    private TextArea descriptionArea;
    @FXML
    private VBox historyContainer;
    @FXML
    private Button closeBtn;

    private Intervention intervention;
    private long interventionId;

    /**
     * Initialize the controller with an intervention ID
     */
    public void initialize(long interventionId) {
        this.interventionId = interventionId;
        loadInterventionDetails();
    }

    /**
     * Load intervention details from database
     */
    private void loadInterventionDetails() {
        intervention = InterventionDAO.getInterventionById(interventionId);

        if (intervention == null) {
            showError("Intervention non trouvée");
            return;
        }

        // Set header information
        interventionRefLabel.setText("INT-" + String.format("%07d", intervention.getId()));
        interventionTitleLabel.setText(intervention.getTitle());
        interventionDateLabel.setText("créé"+intervention.getCreatedAt());


        // Set client information
        var client = ClientDAO.getClientById(intervention.getClientId());
        if (client != null) {
            clientNameLabel.setText(client.getCompanyName());
            clientCodeLabel.setText("SIRET: " + client.getPhone()); // Using phone as SIRET for demo
        }

        // Set location
        locationLabel.setText(intervention.getLocation());

        // Set description
        descriptionArea.setText(intervention.getDescription());
        descriptionArea.setWrapText(true);
        descriptionArea.setEditable(false);

        // Set technician information
        User technician = UserDAO.getUserById(intervention.getAssignedTo());
        if (technician != null) {
            technicianNameLabel.setText(technician.getName());
        }

        // Load history
        loadChangeHistory();

        // Update close button state based on intervention status
        updateCloseButtonState();
    }

    /**
     * Update priority label styling
     */
    private void updatePriorityLabel(String priority) {
        String text = "⚠️ " + intervention.getStatus();

        if ("Haute".equals(priority)) {
            priorityLabel.setStyle(
                    "-fx-background-color: #ffebee; -fx-text-fill: #c8102e; -fx-padding: 6 12; -fx-font-size: 12;");
        } else if ("Normale".equals(priority)) {
            priorityLabel.setStyle(
                    "-fx-background-color: #fff3cd; -fx-text-fill: #856404; -fx-padding: 6 12; -fx-font-size: 12;");
        } else {
            priorityLabel.setStyle(
                    "-fx-background-color: #d4edda; -fx-text-fill: #155724; -fx-padding: 6 12; -fx-font-size: 12;");
        }

        priorityLabel.setText(text);
    }

    /**
     * Load change history from database
     */
    private void loadChangeHistory() {
        historyContainer.getChildren().clear();

        // Sample history items (you can fetch these from database)
        addHistoryItem("📊 Statut modifié en \"En cours\"", "Par Jean-Luc Technicien", "Aujourd'hui, 11:30");
        addHistoryItem("👥 Intervention assignée", "Par Marie Réceptionniste", "24 Mai, 10:45");
        addHistoryItem("📝 Intervention créée", "Par Système (Ticket #4320)", "24 Mai, 09:17");
    }

    /**
     * Add a history item to the display
     */
    private void addHistoryItem(String action, String actor, String timestamp) {
        VBox historyItem = new VBox(4);
        historyItem.setStyle("-fx-padding: 12; -fx-border-color: #e8e8e8; -fx-border-width: 0 0 1 0;");

        Label actionLabel = new Label(action);
        actionLabel.setStyle("-fx-font-size: 11; -fx-font-weight: bold; -fx-text-fill: #2d2d2d;");

        Label actorLabel = new Label(actor);
        actorLabel.setStyle("-fx-font-size: 10; -fx-text-fill: #999;");

        Label timeLabel = new Label(timestamp);
        timeLabel.setStyle("-fx-font-size: 9; -fx-text-fill: #ccc;");

        historyItem.getChildren().addAll(actionLabel, actorLabel, timeLabel);
        historyContainer.getChildren().add(historyItem);
    }

    /**
     * Update close button state based on intervention status
     */
    private void updateCloseButtonState() {
        if ("Terminée".equals(intervention.getStatus()) || "Fermée".equals(intervention.getStatus())) {
            closeBtn.setDisable(true);
            closeBtn.setText("Clôturée");
        }
    }

    /**
     * Handle back button click
     */
    @FXML
    private void handleBack() {
        try {
            // Find and navigate back to interventions view
            javafx.scene.Parent parent = (javafx.scene.Parent) descriptionArea.getScene().getRoot();
            javafx.scene.layout.StackPane contentPane = findContentPane(parent);
            if (contentPane != null) {
                javafx.scene.Parent interventionsView = AppNavigator.loadView("/views/interventions-view.fxml");
                contentPane.getChildren().setAll(interventionsView);
            }
        } catch (Exception e) {
            System.err.println("[InterventionDetail] Error navigating back: " + e.getMessage());
        }
    }

    /**
     * Recursively search for the content pane
     */
    private javafx.scene.layout.StackPane findContentPane(javafx.scene.Node node) {
        if (node instanceof javafx.scene.layout.StackPane) {
            javafx.scene.layout.StackPane pane = (javafx.scene.layout.StackPane) node;
            if (node.getParent() instanceof javafx.scene.layout.BorderPane) {
                return pane;
            }
        }

        if (node instanceof javafx.scene.Parent) {
            javafx.scene.Parent parent = (javafx.scene.Parent) node;
            for (javafx.scene.Node child : parent.getChildrenUnmodifiable()) {
                javafx.scene.layout.StackPane found = findContentPane(child);
                if (found != null) {
                    return found;
                }
            }
        }

        return null;
    }

    /**
     * Handle print button click
     */
    @FXML
    private void handlePrint() {
        System.out.println("[InterventionDetail] Print clicked for intervention: " + intervention.getId());
        showInfo("Impression de l'intervention en cours...");
    }

    /**
     * Handle close button click
     */
    @FXML
    private void handleClose() {
        if (showConfirmation("Êtes-vous sûr de vouloir clôturer cette intervention?")) {
            // Update intervention status to "Fermée"
            InterventionDAO.updateInterventionStatus(intervention.getId(), "Fermée");
            showInfo("Intervention clôturée avec succès");
            updateCloseButtonState();
            addHistoryItem("🔒 Intervention clôturée", "Par vous", "Maintenant");
        }
    }

    /**
     * Handle modify status
     */
    @FXML
    private void handleModifyStatus() {
        System.out.println("[InterventionDetail] Modify status clicked");
        showInfo("Fonction à implémenter");
    }

    /**
     * Handle reassign
     */
    @FXML
    private void handleReassign() {
        System.out.println("[InterventionDetail] Reassign clicked");
        showInfo("Fonction à implémenter");
    }

    /**
     * Handle modify intervention
     */
    @FXML
    private void handleModify() {
        System.out.println("[InterventionDetail] Modify clicked");
        showInfo("Fonction à implémenter");
    }

    /**
     * Handle cancel intervention
     */
    @FXML
    private void handleCancel() {
        if (showConfirmation("Êtes-vous sûr de vouloir annuler cette intervention?")) {
            showInfo("Intervention annulée");
        }
    }

    /**
     * Handle show map
     */
    @FXML
    private void handleShowMap() {
        try {
            String address = locationLabel.getText().replace(" ", "+");
            String url = "https://www.google.com/maps/search/" + address;
            Desktop.getDesktop().browse(new URI(url));
        } catch (Exception e) {
            showError("Impossible d'ouvrir la carte: " + e.getMessage());
        }
    }

    /**
     * Handle call technician
     */
    @FXML
    private void handleCallTechnician() {
        System.out.println("[InterventionDetail] Call technician");
        showInfo("Appel au technicien...");
    }

    /**
     * Handle message technician
     */
    @FXML
    private void handleMessageTechnician() {
        System.out.println("[InterventionDetail] Message technician");
        showInfo("Envoi d'un message au technicien...");
    }

    /**
     * Handle email technician
     */
    @FXML
    private void handleEmailTechnician() {
        System.out.println("[InterventionDetail] Email technician");
        showInfo("Envoi d'un email au technicien...");
    }

    /**
     * Show error dialog
     */
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Show info dialog
     */
    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Show confirmation dialog
     */
    private boolean showConfirmation(String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText(null);
        alert.setContentText(message);
        var result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }
}
