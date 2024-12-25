package com.igorlink.stanleygpt.client.eventhandlers.handlers;

import com.igorlink.stanleygpt.client.eventhandlers.handlers.service.FrequencyAndPriority;
import com.igorlink.stanleygpt.payloads.PlayerTakeItemFromFurnacePayload;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.client.MinecraftClient;

/**
 * Handles the event of the player taking an item from a furnace.
 */
@Slf4j
public class PlayerTakeItemFromFurnaceEventHandler extends AbstractEventHandler<PlayerTakeItemFromFurnacePayload> {

    public PlayerTakeItemFromFurnaceEventHandler() {
        super(PlayerTakeItemFromFurnacePayload.ID, false);
    }


    @Override
    protected FrequencyAndPriority getPrioirty(PlayerTakeItemFromFurnacePayload payload) {
        return FrequencyAndPriority.of(100, 50);
    }


    @Override
    protected String handleEvent(MinecraftClient client, PlayerTakeItemFromFurnacePayload payload) {
        String prompt = "Игрок приготовил предмет в печи!" +
                "Приготовленный в печи предмет: " + payload.item().toItemString() + ".";

        log.info(prompt);

        return prompt;
    }
}
