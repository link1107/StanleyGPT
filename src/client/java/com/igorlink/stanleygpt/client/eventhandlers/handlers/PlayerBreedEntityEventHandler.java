package com.igorlink.stanleygpt.client.eventhandlers.handlers;

import com.igorlink.stanleygpt.client.eventhandlers.handlers.service.FrequencyAndPriority;
import com.igorlink.stanleygpt.payloads.PlayerBreedEntityPayload;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.client.MinecraftClient;

/**
 * Handles the event of two mobs near the player breeding a child.
 */
@Slf4j
public class PlayerBreedEntityEventHandler extends AbstractEventHandler<PlayerBreedEntityPayload> {

    public PlayerBreedEntityEventHandler() {
        super(PlayerBreedEntityPayload.ID, false);
    }


    @Override
    protected FrequencyAndPriority getPrioirty(PlayerBreedEntityPayload payload) {
        return FrequencyAndPriority.of(100, 60);
    }


    @Override
    protected String handleEvent(MinecraftClient client, PlayerBreedEntityPayload payload) {
        String prompt = "У двух мобов недалеко от игрока родился ребенок! " +
                "Ребенок: " + payload.child() + ", " +
                "первый родитель: " + payload.parentNumberOne() + ", " +
                "второй родитель" + payload.parentNumberTwo() + ".";

        log.info(prompt);

        return prompt;
    }
}
