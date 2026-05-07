package com.company.workorders.controller;

import com.company.workorders.dao.InterventionDAO;
import com.company.workorders.dao.ClientDAO;
import com.company.workorders.model.Client;
import com.company.workorders.util.AppNavigator;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.Map;

public class NewInterventionController {

    @FXML private TextField titleField;
    @FXML private TextArea descriptionArea;
    @FXML private TextField locationField;
    @FXML private ComboBox<String> priorityComboBox;
    @FXML private ComboBox<String> clientComboBox;
    @FXML private ComboBox<String> technicianComboBox;
    @FXML private VBox validationMessagesBox;
    @FXML private Label validationTitleLabel;
    @FXML private Label validationMessageLabel;

    private Map<String, Long> clientMap = new java.util.HashMap<>();
    private Map<String, Long> technicianMap = new java.util.HashMap<>();

    @FXML
    public void initialize() {
        setupPriorityComboBox();
        loadClients();
        loadTechnicians();
        
        // Add real-time validation
        setupValidationListeners();
    }

    private void setupPriorityComboBox() {
        priorityComboBox.getItems().addAll(
            "Basse", "Normale", "Haute", "Critique", "Urgente"
        );
        priorityComboBox.setValue("Normale");
    }

    private void loadClients() {
        try {
            List<Client> clients = ClientDAO.getAllClients();
            clientMap.clear();
            
            for (Client client : clients) {
                String name = client.getCompanyName();
                Long id = client.getId();
                clientMap.put(name, id);
                clientComboBox.getItems().add(name);
            }
        } catch (Exception e) {
            System.err.println("[NewInterventionController] Error loading clients: " + e.getMessage());
        }
    }

    private void loadTechnicians() {
        try {
            List<Object[]> technicians = InterventionDAO.getAllTechnicians();
            technicianMap.clear();
            
            technicianComboBox.getItems().add("Non assigné");
            for (Object[] tech : technicians) {
                String name = (String) tech[1];
                Long id = (Long) tech[0];
                technicianMap.put(name, id);
                technicianComboBox.getItems().add(name);
            }
            technicianComboBox.setValue("Non assigné");
        } catch (Exception e) {
            System.err.println("[NewInterventionController] Error loading technicians: " + e.getMessage());
        }
    }

    private void setupValidationListeners() {
        titleField.textProperty().addListener((obs, oldVal, newVal) -> {
            hideValidationMessages();
        });
        
        descriptionArea.textProperty().addListener((obs, oldVal, newVal) -> {
            hideValidationMessages();
        });
        
        locationField.textProperty().addListener((obs, oldVal, newVal) -> {
            hideValidationMessages();
        });
        
        clientComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            hideValidationMessages();
        });
    }

    @FXML
    private void handleCreateIntervention() {
        if (!validateForm()) {
            return;
        }

        try {
            String title = titleField.getText().trim();
            String description = descriptionArea.getText().trim();
            String location = locationField.getText().trim();
            String priority = priorityComboBox.getValue();
            String clientName = clientComboBox.getValue();
            String technicianName = technicianComboBox.getValue();

            Long clientId = clientMap.get(clientName);
            Long technicianId = technicianMap.containsKey(technicianName) ? 
                technicianMap.get(technicianName) : null;

            // Create the intervention
            long interventionId = InterventionDAO.createIntervention(
                title, description, priority, "Nouvelle", location, clientId, technicianId != null ? technicianId : 0
            );

            boolean success = interventionId > 0;

            if (success) {
                showAlert("Succès", "L'intervention a été créée avec succès.");
                navigateBackToInterventions();
            } else {
                showAlert("Erreur", "Impossible de créer l'intervention.");
            }

        } catch (Exception e) {
            showAlert("Erreur", "Une erreur est survenue lors de la création: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCancel() {
        navigateBackToInterventions();
    }

    private boolean validateForm() {
        StringBuilder errors = new StringBuilder();

        // Title validation
        String title = titleField.getText().trim();
        if (title == null || title.isEmpty()) {
            errors.append("• Le titre est obligatoire\n");
        } else if (title.length() < 3) {
            errors.append("• Le titre doit contenir au moins 3 caractères\n");
        }

        // Description validation
        String description = descriptionArea.getText().trim();
        if (description == null || description.isEmpty()) {
            errors.append("• La description est obligatoire\n");
        } else if (description.length() < 10) {
            errors.append("• La description doit contenir au moins 10 caractères\n");
        }

        // Location validation
        String location = locationField.getText().trim();
        if (location == null || location.isEmpty()) {
            errors.append("• La localisation est obligatoire\n");
        }

        // Priority validation
        String priority = priorityComboBox.getValue();
        if (priority == null || priority.isEmpty()) {
            errors.append("• La priorité est obligatoire\n");
        }

        // Client validation
        String clientName = clientComboBox.getValue();
        if (clientName == null || clientName.isEmpty()) {
            errors.append("• Le client est obligatoire\n");
        }

        if (errors.length() > 0) {
            showValidationErrors(errors.toString());
            return false;
        }

        return true;
    }

    private void showValidationErrors(String errorMessage) {
        validationMessagesBox.setVisible(true);
        validationMessageLabel.setText(errorMessage);
    }

    private void hideValidationMessages() {
        validationMessagesBox.setVisible(false);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void navigateBackToInterventions() {
        try {
            AppNavigator.loadView("/views/interventions-view.fxml");
        } catch (Exception e) {
            System.err.println("[NewInterventionController] Error navigating back: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
