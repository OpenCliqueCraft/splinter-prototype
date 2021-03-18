package net.gardna.splinter;

import net.gardna.splinter.util.Bungee;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerJoinListener implements Listener {
    public Map<UUID, Location> pendingTeleports = new HashMap<UUID, Location>();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // If server name is unknown, request it
        if (Splinter.Instance.serverName == null) {
            Splinter instance = Splinter.Instance;

            instance.getLogger().info("Requesting server name from Bungee...");
            instance.getServer().getScheduler().runTaskLater(instance, new Runnable() {
                @Override
                public void run() {
                    Bungee.RequestServerName(event.getPlayer());
                }
            }, 5);
        }

        Player player = event.getPlayer();

        if (pendingTeleports.containsKey(player.getUniqueId())) {
            UUID uuid = player.getUniqueId();
            player.teleport(pendingTeleports.get(uuid));
            pendingTeleports.remove(uuid);
        }
    }
}
