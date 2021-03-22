package net.gardna.splinter;

import io.nats.client.Connection;
import io.nats.client.Nats;
import net.gardna.splinter.messages.BlockChangeMessage;
import net.gardna.splinter.messages.NetMessage;
import net.gardna.splinter.messages.PlayerDataMessage;
import net.gardna.splinter.messages.PlayerPrejoinMessage;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeoutException;

public class NetHandler extends BukkitRunnable {
    public Connection connection;

    @Override
    public void run() {
        try {
            connection = Nats.connect();
            CountDownLatch latch = new CountDownLatch(1);

            connection.createDispatcher((msg) ->
                    onPrejoinMessage(new PlayerPrejoinMessage(msg.getData()))
            ).subscribe("player.prejoin");

            connection.createDispatcher((msg) ->
                    onBlockChangeMessage(new BlockChangeMessage(msg.getData()))
            ).subscribe("block.change");

            connection.createDispatcher((msg) ->
                    onPlayerDataMessage(new PlayerDataMessage(msg.getData()))
            ).subscribe("player.data");

            Splinter.getInstance().getLogger().info("NATS listening for messages");

            latch.await();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
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
        System.out.println(msg.uuid);
        PlayerDataMessage.WritePlayerData(msg.uuid, msg.playerData);

        System.out.println("Saved data");
    }
}
