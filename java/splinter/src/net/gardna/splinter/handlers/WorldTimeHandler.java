package net.gardna.splinter.handlers;

import net.gardna.splinter.Splinter;
import net.gardna.splinter.messaging.ByteMessage;
import net.gardna.splinter.messaging.SplinterHandler;
import net.gardna.splinter.messaging.SplinterMessage;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.world.TimeSkipEvent;

public class WorldTimeHandler extends SplinterHandler {
    public WorldTimeHandler(String channel) {
        super(channel);
    }

    public void send(String world, long time) {
        int length = ByteMessage.STRING_SIZE(world) +
                ByteMessage.LONG_SIZE;

        SplinterMessage msg = new SplinterMessage(getServerId(), length);
        msg.putString(world);
        msg.putLong(time);

        publish(msg);
    }

    @Override
    public void recieve(SplinterMessage msg) {
        World world = Bukkit.getWorld(msg.getString());
        world.setTime(msg.getLong());
    }

    @EventHandler
    public void onTimeSkipEvent(TimeSkipEvent event) {
        if (event.getSkipReason() != TimeSkipEvent.SkipReason.CUSTOM) {
            Bukkit.getScheduler().runTaskLater(Splinter.getInstance(), () -> {
                long time = Splinter.getInstance().mainWorld.getTime();
                send(event.getWorld().getName(), time);
            }, 5);
        }
    }
}
