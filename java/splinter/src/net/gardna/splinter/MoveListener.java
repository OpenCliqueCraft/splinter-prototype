package net.gardna.splinter;

import net.gardna.splinter.util.Pair;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MoveListener extends BukkitRunnable {
    public Map<UUID, Pair<Double, Double>> previous;

    public MoveListener() {
        previous = new HashMap<UUID, Pair<Double, Double>>();
    }

    public Pair<Double, Double> pairFromLocation(Location loc) {
        return new Pair<Double, Double>(loc.getX(), loc.getZ());
    }

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            UUID uuid = player.getUniqueId();
            Pair<Double, Double> current = pairFromLocation(player.getLocation());

            if (previous.containsKey(uuid) && !previous.get(uuid).equals(current)) {
                Pair<Double, Double> old = previous.get(uuid);
                if (!old.equals(current)) playerMoved(player);
            }

            previous.put(uuid, current);
        }
    }

    private void playerMoved(Player player) {
        player.sendMessage("You moved!");
    }
}
