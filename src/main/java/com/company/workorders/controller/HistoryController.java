package com.company.workorders.controller;

import com.company.workorders.dao.HistoryDAO;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
        loadHistory();
    }

    private void loadHistory() {
        ObservableList<HistoryRow> rows = FXCollections.observableArrayList();
        for (Object[] data : HistoryDAO.getAllHistory()) {
            rows.add(new HistoryRow(
                    String.valueOf(data[0]),
                    String.valueOf(data[1]),
                    String.valueOf(data[2]),
                    String.valueOf(data[3])
            ));
        }
        historyTable.setItems(rows);
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
