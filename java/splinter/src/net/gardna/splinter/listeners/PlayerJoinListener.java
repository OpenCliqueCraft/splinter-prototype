package net.gardna.splinter.listeners;

import net.gardna.splinter.Bungee;
import net.gardna.splinter.Splinter;
import net.gardna.splinter.messages.PlayerTeleportMessage;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerJoinListener implements Listener {
    public Map<UUID, PlayerTeleportMessage> pendingTeleports = new HashMap<>();

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
            PlayerTeleportMessage msg = pendingTeleports.get(uuid);
            PlayerTeleportMessage.ApplyToPlayer(msg, player);
            pendingTeleports.remove(uuid);
        }
    }
}
