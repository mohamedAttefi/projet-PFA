package com.company.workorders.controller;

import com.company.workorders.dao.ClientDAO;
import com.company.workorders.model.Client;
import com.company.workorders.service.PermissionService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleLongProperty;

public class ClientsController {

    @FXML private TextField companyField;
    @FXML private TextField phoneField;
    @FXML private TextField addressField;
    @FXML private TextField searchField;
    @FXML private TableView<ClientRow> clientTable;
    @FXML private TableColumn<ClientRow, String> companyColumn;
    @FXML private TableColumn<ClientRow, String> phoneColumn;
    @FXML private TableColumn<ClientRow, String> addressColumn;
    @FXML private Button saveButton;
    @FXML private Button deleteButton;
    @FXML private Button resetButton;

    private ClientRow selectedClient = null;

    @FXML
    public void initialize() {
        companyColumn.setCellValueFactory(cell -> cell.getValue().companyProperty());
        phoneColumn.setCellValueFactory(cell -> cell.getValue().phoneProperty());
        addressColumn.setCellValueFactory(cell -> cell.getValue().addressProperty());

        // Load data from database
        loadClients();

        // Table selection listener
        clientTable.setOnMouseClicked(event -> {
            ClientRow selected = clientTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                selectedClient = selected;
                companyField.setText(selected.getCompany());
                phoneField.setText(selected.getPhone());
                addressField.setText(selected.getAddress());
            }
        });

        // Check permissions
        boolean canManage = PermissionService.canManageClients();
        saveButton.setDisable(!canManage);
        deleteButton.setDisable(!canManage);
        if (!canManage) {
            companyField.setEditable(false);
            phoneField.setEditable(false);
            addressField.setEditable(false);
            searchField.setEditable(false);
        }
    }

    private void loadClients() {
        java.util.List<Client> clients = ClientDAO.getAllClients();
        ObservableList<ClientRow> rows = FXCollections.observableArrayList();
        for (Client client : clients) {
            rows.add(new ClientRow(client.getId(), client.getCompanyName(), client.getPhone(), 
                                   client.getAddress(), client.getEmail()));
        }
        clientTable.setItems(rows);
    }

    @FXML
    private void handleSaveClient() {
        if (!PermissionService.canManageClients()) {
            showAlert("Permission refusée", "Vous n'avez pas la permission de gérer les clients");
            return;
        }

        String company = companyField.getText().trim();
        String phone = phoneField.getText().trim();
        String address = addressField.getText().trim();

        if (company.isEmpty()) {
            showAlert("Erreur", "Le nom de l'entreprise est requis");
            return;
        }

        try {
            if (selectedClient != null && selectedClient.getId() != -1) {
                // Update existing client
                boolean success = ClientDAO.updateClient(selectedClient.getId(), company, phone, address);
                if (success) {
                    showAlert("Succès", "Client mis à jour avec succès");
                    selectedClient = null;
                    handleResetForm();
                    loadClients();
                } else {
                    showAlert("Erreur", "Impossible de mettre à jour le client");
                }
            } else {
                // Create new client
                long clientId = ClientDAO.createClient(company, phone, address, "");
                if (clientId > 0) {
                    showAlert("Succès", "Client créé avec succès");
                    handleResetForm();
                    loadClients();
                } else {
                    showAlert("Erreur", "Impossible de créer le client");
                }
            }
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors de la sauvegarde: " + e.getMessage());
        }
    }

    @FXML
    private void handleDeleteClient() {
        if (!PermissionService.canManageClients()) {
            showAlert("Permission refusée", "Vous n'avez pas la permission de supprimer les clients");
            return;
        }

        if (selectedClient == null) {
            showAlert("Erreur", "Sélectionnez un client à supprimer");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText(null);
        confirm.setContentText("Êtes-vous sûr de vouloir supprimer ce client ?");

        if (confirm.showAndWait().get() == ButtonType.OK) {
            try {
                boolean success = ClientDAO.deleteClient(selectedClient.getId());
                if (success) {
                    showAlert("Succès", "Client supprimé avec succès");
                    selectedClient = null;
                    handleResetForm();
                    loadClients();
                } else {
                    showAlert("Erreur", "Impossible de supprimer le client");
                }
            } catch (Exception e) {
                showAlert("Erreur", "Erreur lors de la suppression: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleResetForm() {
        companyField.clear();
        phoneField.clear();
        addressField.clear();
        searchField.clear();
        selectedClient = null;
        clientTable.getSelectionModel().clearSelection();
        loadClients();
    }

    @FXML
    private void handleSearch() {
        String searchTerm = searchField.getText().trim();
        if (searchTerm.isEmpty()) {
            loadClients();
            return;
        }

        try {
            java.util.List<Client> results = ClientDAO.searchClients(searchTerm);
            ObservableList<ClientRow> rows = FXCollections.observableArrayList();
            for (Client client : results) {
                rows.add(new ClientRow(client.getId(), client.getCompanyName(), client.getPhone(),
                                       client.getAddress(), client.getEmail()));
            }
            clientTable.setItems(rows);
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors de la recherche: " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static final class ClientRow {
        private final SimpleLongProperty id;
        private final SimpleStringProperty company;
        private final SimpleStringProperty phone;
        private final SimpleStringProperty address;

        public ClientRow(long id, String company, String phone, String address, String email) {
            this.id = new SimpleLongProperty(id);
            this.company = new SimpleStringProperty(company);
            this.phone = new SimpleStringProperty(phone);
            this.address = new SimpleStringProperty(address);
        }

        public long getId() { return id.get(); }
        public String getCompany() { return company.get(); }
        public String getPhone() { return phone.get(); }
        public String getAddress() { return address.get(); }

        public SimpleStringProperty companyProperty() { return company; }
        public SimpleStringProperty phoneProperty() { return phone; }
        public SimpleStringProperty addressProperty() { return address; }
    }
}