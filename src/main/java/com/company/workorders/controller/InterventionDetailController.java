package com.company.workorders.controller;

import com.company.workorders.model.Intervention;
import com.company.workorders.model.User;
import com.company.workorders.model.InterventionComment;
import com.company.workorders.dao.InterventionDAO;
import com.company.workorders.dao.UserDAO;
import com.company.workorders.dao.ClientDAO;
import com.company.workorders.dao.InterventionCommentDAO;
import com.company.workorders.util.AppNavigator;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.GridPane;
import javafx.geometry.Insets;

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
    private Label commentCountLabel;
    @FXML
    private ScrollPane commentsScrollPane;
    @FXML
    private VBox commentsContainer;
    @FXML
    private Label noCommentsLabel;
    @FXML
    private TextArea newCommentArea;
    @FXML
    private ComboBox<String> commentTypeComboBox;
    @FXML
    private Button closeBtn;

    private Intervention intervention;
    private long interventionId;

    /**
     * Initialize the controller with an intervention ID
     */
    public void initialize(long interventionId) {
        this.interventionId = interventionId;
        
        // Create comments table if it doesn't exist
        InterventionCommentDAO.createTableIfNotExists();
        
        initializeCommentSection();
        loadInterventionDetails();
        loadComments();
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
        interventionDateLabel.setText("Créé " + intervention.getFormattedCreatedAt());


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
        ChoiceDialog<String> dialog = new ChoiceDialog<>();
        dialog.setTitle("Modifier le statut");
        dialog.setHeaderText("Changer le statut de l'intervention");
        dialog.setContentText("Nouveau statut:");

        java.util.List<String> statuses = java.util.Arrays.asList(
            "Nouvelle", "Assignée", "En cours", "Terminée", "Fermée", "Annulée"
        );
        dialog.getItems().setAll(statuses);
        dialog.setSelectedItem(intervention.getStatus());

        dialog.showAndWait().ifPresent(newStatus -> {
            try {
                boolean success = InterventionDAO.updateInterventionStatus(intervention.getId(), newStatus);
                if (success) {
                    showInfo("Statut modifié avec succès en: " + newStatus);
                    intervention.setStatus(newStatus);
                    updateCloseButtonState();
                    loadChangeHistory();
                } else {
                    showError("Impossible de modifier le statut");
                }
            } catch (Exception e) {
                showError("Erreur lors de la modification: " + e.getMessage());
            }
        });
    }

    /**
     * Handle reassign
     */
    @FXML
    private void handleReassign() {
        try {
            java.util.List<Object[]> technicians = InterventionDAO.getAllTechnicians();
            ChoiceDialog<String> dialog = new ChoiceDialog<>();
            dialog.setTitle("Réassigner l'intervention");
            dialog.setHeaderText("Assigner à un nouveau technicien");
            dialog.setContentText("Technicien:");

            java.util.List<String> techNames = new java.util.ArrayList<>();
            techNames.add("Non assigné");
            for (Object[] tech : technicians) {
                techNames.add((String) tech[1]);
            }

            dialog.getItems().setAll(techNames);
            dialog.setSelectedItem(technicianNameLabel.getText());

            dialog.showAndWait().ifPresent(selectedTech -> {
                try {
                    Long technicianId = null;
                    if (!"Non assigné".equals(selectedTech)) {
                        for (Object[] tech : technicians) {
                            if (selectedTech.equals(tech[1])) {
                                technicianId = (Long) tech[0];
                                break;
                            }
                        }
                    }

                    boolean success = InterventionDAO.updateIntervention(
                        intervention.getId(),
                        intervention.getTitle(),
                        intervention.getDescription(),
                        intervention.getPriority(),
                        intervention.getStatus(),
                        intervention.getLocation(),
                        technicianId != null ? technicianId : 0
                    );

                    if (success) {
                        showInfo("Intervention réassignée à: " + selectedTech);
                        technicianNameLabel.setText(selectedTech);
                        loadChangeHistory();
                    } else {
                        showError("Impossible de réassigner l'intervention");
                    }
                } catch (Exception e) {
                    showError("Erreur lors de la réassignation: " + e.getMessage());
                }
            });
        } catch (Exception e) {
            showError("Erreur lors du chargement des techniciens: " + e.getMessage());
        }
    }

    /**
     * Handle modify intervention
     */
    @FXML
    private void handleModify() {
        // Create a simple edit dialog
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Modifier l'intervention");
        dialog.setHeaderText("Modifier les informations de l'intervention");

        // Create the form
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField titleField = new TextField(intervention.getTitle());
        TextArea descField = new TextArea(intervention.getDescription());
        descField.setWrapText(true);
        descField.setPrefRowCount(4);
        TextField locationField = new TextField(intervention.getLocation());

        // Add validation
        Label titleError = new Label();
        titleError.setStyle("-fx-text-fill: red; -fx-font-size: 10;");
        Label descError = new Label();
        descError.setStyle("-fx-text-fill: red; -fx-font-size: 10;");
        Label locationError = new Label();
        locationError.setStyle("-fx-text-fill: red; -fx-font-size: 10;");

        grid.add(new Label("Titre:"), 0, 0);
        grid.add(titleField, 1, 0);
        grid.add(titleError, 2, 0);
        grid.add(new Label("Description:"), 0, 1);
        grid.add(descField, 1, 1);
        grid.add(descError, 2, 1);
        grid.add(new Label("Localisation:"), 0, 2);
        grid.add(locationField, 1, 2);
        grid.add(locationError, 2, 2);

        dialog.getDialogPane().setContent(grid);

        // Add buttons
        ButtonType saveButtonType = new ButtonType("Sauvegarder", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Annuler", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, cancelButtonType);

        // Enable/disable save button based on validation
        Button saveButton = (Button) dialog.getDialogPane().lookupButton(saveButtonType);
        saveButton.setDisable(false);

        // Add validation listeners
        titleField.textProperty().addListener((obs, oldVal, newVal) -> {
            boolean isValid = validateTitle(newVal);
            titleError.setText(isValid ? "" : "Le titre est requis (min. 3 caractères)");
            saveButton.setDisable(!isValid || validateDescription(descField.getText()) || validateLocation(locationField.getText()));
        });

        descField.textProperty().addListener((obs, oldVal, newVal) -> {
            boolean isValid = validateDescription(newVal);
            descError.setText(isValid ? "" : "Description requise (min. 10 caractères)");
            saveButton.setDisable(!validateTitle(titleField.getText()) || !isValid || validateLocation(locationField.getText()));
        });

        locationField.textProperty().addListener((obs, oldVal, newVal) -> {
            boolean isValid = validateLocation(newVal);
            locationError.setText(isValid ? "" : "Localisation requise (min. 5 caractères)");
            saveButton.setDisable(!validateTitle(titleField.getText()) || validateDescription(descField.getText()) || !isValid);
        });

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                // Validate before saving
                if (!validateTitle(titleField.getText()) || !validateDescription(descField.getText()) || !validateLocation(locationField.getText())) {
                    showError("Veuillez corriger les erreurs de validation avant de sauvegarder");
                    return null;
                }

                try {
                    boolean success = InterventionDAO.updateIntervention(
                        intervention.getId(),
                        titleField.getText().trim(),
                        descField.getText().trim(),
                        intervention.getPriority(),
                        intervention.getStatus(),
                        locationField.getText().trim(),
                        intervention.getAssignedTo()
                    );

                    if (success) {
                        intervention.setTitle(titleField.getText().trim());
                        intervention.setDescription(descField.getText().trim());
                        intervention.setLocation(locationField.getText().trim());
                        
                        // Update UI
                        interventionTitleLabel.setText(titleField.getText().trim());
                        descriptionArea.setText(descField.getText().trim());
                        locationLabel.setText(locationField.getText().trim());
                        
                        showInfo("Intervention modifiée avec succès");
                        loadChangeHistory();
                    } else {
                        showError("Impossible de modifier l'intervention - Vérifiez les permissions");
                    }
                } catch (Exception e) {
                    System.err.println("Error modifying intervention: " + e.getMessage());
                    e.printStackTrace();
                    showError("Erreur lors de la modification: " + e.getMessage());
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    /**
     * Validate title field
     */
    private boolean validateTitle(String title) {
        return title != null && title.trim().length() >= 3 && title.trim().length() <= 200;
    }

    /**
     * Validate description field
     */
    private boolean validateDescription(String description) {
        return description != null && description.trim().length() >= 10 && description.trim().length() <= 2000;
    }

    /**
     * Validate location field
     */
    private boolean validateLocation(String location) {
        return location != null && location.trim().length() >= 5 && location.trim().length() <= 500;
    }

    /**
     * Handle cancel intervention
     */
    @FXML
    private void handleCancel() {
        if (showConfirmation("Êtes-vous sûr de vouloir annuler cette intervention? Cette action est irréversible.")) {
            try {
                boolean success = InterventionDAO.updateInterventionStatus(intervention.getId(), "Annulée");
                if (success) {
                    showInfo("Intervention annulée avec succès");
                    intervention.setStatus("Annulée");
                    updateCloseButtonState();
                    loadChangeHistory();
                } else {
                    showError("Impossible d'annuler l'intervention");
                }
            } catch (Exception e) {
                showError("Erreur lors de l'annulation: " + e.getMessage());
            }
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

    /**
     * Initialize comment section
     */
    private void initializeCommentSection() {
        if (commentTypeComboBox != null) {
            commentTypeComboBox.getItems().addAll("INTERNE", "CLIENT", "MISE À JOUR STATUT", "NOTE");
            commentTypeComboBox.setValue("INTERNE");
        }
    }

    /**
     * Load comments for the intervention
     */
    private void loadComments() {
        try {
            java.util.List<InterventionComment> comments = InterventionCommentDAO.getCommentsByInterventionId(interventionId);
            
            if (commentsContainer != null) {
                commentsContainer.getChildren().clear();
                
                if (comments.isEmpty()) {
                    if (noCommentsLabel != null) {
                        noCommentsLabel.setVisible(true);
                    }
                } else {
                    if (noCommentsLabel != null) {
                        noCommentsLabel.setVisible(false);
                    }
                    
                    for (InterventionComment comment : comments) {
                        commentsContainer.getChildren().add(createCommentNode(comment));
                    }
                }
                
                // Update comment count
                if (commentCountLabel != null) {
                    commentCountLabel.setText("(" + comments.size() + ")");
                }
                
                // Scroll to bottom
                if (commentsScrollPane != null) {
                    commentsScrollPane.setVvalue(1.0);
                }
            }
        } catch (Exception e) {
            System.err.println("[InterventionDetailController] Error loading comments: " + e.getMessage());
        }
    }

    /**
     * Create UI node for a comment
     */
    private VBox createCommentNode(InterventionComment comment) {
        VBox commentBox = new VBox(8);
        commentBox.setStyle("-fx-background-color: #f8f9fa; -fx-padding: 12; -fx-border-radius: 8;");
        
        // Comment header
        HBox headerBox = new HBox(8);
        headerBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        
        Label authorLabel = new Label(comment.getUserName());
        authorLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #2d2d2d; -fx-font-size: 12;");
        
        Label typeLabel = new Label(comment.getCommentType());
        typeLabel.setStyle("-fx-background-color: #e3f2fd; -fx-text-fill: white; -fx-padding: 2 6; -fx-border-radius: 4; -fx-font-size: 10;");
        
        Label timeLabel = new Label(formatCommentTime(comment.getCreatedAt()));
        timeLabel.setStyle("-fx-text-fill: #999; -fx-font-size: 11;");
        
        headerBox.getChildren().addAll(authorLabel, typeLabel, new javafx.scene.layout.Region(), timeLabel);
        
        // Comment content
        Label contentLabel = new Label(comment.getContent());
        contentLabel.setStyle("-fx-text-fill: #2d2d2d; -fx-font-size: 12; -fx-wrap-text: true;");
        contentLabel.setMaxWidth(Double.MAX_VALUE);
        
        commentBox.getChildren().addAll(headerBox, contentLabel);
        
        return commentBox;
    }

    /**
     * Format comment time for display
     */
    private String formatCommentTime(java.time.LocalDateTime dateTime) {
        if (dateTime == null) return "";
        
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        java.time.Duration duration = java.time.Duration.between(dateTime, now);
        
        if (duration.toMinutes() < 1) {
            return "À l'instant";
        } else if (duration.toHours() < 1) {
            return "Il y a " + duration.toMinutes() + " min";
        } else if (duration.toDays() < 1) {
            return "Il y a " + duration.toHours() + " h";
        } else {
            return dateTime.format(java.time.format.DateTimeFormatter.ofPattern("dd MMM yyyy à HH:mm"));
        }
    }

    /**
     * Handle adding a new comment
     */
    @FXML
    private void handleAddComment() {
        String content = newCommentArea != null ? newCommentArea.getText().trim() : "";
        String commentType = commentTypeComboBox != null ? commentTypeComboBox.getValue() : "INTERNE";
        
        if (content.isEmpty()) {
            showAlert("Commentaire requis", "Veuillez entrer un commentaire avant de l'ajouter.");
            return;
        }
        
        try {
            // Get current user ID (you may need to adjust this based on your auth system)
            long currentUserId = getCurrentUserId();
            
            // Create the comment
            long commentId = InterventionCommentDAO.createComment(interventionId, currentUserId, content, commentType);
            
            if (commentId > 0) {
                // Clear the comment area
                if (newCommentArea != null) {
                    newCommentArea.clear();
                }
                
                // Reload comments
                loadComments();
                
                showInfo("Commentaire ajouté avec succès");
            } else {
                showError("Erreur lors de l'ajout du commentaire");
            }
        } catch (Exception e) {
            System.err.println("[InterventionDetailController] Error adding comment: " + e.getMessage());
            showError("Erreur lors de l'ajout du commentaire");
        }
    }

    /**
     * Get current user ID (implement based on your authentication system)
     */
    private long getCurrentUserId() {
        // This is a placeholder - implement based on your auth system
        // For now, return 1 as a default
        return 1L;
    }

    /**
     * Show alert dialog
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
