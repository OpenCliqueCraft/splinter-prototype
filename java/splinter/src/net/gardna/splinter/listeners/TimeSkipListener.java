package net.gardna.splinter.listeners;

import net.gardna.splinter.Splinter;
import net.gardna.splinter.messages.WorldTimeMessage;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.TimeSkipEvent;

public class TimeSkipListener implements Listener {
    public TimeSkipListener() {
    }

    @EventHandler
    public void onTimeSkipEvent(TimeSkipEvent event) {
        if (event.getSkipReason() != TimeSkipEvent.SkipReason.CUSTOM) {
            Bukkit.getScheduler().runTaskLater(Splinter.getInstance(), () -> {
                long time = Splinter.getInstance().mainWorld.getTime();
                Splinter.getInstance().netHandler.publish("world.time", new WorldTimeMessage(time));
                System.out.println("Publish time " + time);
            }, 5);
        }
    }
}
