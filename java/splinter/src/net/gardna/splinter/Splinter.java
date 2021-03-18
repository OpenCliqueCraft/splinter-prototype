package net.gardna.splinter;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import net.gardna.splinter.util.Bungee;
import net.gardna.splinter.util.Vector2;
import net.gardna.splinter.zoner.MassiveRegion;
import net.gardna.splinter.zoner.Region;
import net.gardna.splinter.zoner.Zoner;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

class MyCommandExecutor implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage("You ran /mycommand");

        Bungee.MovePlayer((Player) sender, "server2");

        return true;
    }
}

public class Splinter extends JavaPlugin implements PluginMessageListener {
    public static Splinter Instance;
    public MoveListener moveListener;
    public MyCommandExecutor myCommandExecutor;
    public PlayerJoinListener playerJoinListener;
    public BlockChangeListener blockChangeListener;
    public NetHandler netHandler;
    public Zoner zoner;
    public String serverName;
    public World mainWorld;

    @Override
    public void onEnable() {
        Instance = this;

        zoner = new Zoner(new Region[]{
                new Region("server1", new Vector2(-100, -100), new Vector2(100, 100)),
                new MassiveRegion("server2")
        });

        myCommandExecutor = new MyCommandExecutor();
        moveListener = new MoveListener();
        playerJoinListener = new PlayerJoinListener();
        blockChangeListener = new BlockChangeListener();
        netHandler = new NetHandler();
        mainWorld = getServer().getWorld("world");

        moveListener.runTaskTimer(this, 0, 10);
        netHandler.runTaskAsynchronously(this);

        getCommand("mycommand").setExecutor(myCommandExecutor);

        getServer().getPluginManager().registerEvents(playerJoinListener, this);
        getServer().getPluginManager().registerEvents(blockChangeListener, this);
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", this);
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
