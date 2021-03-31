package net.gardna.splinter.messaging;

public class SplinterMessage extends ByteMessage {
    private long senderId;

    public SplinterMessage(byte[] raw) {
        super(raw);

        // Set sender ID to first long in message
        this.senderId = getLong();
    }

    public SplinterMessage(long senderId, int size) {
        super(ByteMessage.LONG_SIZE + size);

        // Put sender ID as first long in message
        this.senderId = senderId;
        putLong(senderId);
    }

    public long getSenderId() {
        return this.senderId;
    }
}
