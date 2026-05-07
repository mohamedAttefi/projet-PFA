package com.company.util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Logger;

public class IconGenerator {

    private static final Logger LOGGER = Logger.getLogger(IconGenerator.class.getName());

    public static final String ICON_DIRECTORY = System.getProperty("user.home")
            + File.separator + ".canal-informatique";
    public static final String ICON_PNG_PATH = ICON_DIRECTORY + File.separator + "logo-icon.png";
    public static final String ICON_ICO_PATH = ICON_DIRECTORY + File.separator + "logo-icon.ico";

    private IconGenerator() {
    }

    public static void generateIconIfNotExists() {
        try {
            File outputDir = new File(ICON_DIRECTORY);
            if (!outputDir.exists()) {
                Files.createDirectories(Path.of(outputDir.getAbsolutePath()));
            }

            BufferedImage icon = createBufferedImageIcon(256);
            ImageIO.write(icon, "png", new File(ICON_PNG_PATH));
            writeIcoFile(icon, new File(ICON_ICO_PATH));
        } catch (Exception e) {
            LOGGER.warning("Failed to generate icon: " + e.getMessage());
        }
    }

    public static BufferedImage createBufferedImageIcon(int size) {
        // If a packaged logo image exists, use it and scale to requested size
        try (InputStream is = IconGenerator.class.getResourceAsStream("/images/canal_logo.png")) {
            if (is != null) {
                BufferedImage src = ImageIO.read(is);
                BufferedImage scaled = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g = scaled.createGraphics();
                g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g.drawImage(src, 0, 0, size, size, null);
                g.dispose();
                return scaled;
            }
        } catch (Exception e) {
            // ignore and fall back to programmatic generation
        }

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

    private static void writeIcoFile(BufferedImage image, File outputFile) throws IOException {
        ByteArrayOutputStream pngBuffer = new ByteArrayOutputStream();
        ImageIO.write(image, "png", pngBuffer);
        byte[] pngBytes = pngBuffer.toByteArray();

        try (FileOutputStream out = new FileOutputStream(outputFile)) {
            writeLittleEndianShort(out, 0);
            writeLittleEndianShort(out, 1);
            writeLittleEndianShort(out, 1);

            out.write(image.getWidth() >= 256 ? 0 : image.getWidth());
            out.write(image.getHeight() >= 256 ? 0 : image.getHeight());
            out.write(0);
            out.write(0);
            writeLittleEndianShort(out, 1);
            writeLittleEndianShort(out, 32);
            writeLittleEndianInt(out, pngBytes.length);
            writeLittleEndianInt(out, 6 + 16);
            out.write(pngBytes);
        }
    }

    private static void writeLittleEndianShort(FileOutputStream out, int value) throws IOException {
        out.write(value & 0xFF);
        out.write((value >>> 8) & 0xFF);
    }

    private static void writeLittleEndianInt(FileOutputStream out, int value) throws IOException {
        out.write(value & 0xFF);
        out.write((value >>> 8) & 0xFF);
        out.write((value >>> 16) & 0xFF);
        out.write((value >>> 24) & 0xFF);
    }
}
