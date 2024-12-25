package com.igorlink.stanleygpt.client.eventhandlers.handlers;

import com.igorlink.stanleygpt.client.eventhandlers.handlers.service.FrequencyAndPriority;
import com.igorlink.stanleygpt.payloads.PlayerTamedEntityPayload;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.client.MinecraftClient;

/**
 * Handles the event of the player taming an entity.
 */
@Slf4j
public class PlayerTamedEntityEventHandler extends AbstractEventHandler<PlayerTamedEntityPayload>{

    public PlayerTamedEntityEventHandler() {
        super(PlayerTamedEntityPayload.ID, false);
    }


    @Override
    protected FrequencyAndPriority getPrioirty(PlayerTamedEntityPayload payload) {
        return FrequencyAndPriority.of(100, 85);
    }


    @Override
    protected String handleEvent(MinecraftClient client, PlayerTamedEntityPayload payload) {
        String prompt = "Игрок приручил существо! " +
                "Прирученное существо: " + payload.tamedEntity() + ".";

        log.info(prompt);

        return prompt;
    }
}
