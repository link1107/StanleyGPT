package com.igorlink.stanleygpt.client.eventhandlers.handlers;

import com.igorlink.stanleygpt.client.eventhandlers.handlers.service.FrequencyAndPriority;
import com.igorlink.stanleygpt.payloads.PlayerMovedToAnotherWorldPayload;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.client.MinecraftClient;

/**
 * Handles the event of the player moving to another world.
 */
@Slf4j
public class PlayerMovedToAnotherWorldEventHandler extends AbstractEventHandler<PlayerMovedToAnotherWorldPayload> {

    public PlayerMovedToAnotherWorldEventHandler() {
        super(PlayerMovedToAnotherWorldPayload.ID, false);
    }


    @Override
    protected FrequencyAndPriority getPrioirty(PlayerMovedToAnotherWorldPayload payload) {
        return FrequencyAndPriority.of(100, 70);
    }


    @Override
    protected String handleEvent(MinecraftClient client, PlayerMovedToAnotherWorldPayload payload) {
        String prompt = "Игрок телепортировался в другой мир! " +
                "Исходный мир: " + payload.fromWorld() + ", " +
                "новый мир: " + payload.toWorld() + ".";

        log.info(prompt);

        return prompt;
    }
}
