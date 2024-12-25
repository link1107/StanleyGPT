package com.igorlink.stanleygpt.payloads;

import com.igorlink.stanleygpt.ModIdentifiers;
import com.igorlink.stanleygpt.payloads.codecs.ExtendedPacketCodecs;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

/**
 * Payload for player moved to another world event.
 *
 * @param fromWorld the world from which the player moved
 * @param toWorld the world to which the player moved
 */
public record PlayerMovedToAnotherWorldPayload(String fromWorld,
                                               String toWorld
) implements CustomPayload {

    public static final CustomPayload.Id<PlayerMovedToAnotherWorldPayload> ID =
            new CustomPayload.Id<>(ModIdentifiers.PLAYER_MOVED_TO_ANOTHER_WORLD_PACKET_ID);


    public static final PacketCodec<ByteBuf, PlayerMovedToAnotherWorldPayload> CODEC =
            PacketCodec.tuple(
                    ExtendedPacketCodecs.STRING, PlayerMovedToAnotherWorldPayload::fromWorld,
                    ExtendedPacketCodecs.STRING, PlayerMovedToAnotherWorldPayload::toWorld,
                    PlayerMovedToAnotherWorldPayload::new
            );


    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }

}
