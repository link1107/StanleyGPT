package com.igorlink.stanleygpt.payloads;

import com.igorlink.stanleygpt.ModIdentifiers;
import com.igorlink.stanleygpt.payloads.codecs.ExtendedPacketCodecs;
import com.igorlink.stanleygpt.service.ItemStackDto;
import com.igorlink.stanleygpt.service.LivingEntityDto;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

/**
 * Payload for tamed entity killed living entity event.
 *
 * @param attacker the attacker
 * @param killedEntity the killed entity
 * @param drops the drops
 */
public record TamedEntityKilledLivingEntityPayload(LivingEntityDto attacker,
                                                   LivingEntityDto killedEntity,
                                                   ItemStackDto[] drops
) implements CustomPayload {

    public static final CustomPayload.Id<TamedEntityKilledLivingEntityPayload> ID =
            new CustomPayload.Id<>(ModIdentifiers.TAMED_ENTITY_KILLED_LIVING_ENTITY_PACKET_ID);


    public static final PacketCodec<ByteBuf, TamedEntityKilledLivingEntityPayload> CODEC =
            PacketCodec.tuple(
                    ExtendedPacketCodecs.LIVING_ENTITY_DTO, TamedEntityKilledLivingEntityPayload::attacker,
                    ExtendedPacketCodecs.LIVING_ENTITY_DTO, TamedEntityKilledLivingEntityPayload::killedEntity,
                    ExtendedPacketCodecs.ITEM_STACK_DTO_ARRAY, TamedEntityKilledLivingEntityPayload::drops,
                    TamedEntityKilledLivingEntityPayload::new
            );


    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
