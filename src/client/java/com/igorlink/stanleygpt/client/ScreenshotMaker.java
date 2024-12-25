package com.igorlink.stanleygpt.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

/**
 * Takes a screenshot of the current screen.
 */
public class ScreenshotMaker {
    public final int SCREENSHOT_TARGET_WIDTH = 500;

    /**
     * Takes a screenshot of the current screen.
     *
     * @return The screenshot as a BufferedImage.
     */
    public BufferedImage takeCustomScreenshot() {
        MinecraftClient client = MinecraftClient.getInstance();
        int width = client.getFramebuffer().textureWidth;
        int height = client.getFramebuffer().textureHeight;

        // Create a ByteBuffer to store the pixels
        ByteBuffer buffer = ByteBuffer.allocateDirect(width * height * 4);

        // Read the pixels from the framebuffer
        RenderSystem.bindTexture(client.getFramebuffer().getColorAttachment());
        RenderSystem.readPixels(0, 0, width, height, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);

        // Convert the pixels to a BufferedImage
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int i = (x + (height - y - 1) * width) * 4; // Инверсия Y-координаты
                int r = buffer.get(i) & 0xFF;
                int g = buffer.get(i + 1) & 0xFF;
                int b = buffer.get(i + 2) & 0xFF;
                image.setRGB(x, y, new Color(r, g, b).getRGB());
            }
        }

        // Scale the image if it's too large
        BufferedImage resizedImage;
        if (image.getWidth() > SCREENSHOT_TARGET_WIDTH) {
            int newWidth = SCREENSHOT_TARGET_WIDTH;
            int newHeight = (int) ((double) SCREENSHOT_TARGET_WIDTH / width * height);
            Image scaledImage = image.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
            resizedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = resizedImage.createGraphics();
            g2d.drawImage(scaledImage, 0, 0, null);
            g2d.dispose();
        } else {
            resizedImage = image;
        }

        return resizedImage;
    }
}
