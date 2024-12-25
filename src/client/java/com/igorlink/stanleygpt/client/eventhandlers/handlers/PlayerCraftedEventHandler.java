package com.igorlink.stanleygpt.client.eventhandlers.handlers;


import com.igorlink.stanleygpt.client.eventhandlers.handlers.service.FrequencyAndPriority;
import com.igorlink.stanleygpt.payloads.PlayerCraftedItemPayload;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Item;

import java.util.Map;

import static net.minecraft.item.Items.*;

/**
 * Handles the event of a player crafting an item.
 */
@Slf4j
public class PlayerCraftedEventHandler extends AbstractEventHandler<PlayerCraftedItemPayload> {

    public PlayerCraftedEventHandler() {
        super(PlayerCraftedItemPayload.ID, false);
    }


    // Map of items to their frequency and priority
    public static final Map<Item, FrequencyAndPriority> pnqMap = Map.<Item, FrequencyAndPriority>ofEntries(
            Map.entry(DIAMOND_SWORD, FrequencyAndPriority.of(95, 95)),
            Map.entry(NETHERITE_SWORD, FrequencyAndPriority.of(95, 95)),
            Map.entry(DIAMOND_PICKAXE, FrequencyAndPriority.of(95, 95)),
            Map.entry(NETHERITE_PICKAXE, FrequencyAndPriority.of(95, 95)),
            Map.entry(DIAMOND_AXE, FrequencyAndPriority.of(95, 95)),
            Map.entry(NETHERITE_AXE, FrequencyAndPriority.of(95, 95)),
            Map.entry(DIAMOND_SHOVEL, FrequencyAndPriority.of(95, 95)),
            Map.entry(NETHERITE_SHOVEL, FrequencyAndPriority.of(95, 95)),
            Map.entry(DIAMOND_HOE, FrequencyAndPriority.of(95, 95)),
            Map.entry(NETHERITE_HOE, FrequencyAndPriority.of(95, 95)),
            Map.entry(DIAMOND_HELMET, FrequencyAndPriority.of(95, 95)),
            Map.entry(NETHERITE_HELMET, FrequencyAndPriority.of(95, 95)),
            Map.entry(DIAMOND_CHESTPLATE, FrequencyAndPriority.of(95, 95)),
            Map.entry(NETHERITE_CHESTPLATE, FrequencyAndPriority.of(95, 95)),
            Map.entry(DIAMOND_LEGGINGS, FrequencyAndPriority.of(95, 95)),
            Map.entry(NETHERITE_LEGGINGS, FrequencyAndPriority.of(95, 95)),
            Map.entry(DIAMOND_BOOTS, FrequencyAndPriority.of(95, 95)),
            Map.entry(NETHERITE_BOOTS, FrequencyAndPriority.of(95, 95)),
            Map.entry(IRON_SWORD, FrequencyAndPriority.of(95, 95)),
            Map.entry(IRON_PICKAXE, FrequencyAndPriority.of(95, 95)),
            Map.entry(IRON_AXE, FrequencyAndPriority.of(95, 95)),
            Map.entry(IRON_SHOVEL, FrequencyAndPriority.of(95, 95)),
            Map.entry(IRON_HOE, FrequencyAndPriority.of(95, 95)),
            Map.entry(IRON_HELMET, FrequencyAndPriority.of(95, 95)),
            Map.entry(IRON_CHESTPLATE, FrequencyAndPriority.of(95, 95)),
            Map.entry(IRON_LEGGINGS, FrequencyAndPriority.of(95, 95)),
            Map.entry(IRON_BOOTS, FrequencyAndPriority.of(95, 95)),
            Map.entry(GOLDEN_SWORD, FrequencyAndPriority.of(95, 95)),
            Map.entry(GOLDEN_PICKAXE, FrequencyAndPriority.of(95, 95)),
            Map.entry(GOLDEN_AXE, FrequencyAndPriority.of(95, 95)),
            Map.entry(GOLDEN_SHOVEL, FrequencyAndPriority.of(95, 95)),
            Map.entry(GOLDEN_HOE, FrequencyAndPriority.of(95, 95)),
            Map.entry(GOLDEN_HELMET, FrequencyAndPriority.of(95, 95)),
            Map.entry(GOLDEN_CHESTPLATE, FrequencyAndPriority.of(95, 95)),
            Map.entry(GOLDEN_LEGGINGS, FrequencyAndPriority.of(95, 95)),
            Map.entry(GOLDEN_BOOTS, FrequencyAndPriority.of(95, 95)),
            Map.entry(STONE_SWORD, FrequencyAndPriority.of(95, 95)),
            Map.entry(STONE_PICKAXE, FrequencyAndPriority.of(95, 95)),
            Map.entry(STONE_AXE, FrequencyAndPriority.of(95, 95)),
            Map.entry(STONE_SHOVEL, FrequencyAndPriority.of(95, 95)),
            Map.entry(STONE_HOE, FrequencyAndPriority.of(95, 95)),
            Map.entry(LEATHER_HELMET, FrequencyAndPriority.of(95, 95)),
            Map.entry(LEATHER_CHESTPLATE, FrequencyAndPriority.of(95, 95)),
            Map.entry(LEATHER_LEGGINGS, FrequencyAndPriority.of(95, 95)),
            Map.entry(LEATHER_BOOTS, FrequencyAndPriority.of(95, 95)),
            Map.entry(WOODEN_SWORD, FrequencyAndPriority.of(95, 95)),
            Map.entry(WOODEN_PICKAXE, FrequencyAndPriority.of(95, 95)),
            Map.entry(WOODEN_AXE, FrequencyAndPriority.of(95, 95)),
            Map.entry(WOODEN_SHOVEL, FrequencyAndPriority.of(95, 95)),
            Map.entry(WOODEN_HOE, FrequencyAndPriority.of(95, 95)),
            Map.entry(SHIELD, FrequencyAndPriority.of(95, 95)),
            Map.entry(BOW, FrequencyAndPriority.of(95, 95)),
            Map.entry(CROSSBOW, FrequencyAndPriority.of(95, 95)),
            Map.entry(TRIDENT, FrequencyAndPriority.of(95, 95)),
            Map.entry(ELYTRA, FrequencyAndPriority.of(95, 95)),
            Map.entry(FISHING_ROD, FrequencyAndPriority.of(95, 95)),
            Map.entry(SHEARS, FrequencyAndPriority.of(95, 95)),
            Map.entry(FLINT_AND_STEEL, FrequencyAndPriority.of(95, 95)),
            Map.entry(ENDER_CHEST, FrequencyAndPriority.of(95, 95)),
            Map.entry(CRAFTING_TABLE, FrequencyAndPriority.of(95, 95)),
            Map.entry(FURNACE, FrequencyAndPriority.of(95, 95)),
            Map.entry(BREWING_STAND, FrequencyAndPriority.of(95, 95)),
            Map.entry(ANVIL, FrequencyAndPriority.of(95, 95)),
            Map.entry(ENCHANTING_TABLE, FrequencyAndPriority.of(95, 95)),
            Map.entry(TORCH, FrequencyAndPriority.of(35, 35)),
            Map.entry(REDSTONE_TORCH, FrequencyAndPriority.of(95, 95)),
            Map.entry(RAIL, FrequencyAndPriority.of(95, 95)),
            Map.entry(POWERED_RAIL, FrequencyAndPriority.of(95, 95)),
            Map.entry(DETECTOR_RAIL, FrequencyAndPriority.of(95, 95))
    );


    @Override
    protected FrequencyAndPriority getPrioirty(PlayerCraftedItemPayload payload) {
        Item item = payload.craftedItem().getItem();

        return pnqMap.getOrDefault(item, FrequencyAndPriority.of(3, 20));
    }


    @Override
    protected String handleEvent(MinecraftClient client, PlayerCraftedItemPayload payload) {
        String prompt = "Игрок скрафтил предмет! " +
                "Скрафченный предмет: " + payload.craftedItem().toString() + ". " +
                "Примечание: полученные в результате крафта вещи еще не были добавлены в инвентарь игрока, " +
                "приведенный ниже в блоке дополнительной информации.";

        log.info(prompt);

        return prompt;
    }
}
