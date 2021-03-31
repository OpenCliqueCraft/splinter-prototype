package net.gardna.splinter.handlers;

import net.gardna.splinter.Splinter;
import net.gardna.splinter.messaging.ByteMessage;
import net.gardna.splinter.messaging.SplinterHandler;
import net.gardna.splinter.messaging.SplinterMessage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

class JoinInfo {
    public Location location;
    public Vector velocity;
    public boolean flying;
    public boolean sprinting;

    public JoinInfo(Location location, Vector velocity, boolean flying, boolean sprinting) {
        this.location = location;
        this.velocity = velocity;
        this.flying = flying;
        this.sprinting = sprinting;
    }

    public void applyToPlayer(Player player) {
        player.teleport(location);
        player.setVelocity(velocity);
        player.setFlying(flying);
        player.setSprinting(sprinting);
    }
}

public class PlayerJoinHandler extends SplinterHandler {
    public Map<UUID, JoinInfo> pendingTeleports = new HashMap<>();

    public PlayerJoinHandler(String channel) {
        super(channel);
    }

    public void send(Player player) {
        UUID uuid = player.getUniqueId();
        Location location = player.getLocation();
        String worldName = location.getWorld().getName();
        Vector velocity = player.getVelocity();
        boolean flying = player.isFlying();
        boolean sprinting = player.isSprinting();

        int length = ByteMessage.UUID_SIZE +
                ByteMessage.STRING_SIZE(worldName) +
                ByteMessage.DOUBLE_SIZE * 3 +
                ByteMessage.FLOAT_SIZE * 2 +
                ByteMessage.DOUBLE_SIZE * 3 +
                ByteMessage.BOOL_SIZE * 2;

        SplinterMessage msg = new SplinterMessage(getServerId(), length);

        msg.putLong(uuid.getMostSignificantBits());
        msg.putLong(uuid.getLeastSignificantBits());
        msg.putString(worldName);
        msg.putDouble(location.getX());
        msg.putDouble(location.getY());
        msg.putDouble(location.getZ());
        msg.putFloat(location.getYaw());
        msg.putFloat(location.getPitch());
        msg.putDouble(velocity.getX());
        msg.putDouble(velocity.getY());
        msg.putDouble(velocity.getZ());
        msg.putBool(flying);
        msg.putBool(sprinting);

        System.out.println("SEND " + flying + ":" + sprinting);

        publish(msg);
    }

    @Override
    public void recieve(SplinterMessage msg) {
        UUID uuid = new UUID(
                msg.getLong(),
                msg.getLong()
        );

        World world = Bukkit.getWorld(msg.getString());

        Location location = new Location(
                world,
                msg.getDouble(),
                msg.getDouble(),
                msg.getDouble(),
                msg.getFloat(),
                msg.getFloat()
        );

        Vector velocity = new Vector(
                msg.getDouble(),
                msg.getDouble(),
                msg.getDouble()
        );

        boolean flying = msg.getBool();
        boolean sprinting = msg.getBool();

        movePlayer(
                uuid,
                location,
                velocity,
                flying,
                sprinting
        );
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        // If server name is unknown, request it
        Splinter.getInstance().requestServerNameIfNeeded(player);

        // If state has been saved (ie: server is expecting join) then fetch and apply
        if (pendingTeleports.containsKey(player.getUniqueId())) {
            UUID uuid = player.getUniqueId();
            JoinInfo msg = pendingTeleports.get(uuid);
            msg.applyToPlayer(player);
            pendingTeleports.remove(uuid);
        }
    }

    public void movePlayer(UUID uuid, Location location, Vector velocity, boolean flying, boolean sprinting) {
        JoinInfo info = new JoinInfo(location, velocity, flying, sprinting);

        if (Bukkit.getPlayer(uuid) != null) {
            // If player is already connected, apply state
            Player player = Splinter.getInstance().getServer().getPlayer(uuid);
            info.applyToPlayer(player);
        } else {
            // Else add state to queue for application later
            pendingTeleports.put(uuid, info);
        }
    }
}
