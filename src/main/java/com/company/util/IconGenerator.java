package com.company.util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

public class IconGenerator {

    public static final String ICON_PATH = System.getProperty("user.home")
            + File.separator + ".canal-informatique" + File.separator + "logo-icon.png";

    public static void generateIconIfNotExists() {
        File outputFile = new File(ICON_PATH);
        if (outputFile.exists()) {
            return;
        }

        // Generate icon at 256x256 size
        BufferedImage icon = createBufferedImageIcon(256);
        
        try {
            File outputDir = outputFile.getParentFile();
            if (!outputDir.exists()) {
                Files.createDirectories(Path.of(outputDir.getAbsolutePath()));
            }
            ImageIO.write(icon, "png", outputFile);
        } catch (Exception e) {
            System.err.println("Failed to generate icon: " + e.getMessage());
        }
    }

    public static BufferedImage createBufferedImageIcon(int size) {
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        
        // Enable anti-aliasing
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Draw transparent background
        g2d.setComposite(AlphaComposite.Clear);
        g2d.fillRect(0, 0, size, size);
        g2d.setComposite(AlphaComposite.SrcOver);

        // Draw rounded red rectangle
        g2d.setColor(new Color(0xC8102E)); // Canal red
        int arcSize = (int)(size * 0.28);
        g2d.fillRoundRect(0, 0, size, size, arcSize, arcSize);

        // Draw white "CI" text
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Georgia", Font.BOLD, (int)(size * 0.54)));
        FontMetrics fm = g2d.getFontMetrics();
        String text = "CI";
        int textX = (size - fm.stringWidth(text)) / 2;
        int textY = (size - fm.getHeight()) / 2 + fm.getAscent();
        g2d.drawString(text, textX, textY);

        g2d.dispose();
        return image;
    }
}
