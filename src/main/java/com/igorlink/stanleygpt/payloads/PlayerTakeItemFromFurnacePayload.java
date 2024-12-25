package com.igorlink.stanleygpt.payloads;

import com.igorlink.stanleygpt.ModIdentifiers;
import com.igorlink.stanleygpt.payloads.codecs.ExtendedPacketCodecs;
import com.igorlink.stanleygpt.service.ItemStackDto;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

/**
 * Payload for player take item from furnace event.
 *
 * @param item the item
 */
public record PlayerTakeItemFromFurnacePayload(ItemStackDto item) implements CustomPayload {

    public static final CustomPayload.Id<PlayerTakeItemFromFurnacePayload> ID =
            new CustomPayload.Id<>(ModIdentifiers.PLAYER_TAKE_ITEM_FROM_FURNACE_PACKET_ID);


    public static final PacketCodec<ByteBuf, PlayerTakeItemFromFurnacePayload> CODEC =
            PacketCodec.tuple(
                    ExtendedPacketCodecs.ITEM_STACK_DTO, PlayerTakeItemFromFurnacePayload::item,
                    PlayerTakeItemFromFurnacePayload::new
            );


    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }
}
