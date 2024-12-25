package com.igorlink.stanleygpt.payloads.codecs;

import com.igorlink.stanleygpt.service.ItemStackDto;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;

import static com.igorlink.stanleygpt.payloads.codecs.bufutils.StringBufUtils.*;

/**
 * Codec for ItemStackDto.
 */
public class ItemStackDtoCodec implements PacketCodec<ByteBuf, ItemStackDto> {

    /**
     * Encodes the given value into the given buffer.
     */
    @Override
    public void encode(ByteBuf buf, ItemStackDto value) {
        // Write a flag indicating whether the object is null
        buf.writeBoolean(value != null);

        if (value == null) {
            return;
        }

        // Write itemId
        buf.writeInt(value.getItemId());

        // Write custom name length and then the custom name itself
        writeString(buf, value.getItemCustomName());

        // Write amount
        buf.writeInt(value.getAmount());
    }


    /**
     * Decodes the given buffer into a value.
     */
    @Override
    public ItemStackDto decode(ByteBuf buf) {
        // Read the flag indicating whether the object is null
        boolean isNotNull = buf.readBoolean();

        if (!isNotNull) {
            return null;
        }

        // Read itemId
        int itemId = buf.readInt();

        // Read custom name length and then the custom name itself
        String customName = readString(buf);

        // Read amount
        int amount = buf.readInt();

        // Create and return the ItemStackDto
        return new ItemStackDto(itemId, customName, amount);
    }
}
