package com.igorlink.stanleygpt.payloads;

import com.igorlink.stanleygpt.ModIdentifiers;
import com.igorlink.stanleygpt.payloads.codecs.ExtendedPacketCodecs;
import com.igorlink.stanleygpt.service.ItemStackDto;
import com.igorlink.stanleygpt.service.LivingEntityDto;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

/**
 * Payload for player killed entity event.
 *
 * @param killedEntity the killed entity
 * @param drops the drops
 */
public record PlayerKilledEntityPayload(LivingEntityDto killedEntity,
                                        ItemStackDto[] drops
) implements CustomPayload {

    public static final CustomPayload.Id<PlayerKilledEntityPayload> ID =
            new CustomPayload.Id<>(ModIdentifiers.PLAYER_KILLED_ENTITY_PACKET_ID);


    public static final PacketCodec<ByteBuf, PlayerKilledEntityPayload> CODEC =
            PacketCodec.tuple(
                    ExtendedPacketCodecs.LIVING_ENTITY_DTO, PlayerKilledEntityPayload::killedEntity,
                    ExtendedPacketCodecs.ITEM_STACK_DTO_ARRAY, PlayerKilledEntityPayload::drops,
                    PlayerKilledEntityPayload::new
            );


    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }
}
