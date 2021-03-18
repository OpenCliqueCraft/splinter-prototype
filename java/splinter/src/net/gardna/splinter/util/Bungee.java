package net.gardna.splinter.util;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.gardna.splinter.Splinter;
import org.bukkit.entity.Player;

public class Bungee {
    public static void MovePlayer(Player player, String server) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF(server);

        player.sendPluginMessage(Splinter.Instance, "BungeeCord", out.toByteArray());
    }

    public static void RequestServerName(Player player) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("GetServer");

        player.sendPluginMessage(Splinter.Instance, "BungeeCord", out.toByteArray());
    }
}
