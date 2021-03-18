package net.gardna.splinter;

import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.concurrent.TimeoutException;

public class BlockChangeListener implements Listener {
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block b = event.getBlock();
        World w = b.getWorld();

        if (!w.getName().equals(Splinter.Instance.mainWorld.getName())) return;

        ByteBuffer bb = ByteBuffer.allocate(4 * 3);
        bb.putInt(b.getX());
        bb.putInt(b.getY());
        bb.putInt(b.getZ());

        Splinter.Instance.netHandler.connection.publish("block.break", bb.array());

        try {
            Splinter.Instance.netHandler.connection.flush(Duration.ZERO);
        } catch (TimeoutException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Block b = event.getBlock();
        World w = b.getWorld();

        if (!w.getName().equals(Splinter.Instance.mainWorld.getName())) return;

        ByteBuffer bb = ByteBuffer.allocate(12 + b.getBlockData().getAsString().length());
        bb.putInt(b.getX());
        bb.putInt(b.getY());
        bb.putInt(b.getZ());
        bb.put(b.getBlockData().getAsString().getBytes(StandardCharsets.UTF_8));

        Splinter.Instance.netHandler.connection.publish("block.place", bb.array());

        try {
            Splinter.Instance.netHandler.connection.flush(Duration.ZERO);
        } catch (TimeoutException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
