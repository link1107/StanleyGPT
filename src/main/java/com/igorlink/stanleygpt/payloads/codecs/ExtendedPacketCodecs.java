package com.igorlink.stanleygpt.payloads.codecs;

import net.minecraft.network.codec.PacketCodecs;

/**
 * Extended collection of packet codecs.
 */
public interface ExtendedPacketCodecs extends PacketCodecs {
    ItemStackDtoArrayCodec ITEM_STACK_DTO_ARRAY = new ItemStackDtoArrayCodec();
    ItemStackDtoCodec ITEM_STACK_DTO = new ItemStackDtoCodec();
    LivingEntityDtoArrayCodec LIVING_ENTITY_DTO_ARRAY = new LivingEntityDtoArrayCodec();
    LivingEntityDtoCodec LIVING_ENTITY_DTO = new LivingEntityDtoCodec();
}
