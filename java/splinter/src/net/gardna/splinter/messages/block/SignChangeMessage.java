package net.gardna.splinter.messages.block;

import net.gardna.splinter.messages.NetMessage;
import net.gardna.splinter.util.Helpers;
import org.bukkit.util.Vector;

import java.nio.charset.StandardCharsets;

public class SignChangeMessage extends NetMessage {
    public static final int MESSAGE_SIZE = INT_SIZE * 3;
    public static final byte DELIMINATOR = 0;

    public Vector location;
    public String[] lines;

    public static int LinesLength(String[] lines) {
        int total = lines.length;
        for (String line : lines) total += line.length();
        return total;
    }

    public SignChangeMessage(Vector location, String[] lines) {
        super(MESSAGE_SIZE + LinesLength(lines));

        this.location = location;
        this.lines = lines;

        data.putInt((int) location.getX());
        data.putInt((int) location.getY());
        data.putInt((int) location.getZ());

        for (String line : lines) {
            data.put(line.getBytes(StandardCharsets.UTF_8));
            data.put(DELIMINATOR);
        }
    }

    public SignChangeMessage(byte[] raw) {
        super(raw);

        this.location = new Vector(
                data.getInt(),
                data.getInt(),
                data.getInt()
        );

        byte[] bdb = Helpers.SliceArray(raw, MESSAGE_SIZE + LONG_SIZE);

        this.lines = new String[4];

        // TODO: fix this mess
        int s = 0;
        int c = 0;
        for (int i = 0; i < bdb.length; i++) {
            if (bdb[i] == DELIMINATOR) {
                byte[] line = Helpers.SliceArray(bdb, s, i);
                this.lines[c] = new String(line, StandardCharsets.UTF_8);
                c++;
                s = i + 1;
            }

            if (c == 4) break;
        }
    }
}
