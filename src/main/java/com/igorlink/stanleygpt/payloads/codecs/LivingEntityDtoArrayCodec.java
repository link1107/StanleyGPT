package com.igorlink.stanleygpt.payloads.codecs;

import com.igorlink.stanleygpt.service.LivingEntityDto;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;

import static com.igorlink.stanleygpt.payloads.codecs.bufutils.StringBufUtils.*;

/**
 * Codec for LivingEntityDto array.
 */
public class LivingEntityDtoArrayCodec implements PacketCodec<ByteBuf, LivingEntityDto[]> {

    /**
     * Encodes the given value into the given buffer.
     */
    @Override
    public void encode(ByteBuf buf, LivingEntityDto[] value) {
        // Write a flag indicating whether the object is null
        buf.writeBoolean(value != null);

        if (value == null) {
            return;
        }

        // Write the size of the array
        buf.writeInt(value.length);

        // Write each LivingEntityDto
        for (LivingEntityDto entity : value) {
            // Write entityId
            buf.writeInt(entity.getEntityId());

            // Write custom name length and then the custom name itself
            writeString(buf, entity.getEntityCustomName());

            // Write isBaby
            buf.writeBoolean(entity.isBaby());

            // Write isTamedByPlayer
            buf.writeBoolean(entity.isTamedByPlayer());
        }

    }


    /**
     * Decodes the given buffer into a value.
     */
    @Override
    public LivingEntityDto[] decode(ByteBuf buf) {
        // Read the flag indicating whether the object is null
        boolean isNotNull = buf.readBoolean();

        if (!isNotNull) {
            return null;
        }

        int size = buf.readInt(); // Read the size of the array
        LivingEntityDto[] entities = new LivingEntityDto[size];

        // Read each LivingEntityDto
        for (int i = 0; i < size; i++) {
            // Read entityId
            int entityId = buf.readInt();

            // Read custom name length and then the custom name itself
            String customName = readString(buf);

            // Read isBaby
            boolean isBaby = buf.readBoolean();

            // Read isTamedByPlayer
            boolean isTamedByPlayer = buf.readBoolean();

            // Create and add the LivingEntityDto
            entities[i] = new LivingEntityDto(entityId, customName, isBaby, isTamedByPlayer);
        }

        return entities;
    }

}
