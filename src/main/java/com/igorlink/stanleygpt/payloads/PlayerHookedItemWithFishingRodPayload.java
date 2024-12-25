package com.igorlink.stanleygpt.payloads;

import com.igorlink.stanleygpt.ModIdentifiers;
import com.igorlink.stanleygpt.payloads.codecs.ExtendedPacketCodecs;
import com.igorlink.stanleygpt.service.ItemStackDto;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

/**
 * Payload for player hooked item with fishing rod event.
 *
 * @param loot the loot
 */
public record PlayerHookedItemWithFishingRodPayload(ItemStackDto[] loot) implements CustomPayload {

    public static final CustomPayload.Id<PlayerHookedItemWithFishingRodPayload> ID =
            new CustomPayload.Id<>(ModIdentifiers.PLAYER_HOOKED_ITEM_WITH_FISHING_BOBBER_PACKET_ID);


    public static final PacketCodec<ByteBuf, PlayerHookedItemWithFishingRodPayload> CODEC =
            PacketCodec.tuple(
                    ExtendedPacketCodecs.ITEM_STACK_DTO_ARRAY, PlayerHookedItemWithFishingRodPayload::loot,
                    PlayerHookedItemWithFishingRodPayload::new);


    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }

}
