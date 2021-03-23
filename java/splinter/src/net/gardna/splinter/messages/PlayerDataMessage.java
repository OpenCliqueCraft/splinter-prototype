package net.gardna.splinter.messages;

import net.gardna.splinter.Splinter;
import net.gardna.splinter.util.Helpers;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class PlayerDataMessage extends NetMessage {
    public static final int MESSAGE_SIZE = UUID_SIZE;

    public UUID uuid;
    public byte[] playerData;

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
        player.loadData();
    }

    public PlayerDataMessage(UUID uuid, byte[] playerData) {
        super(UUID_SIZE + playerData.length);

        this.uuid = uuid;
        this.playerData = playerData;

        data.putLong(uuid.getMostSignificantBits());
        data.putLong(uuid.getLeastSignificantBits());
        data.put(playerData);
    }

    public PlayerDataMessage(byte[] raw) {
        super(raw);

        this.uuid = new UUID(data.getLong(), data.getLong());
        this.playerData = Helpers.SliceArray(raw, MESSAGE_SIZE + LONG_SIZE);
    }
}
