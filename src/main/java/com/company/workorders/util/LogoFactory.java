package com.company.workorders.util;

import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public final class LogoFactory {
    private LogoFactory() {
    }

    public static Image createAppIcon(int size) {
        Canvas canvas = new Canvas(size, size);
        GraphicsContext graphics = canvas.getGraphicsContext2D();

        graphics.setFill(Color.web("#c8102e"));
        graphics.fillRoundRect(0, 0, size, size, size * 0.28, size * 0.28);

        graphics.setFill(Color.WHITE);
        graphics.setFont(Font.font("Georgia", FontWeight.BOLD, size * 0.54));
        graphics.fillText("CI", size * 0.12, size * 0.67);

        SnapshotParameters parameters = new SnapshotParameters();
        parameters.setFill(Color.TRANSPARENT);
        WritableImage snapshot = new WritableImage(size, size);
        return canvas.snapshot(parameters, snapshot);
    }
}