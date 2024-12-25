package com.igorlink.stanleygpt.payloads;

import com.igorlink.stanleygpt.ModIdentifiers;
import com.igorlink.stanleygpt.payloads.codecs.ExtendedPacketCodecs;
import com.igorlink.stanleygpt.service.ItemStackDto;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

/**
 * Payload for player eat event.
 *
 * @param food the food
 */
public record PlayerEatPayload(ItemStackDto food) implements CustomPayload {

    public static final CustomPayload.Id<PlayerEatPayload> ID =
            new CustomPayload.Id<>(ModIdentifiers.PLAYER_EAT_PACKET_ID);


    public static final PacketCodec<ByteBuf, PlayerEatPayload> CODEC =
            PacketCodec.tuple(
                    ExtendedPacketCodecs.ITEM_STACK_DTO, PlayerEatPayload::food,
                    PlayerEatPayload::new);


    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
