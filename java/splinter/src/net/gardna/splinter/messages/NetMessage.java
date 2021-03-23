package net.gardna.splinter.messages;

import net.gardna.splinter.Splinter;

import java.nio.ByteBuffer;

public class NetMessage {
    protected ByteBuffer data;

    public static final int UUID_SIZE = 16;
    public static final int DOUBLE_SIZE = 8;
    public static final int LONG_SIZE = 8;
    public static final int FLOAT_SIZE = 4;
    public static final int BYTE_SIZE = 1;
    public static final int INT_SIZE = 4;

    public long senderId;

    public static byte EncodeBoolean(boolean bool) {
        return (byte) (bool ? 1 : 0);
    }

    public static boolean DecodeBoolean(byte data) {
        return data == 1;
    }

    public NetMessage(int size) {
        this.data = ByteBuffer.allocate(size + LONG_SIZE);
        this.senderId = Splinter.getInstance().netHandler.serverId;

        data.putLong(senderId);
    }

    public NetMessage(byte[] raw) {
        data = ByteBuffer.wrap(raw);

        this.senderId = data.getLong();
    }

    public byte[] getData() {
        return data.array();
    }
}
