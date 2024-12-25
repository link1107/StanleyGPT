package com.igorlink.stanleygpt.payloads;

import com.igorlink.stanleygpt.ModIdentifiers;
import com.igorlink.stanleygpt.payloads.codecs.ExtendedPacketCodecs;
import com.igorlink.stanleygpt.service.ItemStackDto;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

/**
 * Payload for player crafted item event.
 *
 * @param craftedItem the crafted item
 */
public record PlayerCraftedItemPayload(ItemStackDto craftedItem) implements CustomPayload {

    public static final CustomPayload.Id<PlayerCraftedItemPayload> ID =
            new CustomPayload.Id<>(ModIdentifiers.PLAYER_CRAFTED_ITEM_PACKET_ID);


    public static final PacketCodec<ByteBuf, PlayerCraftedItemPayload> CODEC =
            PacketCodec.tuple(
                    ExtendedPacketCodecs.ITEM_STACK_DTO, PlayerCraftedItemPayload::craftedItem,
                    PlayerCraftedItemPayload::new
            );


    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
