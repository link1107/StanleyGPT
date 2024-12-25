package com.igorlink.stanleygpt.payloads.codecs.bufutils;

import io.netty.buffer.ByteBuf;

import java.nio.charset.StandardCharsets;

/**
 * Utility class for reading and writing strings from/to ByteBuf.
 */
public class StringBufUtils {

    /**
     * Reads a string from the ByteBuf.
     *
     * @param byteBuf the byte buf
     * @return the string
     */
    public static String readString(ByteBuf byteBuf) {
        int length = byteBuf.readInt();
        byte[] bytes = new byte[length];
        byteBuf.readBytes(bytes);
        return new String(bytes, StandardCharsets.UTF_8);
    }


    /**
     * Writes a string to the ByteBuf.
     *
     * @param byteBuf the byte buf
     * @param string the string
     */
    public static void writeString(ByteBuf byteBuf, String string) {
        byte[] bytes = string.getBytes(StandardCharsets.UTF_8);
        byteBuf.writeInt(bytes.length);
        byteBuf.writeBytes(bytes);
    }
}
