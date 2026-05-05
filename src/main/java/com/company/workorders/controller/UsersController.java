package com.company.workorders.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.beans.property.SimpleStringProperty;

public class UsersController {

    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private ComboBox<String> roleBox;
    @FXML private TableView<UserRow> userTable;
    @FXML private TableColumn<UserRow, String> nameColumn;
    @FXML private TableColumn<UserRow, String> emailColumn;
    @FXML private TableColumn<UserRow, String> roleColumn;

    @FXML
    public void initialize() {
        roleBox.getItems().addAll("Réceptionniste", "Technicien", "Administrateur");
        nameColumn.setCellValueFactory(cell -> cell.getValue().nameProperty());
        emailColumn.setCellValueFactory(cell -> cell.getValue().emailProperty());
        roleColumn.setCellValueFactory(cell -> cell.getValue().roleProperty());
        userTable.setItems(FXCollections.observableArrayList(
                new UserRow("Nadia Ben", "nadia@canal-info.fr", "Réceptionniste"),
                new UserRow("Karim Ali", "karim@canal-info.fr", "Technicien"),
                new UserRow("Admin Canal", "admin@canal-info.fr", "Administrateur")
        ));
    }

    @FXML private void handleSaveUser() { }

    public static final class UserRow {
        private final SimpleStringProperty name;
        private final SimpleStringProperty email;
        private final SimpleStringProperty role;

        public UserRow(String name, String email, String role) {
            this.name = new SimpleStringProperty(name);
            this.email = new SimpleStringProperty(email);
            this.role = new SimpleStringProperty(role);
        }

        public SimpleStringProperty nameProperty() { return name; }
        public SimpleStringProperty emailProperty() { return email; }
        public SimpleStringProperty roleProperty() { return role; }
    }
}