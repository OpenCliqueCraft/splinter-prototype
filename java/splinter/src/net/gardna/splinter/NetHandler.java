package net.gardna.splinter;

import io.nats.client.Connection;
import io.nats.client.Dispatcher;
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
import org.bukkit.block.Sign;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.time.Duration;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeoutException;

interface MessageAction {
    public void recieve(byte[] msg);
}

class MessageHandler {
    private NetHandler netHandler;
    private String channel;
    private MessageAction action;
    private Dispatcher dispatcher;

    public MessageHandler(NetHandler netHandler, String channel, MessageAction action) {
        this.netHandler = netHandler;
        this.channel = channel;
        this.action = action;

        this.dispatcher = netHandler.connection.createDispatcher((incoming) -> {
            NetMessage msg = new NetMessage(incoming.getData());
            if (msg.senderId != netHandler.serverId) {
                Bukkit.getScheduler().runTask(
                        Splinter.getInstance(),
                        () -> action.recieve(incoming.getData())
                );
            }
        }).subscribe(channel);
    }
}

public class NetHandler extends BukkitRunnable {
    public Connection connection;
    public long serverId = new Random().nextLong();

    @Override
    public void run() {
        Splinter.getInstance().getLogger().info("Server ID is: " + serverId);

        try {
            connection = Nats.connect();
            CountDownLatch latch = new CountDownLatch(1);

            MessageHandler playerPrejoin = new MessageHandler(this, "player.prejoin", (byte[] data) -> {
                PlayerPrejoinMessage msg = new PlayerPrejoinMessage(data);
                Splinter.getInstance().listeners.playerJoinListener.movePlayer(msg);
            });

            MessageHandler playerData = new MessageHandler(this, "player.data", (byte[] data) -> {
                PlayerDataMessage msg = new PlayerDataMessage(data);
                PlayerDataMessage.WritePlayerData(msg.uuid, msg.playerData);
            });

            MessageHandler worldTime = new MessageHandler(this, "world.time", (byte[] data) -> {
                WorldTimeMessage msg = new WorldTimeMessage(data);
                Splinter.getInstance().mainWorld.setTime(msg.time);
            });

            MessageHandler worldWeather = new MessageHandler(this, "world.weather", (byte[] data) -> {
                WeatherChangeMessage msg = new WeatherChangeMessage(data);
                Splinter.getInstance().mainWorld.setThundering(msg.thundering);
                Splinter.getInstance().mainWorld.setStorm(msg.raining);
            });

            MessageHandler blockChange = new MessageHandler(this, "block.change", (byte[] data) -> {
                BlockChangeMessage msg = new BlockChangeMessage(data);

                World world = Splinter.getInstance().mainWorld;
                Block block = world.getBlockAt(msg.location.toLocation(world));

                if (Helpers.IsDoor(msg.blockData.getMaterial())) {
                    Helpers.PlaceDoor(block, msg.blockData);
                } else if (Helpers.IsBed(msg.blockData.getMaterial())) {
                    Helpers.PlaceBed(block, msg.blockData);
                } else {
                    block.setBlockData(msg.blockData, true);
                }
            });

            MessageHandler blockSign = new MessageHandler(this, "block.sign", (byte[] data) -> {
                SignChangeMessage msg = new SignChangeMessage(data);

                World mainWorld = Splinter.getInstance().mainWorld;
                Block block = mainWorld.getBlockAt(msg.location.toLocation(mainWorld));
                Sign sign = (Sign) block.getState();

                for (int i = 0; i < msg.lines.length; i++)
                    sign.setLine(i, msg.lines[i]);

                sign.update();
            });

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
}
