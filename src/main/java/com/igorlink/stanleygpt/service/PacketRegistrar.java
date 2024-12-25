package com.igorlink.stanleygpt.service;

import com.igorlink.stanleygpt.payloads.*;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

/**
 * A class that is purposed for registering packets.
 */
public class PacketRegistrar {

    /**
     * Registers all packets for sending from the server to the client. Must be called in the
     * {@link com.igorlink.stanleygpt.StanleyGpt#onInitialize()} method.
     */
    public static void registerPackets() {
        PayloadTypeRegistry.playS2C()
                .register(PlayerDeathPayload.ID, PlayerDeathPayload.CODEC);

        PayloadTypeRegistry.playS2C()
                .register(PlayerKilledEntityPayload.ID, PlayerKilledEntityPayload.CODEC);

        PayloadTypeRegistry.playS2C()
                .register(DropFromBlockPayload.ID, DropFromBlockPayload.CODEC);

        PayloadTypeRegistry.playS2C()
                .register(PlayerCraftedItemPayload.ID, PlayerCraftedItemPayload.CODEC);

        PayloadTypeRegistry.playS2C()
                .register(PlayerMovedToAnotherWorldPayload.ID, PlayerMovedToAnotherWorldPayload.CODEC);

        PayloadTypeRegistry.playS2C()
                .register(PlayerTakeItemFromFurnacePayload.ID, PlayerTakeItemFromFurnacePayload.CODEC);

        PayloadTypeRegistry.playS2C()
                .register(PlayerTamedEntityPayload.ID, PlayerTamedEntityPayload.CODEC);

        PayloadTypeRegistry.playS2C()
                .register(TamedEntityKilledLivingEntityPayload.ID, TamedEntityKilledLivingEntityPayload.CODEC);

        PayloadTypeRegistry.playS2C()
                .register(ExplosionHappenedPayload.ID, ExplosionHappenedPayload.CODEC);

        PayloadTypeRegistry.playS2C()
                .register(PlayerDamagedPayload.ID, PlayerDamagedPayload.CODEC);

        PayloadTypeRegistry.playS2C()
                .register(PlayerEatPayload.ID, PlayerEatPayload.CODEC);

        PayloadTypeRegistry.playS2C()
                .register(PlayerFeedEntityPayload.ID, PlayerFeedEntityPayload.CODEC);

        PayloadTypeRegistry.playS2C()
                .register(PlayerHookedItemWithFishingRodPayload.ID, PlayerHookedItemWithFishingRodPayload.CODEC);

        PayloadTypeRegistry.playS2C()
                .register(PlayerBreedEntityPayload.ID, PlayerBreedEntityPayload.CODEC);

        PayloadTypeRegistry.playS2C()
                .register(TamedEntityKilledPayload.ID, TamedEntityKilledPayload.CODEC);
    }

}
