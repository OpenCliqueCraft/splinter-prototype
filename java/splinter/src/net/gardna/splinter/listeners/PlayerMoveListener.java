package net.gardna.splinter.listeners;

import net.gardna.splinter.Bungee;
import net.gardna.splinter.Splinter;
import net.gardna.splinter.util.Vector2;
import net.gardna.splinter.zoner.Region;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

public class PlayerMoveListener extends BukkitRunnable {
    public Map<UUID, Vector2> previous;

    public PlayerMoveListener() {
        previous = new HashMap<UUID, Vector2>();
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
        Splinter instance = Splinter.Instance;
        Region supposed = instance.zoner.getSupposedRegion(player);

        if (!supposed.server.equals(instance.serverName)) {
            Location loc = player.getLocation();
            Vector vel = player.getVelocity();

            UUID uuid = player.getUniqueId();
            ByteBuffer bb = ByteBuffer.wrap(new byte[16 + 8 + 8 + 8 + 4 + 4 + 8 + 8 + 8 + 1 + 1]);
            bb.putLong(uuid.getMostSignificantBits());
            bb.putLong(uuid.getLeastSignificantBits());
            bb.putDouble(loc.getX());
            bb.putDouble(loc.getY());
            bb.putDouble(loc.getZ());
            bb.putFloat(loc.getPitch());
            bb.putFloat(loc.getYaw());
            bb.putDouble(vel.getX());
            bb.putDouble(vel.getY());
            bb.putDouble(vel.getZ());
            bb.put((byte) (player.isFlying() ? 1 : 0));
            bb.put((byte) (player.isSprinting() ? 1 : 0));

            Splinter.Instance.netHandler.connection.publish("teleport", bb.array());

            try {
                Splinter.Instance.netHandler.connection.flush(Duration.ZERO);
                Bungee.MovePlayer(player, supposed.server);
            } catch (TimeoutException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
