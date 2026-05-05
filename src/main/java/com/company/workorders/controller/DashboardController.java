package com.company.workorders.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;

public class DashboardController {

    @FXML
    private ListView<String> recentActivityList;

    @FXML
    private ListView<String> priorityAlertsList;

    @FXML
    public void initialize() {
        recentActivityList.getItems().setAll(
                "08:15 - Nouvelle intervention enregistrée",
                "09:00 - Intervention #102 passée en cours",
                "09:40 - Client ACME mis à jour",
                "10:10 - Intervention urgente assignée"
        );

        priorityAlertsList.getItems().setAll(
                "14 interventions urgentes en attente",
                "3 tickets sans technicien assigné",
                "8 interventions terminées aujourd'hui"
        );
    }
}