package com.igorlink.stanleygpt.payloads;

import com.igorlink.stanleygpt.ModIdentifiers;
import com.igorlink.stanleygpt.payloads.codecs.ExtendedPacketCodecs;
import com.igorlink.stanleygpt.service.LivingEntityDto;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

/**
 * Payload for player breed entity event.
 *
 * @param child the child
 * @param parentNumberOne the parent number one
 * @param parentNumberTwo the parent number two
 */
public record PlayerBreedEntityPayload(LivingEntityDto child,
                                       LivingEntityDto parentNumberOne,
                                       LivingEntityDto parentNumberTwo
) implements CustomPayload {

    public static final CustomPayload.Id<PlayerBreedEntityPayload> ID =
            new CustomPayload.Id<>(ModIdentifiers.PLAYER_BREED_ENTITY_PACKET_ID);


    public static final PacketCodec<ByteBuf, PlayerBreedEntityPayload> CODEC =
            PacketCodec.tuple(
                    ExtendedPacketCodecs.LIVING_ENTITY_DTO, PlayerBreedEntityPayload::child,
                    ExtendedPacketCodecs.LIVING_ENTITY_DTO, PlayerBreedEntityPayload::parentNumberOne,
                    ExtendedPacketCodecs.LIVING_ENTITY_DTO, PlayerBreedEntityPayload::parentNumberTwo,
                    PlayerBreedEntityPayload::new
            );


    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
