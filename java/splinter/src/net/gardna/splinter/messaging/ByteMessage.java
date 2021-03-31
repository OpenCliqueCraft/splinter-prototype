package net.gardna.splinter.messaging;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class ByteMessage {
    public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    public static final int UUID_SIZE = 16;
    public static final int DOUBLE_SIZE = 8;
    public static final int LONG_SIZE = 8;
    public static final int FLOAT_SIZE = 4;
    public static final int INT_SIZE = 4;
    public static final int BOOL_SIZE = 1;

    public static int STRING_SIZE(String str) {
        return STRING_SIZE(str, DEFAULT_CHARSET);
    }

    public static int STRING_SIZE(String str, Charset charset) {
        return RAW_SIZE(str.getBytes(charset));
    }

    public static int RAW_SIZE(byte[] raw) {
        return INT_SIZE + raw.length;
    }

    private ByteBuffer bb;

    public ByteMessage(byte[] raw) {
        this.bb = ByteBuffer.wrap(raw);
    }

    public ByteMessage(int size) {
        this.bb = ByteBuffer.allocate(size);
    }

    public byte[] getData() {
        return bb.array();
    }

    public void putUuid(UUID uuid) {
        putLong(uuid.getMostSignificantBits());
        putLong(uuid.getLeastSignificantBits());
    }

    public UUID getUuid() {
        return new UUID(getLong(), getLong());
    }

    public void putDouble(double val) {
        bb.putDouble(val);
    }

    public double getDouble() {
        return bb.getDouble();
    }

    public void putLong(long val) {
        bb.putLong(val);
    }

    public long getLong() {
        return bb.getLong();
    }

    public void putFloat(float val) {
        bb.putFloat(val);
    }

    public float getFloat() {
        return bb.getFloat();
    }

    public void putInt(int val) {
        bb.putInt(val);
    }

    public int getInt() {
        return bb.getInt();
    }

    public void putBool(boolean val) {
        bb.put((byte) (val ? 1 : 0));
    }

    public boolean getBool() {
        return bb.get() == 1;
    }

    public void putString(String str) {
        putString(str, DEFAULT_CHARSET);
    }

    public void putString(String str, Charset charset) {
        putRaw(str.getBytes(charset));
    }

    public String getString() {
        return getString(DEFAULT_CHARSET);
    }

    public String getString(Charset charset) {
        return new String(getRaw(), charset);
    }

    public void putRaw(byte[] raw) {
        putInt(raw.length);
        bb.put(raw);
    }

    public byte[] getRaw() {
        int length = getInt();
        byte[] raw = new byte[length];

        for (int i = 0; i < length; i++)
            raw[i] = bb.get();

        return raw;
    }
}
