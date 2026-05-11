package com.company;

import com.company.util.IconGenerator;
import com.company.workorders.util.AppNavigator;
import com.company.workorders.util.SchemaManager;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

public class Main extends Application {

    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    @Override
    public void start(Stage stage) throws IOException {
        SchemaManager.ensureCompatibility();

        // Generate icon files at startup for the app and desktop shortcut
        IconGenerator.generateIconIfNotExists();
        
        AppNavigator.initialize(stage);

        applyWindowIcon(stage);
        Scene scene = new Scene(AppNavigator.loadLoginView());
        stage.setTitle("Canal Informatique - Gestion des Interventions");
        stage.setScene(scene);
        stage.show();

        Platform.runLater(() -> applyWindowIcon(stage));
    }

    private void applyWindowIcon(Stage stage) {
        try {
            // Try multiple ways to load the icon
            InputStream iconStream = Main.class.getResourceAsStream("/images/canal_logo.png");
            
            if (iconStream == null) {
                LOGGER.warning("Could not find /images/canal_logo.png in resources");
                // Try alternative path
                iconStream = Main.class.getResourceAsStream("/canal_logo.png");
                if (iconStream == null) {
                    LOGGER.warning("Could not find /canal_logo.png in resources");
                    return;
                }
            }

            Image icon = new Image(iconStream);
            if (icon.isError()) {
                LOGGER.warning("Window icon image failed to load. Error: " + icon.getException());
            }

            // Set multiple sizes for better quality
            stage.getIcons().setAll(icon);
            LOGGER.info("Window icon applied successfully: " + icon.getWidth() + "x" + icon.getHeight());
            
        } catch (Exception e) {
            LOGGER.warning("Failed to apply window icon: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch();
    }
}