package com.igorlink.stanleygpt.client.eventhandlers.handlers;

import com.igorlink.stanleygpt.client.eventhandlers.handlers.service.FrequencyAndPriority;
import com.igorlink.stanleygpt.payloads.PlayerHookedItemWithFishingRodPayload;
import com.igorlink.stanleygpt.service.ItemStackDto;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.client.MinecraftClient;

/**
 * Handles the event of the player hooking an item with a fishing rod.
 */
@Slf4j
public class PlayerHookedItemWithFishingBobberEventHandler extends AbstractEventHandler<PlayerHookedItemWithFishingRodPayload> {

    public PlayerHookedItemWithFishingBobberEventHandler() {
        super(PlayerHookedItemWithFishingRodPayload.ID, false);
    }

    @Override
    protected FrequencyAndPriority getPrioirty(PlayerHookedItemWithFishingRodPayload payload) {
        return FrequencyAndPriority.of(100, 65);
    }


    @Override
    protected String handleEvent(MinecraftClient client, PlayerHookedItemWithFishingRodPayload payload) {
        String prompt = "Игрок выловил удочкой следующие предметы: " +
                ItemStackDto.createStringFromArray(payload.loot()) + ".";

        log.info(prompt);

        return prompt;
    }
}
