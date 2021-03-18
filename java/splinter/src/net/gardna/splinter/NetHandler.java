package net.gardna.splinter;

import io.nats.client.Connection;
import io.nats.client.Dispatcher;
import io.nats.client.Message;
import io.nats.client.Nats;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

public class NetHandler extends BukkitRunnable {
    public Connection connection;

    @Override
    public void run() {
        try {
            connection = Nats.connect();
            CountDownLatch latch = new CountDownLatch(1);

            Dispatcher dispatcher = connection.createDispatcher((msg) -> {
                onTeleportMessage(msg);
            }).subscribe("teleport");

            System.out.println("Listening for updates");

            latch.await();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void onTeleportMessage(Message msg) {
        ByteBuffer data = ByteBuffer.wrap(msg.getData());
        UUID uuid = new UUID(data.getLong(), data.getLong());
        double x = data.getDouble();
        double y = data.getDouble();
        double z = data.getDouble();
        float pitch = data.getFloat();
        float yaw = data.getFloat();

        Location loc = new Location(Splinter.Instance.mainWorld, x, y, z, yaw, pitch);

        if (Bukkit.getPlayer(uuid) != null) {
            Splinter.Instance.getServer().getPlayer(uuid).teleport(loc);
        } else {
            Splinter.Instance.playerJoinListener.pendingTeleports.put(uuid, loc);
        }
    }
}
