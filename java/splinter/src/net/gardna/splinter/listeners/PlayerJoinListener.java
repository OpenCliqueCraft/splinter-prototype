package net.gardna.splinter.listeners;

import net.gardna.splinter.Bungee;
import net.gardna.splinter.Splinter;
import net.gardna.splinter.messages.PlayerPrejoinMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerJoinListener implements Listener {
    public Map<UUID, PlayerPrejoinMessage> pendingTeleports = new HashMap<>();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // If server name is unknown, request it
        if (Splinter.getInstance().serverName == null) {
            Splinter instance = Splinter.getInstance();

            instance.getLogger().info("Requesting server name from Bungee");
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
            PlayerPrejoinMessage msg = pendingTeleports.get(uuid);
            PlayerPrejoinMessage.ApplyToPlayer(msg, player);
            pendingTeleports.remove(uuid);
        }
    }

    public void movePlayer(PlayerPrejoinMessage msg) {
        if (Bukkit.getPlayer(msg.uuid) != null) {
            Player p = Splinter.getInstance().getServer().getPlayer(msg.uuid);
            PlayerPrejoinMessage.ApplyToPlayer(msg, p);
        } else {
            pendingTeleports.put(msg.uuid, msg);
        }
    }
}
