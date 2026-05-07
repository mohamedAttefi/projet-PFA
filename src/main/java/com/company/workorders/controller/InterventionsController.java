package com.company.workorders.controller;

import com.company.workorders.dao.InterventionDAO;
import com.company.workorders.model.Intervention;
import com.company.workorders.service.PermissionService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleLongProperty;

public class InterventionsController {

    @FXML private TextField searchField;
    @FXML private ComboBox<String> filterStatusBox;
    @FXML private ComboBox<String> filterPriorityBox;
    @FXML private ComboBox<String> filterTechnicianBox;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private Button page1Button;
    @FXML private Button page2Button;
    @FXML private Button page3Button;
    @FXML private Label paginationInfoLabel;
    @FXML private TableView<InterventionRow> interventionTable;
    @FXML private TableColumn<InterventionRow, String> idColumn;
    @FXML private TableColumn<InterventionRow, String> titleColumn;
    @FXML private TableColumn<InterventionRow, String> clientColumn;
    @FXML private TableColumn<InterventionRow, String> priorityColumn;
    @FXML private TableColumn<InterventionRow, String> statusColumn;
    @FXML private TableColumn<InterventionRow, String> technicianColumn;
    @FXML private TableColumn<InterventionRow, String> dateColumn;
    @FXML private TableColumn<InterventionRow, String> actionsColumn;

    private InterventionRow selectedIntervention = null;
    private java.util.Map<String, Long> technicianMap = new java.util.HashMap<>();
    private int currentPage = 1;
    private int totalPages = 1;
    private int totalItems = 0;
    private final int itemsPerPage = 10;

    @FXML
    public void initialize() {
        filterStatusBox.getItems().addAll("Tous", "Nouvelle", "Assignée", "En cours", "Terminée", "Fermée", "Annulée");
        filterStatusBox.setValue("Tous");
        filterPriorityBox.getItems().addAll("Toutes", "Basse", "Normale", "Haute", "Critique", "Urgente");
        filterPriorityBox.setValue("Toutes");
        filterTechnicianBox.getItems().add("Tous");
        filterTechnicianBox.setValue("Tous");
        idColumn.setCellValueFactory(cell -> new SimpleStringProperty("#" + cell.getValue().getId()));
        titleColumn.setCellValueFactory(cell -> cell.getValue().titleProperty());
        clientColumn.setCellValueFactory(cell -> cell.getValue().clientProperty());
        priorityColumn.setCellValueFactory(cell -> cell.getValue().priorityProperty());
        statusColumn.setCellValueFactory(cell -> cell.getValue().statusProperty());
        technicianColumn.setCellValueFactory(cell -> cell.getValue().assignedProperty());
        dateColumn.setCellValueFactory(cell -> {
            String createdAt = cell.getValue().getCreatedAt();
            if (createdAt != null && !createdAt.equals("N/A")) {
                try {
                    java.time.LocalDateTime dateTime = java.time.LocalDateTime.parse(createdAt, 
                        java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                    return new SimpleStringProperty(dateTime.format(java.time.format.DateTimeFormatter.ofPattern("dd MMM yyyy")));
                } catch (Exception e) {
                    return new SimpleStringProperty(createdAt);
                }
            }
            return new SimpleStringProperty("N/A");
        });
        actionsColumn.setCellValueFactory(cell -> new SimpleStringProperty(""));

        // Load technicians for filter
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
                }
            }
        });
    }

    private void loadTechnicians() {
        java.util.List<Object[]> technicians = InterventionDAO.getAllTechnicians();
        technicianMap.clear();
        for (Object[] tech : technicians) {
            String name = (String) tech[1];
            Long id = (Long) tech[0];
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

        // Get date filters
        java.time.LocalDate startDate = startDatePicker != null ? startDatePicker.getValue() : null;
        java.time.LocalDate endDate = endDatePicker != null ? endDatePicker.getValue() : null;

        // Get total count for pagination
        int totalCount = InterventionDAO.countSearchInterventionsWithDates(
                term,
                status,
                priority,
                technicianId,
                PermissionService.canViewOnlyAssignedInterventions(),
                PermissionService.getCurrentUserId(),
                startDate,
                endDate
        );

        // Set total items and calculate total pages
        totalItems = totalCount;
        totalPages = (int) Math.ceil((double) totalCount / itemsPerPage);
        
        // Reset to page 1 if current page is out of bounds
        if (currentPage > totalPages) {
            currentPage = 1;
        }

        // Get paginated results
        java.util.List<Intervention> interventions = InterventionDAO.searchInterventionsPaginatedWithDates(
                term,
                status,
                priority,
                technicianId,
                PermissionService.canViewOnlyAssignedInterventions(),
                PermissionService.getCurrentUserId(),
                currentPage,
                itemsPerPage,
                startDate,
                endDate
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
                    intervention.getTechnicianName() != null ? intervention.getTechnicianName() : "Non assigné",
                    intervention.getCreatedAt()
            ));
        }
        interventionTable.setItems(rows);
        
        // Update pagination info
        updatePaginationInfo();
    }

    @FXML
    private void handleSearch() {
        loadInterventions();
    }

    @FXML
    public void createNewIntervention() {
        try {
            javafx.scene.Parent detailView = com.company.workorders.util.AppNavigator.loadView("/views/new-intervention-view.fxml");
            
            javafx.scene.Parent currentParent = (javafx.scene.Parent) interventionTable.getScene().getRoot();
            javafx.scene.layout.StackPane contentPane = findContentPane(currentParent);
            
            if (contentPane != null) {
                contentPane.getChildren().setAll(detailView);
            } else {
                System.err.println("[InterventionsController] Could not find content pane");
            }
        } catch (Exception e) {
            System.err.println("Error creating new intervention: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleViewDetails() {
        if (selectedIntervention == null) {
            showAlert("Détails", "Veuillez sélectionner une intervention pour voir les détails.");
            return;
        }
        openInterventionDetail(selectedIntervention.getId());
    }

    @FXML
    private void handleEditIntervention() {
        if (selectedIntervention == null) {
            showAlert("Modification", "Veuillez sélectionner une intervention à modifier.");
            return;
        }
        openInterventionDetail(selectedIntervention.getId());
    }

    @FXML
    private void handleDeleteIntervention() {
        if (selectedIntervention == null) {
            showAlert("Suppression", "Veuillez sélectionner une intervention à supprimer.");
            return;
        }

        if (!com.company.workorders.service.PermissionService.canDeleteIntervention()) {
            showAlert("Permission refusée", "Vous n'avez pas la permission de supprimer les interventions.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation de suppression");
        confirm.setHeaderText(null);
        confirm.setContentText("Êtes-vous sûr de vouloir supprimer l'intervention \"" + selectedIntervention.getTitle() + "\" ?");

        if (confirm.showAndWait().get() == ButtonType.OK) {
            try {
                if (com.company.workorders.dao.InterventionDAO.deleteIntervention(selectedIntervention.getId())) {
                    showAlert("Succès", "L'intervention a été supprimée avec succès.");
                    loadInterventions();
                    selectedIntervention = null;
                } else {
                    showAlert("Erreur", "Impossible de supprimer l'intervention.");
                }
            } catch (Exception e) {
                showAlert("Erreur", "Une erreur est survenue lors de la suppression: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleAssignTechnician() {
        if (selectedIntervention == null) {
            showAlert("Assignation", "Veuillez sélectionner une intervention à assigner.");
            return;
        }

        if (!com.company.workorders.service.PermissionService.canModifyIntervention()) {
            showAlert("Permission refusée", "Vous n'avez pas la permission de modifier les interventions.");
            return;
        }

        // Create a dialog to select technician
        ChoiceDialog<String> dialog = new ChoiceDialog<>();
        dialog.setTitle("Assigner un technicien");
        dialog.setHeaderText("Assigner un technicien à l'intervention");
        dialog.setContentText("Sélectionnez un technicien:");

        // Load technicians for the dialog
        java.util.List<String> technicians = new java.util.ArrayList<>();
        technicians.add("Non assigné");
        try {
            java.util.List<Object[]> techList = com.company.workorders.dao.InterventionDAO.getAllTechnicians();
            for (Object[] tech : techList) {
                technicians.add((String) tech[1]);
            }
        } catch (Exception e) {
            System.err.println("Error loading technicians: " + e.getMessage());
        }

        dialog.getItems().setAll(technicians);
        dialog.setSelectedItem(selectedIntervention.getAssigned());

        dialog.showAndWait().ifPresent(selectedTech -> {
            try {
                Long technicianId = null;
                if (!"Non assigné".equals(selectedTech)) {
                    technicianId = technicianMap.get(selectedTech);
                }
                
                boolean success = com.company.workorders.dao.InterventionDAO.updateIntervention(
                    selectedIntervention.getId(),
                    selectedIntervention.getTitle(),
                    selectedIntervention.getDescription(),
                    selectedIntervention.getPriority(),
                    selectedIntervention.getStatus(),
                    selectedIntervention.getLocation(),
                    technicianId != null ? technicianId : 0
                );
                
                if (success) {
                    showAlert("Succès", "L'intervention a été assignée à " + selectedTech);
                    loadInterventions();
                } else {
                    showAlert("Erreur", "Impossible d'assigner le technicien.");
                }
            } catch (Exception e) {
                showAlert("Erreur", "Une erreur est survenue lors de l'assignation: " + e.getMessage());
            }
        });
    }

    @FXML
    private void handleChangeStatus() {
        if (selectedIntervention == null) {
            showAlert("Changement de statut", "Veuillez sélectionner une intervention.");
            return;
        }

        if (!com.company.workorders.service.PermissionService.canChangeInterventionStatus()) {
            showAlert("Permission refusée", "Vous n'avez pas la permission de modifier le statut des interventions.");
            return;
        }

        ChoiceDialog<String> dialog = new ChoiceDialog<>();
        dialog.setTitle("Changer le statut");
        dialog.setHeaderText("Modifier le statut de l'intervention");
        dialog.setContentText("Nouveau statut:");

        java.util.List<String> statuses = java.util.Arrays.asList(
            "Nouvelle", "Assignée", "En cours", "Terminée", "Fermée", "Annulée"
        );
        dialog.getItems().setAll(statuses);
        dialog.setSelectedItem(selectedIntervention.getStatus());

        dialog.showAndWait().ifPresent(newStatus -> {
            try {
                boolean success = com.company.workorders.dao.InterventionDAO.updateInterventionStatus(
                    selectedIntervention.getId(), newStatus
                );
                
                if (success) {
                    showAlert("Succès", "Le statut a été modifié en: " + newStatus);
                    loadInterventions();
                } else {
                    showAlert("Erreur", "Impossible de modifier le statut.");
                }
            } catch (Exception e) {
                showAlert("Erreur", "Une erreur est survenue lors de la modification: " + e.getMessage());
            }
        });
    }

    @FXML
    private void handlePagination(int page) {
        if (page >= 1 && page <= totalPages) {
            currentPage = page;
            loadInterventions();
        }
    }

    @FXML
    private void handleExportToCSV() {
        try {
            java.util.List<Intervention> allInterventions = InterventionDAO.searchInterventions(
                searchField != null ? searchField.getText() : "",
                filterStatusBox != null ? filterStatusBox.getValue() : "Tous",
                filterPriorityBox != null ? filterPriorityBox.getValue() : "Toutes",
                technicianMap.get(filterTechnicianBox.getValue()),
                PermissionService.canViewOnlyAssignedInterventions(),
                PermissionService.getCurrentUserId()
            );

            if (allInterventions.isEmpty()) {
                showAlert("Export", "Aucune intervention à exporter.");
                return;
            }

            // Create file chooser for save location
            javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
            fileChooser.setTitle("Exporter les interventions");
            fileChooser.getExtensionFilters().add(
                new javafx.stage.FileChooser.ExtensionFilter("Fichiers CSV", "*.csv")
            );
            fileChooser.setInitialFileName("interventions_" + java.time.LocalDate.now() + ".csv");

            java.io.File file = fileChooser.showSaveDialog(interventionTable.getScene().getWindow());
            if (file != null) {
                exportToCSV(allInterventions, file);
                showAlert("Export réussi", "Les interventions ont été exportées vers: " + file.getAbsolutePath());
            }
        } catch (Exception e) {
            showAlert("Erreur d'export", "Une erreur est survenue lors de l'export: " + e.getMessage());
        }
    }

    private void exportToCSV(java.util.List<Intervention> interventions, java.io.File file) throws java.io.IOException {
        try (java.io.PrintWriter writer = new java.io.PrintWriter(new java.io.FileWriter(file))) {
            // Write CSV header
            writer.println("ID,Titre,Description,Priorité,Statut,Localisation,Client,Technicien,Date de création");

            // Write data rows
            for (Intervention intervention : interventions) {
                writer.printf("%d,\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"%n",
                    intervention.getId(),
                    escapeCsv(intervention.getTitle()),
                    escapeCsv(intervention.getDescription()),
                    intervention.getPriority(),
                    intervention.getStatus(),
                    escapeCsv(intervention.getLocation()),
                    escapeCsv(intervention.getClientName()),
                    escapeCsv(intervention.getTechnicianName()),
                    intervention.getFormattedCreatedAt()
                );
            }
        }
    }

    private String escapeCsv(String value) {
        if (value == null) return "";
        return value.replace("\"", "\"\"");
    }

    @FXML
    private void handlePreviousPage() {
        if (currentPage > 1) {
            currentPage--;
            loadInterventions();
        }
    }

    @FXML
    private void handleNextPage() {
        if (currentPage < totalPages) {
            currentPage++;
            loadInterventions();
        }
    }

    @FXML
    private void handlePage1() {
        handlePagination(1);
    }

    @FXML
    private void handlePage2() {
        handlePagination(2);
    }

    @FXML
    private void handlePage3() {
        handlePagination(3);
    }

    private void updatePaginationInfo() {
        // Update pagination button states
        if (page1Button != null) {
            page1Button.setText("1");
            page1Button.setStyle(currentPage == 1 ? 
                "-fx-background-color: #c8102e; -fx-text-fill: white; -fx-cursor: hand;" : 
                "-fx-background-color: white; -fx-border-color: #e1d7d3; -fx-cursor: hand;");
        }
        
        if (page2Button != null) {
            page2Button.setText("2");
            page2Button.setStyle(currentPage == 2 ? 
                "-fx-background-color: #c8102e; -fx-text-fill: white; -fx-cursor: hand;" : 
                "-fx-background-color: white; -fx-border-color: #e1d7d3; -fx-cursor: hand;");
            page2Button.setVisible(totalPages >= 2);
        }
        
        if (page3Button != null) {
            page3Button.setText("3");
            page3Button.setStyle(currentPage == 3 ? 
                "-fx-background-color: #c8102e; -fx-text-fill: white; -fx-cursor: hand;" : 
                "-fx-background-color: white; -fx-border-color: #e1d7d3; -fx-cursor: hand;");
            page3Button.setVisible(totalPages >= 3);
        }
        
        // Update pagination info label
        if (paginationInfoLabel != null) {
            int startItem = (currentPage - 1) * itemsPerPage + 1;
            int endItem = Math.min(currentPage * itemsPerPage, totalItems);
            paginationInfoLabel.setText(String.format("Affichage de %d à %d sur %d interventions", 
                startItem, endItem, totalItems));
        }
        
        System.out.println("Page " + currentPage + " of " + totalPages + " (items per page: " + itemsPerPage + ")");
    }

    private javafx.scene.layout.StackPane findContentPane(javafx.scene.Parent parent) {
        // Look for StackPane with ID "contentPane" or the first StackPane
        if (parent instanceof javafx.scene.layout.StackPane) {
            javafx.scene.layout.StackPane stackPane = (javafx.scene.layout.StackPane) parent;
            if ("contentPane".equals(stackPane.getId())) {
                return stackPane;
            }
        }
        
        // Search children recursively
        for (javafx.scene.Node child : parent.getChildrenUnmodifiable()) {
            if (child instanceof javafx.scene.Parent) {
                javafx.scene.layout.StackPane result = findContentPane((javafx.scene.Parent) child);
                if (result != null) {
                    return result;
                }
            }
        }
        
        return null;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Open the intervention detail view for a specific intervention
     */
    private void openInterventionDetail(long interventionId) {
        try {
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
        private final SimpleStringProperty createdAt;

        public InterventionRow(long id, String title, String description, String priority, 
                              String status, String location, String client, String assigned) {
            this(id, title, description, priority, status, location, client, assigned, null);
        }

        public InterventionRow(long id, String title, String description, String priority, 
                              String status, String location, String client, String assigned, String createdAt) {
            this.id = new SimpleLongProperty(id);
            this.title = new SimpleStringProperty(title);
            this.description = new SimpleStringProperty(description);
            this.priority = new SimpleStringProperty(priority);
            this.status = new SimpleStringProperty(status);
            this.location = new SimpleStringProperty(location);
            this.client = new SimpleStringProperty(client);
            this.assigned = new SimpleStringProperty(assigned);
            this.createdAt = new SimpleStringProperty(createdAt != null ? createdAt : "N/A");
        }

        public long getId() { return id.get(); }
        public String getTitle() { return title.get(); }
        public String getDescription() { return description.get(); }
        public String getPriority() { return priority.get(); }
        public String getStatus() { return status.get(); }
        public String getLocation() { return location.get(); }
        public String getClient() { return client.get(); }
        public String getAssigned() { return assigned.get(); }
        public String getCreatedAt() { return createdAt.get(); }

        public SimpleStringProperty titleProperty() { return title; }
        public SimpleStringProperty priorityProperty() { return priority; }
        public SimpleStringProperty statusProperty() { return status; }
        public SimpleStringProperty clientProperty() { return client; }
        public SimpleStringProperty assignedProperty() { return assigned; }
        public SimpleStringProperty createdAtProperty() { return createdAt; }
    }
}