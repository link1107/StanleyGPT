package com.igorlink.stanleygpt.client.eventhandlers.handlers;

import com.igorlink.stanleygpt.client.eventhandlers.handlers.service.FrequencyAndPriority;
import com.igorlink.stanleygpt.payloads.PlayerDamagedPayload;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.client.MinecraftClient;

/**
 * Handles the event of the player taking damage.
 */
@Slf4j
public class PlayerDamagedEventHandler extends AbstractEventHandler<PlayerDamagedPayload>{

    public PlayerDamagedEventHandler() {
        super(PlayerDamagedPayload.ID, false);
    }


    @Override
    protected FrequencyAndPriority getPrioirty(PlayerDamagedPayload payload) {
        return FrequencyAndPriority.of(100, 60);
    }


    @Override
    protected String handleEvent(MinecraftClient client, PlayerDamagedPayload payload) {
        String prompt = "Игрок получил урон! " +
                "Причина получения урона: " + payload.damageSource() + ", " +
                "размер урона: " + payload.damageAmount() + " единиц" +
                (payload.attacker() == null ? "" : ", атаковавший: " + payload.attacker()) +
                ".";

        log.info(prompt);

        return prompt;
    }
}
