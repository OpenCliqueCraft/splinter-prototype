package net.gardna.splinter;

import net.gardna.splinter.messages.PlayerDataMessage;
import net.gardna.splinter.messages.PlayerPrejoinMessage;
import net.gardna.splinter.util.Vector2;
import net.gardna.splinter.zoner.Region;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MoveDetector extends BukkitRunnable {
    public Map<UUID, Vector2> previous;

    public MoveDetector() {
        previous = new HashMap<>();
    }

    public Vector2 pairFromLocation(Location loc) {
        return new Vector2(loc.getX(), loc.getZ());
    }

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            UUID uuid = player.getUniqueId();
            Vector2 current = pairFromLocation(player.getLocation());

            if (previous.containsKey(uuid) && !previous.get(uuid).equals(current)) {
                Vector2 old = previous.get(uuid);
                if (!old.equals(current)) playerMoved(player);
            }

            previous.put(uuid, current);
        }
    }

    private void playerMoved(Player player) {
        Splinter instance = Splinter.getInstance();
        Region supposed = instance.zoner.getSupposedRegion(player);

        if (!supposed.server.equals(instance.serverName)) {
            PlayerPrejoinMessage prejoinMessage = new PlayerPrejoinMessage(
                    player.getUniqueId(),
                    player.getLocation(),
                    player.getVelocity(),
                    player.isFlying(),
                    player.isSprinting()
            );

            PlayerDataMessage dataMessage = new PlayerDataMessage(
                    player.getUniqueId(),
                    PlayerDataMessage.ReadPlayerdata(player.getUniqueId())
            );

            Splinter.getInstance().netHandler.publish("player.prejoin", prejoinMessage);
            Splinter.getInstance().netHandler.publish("player.data", dataMessage);

            Bungee.MovePlayer(player, supposed.server);
        }
    }
}
