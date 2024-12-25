package com.igorlink.stanleygpt.client.eventhandlers.handlers;

import com.igorlink.stanleygpt.client.eventhandlers.handlers.service.FrequencyAndPriority;
import com.igorlink.stanleygpt.payloads.TamedEntityKilledLivingEntityPayload;
import com.igorlink.stanleygpt.service.ItemStackDto;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.client.MinecraftClient;

/**
 * Handles the event when a tamed entity is killed by a living entity.
 */
@Slf4j
public class TamedEntityKilledLivingEntityEventHandler extends AbstractEventHandler<TamedEntityKilledLivingEntityPayload> {

    public TamedEntityKilledLivingEntityEventHandler() {
        super(TamedEntityKilledLivingEntityPayload.ID, false);
    }


    @Override
    protected FrequencyAndPriority getPrioirty(TamedEntityKilledLivingEntityPayload payload) {
        return FrequencyAndPriority.of(100, 55);
    }


    @Override
    protected String handleEvent(MinecraftClient client, TamedEntityKilledLivingEntityPayload payload) {
        String prompt = "Прирученный игроком моб убил другого моба! " +
                "Моб-убийца: " + payload.attacker() + ", " +
                "моб-жертва: " + payload.killedEntity() + ", " +
                "из жертвы выпало: " + ItemStackDto.createStringFromArray(payload.drops()) + ".";

        log.info(prompt);

        return prompt;
    }
}
