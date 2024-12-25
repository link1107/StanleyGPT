package com.igorlink.stanleygpt.payloads;

import com.igorlink.stanleygpt.ModIdentifiers;
import com.igorlink.stanleygpt.payloads.codecs.ExtendedPacketCodecs;
import com.igorlink.stanleygpt.service.LivingEntityDto;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

/**
 * Payload for player tamed entity event.
 *
 * @param tamedEntity the tamed entity
 */
public record PlayerTamedEntityPayload(LivingEntityDto tamedEntity) implements CustomPayload {

    public static final CustomPayload.Id<PlayerTamedEntityPayload> ID =
            new CustomPayload.Id<>(ModIdentifiers.PLAYER_TAMED_ENTITY_PACKET_ID);


    public static final PacketCodec<ByteBuf, PlayerTamedEntityPayload> CODEC =
            PacketCodec.tuple(
                    ExtendedPacketCodecs.LIVING_ENTITY_DTO, PlayerTamedEntityPayload::tamedEntity,
                    PlayerTamedEntityPayload::new
            );


    @Override
    public CustomPayload.Id<PlayerTamedEntityPayload> getId() {
        return ID;
    }


}
