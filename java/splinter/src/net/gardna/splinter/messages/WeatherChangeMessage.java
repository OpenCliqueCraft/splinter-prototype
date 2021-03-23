package net.gardna.splinter.messages;

public class WeatherChangeMessage extends NetMessage {
    public static final int MESSAGE_SIZE = BYTE_SIZE * 2;

    public boolean thundering;
    public boolean raining;

    public WeatherChangeMessage(boolean thundering, boolean raining) {
        super(MESSAGE_SIZE);

        this.thundering = thundering;
        this.raining = raining;

        data.put(EncodeBoolean(thundering));
        data.put(EncodeBoolean(raining));
    }

    public WeatherChangeMessage(byte[] raw) {
        super(raw);

        this.thundering = DecodeBoolean(data.get());
        this.raining = DecodeBoolean(data.get());
    }
}
