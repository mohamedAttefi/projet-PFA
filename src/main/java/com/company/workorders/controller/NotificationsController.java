package com.company.workorders.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;

public class NotificationsController {

    @FXML
    private ListView<String> notificationList;

    @FXML
    public void initialize() {
        notificationList.getItems().setAll(
                "Nouvelle intervention urgente reçue",
                "Intervention #110 passée à En cours",
                "Technicien Jean Martin a clôturé un ticket"
        );
    }
}