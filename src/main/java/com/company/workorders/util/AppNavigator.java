package com.company.workorders.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Central navigation helper for switching between the login screen and the app shell.
 */
public final class AppNavigator {
    private static Stage primaryStage;

    private AppNavigator() {
    }

    public static void initialize(Stage stage) {
        primaryStage = stage;
    }

    public static Parent loadLoginView() throws IOException {
        return load("/main_view.fxml");
    }

    public static Parent loadAppShell() throws IOException {
        return load("/views/app-shell.fxml");
    }

    public static void showAppShell() {
        try {
            setScene(loadAppShell(), 1400, 900);
            primaryStage.setTitle("Canal Informatique - Gestion des interventions");
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load app shell", exception);
        }
    }

    public static void showLogin() {
        try {
            setScene(loadLoginView(), 1200, 800);
            primaryStage.setTitle("Work Orders Management System");
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load login view", exception);
        }
    }

    public static Parent loadView(String fxmlPath) {
        try {
            return load(fxmlPath);
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load view: " + fxmlPath, exception);
        }
    }

    private static void setScene(Parent root, double width, double height) {
        Scene currentScene = primaryStage.getScene();
        if (currentScene == null) {
            primaryStage.setScene(new Scene(root, width, height));
            return;
        }

        currentScene.setRoot(root);
        currentScene.getWindow().sizeToScene();
    }

    private static Parent load(String fxmlPath) throws IOException {
        FXMLLoader loader = new FXMLLoader(AppNavigator.class.getResource(fxmlPath));
        return loader.load();
    }
}