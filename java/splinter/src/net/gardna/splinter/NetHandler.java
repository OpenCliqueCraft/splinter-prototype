package net.gardna.splinter;

import io.nats.client.Connection;
import io.nats.client.Message;
import io.nats.client.Nats;
import net.gardna.splinter.util.Helpers;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

public class NetHandler extends BukkitRunnable {
    public Connection connection;

    @Override
    public void run() {
        try {
            connection = Nats.connect();
            CountDownLatch latch = new CountDownLatch(1);

            connection.createDispatcher((msg) -> {
                onTeleportMessage(msg);
            }).subscribe("teleport");

            connection.createDispatcher((msg) -> {
                onBreakMessage(msg);
            }).subscribe("block.break");

            connection.createDispatcher((msg) -> {
                onPlaceMessage(msg);
            }).subscribe("block.place");

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

    private void onBreakMessage(Message msg) {
        ByteBuffer data = ByteBuffer.wrap(msg.getData());
        int x = data.getInt();
        int y = data.getInt();
        int z = data.getInt();

        Splinter instance = Splinter.Instance;
        instance.getServer().getScheduler().runTask(instance, new Runnable() {
            @Override
            public void run() {
                Block b = instance.mainWorld.getBlockAt(x, y, z);
                b.setBlockData(Material.AIR.createBlockData());
            }
        });
    }

    private void onPlaceMessage(Message msg) {
        ByteBuffer data = ByteBuffer.wrap(msg.getData());
        int x = data.getInt();
        int y = data.getInt();
        int z = data.getInt();

        byte[] bdb = Helpers.SliceArray(msg.getData(), 12);
        String bd = new String(bdb, StandardCharsets.UTF_8);

        System.out.println(bd);

        Splinter instance = Splinter.Instance;
        instance.getServer().getScheduler().runTask(instance, new Runnable() {
            @Override
            public void run() {
                Block b = instance.mainWorld.getBlockAt(x, y, z);
                b.setBlockData(instance.getServer().createBlockData(bd));
            }
        });
    }

}
