package com.igorlink.stanleygpt.payloads;

import com.igorlink.stanleygpt.ModIdentifiers;
import com.igorlink.stanleygpt.payloads.codecs.ExtendedPacketCodecs;
import com.igorlink.stanleygpt.service.ItemStackDto;
import com.igorlink.stanleygpt.service.LivingEntityDto;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

/**
 * Payload for player feed entity event.
 *
 * @param foodItem the food item
 * @param entity the entity
 */
public record PlayerFeedEntityPayload(ItemStackDto foodItem,
                                      LivingEntityDto entity
) implements CustomPayload {
    public static final Id<PlayerFeedEntityPayload> ID = new Id<>(ModIdentifiers.PLAYER_FEED_ENTITY);


    public static final PacketCodec<ByteBuf, PlayerFeedEntityPayload> CODEC =
            PacketCodec.tuple(
                    ExtendedPacketCodecs.ITEM_STACK_DTO, PlayerFeedEntityPayload::foodItem,
                    ExtendedPacketCodecs.LIVING_ENTITY_DTO, PlayerFeedEntityPayload::entity,
                    PlayerFeedEntityPayload::new);


    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
