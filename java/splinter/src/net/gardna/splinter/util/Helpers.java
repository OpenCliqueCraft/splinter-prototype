package net.gardna.splinter.util;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Bed;

public class Helpers {
    public static final Material[] DOORS = new Material[]{
            Material.OAK_DOOR,
            Material.SPRUCE_DOOR,
            Material.BIRCH_DOOR,
            Material.JUNGLE_DOOR,
            Material.ACACIA_DOOR,
            Material.DARK_OAK_DOOR,
            Material.CRIMSON_DOOR,
            Material.WARPED_DOOR,
            Material.IRON_DOOR
    };

    public static final Material[] BEDS = new Material[]{
            Material.BLACK_BED,
            Material.BLUE_BED,
            Material.BROWN_BED,
            Material.CYAN_BED,
            Material.GRAY_BED,
            Material.GREEN_BED,
            Material.LIGHT_BLUE_BED,
            Material.LIGHT_GRAY_BED,
            Material.LIME_BED,
            Material.MAGENTA_BED,
            Material.ORANGE_BED,
            Material.PINK_BED,
            Material.PURPLE_BED,
            Material.RED_BED,
            Material.WHITE_BED,
            Material.YELLOW_BED
    };

    public static byte[] SliceArray(byte[] array, int start) {
        return SliceArray(array, start, array.length);
    }

    public static byte[] SliceArray(byte[] array, int start, int end) {
        byte[] out = new byte[end - start];

        for (int i = start; i < end; i++) {
            out[i - start] = array[i];
        }

        return out;
    }

    public static boolean IsBed(Material material) {
        for (int i = 0; i < BEDS.length; i++)
            if (BEDS[i].equals(material)) return true;
        return false;
    }

    public static boolean IsDoor(Material material) {
        for (int i = 0; i < DOORS.length; i++)
            if (DOORS[i].equals(material)) return true;
        return false;
    }

    public static void PlaceDoor(Block block, BlockData blockData) {
        Bisected belowData = (Bisected) blockData;
        Bisected aboveData = (Bisected) belowData.clone();
        belowData.setHalf(Bisected.Half.BOTTOM);
        aboveData.setHalf(Bisected.Half.TOP);

        block.setBlockData(belowData, false);
        block.getRelative(BlockFace.UP).setBlockData(aboveData, false);
    }

    public static void PlaceBed(Block block, BlockData blockData) {
        Bed footData = (Bed) blockData;
        Bed headData = (Bed) footData.clone();
        footData.setPart(Bed.Part.FOOT);
        headData.setPart(Bed.Part.HEAD);

        block.setBlockData(footData, false);
        block.getRelative(footData.getFacing()).setBlockData(headData);
    }
}
