package com.igorlink.stanleygpt.client.eventhandlers.handlers;

import com.igorlink.stanleygpt.client.eventhandlers.handlers.service.FrequencyAndPriority;
import com.igorlink.stanleygpt.payloads.PlayerDeathPayload;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

/**
 * Handles the event of the player dying.
 */
@Slf4j
public class PlayerDeathEventHandler extends AbstractEventHandler<PlayerDeathPayload> {

    public PlayerDeathEventHandler() {
        super(PlayerDeathPayload.ID, false);
    }


    @Override
    protected FrequencyAndPriority getPrioirty(PlayerDeathPayload payload) {
        return FrequencyAndPriority.of(100, 100);
    }


    @Override
    protected String handleEvent(MinecraftClient client, PlayerDeathPayload payload) {
        String prompt = "Игрок позорно умер! " +
                "Причина смерти: " + payload.deathReason() +
                (payload.attacker() == null ? "" : ", убийца: " + payload.attacker()) +
                ".";

        log.info(prompt);

        return prompt;
    }
}
