package net.gardna.splinter.messages;

public class WorldTimeMessage extends NetMessage {
    public static final int MESSAGE_SIZE = LONG_SIZE;

    public long time;

    public WorldTimeMessage(long time) {
        super(MESSAGE_SIZE);

        this.time = time;

        data.putLong(time);
    }

    public WorldTimeMessage(byte[] raw) {
        super(raw);

        this.time = data.getLong();
    }
}
