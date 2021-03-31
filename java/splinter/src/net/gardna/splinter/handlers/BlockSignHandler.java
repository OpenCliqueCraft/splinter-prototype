package net.gardna.splinter.handlers;

import net.gardna.splinter.messaging.ByteMessage;
import net.gardna.splinter.messaging.SplinterHandler;
import net.gardna.splinter.messaging.SplinterMessage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.SignChangeEvent;

public class BlockSignHandler extends SplinterHandler {
    public BlockSignHandler(String channel) {
        super(channel);
    }

    public void send(Location location, String[] lines) {
        String worldName = location.getWorld().getName();
        int length = ByteMessage.STRING_SIZE(worldName) +
                ByteMessage.INT_SIZE * 3 +
                ByteMessage.STRING_SIZE(lines[0]) +
                ByteMessage.STRING_SIZE(lines[1]) +
                ByteMessage.STRING_SIZE(lines[2]) +
                ByteMessage.STRING_SIZE(lines[3]);

        SplinterMessage msg = new SplinterMessage(getServerId(), length);
        msg.putString(worldName);
        msg.putInt((int) location.getX());
        msg.putInt((int) location.getY());
        msg.putInt((int) location.getZ());
        msg.putString(lines[0]);
        msg.putString(lines[1]);
        msg.putString(lines[2]);
        msg.putString(lines[3]);

        publish(msg);
    }

    @Override
    public void recieve(SplinterMessage msg) {
        World world = Bukkit.getWorld(msg.getString());
        Location loc = new Location(
                world,
                msg.getInt(),
                msg.getInt(),
                msg.getInt()
        );

        Block block = world.getBlockAt(loc);
        Sign sign = (Sign) block.getState();

        for (int i = 0; i < 4; i++) {
            String line = msg.getString();
            System.out.println(line);
            sign.setLine(i, line);
        }

        sign.update();
    }

    @EventHandler
    public void onSignChange(SignChangeEvent event) {
        Location loc = event.getBlock().getLocation();
        send(loc, event.getLines());
    }
}
