package com.igorlink.stanleygpt.payloads;

import com.igorlink.stanleygpt.ModIdentifiers;
import com.igorlink.stanleygpt.payloads.codecs.ExtendedPacketCodecs;
import com.igorlink.stanleygpt.service.ItemStackDto;
import com.igorlink.stanleygpt.service.LivingEntityDto;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

/**
 * Payload for tamed entity killed event.
 *
 * @param killedEntity the killed entity
 * @param damageSource the damage source
 * @param attacker the attacker
 * @param drops the drops
 */
public record TamedEntityKilledPayload(LivingEntityDto killedEntity,
                                       String damageSource,
                                       LivingEntityDto attacker,
                                       ItemStackDto[] drops
)  implements CustomPayload {

    public static final CustomPayload.Id<TamedEntityKilledPayload> ID =
            new CustomPayload.Id<>(ModIdentifiers.TAMED_ENTITY_KILLED_PACKET_ID);

    public static final PacketCodec<ByteBuf, TamedEntityKilledPayload> CODEC =
            PacketCodec.tuple(
                    ExtendedPacketCodecs.LIVING_ENTITY_DTO, TamedEntityKilledPayload::killedEntity,
                    ExtendedPacketCodecs.STRING, TamedEntityKilledPayload::damageSource,
                    ExtendedPacketCodecs.LIVING_ENTITY_DTO, TamedEntityKilledPayload::attacker,
                    ExtendedPacketCodecs.ITEM_STACK_DTO_ARRAY, TamedEntityKilledPayload::drops,
                    TamedEntityKilledPayload::new
            );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
