package com.igorlink.stanleygpt.client.eventhandlers.handlers;

import com.igorlink.stanleygpt.client.eventhandlers.handlers.service.FrequencyAndPriority;
import com.igorlink.stanleygpt.payloads.PlayerFeedEntityPayload;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.client.MinecraftClient;

/**
 * Handles the event of the player feeding an entity.
 */
@Slf4j
public class PlayerFeedEventHandler extends AbstractEventHandler<PlayerFeedEntityPayload> {

    public PlayerFeedEventHandler() {
        super(PlayerFeedEntityPayload.ID, false);
    }


    @Override
    protected FrequencyAndPriority getPrioirty(PlayerFeedEntityPayload payload) {
        return FrequencyAndPriority.of(40, 40);
    }


    @Override
    protected String handleEvent(MinecraftClient client, PlayerFeedEntityPayload payload) {
        String prompt = "Игрок покормил существо! " +
                "Существо: " + payload.entity() + ", " +
                "еда: " + payload.foodItem().toItemString() + ".";

        log.info(prompt);

        return prompt;

    }
}
