package com.igorlink.stanleygpt.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;

/**
 * Utility methods.
 */
public class Utils {

    /**
     * Converts a BufferedImage to a Base64 string.
     *
     * @param image The image to convert.
     * @return The Base64 string.
     */
    public static String convertJpgToBase64(BufferedImage image) {
        try {
            // Создаём поток в памяти
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            // Сохраняем BufferedImage в поток
            ImageIO.write(image, "jpg", outputStream);

            // Кодируем байты из потока в строку Base64
            byte[] imageBytes = outputStream.toByteArray();
            return Base64.getEncoder().encodeToString(imageBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * Shows a notification to the user.
     *
     * @param title      The title of the notification.
     * @param message    The message of the notification.
     * @param titleColor The color of the title.
     */
    public static void showNotification(String title, String message, Formatting titleColor) {
        // Заголовок и текст самого уведомления
        Text description = Text.literal(message);

        // Создаем и показываем "тост"
        SystemToast toast = new SystemToast(
                SystemToast.Type.PERIODIC_NOTIFICATION, // можете использовать готовый Type или сделать свой
                Text.literal(title).styled(style -> style.withColor(titleColor)), // заголовок
                description
        );

        MinecraftClient.getInstance().getToastManager().add(toast);
    }

    /**
     * Shows an error notification to the user.
     *
     * @param message The message of the notification.
     */
    public static void showErrorNotification(String message) {
        showNotification("Ошибка", message, Formatting.YELLOW);
    }


}
