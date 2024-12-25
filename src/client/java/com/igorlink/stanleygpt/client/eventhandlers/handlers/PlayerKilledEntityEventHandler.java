package com.igorlink.stanleygpt.client.eventhandlers.handlers;

import com.igorlink.stanleygpt.client.eventhandlers.handlers.service.FrequencyAndPriority;
import com.igorlink.stanleygpt.payloads.PlayerKilledEntityPayload;
import com.igorlink.stanleygpt.service.ItemStackDto;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.*;

import java.util.Map;

/**
 * Handles the event of a player killing an entity.
 */
@Slf4j
public class PlayerKilledEntityEventHandler extends AbstractEventHandler<PlayerKilledEntityPayload> {

    public PlayerKilledEntityEventHandler() {
        super(PlayerKilledEntityPayload.ID, false);
    }


    // Map of entity types to their frequency and priority values
    private final Map<EntityType<? extends MobEntity>, FrequencyAndPriority> pnqMap = Map.ofEntries(
            // High priority (bosses)
            Map.entry(EntityType.ENDER_DRAGON, FrequencyAndPriority.of(100, 100)),
            Map.entry(EntityType.WITHER, FrequencyAndPriority.of(95, 95)),
            Map.entry(EntityType.WARDEN, FrequencyAndPriority.of(90, 90)),

            // Average priority (hostile mobs)
            Map.entry(EntityType.BLAZE, FrequencyAndPriority.of(80, 80)),
            Map.entry(EntityType.EVOKER, FrequencyAndPriority.of(75, 75)),
            Map.entry(EntityType.GUARDIAN, FrequencyAndPriority.of(70, 70)),
            Map.entry(EntityType.ILLUSIONER, FrequencyAndPriority.of(70, 70)),
            Map.entry(EntityType.PIGLIN_BRUTE, FrequencyAndPriority.of(65, 65)),
            Map.entry(EntityType.RAVAGER, FrequencyAndPriority.of(60, 60)),
            Map.entry(EntityType.VINDICATOR, FrequencyAndPriority.of(55, 55)),
            Map.entry(EntityType.WITCH, FrequencyAndPriority.of(50, 50)),

            // Low priority (common mobs)
            Map.entry(EntityType.CREEPER, FrequencyAndPriority.of(50, 50)),
            Map.entry(EntityType.SKELETON, FrequencyAndPriority.of(45, 45)),
            Map.entry(EntityType.ZOMBIE, FrequencyAndPriority.of(40, 40)),
            Map.entry(EntityType.SPIDER, FrequencyAndPriority.of(40, 40)),
            Map.entry(EntityType.SLIME, FrequencyAndPriority.of(35, 35)),

            // Very low priority (passive mobs)
            Map.entry(EntityType.COW, FrequencyAndPriority.of(35, 30)),
            Map.entry(EntityType.CHICKEN, FrequencyAndPriority.of(35, 30)),
            Map.entry(EntityType.SHEEP, FrequencyAndPriority.of(35, 30)),
            Map.entry(EntityType.PIG, FrequencyAndPriority.of(35, 30)),

            // Specific mobs
            Map.entry(EntityType.ALLAY, FrequencyAndPriority.of(60, 60)), // Важный моб для поиска предметов
            Map.entry(EntityType.VILLAGER, FrequencyAndPriority.of(100, 55)), // Может повлиять на торговлю
            Map.entry(EntityType.IRON_GOLEM, FrequencyAndPriority.of(100, 60)), // Связано с защитой деревни
            Map.entry(EntityType.WOLF, FrequencyAndPriority.of(50, 25)), // Полезный союзник
            Map.entry(EntityType.FOX, FrequencyAndPriority.of(100, 40)), // Декоративный и редкий

            // Water mobs
            Map.entry(EntityType.DOLPHIN, FrequencyAndPriority.of(50, 50)),
            Map.entry(EntityType.TURTLE, FrequencyAndPriority.of(50, 30)),
            Map.entry(EntityType.SQUID, FrequencyAndPriority.of(50, 30)),

            // Rare mobs
            Map.entry(EntityType.MOOSHROOM, FrequencyAndPriority.of(40, 25)),
            Map.entry(EntityType.PANDA, FrequencyAndPriority.of(100, 40))
    );


    @Override
    protected FrequencyAndPriority getPrioirty(PlayerKilledEntityPayload payload) {
        if (payload.killedEntity().isBaby()) {
            return FrequencyAndPriority.of(100, 75);
        }

        if (payload.killedEntity().isTamedByPlayer()) {
            return FrequencyAndPriority.of(100, 95);
        }

        if (!payload.killedEntity().getEntityCustomName().isEmpty()) {
            return FrequencyAndPriority.of(100, 95);
        }

        // Get the priority value of the entity
        return pnqMap.getOrDefault(payload.killedEntity().getEntityType(),
                FrequencyAndPriority.of(50, 50));
    }


    @Override
    protected String handleEvent(MinecraftClient client, PlayerKilledEntityPayload payload) {
        String prompt = "Игрок убил существо! " +
                "Убитое существо: " + payload.killedEntity() + ", " +
                "выпавший лут: " + ItemStackDto.createStringFromArray(payload.drops()) + ".";

        log.info(prompt);

        return prompt;
    }
}
