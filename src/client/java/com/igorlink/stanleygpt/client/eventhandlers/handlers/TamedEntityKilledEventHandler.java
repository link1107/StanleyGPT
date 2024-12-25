package com.igorlink.stanleygpt.client.eventhandlers.handlers;

import com.igorlink.stanleygpt.client.eventhandlers.handlers.service.FrequencyAndPriority;
import com.igorlink.stanleygpt.payloads.TamedEntityKilledPayload;
import com.igorlink.stanleygpt.service.ItemStackDto;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.client.MinecraftClient;

/**
 * Handles the event of a tamed entity being killed.
 */
@Slf4j
public class TamedEntityKilledEventHandler extends AbstractEventHandler<TamedEntityKilledPayload>{

    public TamedEntityKilledEventHandler() {
        super(TamedEntityKilledPayload.ID, false);
    }


    @Override
    protected FrequencyAndPriority getPrioirty(TamedEntityKilledPayload payload) {
        return FrequencyAndPriority.of(100,95);
    }


    @Override
    protected String handleEvent(MinecraftClient client, TamedEntityKilledPayload payload) {
        String prompt = "Прирученный игроком моб умер! " +
                "Моб-жертва: " + payload.killedEntity() + ", " +
                "причина смерти: " + payload.damageSource() + ", " +
                ((payload.attacker() == null) ? "" : ("убийца: " + payload.attacker() + ", ")) +
                "из жертвы выпало: " + ItemStackDto.createStringFromArray(payload.drops()) + ".";

        log.info(prompt);

        return prompt;
    }
}
