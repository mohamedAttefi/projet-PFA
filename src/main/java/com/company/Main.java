package com.company;

import com.company.util.IconGenerator;
import com.company.workorders.util.AppNavigator;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Path;
import java.util.List;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        // Generate icon file at startup
        IconGenerator.generateIconIfNotExists();
        
        AppNavigator.initialize(stage);

        applyWindowIcon(stage);
        Scene scene = new Scene(AppNavigator.loadLoginView(), 1200, 800);
        stage.setTitle("Canal Informatique - Gestion des Interventions");
        stage.setScene(scene);
        stage.show();

        Platform.runLater(() -> applyWindowIcon(stage));
    }

    private void applyWindowIcon(Stage stage) {
        try {
            File iconFile = Path.of(IconGenerator.ICON_PATH).toFile();
            if (!iconFile.exists()) {
                return;
            }

            Image icon = new Image(new FileInputStream(iconFile));
            if (icon.isError()) {
                System.err.println("Window icon image failed to load.");
                return;
            }

            List<Image> icons = stage.getIcons();
            icons.clear();
            icons.add(icon);
        } catch (Exception e) {
            System.err.println("Failed to apply window icon: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch();
    }
}