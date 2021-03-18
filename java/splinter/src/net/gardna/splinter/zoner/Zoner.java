package net.gardna.splinter.zoner;

import net.gardna.splinter.util.Vector2;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class Zoner {
    List<Region> regions;

    public Zoner(Region[] regions) {
        this.regions = Arrays.asList(regions);
    }

    public Region getSupposedRegion(Player player) {
        return getSupposedRegion(player.getLocation());
    }

    public Region getSupposedRegion(Location loc) {
        return getSupposedRegion(new Vector2(loc.getX(), loc.getZ()));
    }

    public Region getSupposedRegion(Vector2 vec) {
        for (Region r : regions) {
            if (r.contains(vec)) return r;
        }

        return null;
    }
}
