package com.igorlink.stanleygpt.client.eventhandlers.handlers;

import com.igorlink.stanleygpt.client.eventhandlers.handlers.service.FrequencyAndPriority;
import com.igorlink.stanleygpt.payloads.PlayerEatPayload;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.client.MinecraftClient;

/**
 * Handles the event of the player eating food.
 */
@Slf4j
public class PlayerEatEventHandler extends AbstractEventHandler<PlayerEatPayload> {

    public PlayerEatEventHandler() {
        super(PlayerEatPayload.ID, false);
    }


    @Override
    protected FrequencyAndPriority getPrioirty(PlayerEatPayload payload) {
        return FrequencyAndPriority.of(100, 50);
    }


    @Override
    protected String handleEvent(MinecraftClient client, PlayerEatPayload payload) {
        String prompt = "Игрок употребил пищу! " +
                "Употребленный в пищу предмет: " + payload.food().toItemString() + ".";

        log.info(prompt);

        return prompt;
    }
}
