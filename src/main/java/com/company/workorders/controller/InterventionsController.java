package com.company.workorders.controller;

import com.company.workorders.dao.ClientDAO;
import com.company.workorders.dao.InterventionDAO;
import com.company.workorders.model.Client;
import com.company.workorders.model.Intervention;
import com.company.workorders.service.PermissionService;
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
        statusBox.getItems().addAll("Nouvelle", "En cours", "Terminée", "Fermée");

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

        // Table selection listener
        interventionTable.setOnMouseClicked(event -> {
            InterventionRow selected = interventionTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                selectedIntervention = selected;
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
        }
    }

    private void loadInterventions() {
        java.util.List<Intervention> interventions;
        
        // Filter based on user role and permissions
        if (PermissionService.canViewAllInterventions()) {
            interventions = InterventionDAO.getAllInterventions();
        } else if (PermissionService.canViewOnlyAssignedInterventions()) {
            interventions = InterventionDAO.getInterventionsByTechnician(PermissionService.getCurrentUserId());
        } else {
            interventions = new java.util.ArrayList<>();
        }

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
        searchField.clear();
        selectedIntervention = null;
        interventionTable.getSelectionModel().clearSelection();
        loadInterventions();
    }

    @FXML
    private void handleSearch() {
        String searchTerm = searchField.getText().trim();
        if (searchTerm.isEmpty()) {
            loadInterventions();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
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