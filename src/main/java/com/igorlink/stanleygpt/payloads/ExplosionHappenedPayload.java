package com.igorlink.stanleygpt.payloads;

import com.igorlink.stanleygpt.ModIdentifiers;
import com.igorlink.stanleygpt.payloads.codecs.ExtendedPacketCodecs;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

/**
 * Payload for explosion happened event.
 *
 * @param explosionSource the explosion source
 * @param distanceFromPlayer the distance from player
 */
public record ExplosionHappenedPayload(String explosionSource,
                                       int distanceFromPlayer
) implements CustomPayload {

    public static final CustomPayload.Id<ExplosionHappenedPayload> ID =
            new CustomPayload.Id<>(ModIdentifiers.EXPLOSION_HAPPENED_PACKET_ID);


    public static final PacketCodec<ByteBuf, ExplosionHappenedPayload> CODEC =
            PacketCodec.tuple(
                    ExtendedPacketCodecs.STRING, ExplosionHappenedPayload::explosionSource,
                    ExtendedPacketCodecs.INTEGER, ExplosionHappenedPayload::distanceFromPlayer,
                    ExplosionHappenedPayload::new
            );


    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

}
