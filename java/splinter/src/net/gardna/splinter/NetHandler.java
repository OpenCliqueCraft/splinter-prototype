package net.gardna.splinter;

import io.nats.client.Connection;
import io.nats.client.Nats;
import net.gardna.splinter.messages.BlockChangeMessage;
import net.gardna.splinter.messages.NetMessage;
import net.gardna.splinter.messages.PlayerDataMessage;
import net.gardna.splinter.messages.PlayerPrejoinMessage;
import net.gardna.splinter.messages.WorldTimeMessage;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.time.Duration;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeoutException;

public class NetHandler extends BukkitRunnable {
    public Connection connection;
    public long serverId = new Random().nextLong();

    @Override
    public void run() {
        Splinter.getInstance().getLogger().info("Server ID is: " + serverId);

        try {
            connection = Nats.connect();
            CountDownLatch latch = new CountDownLatch(1);

            connection.createDispatcher((incoming) -> {
                PlayerPrejoinMessage msg = new PlayerPrejoinMessage(incoming.getData());
                if (!isOwnMessage(msg)) onPrejoinMessage(msg);
            }).subscribe("player.prejoin");

            connection.createDispatcher((incoming) -> {
                BlockChangeMessage msg = new BlockChangeMessage(incoming.getData());
                if (!isOwnMessage(msg)) onBlockChangeMessage(msg);
            }).subscribe("block.change");

            connection.createDispatcher((incoming) -> {
                PlayerDataMessage msg = new PlayerDataMessage(incoming.getData());
                if (!isOwnMessage(msg)) onPlayerDataMessage(msg);
            }).subscribe("player.data");

            connection.createDispatcher((incoming) -> {
                WorldTimeMessage msg = new WorldTimeMessage(incoming.getData());
                if (!isOwnMessage(msg)) onWorldTimeMessage(msg);
            }).subscribe("world.time");

            Splinter.getInstance().getLogger().info("NATS listening for messages");

            latch.await();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public boolean isOwnMessage(NetMessage msg) {
        System.out.println("MSG ID " + msg.senderId);
        return msg.senderId == serverId;
    }

    public void publish(String channel, NetMessage msg) {
        try {
            connection.publish(channel, msg.getData());
            connection.flush(Duration.ZERO);
        } catch (TimeoutException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void onPrejoinMessage(PlayerPrejoinMessage msg) {
        Splinter.getInstance().playerJoinListener.movePlayer(msg);
    }

    private void onBlockChangeMessage(BlockChangeMessage msg) {
        World world = Splinter.getInstance().mainWorld;
        Block block = world.getBlockAt(msg.location.toLocation(world));

        Bukkit.getScheduler().runTask(
                Splinter.getInstance(),
                () -> block.setBlockData(msg.blockData, true)
        );
    }

    private void onPlayerDataMessage(PlayerDataMessage msg) {
        PlayerDataMessage.WritePlayerData(msg.uuid, msg.playerData);
    }

    private void onWorldTimeMessage(WorldTimeMessage msg) {
        System.out.println("Recieved time " + msg.time);
        Bukkit.getScheduler().runTask(
                Splinter.getInstance(),
                () -> Splinter.getInstance().mainWorld.setTime(msg.time)
        );
    }
}
