package com.company.workorders.controller;

import com.company.workorders.dao.DashboardDAO;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

public class DashboardController {

    @FXML private Label totalInterventionsLabel;
    @FXML private Label urgentesLabel;
    @FXML private Label enCoursLabel;
    @FXML private Label termineesLabel;
    @FXML private ListView<String> recentActivityList;
    @FXML private ListView<String> priorityAlertsList;

    @FXML
    public void initialize() {
        refreshDashboard();
    }

    private void refreshDashboard() {
        totalInterventionsLabel.setText(String.valueOf(DashboardDAO.countInterventions()));
        urgentesLabel.setText(String.valueOf(DashboardDAO.countUrgentes()));
        enCoursLabel.setText(String.valueOf(DashboardDAO.countEnCours()));
        termineesLabel.setText(String.valueOf(DashboardDAO.countTerminees()));

        recentActivityList.getItems().setAll(DashboardDAO.loadRecentActivity(8));
        if (recentActivityList.getItems().isEmpty()) {
            recentActivityList.getItems().add("Aucune activité récente enregistrée.");
        }

        priorityAlertsList.getItems().setAll(DashboardDAO.loadPriorityAlerts());
    }
}
