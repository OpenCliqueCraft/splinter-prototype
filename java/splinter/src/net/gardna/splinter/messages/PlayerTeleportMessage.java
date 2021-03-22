package net.gardna.splinter.messages;

import net.gardna.splinter.Splinter;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.UUID;

public class PlayerTeleportMessage extends NetMessage {
    public static final int MESSAGE_SIZE = (UUID_SIZE * 1) + (DOUBLE_SIZE * 3) + (FLOAT_SIZE * 2) + (DOUBLE_SIZE * 3) + (BYTE_SIZE * 2);

    public UUID uuid;
    public Location location;
    public Vector velocity;
    public boolean flying;
    public boolean sprinting;

    public PlayerTeleportMessage(UUID uuid, Location location, Vector velocity, boolean flying, boolean sprinting) {
        super(MESSAGE_SIZE);

        this.uuid = uuid;
        this.location = location;
        this.velocity = velocity;
        this.flying = flying;
        this.sprinting = sprinting;

        data.putLong(uuid.getMostSignificantBits());
        data.putLong(uuid.getLeastSignificantBits());
        data.putDouble(location.getX());
        data.putDouble(location.getY());
        data.putDouble(location.getZ());
        data.putFloat(location.getPitch());
        data.putFloat(location.getYaw());
        data.putDouble(velocity.getX());
        data.putDouble(velocity.getY());
        data.putDouble(velocity.getZ());
        data.put(EncodeBoolean(flying));
        data.put(EncodeBoolean(sprinting));
    }

    public PlayerTeleportMessage(byte[] raw) {
        super(MESSAGE_SIZE);
        data.put(raw);

        this.uuid = new UUID(data.getLong(), data.getLong());

        this.location = new Location(
                Splinter.Instance.mainWorld,
                data.getDouble(),
                data.getDouble(),
                data.getDouble(),
                data.getFloat(),
                data.getFloat()
        );

        this.velocity = new Vector(
                data.getDouble(),
                data.getDouble(),
                data.getDouble()
        );

        this.flying = DecodeBoolean(data.get());
        this.sprinting = DecodeBoolean(data.get());
    }

    public static void ApplyToPlayer(PlayerTeleportMessage msg, Player player) {
        player.teleport(msg.location);
        player.setVelocity(msg.velocity);
        player.setFlying(msg.flying);
        player.setSprinting(msg.sprinting);
    }
}
