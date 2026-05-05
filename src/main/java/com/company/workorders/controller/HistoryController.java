package com.company.workorders.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class HistoryController {

    @FXML private TableView<HistoryRow> historyTable;
    @FXML private TableColumn<HistoryRow, String> dateColumn;
    @FXML private TableColumn<HistoryRow, String> actionColumn;
    @FXML private TableColumn<HistoryRow, String> userColumn;
    @FXML private TableColumn<HistoryRow, String> detailColumn;

    @FXML
    public void initialize() {
        dateColumn.setCellValueFactory(cell -> cell.getValue().dateProperty());
        actionColumn.setCellValueFactory(cell -> cell.getValue().actionProperty());
        userColumn.setCellValueFactory(cell -> cell.getValue().userProperty());
        detailColumn.setCellValueFactory(cell -> cell.getValue().detailProperty());
        historyTable.setItems(FXCollections.observableArrayList(
                new HistoryRow("2026-05-05 08:20", "Création intervention", "Nadia Ben", "Ticket #110 créé pour ACME Services"),
                new HistoryRow("2026-05-05 09:05", "Mise à jour statut", "Karim Ali", "Ticket #110 passé à En cours"),
                new HistoryRow("2026-05-05 10:12", "Clôture", "Jean Martin", "Ticket #109 clôturé avec succès")
        ));
    }

    public static final class HistoryRow {
        private final SimpleStringProperty date;
        private final SimpleStringProperty action;
        private final SimpleStringProperty user;
        private final SimpleStringProperty detail;

        public HistoryRow(String date, String action, String user, String detail) {
            this.date = new SimpleStringProperty(date);
            this.action = new SimpleStringProperty(action);
            this.user = new SimpleStringProperty(user);
            this.detail = new SimpleStringProperty(detail);
        }

        public SimpleStringProperty dateProperty() { return date; }
        public SimpleStringProperty actionProperty() { return action; }
        public SimpleStringProperty userProperty() { return user; }
        public SimpleStringProperty detailProperty() { return detail; }
    }
}