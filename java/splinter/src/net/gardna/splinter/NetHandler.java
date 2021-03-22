package net.gardna.splinter;

import io.nats.client.Connection;
import io.nats.client.Message;
import io.nats.client.Nats;
import net.gardna.splinter.messages.PlayerTeleportMessage;
import net.gardna.splinter.util.Helpers;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CountDownLatch;

public class NetHandler extends BukkitRunnable {
    public Connection connection;

    @Override
    public void run() {
        try {
            connection = Nats.connect();
            CountDownLatch latch = new CountDownLatch(1);

            connection.createDispatcher((msg) -> {
                onTeleportMessage(new PlayerTeleportMessage(msg.getData()));
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

    private void onTeleportMessage(PlayerTeleportMessage msg) {
        if (Bukkit.getPlayer(msg.uuid) != null) {
            Player p = Splinter.Instance.getServer().getPlayer(msg.uuid);
            PlayerTeleportMessage.ApplyToPlayer(msg, p);
        } else {
            Splinter.Instance.playerJoinListener.pendingTeleports.put(msg.uuid, msg);
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
