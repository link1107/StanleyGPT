package com.igorlink.stanleygpt.payloads;

import com.igorlink.stanleygpt.ModIdentifiers;
import com.igorlink.stanleygpt.payloads.codecs.ExtendedPacketCodecs;
import com.igorlink.stanleygpt.service.ItemStackDto;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

/**
 * Payload for drop from block event.
 *
 * @param blockId the block ID
 * @param tool the tool
 * @param drops the drops
 */
public record DropFromBlockPayload(Integer blockId,
                                   ItemStackDto tool,
                                   ItemStackDto[] drops
) implements CustomPayload {

    public static final CustomPayload.Id<DropFromBlockPayload> ID =
            new CustomPayload.Id<>(ModIdentifiers.DROP_FROM_BLOCK_PACKET_ID);


    public static final PacketCodec<ByteBuf, DropFromBlockPayload> CODEC =
            PacketCodec.tuple(
                    ExtendedPacketCodecs.INTEGER, DropFromBlockPayload::blockId,
                    ExtendedPacketCodecs.ITEM_STACK_DTO, DropFromBlockPayload::tool,
                    ExtendedPacketCodecs.ITEM_STACK_DTO_ARRAY, DropFromBlockPayload::drops,
                    DropFromBlockPayload::new
            );


    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }

}
