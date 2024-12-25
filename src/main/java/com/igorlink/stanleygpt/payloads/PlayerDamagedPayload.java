package com.igorlink.stanleygpt.payloads;

import com.igorlink.stanleygpt.ModIdentifiers;
import com.igorlink.stanleygpt.payloads.codecs.ExtendedPacketCodecs;
import com.igorlink.stanleygpt.service.LivingEntityDto;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

/**
 * Payload for player damaged event.
 *
 * @param damageSource the damage source
 * @param damageAmount the damage amount
 * @param attacker the attacker
 */
public record PlayerDamagedPayload(String damageSource,
                                   float damageAmount,
                                   LivingEntityDto attacker
) implements CustomPayload {

    public static final CustomPayload.Id<PlayerDamagedPayload> ID =
            new CustomPayload.Id<>(ModIdentifiers.PLAYER_DAMAGED_PACKET_ID);


    public static final PacketCodec<ByteBuf, PlayerDamagedPayload> CODEC =
            PacketCodec.tuple(
                    ExtendedPacketCodecs.STRING, PlayerDamagedPayload::damageSource,
                    ExtendedPacketCodecs.FLOAT, PlayerDamagedPayload::damageAmount,
                    ExtendedPacketCodecs.LIVING_ENTITY_DTO, PlayerDamagedPayload::attacker,
                    PlayerDamagedPayload::new
            );


    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }
}
