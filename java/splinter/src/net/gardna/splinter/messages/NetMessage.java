package net.gardna.splinter.messages;

import java.nio.ByteBuffer;

public class NetMessage {
    protected ByteBuffer data;

    public static final int UUID_SIZE = 16;
    public static final int DOUBLE_SIZE = 8;
    public static final int FLOAT_SIZE = 4;
    public static final int BYTE_SIZE = 1;

    public static byte EncodeBoolean(boolean bool) {
        return (byte) (bool ? 1 : 0);
    }

    public static boolean DecodeBoolean(byte data) {
        return data == 1;
    }

    public NetMessage(int size) {
        data = ByteBuffer.allocate(size);
    }
}
