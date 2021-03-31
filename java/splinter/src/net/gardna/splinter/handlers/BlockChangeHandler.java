package net.gardna.splinter.handlers;

import net.gardna.splinter.Splinter;
import net.gardna.splinter.messaging.ByteMessage;
import net.gardna.splinter.messaging.SplinterHandler;
import net.gardna.splinter.messaging.SplinterMessage;
import net.gardna.splinter.zoner.Zoner;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Bed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFertilizeEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.block.CauldronLevelChangeEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.block.SpongeAbsorbEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.world.StructureGrowEvent;

import java.util.List;

public class BlockChangeHandler extends SplinterHandler {
    public static final BlockData AIR = Material.AIR.createBlockData();
    public static final BlockData FIRE = Material.FIRE.createBlockData();
    public static final BlockData WATER = Material.WATER.createBlockData();
    public static final BlockData LAVA = Material.LAVA.createBlockData();
    public static final BlockData WET_SPONGE = Material.WET_SPONGE.createBlockData();

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

    public static boolean IsDoor(Material material) {
        for (int i = 0; i < DOORS.length; i++)
            if (DOORS[i].equals(material)) return true;
        return false;
    }

    public static boolean IsBed(Material material) {
        for (int i = 0; i < BEDS.length; i++)
            if (BEDS[i].equals(material)) return true;
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

    public BlockChangeHandler(String channel) {
        super(channel);
    }

    public void send(Block block, BlockData blockData) {
        String worldName = block.getWorld().getName();
        String bdString = blockData.getAsString();

        int length = ByteMessage.INT_SIZE * 3 +
                ByteMessage.STRING_SIZE(worldName) +
                ByteMessage.STRING_SIZE(bdString);

        SplinterMessage msg = new SplinterMessage(getServerId(), length);

        msg.putString(worldName);
        msg.putInt(block.getX());
        msg.putInt(block.getY());
        msg.putInt(block.getZ());
        msg.putString(bdString);

        publish(msg);
    }

    @Override
    public void recieve(SplinterMessage msg) {
        Block block = Bukkit.getWorld(msg.getString()).getBlockAt(
                msg.getInt(),
                msg.getInt(),
                msg.getInt()
        );

        BlockData blockData = Bukkit.createBlockData(msg.getString());

        if (IsDoor(blockData.getMaterial())) {
            PlaceDoor(block, blockData);
        } else if (IsBed(blockData.getMaterial())) {
            PlaceBed(block, blockData);
        } else {
            block.setBlockData(blockData, true);
        }
    }

    public static boolean ShouldDoEvent(BlockEvent event) {
        return ShouldDoEvent(event.getBlock().getLocation());
    }

    public static boolean ShouldDoEvent(Location location) {
        String serverName = Splinter.getInstance().serverName;
        Zoner zoner = Splinter.getInstance().zoner;

        return zoner.getSupposedRegion(location).server.equals(serverName);
    }

    // TODO: move into new class
    @EventHandler
    public void onItemSpawn(ItemSpawnEvent event) {
        if (!ShouldDoEvent(event.getLocation())) {
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler
    public void onBucketEvent(PlayerBucketEmptyEvent event) {
        Material bucket = event.getBucket();
        BlockData bd = bucket.toString().contains("LAVA") ? LAVA : WATER;
        send(event.getBlock(), bd);
    }

    @EventHandler
    public void onStructureGrow(StructureGrowEvent event) {
        if (!ShouldDoEvent(event.getLocation())) {
            event.setCancelled(true);
            return;
        }

        List<BlockState> blockStates = event.getBlocks();
        for (BlockState bs : blockStates)
            send(bs.getBlock(), bs.getBlockData());
    }

    @EventHandler
    public void onBlockBurn(BlockBurnEvent event) {
        if (!ShouldDoEvent(event)) {
            event.setCancelled(true);
            return;
        }

        Block block = event.getBlock();
        send(block, AIR);
    }

    @EventHandler
    public void onBlockSpread(BlockSpreadEvent event) {
        if (!ShouldDoEvent(event)) {
            event.setCancelled(true);
            return;
        }

        Block block = event.getBlock();
        send(block, event.getNewState().getBlockData());
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        send(block, AIR);
    }

    @EventHandler
    public void onBlockFade(BlockFadeEvent event) {
        if (!ShouldDoEvent(event)) {
            event.setCancelled(true);
            return;
        }

        Block block = event.getBlock();
        send(block, event.getNewState().getBlockData());
    }

    @EventHandler
    public void onBlockExplode(BlockExplodeEvent event, List<Block> blocks, float yield) {
        send(event.getBlock(), AIR);

        for (int i = 0; i < blocks.size(); i++) {
            send(blocks.get(i), AIR);
        }
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        for (int i = 0; i < event.blockList().size(); i++) {
            send(event.blockList().get(i), AIR);
        }
    }

    @EventHandler
    public void blockFertilizeEvent(BlockFertilizeEvent event) {
        if (!ShouldDoEvent(event.getBlock().getLocation())) {
            event.setCancelled(true);
            return;
        }

        List<BlockState> blockStates = event.getBlocks();
        for (BlockState bs : blockStates)
            send(bs.getBlock(), bs.getBlockData());
    }

    @EventHandler
    public void onBlockFromTo(BlockFromToEvent event) {
        // TODO: Implement
    }

    @EventHandler
    public void onBlockGrowEvent(BlockGrowEvent event) {
        if (!ShouldDoEvent(event)) {
            event.setCancelled(true);
            return;
        }

        Block block = event.getBlock();
        send(block, event.getNewState().getBlockData());
    }

    @EventHandler
    public void onBlockIgnite(BlockIgniteEvent event) {
        if (!ShouldDoEvent(event)) {
            event.setCancelled(true);
            return;
        }

        Block block = event.getBlock();
        send(block, FIRE);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Block block = event.getBlock();
        send(block, block.getBlockData());
    }

    @EventHandler
    public void onCauldronChange(CauldronLevelChangeEvent event) {
        Block block = event.getBlock();
        send(block, block.getBlockData());
    }

    @EventHandler
    public void onLeavesDecay(LeavesDecayEvent event) {
        if (!ShouldDoEvent(event)) {
            event.setCancelled(true);
            return;
        }

        Block block = event.getBlock();
        send(block, AIR);
    }

    @EventHandler
    public void onSpongeAbsorb(SpongeAbsorbEvent event) {
        if (!ShouldDoEvent(event.getBlock().getLocation())) {
            event.setCancelled(true);
            return;
        }

        send(event.getBlock(), WET_SPONGE);

        List<BlockState> blockStates = event.getBlocks();
        for (BlockState bs : blockStates)
            send(bs.getBlock(), bs.getBlockData());
    }
}
