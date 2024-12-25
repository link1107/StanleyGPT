package com.igorlink.stanleygpt.payloads.codecs;

import com.igorlink.stanleygpt.service.LivingEntityDto;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;

import static com.igorlink.stanleygpt.payloads.codecs.bufutils.StringBufUtils.*;

/**
 * Codec for LivingEntityDto.
 */
public class LivingEntityDtoCodec implements PacketCodec<ByteBuf, LivingEntityDto> {

    /**
     * Encodes the given value into the given buffer.
     */
    @Override
    public void encode(ByteBuf buf, LivingEntityDto value) {
        // Write a flag indicating whether the object is null
        buf.writeBoolean(value != null);

        if (value == null) {
            return;
        }

        // Write entityId
        buf.writeInt(value.getEntityId());

        // Write custom name length and then the custom name itself
        writeString(buf, value.getEntityCustomName());

        // Write isBaby
        buf.writeBoolean(value.isBaby());

        // Write isTamedByPlayer
        buf.writeBoolean(value.isTamedByPlayer());

    }


    /**
     * Decodes the given buffer into a value.
     */
    @Override
    public LivingEntityDto decode(ByteBuf buf) {
        // Read the flag indicating whether the object is null
        boolean isNotNull = buf.readBoolean();

        if (!isNotNull) {
            return null;
        }

        // Read entityId
        int entityId = buf.readInt();

        // Read custom name length and then the custom name itself
        String customName = readString(buf);

        // Read isBaby
        boolean isBaby = buf.readBoolean();

        // Read isTamedByPlayer
        boolean isTamedByPlayer = buf.readBoolean();

        // Create and return the LivingEntityDto
        return new LivingEntityDto(entityId, customName, isBaby, isTamedByPlayer);

    }
}
