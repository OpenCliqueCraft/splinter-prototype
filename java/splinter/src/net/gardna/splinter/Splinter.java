package net.gardna.splinter;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import io.nats.client.Connection;
import net.gardna.splinter.handlers.BlockChangeHandler;
import net.gardna.splinter.handlers.BlockSignHandler;
import net.gardna.splinter.handlers.PlayerDataHandler;
import net.gardna.splinter.handlers.PlayerJoinHandler;
import net.gardna.splinter.handlers.WorldTimeHandler;
import net.gardna.splinter.handlers.WorldWeatherHandler;
import net.gardna.splinter.messaging.SplinterHandler;
import net.gardna.splinter.util.Vector2;
import net.gardna.splinter.zoner.MassiveRegion;
import net.gardna.splinter.zoner.Region;
import net.gardna.splinter.zoner.Zoner;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.util.Random;

public class Splinter extends JavaPlugin implements PluginMessageListener {
    private static Splinter instance;
    private Connection connection;

    public long serverId = new Random().nextLong();
    public MoveDetector moveDetector;
    public NetHandler netHandler;
    public Zoner zoner;
    public String serverName;
    public World mainWorld;

    BlockChangeHandler blockChangeHandler = new BlockChangeHandler("block.change");
    BlockSignHandler blockSignHandler = new BlockSignHandler("block.sign");
    PlayerJoinHandler playerJoinHandler = new PlayerJoinHandler("player.join");
    PlayerDataHandler playerDataHandler = new PlayerDataHandler("player.data");
    WorldTimeHandler worldTimeHandler = new WorldTimeHandler("world.time");
    WorldWeatherHandler worldWeatherHandler = new WorldWeatherHandler("world.weather");

    @Override
    public void onEnable() {
        instance = this;

        zoner = new Zoner(new Region[]{
                new Region("server1", new Vector2(-100, -100), new Vector2(100, 100)),
                new MassiveRegion("server2")
        });

        moveDetector = new MoveDetector();
        moveDetector.runTaskTimer(this, 0, 10);

        netHandler = new NetHandler("nats://localhost:4222", new SplinterHandler[]{
                blockChangeHandler,
                blockSignHandler,
                playerJoinHandler,
                playerDataHandler,
                worldTimeHandler,
                worldWeatherHandler
        });
        netHandler.runTaskAsynchronously(this);

        mainWorld = getServer().getWorld("world");
        mainWorld.setAutoSave(false);

        getServer().getPluginManager().registerEvents(blockChangeHandler, this);
        getServer().getPluginManager().registerEvents(blockSignHandler, this);
        getServer().getPluginManager().registerEvents(playerJoinHandler, this);
        getServer().getPluginManager().registerEvents(playerDataHandler, this);
        getServer().getPluginManager().registerEvents(worldTimeHandler, this);
        getServer().getPluginManager().registerEvents(worldWeatherHandler, this);

        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", this);


        getLogger().info("Splinter initialized");
        getLogger().info("Server ID is: " + Splinter.getInstance().serverId);
    }

    public static Splinter getInstance() {
        return instance;
    }

    // TODO: abstract to config file?
    public void requestServerNameIfNeeded(Player player) {
        if (serverName == null) {
            getLogger().info("Requesting server name from Bungee");
            getServer().getScheduler().runTaskLater(this, new Runnable() {
                @Override
                public void run() {
                    Bungee.RequestServerName(player);
                }
            }, 5);
        }
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (!channel.equals("BungeeCord")) return;

        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        String subChannel = in.readUTF();
        String data = in.readUTF();

        if (subChannel.equals("GetServer")) {
            serverName = data;
            getLogger().info("Got server name: " + serverName);
        }
    }
}
