package com.igorlink.stanleygpt.client.eventhandlers.handlers;

import com.igorlink.stanleygpt.client.eventhandlers.handlers.service.FrequencyAndPriority;
import com.igorlink.stanleygpt.payloads.ExplosionHappenedPayload;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.client.MinecraftClient;

/**
 * Handles the event of an explosion happens near the player.
 */
@Slf4j
public class ExplosionHappenedEventHandler extends AbstractEventHandler<ExplosionHappenedPayload> {

    public ExplosionHappenedEventHandler() {
        super(ExplosionHappenedPayload.ID, false);
    }


    @Override
    protected FrequencyAndPriority getPrioirty(ExplosionHappenedPayload payload) {
        return FrequencyAndPriority.of(100, 50);
    }


    @Override
    protected String handleEvent(MinecraftClient client, ExplosionHappenedPayload payload) {
        String prompt = "Где-то возле игрока произошел взрыв! " +
                "Источник взрыва: " + payload.explosionSource() + ", " +
                "Расстояние от игрока: " + payload.distanceFromPlayer() + "блоков.";

        log.info(prompt);

        return prompt;
    }
}
