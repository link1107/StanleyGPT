package com.igorlink.stanleygpt.client.eventhandlers.handlers;

import com.igorlink.stanleygpt.client.eventhandlers.handlers.service.FrequencyAndPriority;
import com.igorlink.stanleygpt.payloads.DropFromBlockPayload;
import com.igorlink.stanleygpt.service.ItemStackDto;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.registry.Registries;

import java.util.Map;

import static net.minecraft.block.Blocks.*;

/**
 * Handles the event of a player breaking a block and items dropping from it.
 */
@Slf4j
public class DropFromBlockEventHandler extends AbstractEventHandler<DropFromBlockPayload> {

    public DropFromBlockEventHandler() {
        super(DropFromBlockPayload.ID, false);
    }


    public static final Map<Block, FrequencyAndPriority> BLOCK_PRIORITIES = Map.ofEntries(
            Map.entry(DIAMOND_ORE, FrequencyAndPriority.of(100, 100)),
            Map.entry(DEEPSLATE_DIAMOND_ORE, FrequencyAndPriority.of(100, 100)),
            Map.entry(ANCIENT_DEBRIS, FrequencyAndPriority.of(100, 100)),
            Map.entry(EMERALD_ORE, FrequencyAndPriority.of(95, 95)),
            Map.entry(DEEPSLATE_EMERALD_ORE, FrequencyAndPriority.of(95, 95)),
            Map.entry(GOLD_ORE, FrequencyAndPriority.of(85, 85)),
            Map.entry(DEEPSLATE_GOLD_ORE, FrequencyAndPriority.of(85, 85)),
            Map.entry(IRON_ORE, FrequencyAndPriority.of(80, 80)),
            Map.entry(DEEPSLATE_IRON_ORE, FrequencyAndPriority.of(80, 80)),
            Map.entry(REDSTONE_ORE, FrequencyAndPriority.of(75, 75)),
            Map.entry(DEEPSLATE_REDSTONE_ORE, FrequencyAndPriority.of(75, 75)),
            Map.entry(LAPIS_ORE, FrequencyAndPriority.of(70, 70)),
            Map.entry(DEEPSLATE_LAPIS_ORE, FrequencyAndPriority.of(70, 70)),
            Map.entry(COPPER_ORE, FrequencyAndPriority.of(50, 50)),
            Map.entry(DEEPSLATE_COPPER_ORE, FrequencyAndPriority.of(50, 50)),
            Map.entry(COAL_ORE, FrequencyAndPriority.of(50, 50)),
            Map.entry(DEEPSLATE_COAL_ORE, FrequencyAndPriority.of(40, 40)),
            Map.entry(OBSIDIAN, FrequencyAndPriority.of(90, 90)),
            Map.entry(SPAWNER, FrequencyAndPriority.of(100, 100)),
            Map.entry(BOOKSHELF, FrequencyAndPriority.of(60, 60)),
            Map.entry(CRAFTING_TABLE, FrequencyAndPriority.of(20, 20)),
            Map.entry(FURNACE, FrequencyAndPriority.of(15, 15)),
            Map.entry(SHULKER_BOX, FrequencyAndPriority.of(90, 90)),
            Map.entry(END_STONE, FrequencyAndPriority.of(10, 10)),
            Map.entry(SOUL_SAND, FrequencyAndPriority.of(50, 50)),
            Map.entry(SANDSTONE, FrequencyAndPriority.of(10, 10)),
            Map.entry(NETHER_QUARTZ_ORE, FrequencyAndPriority.of(70, 70)),
            Map.entry(CLAY, FrequencyAndPriority.of(30, 30)),
            Map.entry(SEA_LANTERN, FrequencyAndPriority.of(80, 80)),
            Map.entry(GOLD_BLOCK, FrequencyAndPriority.of(90, 90)),
            Map.entry(DIAMOND_BLOCK, FrequencyAndPriority.of(100, 100)),
            Map.entry(OAK_LOG, FrequencyAndPriority.of(10, 10)),
            Map.entry(PUMPKIN, FrequencyAndPriority.of(40, 40)),
            Map.entry(WHEAT, FrequencyAndPriority.of(25, 25)),
            Map.entry(CARROTS, FrequencyAndPriority.of(25, 25)),
            Map.entry(BEACON, FrequencyAndPriority.of(100, 100))
    );


    @Override
    protected FrequencyAndPriority getPrioirty(DropFromBlockPayload payload) {
        Block block = Registries.BLOCK.get(payload.blockId());

        return BLOCK_PRIORITIES.getOrDefault(block,
                FrequencyAndPriority.of(3, 5));
    }

    @Override
    protected String handleEvent(MinecraftClient client, DropFromBlockPayload payload) {
        String prompt = "Игрок разбил блок! " +
                "Разбитый блок: " + Registries.BLOCK.get(payload.blockId()).getName().getString() + ", " +
                "инструмент: " + payload.tool().getToolName() + ", " +
                "из блока выпало: " + ItemStackDto.createStringFromArray(payload.drops()) + ". " +
                "Эти выпавшие предметы еще не были добавлены в инвентарь игрока, " +
                "приведенный ниже в блоке дополнительной информации.";

        log.info(prompt);

        return prompt;
    }
}
