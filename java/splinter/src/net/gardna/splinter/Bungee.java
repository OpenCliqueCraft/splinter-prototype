package net.gardna.splinter;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.entity.Player;

public class Bungee {
    public static void MovePlayer(Player player, String server) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF(server);

        player.sendPluginMessage(Splinter.getInstance(), "BungeeCord", out.toByteArray());
    }

    public static void RequestServerName(Player player) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("GetServer");

        player.sendPluginMessage(Splinter.getInstance(), "BungeeCord", out.toByteArray());
    }
}
