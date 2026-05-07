package com.company.workorders.controller;

import com.company.workorders.dao.ClientDAO;
import com.company.workorders.dao.HistoryDAO;
import com.company.workorders.dao.InterventionDAO;
import com.company.workorders.dao.NotificationDAO;
import com.company.workorders.model.Client;
import com.company.workorders.model.Intervention;
import com.company.workorders.service.PermissionService;
import com.company.workorders.service.SessionContext;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleLongProperty;

public class InterventionsController {

    @FXML private TextField titleField;
    @FXML private TextArea descriptionField;
    @FXML private TextField locationField;
    @FXML private ComboBox<String> priorityBox;
    @FXML private ComboBox<String> statusBox;
    @FXML private ComboBox<String> clientBox;
    @FXML private ComboBox<String> assigneeBox;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> filterStatusBox;
    @FXML private ComboBox<String> filterPriorityBox;
    @FXML private ComboBox<String> filterTechnicianBox;
    @FXML private TableView<InterventionRow> interventionTable;
    @FXML private TableColumn<InterventionRow, String> titleColumn;
    @FXML private TableColumn<InterventionRow, String> priorityColumn;
    @FXML private TableColumn<InterventionRow, String> statusColumn;
    @FXML private TableColumn<InterventionRow, String> clientColumn;
    @FXML private TableColumn<InterventionRow, String> assignedColumn;
    @FXML private Button saveButton;
    @FXML private Button deleteButton;
    @FXML private Button resetButton;

    private InterventionRow selectedIntervention = null;
    private java.util.Map<String, Long> clientMap = new java.util.HashMap<>();
    private java.util.Map<String, Long> technicianMap = new java.util.HashMap<>();

    @FXML
    public void initialize() {
        priorityBox.getItems().addAll("Basse", "Normale", "Haute", "Critique");
        statusBox.getItems().addAll("Nouvelle", "Assignée", "En cours", "Terminée", "Fermée", "Annulée");

        filterStatusBox.getItems().addAll("Tous", "Nouvelle", "Assignée", "En cours", "Terminée", "Fermée", "Annulée");
        filterStatusBox.setValue("Tous");
        filterPriorityBox.getItems().addAll("Toutes", "Basse", "Normale", "Haute", "Critique", "Urgente");
        filterPriorityBox.setValue("Toutes");
        filterTechnicianBox.getItems().add("Tous");
        filterTechnicianBox.setValue("Tous");

        titleColumn.setCellValueFactory(cell -> cell.getValue().titleProperty());
        priorityColumn.setCellValueFactory(cell -> cell.getValue().priorityProperty());
        statusColumn.setCellValueFactory(cell -> cell.getValue().statusProperty());
        clientColumn.setCellValueFactory(cell -> cell.getValue().clientProperty());
        assignedColumn.setCellValueFactory(cell -> cell.getValue().assignedProperty());

        // Load clients
        loadClients();

        // Load technicians
        loadTechnicians();

        // Load interventions
        loadInterventions();

        // Table selection listener - double-click to open detail view
        interventionTable.setOnMouseClicked(event -> {
            InterventionRow selected = interventionTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                selectedIntervention = selected;
                
                // Double-click: open detail view
                if (event.getClickCount() == 2) {
                    openInterventionDetail(selected.getId());
                    return;
                }
                
                // Single-click: load into form
                titleField.setText(selected.getTitle());
                descriptionField.setText(selected.getDescription());
                locationField.setText(selected.getLocation());
                priorityBox.setValue(selected.getPriority());
                statusBox.setValue(selected.getStatus());
                clientBox.setValue(selected.getClient());
                assigneeBox.setValue(selected.getAssigned());
            }
        });

        // Check permissions
        boolean canCreate = PermissionService.canCreateIntervention();
        boolean canModify = PermissionService.canModifyIntervention();
        boolean canDelete = PermissionService.canDeleteIntervention();

        saveButton.setDisable(!(canCreate || canModify));
        deleteButton.setDisable(!canDelete);

        if (!canCreate && !canModify) {
            titleField.setEditable(false);
            descriptionField.setEditable(false);
            locationField.setEditable(false);
            priorityBox.setDisable(true);
            statusBox.setDisable(true);
            clientBox.setDisable(true);
            assigneeBox.setDisable(true);
        }
    }

    private void loadClients() {
        java.util.List<Client> clients = ClientDAO.getAllClients();
        clientMap.clear();
        clientBox.getItems().clear();
        for (Client client : clients) {
            clientBox.getItems().add(client.getCompanyName());
            clientMap.put(client.getCompanyName(), client.getId());
        }
    }

    private void loadTechnicians() {
        java.util.List<Object[]> technicians = InterventionDAO.getAllTechnicians();
        technicianMap.clear();
        assigneeBox.getItems().clear();
        for (Object[] tech : technicians) {
            String name = (String) tech[1];
            Long id = (Long) tech[0];
            assigneeBox.getItems().add(name);
            technicianMap.put(name, id);
            filterTechnicianBox.getItems().add(name);
        }
    }

    private void loadInterventions() {
        String term = searchField != null ? searchField.getText() : "";
        String status = filterStatusBox != null ? filterStatusBox.getValue() : "Tous";
        String priority = filterPriorityBox != null ? filterPriorityBox.getValue() : "Toutes";
        String technician = filterTechnicianBox != null ? filterTechnicianBox.getValue() : "Tous";

        Long technicianId = null;
        if (technician != null && !"Tous".equalsIgnoreCase(technician)) {
            technicianId = technicianMap.get(technician);
        }

        java.util.List<Intervention> interventions = InterventionDAO.searchInterventions(
                term,
                status,
                priority,
                technicianId,
                PermissionService.canViewOnlyAssignedInterventions(),
                PermissionService.getCurrentUserId()
        );

        ObservableList<InterventionRow> rows = FXCollections.observableArrayList();
        for (Intervention intervention : interventions) {
            rows.add(new InterventionRow(
                    intervention.getId(),
                    intervention.getTitle(),
                    intervention.getDescription(),
                    intervention.getPriority(),
                    intervention.getStatus(),
                    intervention.getLocation(),
                    intervention.getClientName() != null ? intervention.getClientName() : "N/A",
                    intervention.getTechnicianName() != null ? intervention.getTechnicianName() : "Non assigné"
            ));
        }
        interventionTable.setItems(rows);
    }

    @FXML
    private void handleSaveIntervention() {
        String title = titleField.getText().trim();
        String description = descriptionField.getText().trim();
        String location = locationField.getText().trim();
        String priority = priorityBox.getValue();
        String status = statusBox.getValue();
        String clientName = clientBox.getValue();
        String assigneeName = assigneeBox.getValue();

        if (title.isEmpty() || priority == null || status == null) {
            showAlert("Erreur", "Remplissez tous les champs obligatoires");
            return;
        }

        Long clientId = clientName != null ? clientMap.get(clientName) : null;
        Long assignedTo = assigneeName != null ? technicianMap.get(assigneeName) : 0L;

        try {
            if (selectedIntervention != null && selectedIntervention.getId() != -1) {
                // Update
                if (!PermissionService.canModifyIntervention()) {
                    showAlert("Permission refusée", "Vous n'avez pas la permission de modifier les interventions");
                    return;
                }
                boolean success = InterventionDAO.updateIntervention(selectedIntervention.getId(), 
                        title, description, priority, status, location, assignedTo != null ? assignedTo : 0);
                if (success) {
                    addHistory("Mise à jour intervention", selectedIntervention.getId(), title);
                    showAlert("Succès", "Intervention mise à jour");
                    handleResetForm();
                    loadInterventions();
                }
            } else {
                // Create
                if (!PermissionService.canCreateIntervention()) {
                    showAlert("Permission refusée", "Vous n'avez pas la permission de créer des interventions");
                    return;
                }
                long interventionId = InterventionDAO.createIntervention(title, description, priority, status, 
                        location, clientId != null ? clientId : 0, assignedTo != null ? assignedTo : 0);
                if (interventionId > 0) {
                    addHistory("Création intervention", interventionId, title);
                    NotificationDAO.createNotification(null, "Nouvelle intervention: " + title);
                    showAlert("Succès", "Intervention créée");
                    handleResetForm();
                    loadInterventions();
                }
            }
        } catch (Exception e) {
            showAlert("Erreur", "Erreur: " + e.getMessage());
        }
    }

    @FXML
    private void handleDeleteIntervention() {
        if (!PermissionService.canDeleteIntervention()) {
            showAlert("Permission refusée", "Vous n'avez pas la permission de supprimer les interventions");
            return;
        }

        if (selectedIntervention == null) {
            showAlert("Erreur", "Sélectionnez une intervention à supprimer");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText(null);
        confirm.setContentText("Êtes-vous sûr de vouloir supprimer cette intervention ?");

        if (confirm.showAndWait().get() == ButtonType.OK) {
            try {
                if (InterventionDAO.deleteIntervention(selectedIntervention.getId())) {
                    addHistory("Suppression intervention", selectedIntervention.getId(), selectedIntervention.getTitle());
                    showAlert("Succès", "Intervention supprimée");
                    handleResetForm();
                    loadInterventions();
                }
            } catch (Exception e) {
                showAlert("Erreur", "Erreur: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleResetForm() {
        titleField.clear();
        descriptionField.clear();
        locationField.clear();
        priorityBox.getSelectionModel().clearSelection();
        statusBox.getSelectionModel().clearSelection();
        clientBox.getSelectionModel().clearSelection();
        assigneeBox.getSelectionModel().clearSelection();
        if (searchField != null) {
            searchField.clear();
        }
        if (filterStatusBox != null) {
            filterStatusBox.setValue("Tous");
        }
        if (filterPriorityBox != null) {
            filterPriorityBox.setValue("Toutes");
        }
        if (filterTechnicianBox != null) {
            filterTechnicianBox.setValue("Tous");
        }
        selectedIntervention = null;
        interventionTable.getSelectionModel().clearSelection();
        loadInterventions();
    }

    @FXML
    private void handleSearch() {
        loadInterventions();
    }

    @FXML
    private void handleViewDetails() {
        if (selectedIntervention == null) {
            showAlert("Détails", "Sélectionnez une intervention pour voir les détails.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Détails intervention");
        alert.setHeaderText(selectedIntervention.getTitle());
        alert.setContentText(
                "Client: " + selectedIntervention.getClient() + "\n" +
                "Technicien: " + selectedIntervention.getAssigned() + "\n" +
                "Statut: " + selectedIntervention.getStatus() + "\n" +
                "Priorité: " + selectedIntervention.getPriority() + "\n" +
                "Localisation: " + selectedIntervention.getLocation() + "\n\n" +
                "Description:\n" + selectedIntervention.getDescription()
        );
        alert.showAndWait();
    }

    @FXML
    private void handleAddTechnicalNote() {
        if (!PermissionService.canAddComments()) {
            showAlert("Permission refusée", "Vous n'avez pas la permission d'ajouter des notes.");
            return;
        }
        if (selectedIntervention == null) {
            showAlert("Validation", "Sélectionnez une intervention.");
            return;
        }

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Note technique");
        dialog.setHeaderText("Ajouter une note technique");
        dialog.setContentText("Note:");

        dialog.showAndWait().ifPresent(note -> {
            String trimmed = note.trim();
            if (!trimmed.isEmpty()) {
                addHistory("Note technique", selectedIntervention.getId(), trimmed);
                showAlert("Succès", "Note technique ajoutée.");
            }
        });
    }

    @FXML
    private void handleCloseIntervention() {
        if (selectedIntervention == null) {
            showAlert("Validation", "Sélectionnez une intervention à clôturer.");
            return;
        }

        if (!PermissionService.canChangeInterventionStatus()) {
            showAlert("Permission refusée", "Vous n'avez pas la permission de modifier le statut.");
            return;
        }

        if (PermissionService.canViewOnlyAssignedInterventions()
                && !selectedIntervention.getAssigned().equals(SessionContext.getCurrentUser() != null ? SessionContext.getCurrentUser().getName() : "")) {
            showAlert("Permission refusée", "Vous pouvez clôturer uniquement vos interventions assignées.");
            return;
        }

        boolean success = InterventionDAO.updateInterventionStatus(selectedIntervention.getId(), "Terminée");
        if (success) {
            addHistory("Clôture intervention", selectedIntervention.getId(), selectedIntervention.getTitle());
            NotificationDAO.createNotification(null, "Intervention terminée: " + selectedIntervention.getTitle());
            loadInterventions();
            showAlert("Succès", "Intervention marquée comme Terminée.");
        } else {
            showAlert("Erreur", "Impossible de clôturer l'intervention.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void addHistory(String action, long entityId, String details) {
        long userId = SessionContext.getCurrentUser() != null ? SessionContext.getCurrentUser().getId() : 0;
        HistoryDAO.addHistory(userId, action, "Intervention", entityId, details);
    }

    /**
     * Open the intervention detail view for a specific intervention
     */
    private void openInterventionDetail(long interventionId) {
        try {
            // Load the detail view
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                    getClass().getResource("/views/intervention-detail-view.fxml")
            );
            javafx.scene.Parent detailView = loader.load();
            InterventionDetailController controller = loader.getController();
            controller.initialize(interventionId);
            
            // Find the content pane parent and swap the view
            javafx.scene.Parent currentParent = (javafx.scene.Parent) interventionTable.getScene().getRoot();
            javafx.scene.layout.StackPane contentPane = findContentPane(currentParent);
            
            if (contentPane != null) {
                contentPane.getChildren().setAll(detailView);
            } else {
                System.err.println("[InterventionsController] Could not find content pane");
            }
        } catch (Exception e) {
            System.err.println("[InterventionsController] Error opening intervention detail: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Recursively search for the content pane in the scene graph
     */
    private javafx.scene.layout.StackPane findContentPane(javafx.scene.Node node) {
        if (node instanceof javafx.scene.layout.StackPane) {
            javafx.scene.layout.StackPane pane = (javafx.scene.layout.StackPane) node;
            // Check if this is the content pane by checking for fx:id or other markers
            // For now, we'll assume it's a StackPane that's a child of BorderPane
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

    public static final class InterventionRow {
        private final SimpleLongProperty id;
        private final SimpleStringProperty title;
        private final SimpleStringProperty description;
        private final SimpleStringProperty priority;
        private final SimpleStringProperty status;
        private final SimpleStringProperty location;
        private final SimpleStringProperty client;
        private final SimpleStringProperty assigned;

        public InterventionRow(long id, String title, String description, String priority, 
                              String status, String location, String client, String assigned) {
            this.id = new SimpleLongProperty(id);
            this.title = new SimpleStringProperty(title);
            this.description = new SimpleStringProperty(description);
            this.priority = new SimpleStringProperty(priority);
            this.status = new SimpleStringProperty(status);
            this.location = new SimpleStringProperty(location);
            this.client = new SimpleStringProperty(client);
            this.assigned = new SimpleStringProperty(assigned);
        }

        public long getId() { return id.get(); }
        public String getTitle() { return title.get(); }
        public String getDescription() { return description.get(); }
        public String getPriority() { return priority.get(); }
        public String getStatus() { return status.get(); }
        public String getLocation() { return location.get(); }
        public String getClient() { return client.get(); }
        public String getAssigned() { return assigned.get(); }

        public SimpleStringProperty titleProperty() { return title; }
        public SimpleStringProperty priorityProperty() { return priority; }
        public SimpleStringProperty statusProperty() { return status; }
        public SimpleStringProperty clientProperty() { return client; }
        public SimpleStringProperty assignedProperty() { return assigned; }
    }
}