package com.igorlink.stanleygpt.client;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.util.Formatting;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Shows a warning message when the client is launched.
 */
public class OnLaunchWarning {

    /**
     * The type of the warning message.
     */
    public enum WarningType {
        UNKNOWN_ERROR, FAILED_STATE_ERROR
    }

    /**
     * Shows a warning message when the client is launched.
     *
     * @param warningType The type of the warning message.
     */
    public OnLaunchWarning(WarningType warningType) {
        AtomicBoolean shown = new AtomicBoolean(false);

        ClientTickEvents.START_CLIENT_TICK.register(client -> {

            if ((MinecraftClient.getInstance().currentScreen instanceof TitleScreen) && !shown.get()) {
                shown.set(true);
                if (warningType == WarningType.UNKNOWN_ERROR) {
                    Utils.showNotification("StanleyGPT не активен",
                            "Для запуска введите корректные ключи API в настройках!",
                            Formatting.YELLOW);
                } else {
                    Utils.showErrorNotification("OpenAI не работает в вашей стране, используйте VPN.");
                }
            }


        });
    }

}
