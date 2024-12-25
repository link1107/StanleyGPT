package com.igorlink.stanleygpt.payloads.codecs;

import com.igorlink.stanleygpt.service.ItemStackDto;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;

import static com.igorlink.stanleygpt.payloads.codecs.bufutils.StringBufUtils.*;

/**
 * Codec for ItemStackDto array.
 */
public class ItemStackDtoArrayCodec implements PacketCodec<ByteBuf, ItemStackDto[]> {

    /**
     * Encodes the given value into the given buffer.
     */
    @Override
    public void encode(ByteBuf buf, ItemStackDto[] value) {
        // Write a flag indicating whether the object is null
        buf.writeBoolean(value != null);

        if (value == null) {
            return;
        }

        // Write the size of the array
        buf.writeInt(value.length);

        // Write each ItemStackDto
        for (ItemStackDto item : value) {
            // Write itemId
            buf.writeInt(item.getItemId());

            // Write custom name length and then the custom name itself
            writeString(buf, item.getItemCustomName());

            // Write amount
            buf.writeInt(item.getAmount());
        }
    }


    /**
     * Decodes the given buffer into a value.
     */
    @Override
    public ItemStackDto[] decode(ByteBuf buf) {
        // Read the flag indicating whether the object is null
        boolean isNotNull = buf.readBoolean();

        if (!isNotNull) {
            return null;
        }

        // Read the size of the array
        int size = buf.readInt();
        ItemStackDto[] items = new ItemStackDto[size];

        // Read each ItemStackDto
        for (int i = 0; i < size; i++) {
            // Read itemId
            int itemId = buf.readInt();

            // Read custom name length and then the custom name itself
            String customName = readString(buf);

            // Read amount
            int amount = buf.readInt();

            // Create and add the ItemStackDto
            items[i] = new ItemStackDto(itemId, customName, amount);
        }

        return items;
    }
}
