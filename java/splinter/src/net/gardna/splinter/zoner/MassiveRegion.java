package net.gardna.splinter.zoner;

import net.gardna.splinter.util.Vector2;

public class MassiveRegion extends Region {
    public static final int WORLD_SIZE = 30000000;

    public MassiveRegion(String server) {
        super(server, new Vector2(-WORLD_SIZE, -WORLD_SIZE), new Vector2(WORLD_SIZE, WORLD_SIZE));
    }
}
