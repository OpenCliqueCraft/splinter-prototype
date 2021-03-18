package net.gardna.splinter.zoner;

import net.gardna.splinter.util.Vector2;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class Region {
    public String server;
    public Vector2 pos1;
    public Vector2 pos2;

    public Region(String server, Vector2 pos1, Vector2 pos2) {
        this.server = server;
        this.pos1 = pos1;
        this.pos2 = pos2;
    }

    public boolean contains(Player player) {
        return contains(player.getLocation());
    }

    public boolean contains(Location loc) {
        return contains(new Vector2(loc.getX(), loc.getZ()));
    }

    public boolean contains(Vector2 vec) {
        boolean b0 = pos1.getX() <= vec.getX();
        boolean b1 = pos1.getZ() <= vec.getZ();
        boolean b2 = pos2.getX() >= vec.getX();
        boolean b3 = pos2.getZ() >= vec.getZ();

        return b0 && b1 && b2 && b3;
    }
}
