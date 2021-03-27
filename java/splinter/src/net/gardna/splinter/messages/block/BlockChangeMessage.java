package net.gardna.splinter.messages.block;

import net.gardna.splinter.messages.NetMessage;
import net.gardna.splinter.util.Helpers;
import org.bukkit.Bukkit;
import org.bukkit.block.data.BlockData;
import org.bukkit.util.Vector;

import java.nio.charset.StandardCharsets;

public class BlockChangeMessage extends NetMessage {
    public static final int MESSAGE_SIZE = INT_SIZE * 3;

    public Vector location;
    public BlockData blockData;

    public BlockChangeMessage(Vector location, BlockData blockData) {
        super(MESSAGE_SIZE + blockData.getAsString().length());
        byte[] blockDataBytes = blockData.getAsString().getBytes(StandardCharsets.UTF_8);

        this.location = location;
        this.blockData = blockData;

        data.putInt((int) location.getX());
        data.putInt((int) location.getY());
        data.putInt((int) location.getZ());
        data.put(blockDataBytes);
    }

    public BlockChangeMessage(byte[] raw) {
        super(raw);

        this.location = new Vector(
                data.getInt(),
                data.getInt(),
                data.getInt()
        );

        byte[] bdb = Helpers.SliceArray(raw, MESSAGE_SIZE + LONG_SIZE);
        String bd = new String(bdb, StandardCharsets.UTF_8);

        this.blockData = Bukkit.createBlockData(bd);
    }
}
