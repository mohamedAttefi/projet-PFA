package com.company.workorders.util;

import javafx.application.Platform;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.StackPane;
import javafx.scene.Node;

/**
 * Utility class for managing loading states and indicators
 */
public class LoadingUtils {
    
    /**
     * Show a loading indicator over the specified node
     */
    public static void showLoading(Node node, Runnable loadingTask) {
        if (node == null || loadingTask == null) return;
        
        // Create loading overlay
        ProgressIndicator indicator = new ProgressIndicator();
        indicator.setStyle("-fx-progress-color: #c8102e;");
        indicator.setPrefSize(40, 40);
        
        StackPane loadingOverlay = new StackPane();
        loadingOverlay.setStyle("-fx-background-color: rgba(255, 255, 255, 0.8); -fx-background-radius: 8;");
        loadingOverlay.getChildren().add(indicator);
        
        // Get the parent container
        StackPane parentContainer = findParentStackPane(node);
        if (parentContainer == null) return;
        
        // Add loading overlay
        Platform.runLater(() -> {
            parentContainer.getChildren().add(loadingOverlay);
            
            // Run the loading task in background
            new Thread(() -> {
                try {
                    loadingTask.run();
                } finally {
                    // Remove loading overlay
                    Platform.runLater(() -> {
                        parentContainer.getChildren().remove(loadingOverlay);
                    });
                }
            }).start();
        });
    }
    
    /**
     * Find the parent StackPane container
     */
    private static StackPane findParentStackPane(Node node) {
        if (node == null) return null;
        
        if (node.getParent() instanceof StackPane) {
            return (StackPane) node.getParent();
        }
        
        // Search up the hierarchy
        Node parent = node.getParent();
        while (parent != null) {
            if (parent instanceof StackPane) {
                return (StackPane) parent;
            }
            parent = parent.getParent();
        }
        
        return null;
    }
    
    /**
     * Create a simple loading indicator
     */
    public static ProgressIndicator createLoadingIndicator() {
        ProgressIndicator indicator = new ProgressIndicator();
        indicator.setStyle("-fx-progress-color: #c8102e;");
        indicator.setPrefSize(30, 30);
        return indicator;
    }
}
