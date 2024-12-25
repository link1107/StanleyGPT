package com.igorlink.stanleygpt.payloads;

import com.igorlink.stanleygpt.ModIdentifiers;
import com.igorlink.stanleygpt.payloads.codecs.ExtendedPacketCodecs;
import com.igorlink.stanleygpt.service.LivingEntityDto;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

/**
 * Payload for player death event.
 *
 * @param deathReason the death reason
 * @param attacker the attacker
 */
public record PlayerDeathPayload(String deathReason,
                                 LivingEntityDto attacker
) implements CustomPayload {

    public static final CustomPayload.Id<PlayerDeathPayload> ID =
            new CustomPayload.Id<>(ModIdentifiers.PLAYER_DEATH_PACKET_ID);


    public static final PacketCodec<ByteBuf, PlayerDeathPayload> CODEC =
            PacketCodec.tuple(
                    ExtendedPacketCodecs.STRING, PlayerDeathPayload::deathReason,
                    ExtendedPacketCodecs.LIVING_ENTITY_DTO, PlayerDeathPayload::attacker,
                    PlayerDeathPayload::new
            );


    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }
}
