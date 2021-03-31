package net.gardna.splinter.handlers;

import net.gardna.splinter.Splinter;
import net.gardna.splinter.messaging.ByteMessage;
import net.gardna.splinter.messaging.SplinterHandler;
import net.gardna.splinter.messaging.SplinterMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class PlayerDataHandler extends SplinterHandler {
    private static File PlayerDataFile(UUID uuid) {
        String s = File.separator;

        String worldName = Splinter.getInstance().mainWorld.getName();
        String dataFileName = Bukkit.getWorldContainer() + s + worldName + s + "playerdata" + s + uuid + ".dat";

        return new File(dataFileName);
    }

    public static byte[] ReadPlayerdata(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        player.saveData();

        File dataFile = PlayerDataFile(uuid);

        try {
            byte[] data = new byte[(int) dataFile.length()];

            InputStream inputStream = new FileInputStream(dataFile);
            inputStream.read(data);
            inputStream.close();

            return data;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new byte[]{};
    }

    public static void WritePlayerData(UUID uuid, byte[] playerData) {
        File dataFile = PlayerDataFile(uuid);

        try {
            OutputStream outputStream = new FileOutputStream(dataFile);
            outputStream.write(playerData);
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Player player = Bukkit.getPlayer(uuid);
        if (player != null) player.loadData();
    }

    public PlayerDataHandler(String channel) {
        super(channel);
    }

    public void send(UUID uuid) {
        byte[] playerData = ReadPlayerdata(uuid);
        int length = ByteMessage.UUID_SIZE +
                ByteMessage.RAW_SIZE(playerData);

        SplinterMessage msg = new SplinterMessage(getServerId(), length);
        msg.putUuid(uuid);
        msg.putRaw(playerData);

        publish(msg);
    }

    @Override
    public void recieve(SplinterMessage msg) {
        UUID uuid = msg.getUuid();
        WritePlayerData(uuid, msg.getRaw());
    }
}
