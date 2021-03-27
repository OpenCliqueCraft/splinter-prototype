package net.gardna.splinter;

import io.nats.client.Connection;
import io.nats.client.Nats;
import net.gardna.splinter.messages.NetMessage;
import net.gardna.splinter.messages.PlayerDataMessage;
import net.gardna.splinter.messages.PlayerPrejoinMessage;
import net.gardna.splinter.messages.WeatherChangeMessage;
import net.gardna.splinter.messages.WorldTimeMessage;
import net.gardna.splinter.messages.block.BlockChangeMessage;
import net.gardna.splinter.messages.block.SignChangeMessage;
import net.gardna.splinter.util.Helpers;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Bed;
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
                SignChangeMessage msg = new SignChangeMessage(incoming.getData());
                if (!isOwnMessage(msg)) onSignChangeMessage(msg);
            }).subscribe("block.sign");

            connection.createDispatcher((incoming) -> {
                PlayerDataMessage msg = new PlayerDataMessage(incoming.getData());
                if (!isOwnMessage(msg)) onPlayerDataMessage(msg);
            }).subscribe("player.data");

            connection.createDispatcher((incoming) -> {
                WorldTimeMessage msg = new WorldTimeMessage(incoming.getData());
                if (!isOwnMessage(msg)) onWorldTimeMessage(msg);
            }).subscribe("world.time");

            connection.createDispatcher((incoming) -> {
                WeatherChangeMessage msg = new WeatherChangeMessage(incoming.getData());
                if (!isOwnMessage(msg)) onWeatherChangeMessage(msg);
            }).subscribe("world.weather");

            Splinter.getInstance().getLogger().info("NATS listening for messages");

            latch.await();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public boolean isOwnMessage(NetMessage msg) {
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
        Splinter.getInstance().listeners.playerJoinListener.movePlayer(msg);
    }

    private void placeDoor(Block block, BlockData blockData) {
        Bisected belowData = (Bisected) blockData;
        Bisected aboveData = (Bisected) belowData.clone();
        belowData.setHalf(Bisected.Half.BOTTOM);
        aboveData.setHalf(Bisected.Half.TOP);

        block.setBlockData(belowData, false);
        block.getRelative(BlockFace.UP).setBlockData(aboveData, false);
    }

    private void placeBed(Block block, BlockData blockData) {
        Bed footData = (Bed) blockData;
        Bed headData = (Bed) footData.clone();
        footData.setPart(Bed.Part.FOOT);
        headData.setPart(Bed.Part.HEAD);

        block.setBlockData(footData, false);
        block.getRelative(footData.getFacing()).setBlockData(headData);
    }

    private void onBlockChangeMessage(BlockChangeMessage msg) {
        World world = Splinter.getInstance().mainWorld;
        Block block = world.getBlockAt(msg.location.toLocation(world));

        Bukkit.getScheduler().runTask(
                Splinter.getInstance(),
                () -> {
                    if (Helpers.IsDoor(msg.blockData.getMaterial())) {
                        placeDoor(block, msg.blockData);
                    } else if (Helpers.IsBed(msg.blockData.getMaterial())) {
                        placeBed(block, msg.blockData);
                    } else {
                        block.setBlockData(msg.blockData, true);
                    }
                }
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

    private void onWeatherChangeMessage(WeatherChangeMessage msg) {
        Bukkit.getScheduler().runTask(
                Splinter.getInstance(),
                () -> {
                    Splinter.getInstance().mainWorld.setThundering(msg.thundering);
                    Splinter.getInstance().mainWorld.setStorm(msg.raining);
                }
        );
        System.out.println("rain:" + msg.raining);
        System.out.println("thunder:" + msg.thundering);
    }

    private void onSignChangeMessage(SignChangeMessage msg) {
        Bukkit.getScheduler().runTask(
                Splinter.getInstance(),
                () -> {
                    World mainWorld = Splinter.getInstance().mainWorld;
                    Block block = mainWorld.getBlockAt(msg.location.toLocation(mainWorld));
                    Sign sign = (Sign) block.getState();

                    for (int i = 0; i < msg.lines.length; i++)
                        sign.setLine(i, msg.lines[i]);

                    sign.update();
                }
        );
    }
}
