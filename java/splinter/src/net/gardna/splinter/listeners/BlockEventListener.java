package net.gardna.splinter.listeners;

import net.gardna.splinter.Splinter;
import net.gardna.splinter.messages.block.BlockChangeMessage;
import net.gardna.splinter.messages.block.SignChangeMessage;
import net.gardna.splinter.zoner.Zoner;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
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
import org.bukkit.event.block.MoistureChangeEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.block.SpongeAbsorbEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.world.StructureGrowEvent;
import org.bukkit.util.Vector;

import java.util.List;

public class BlockEventListener implements Listener {
    public static final BlockData AIR = Material.AIR.createBlockData();
    public static final BlockData FIRE = Material.FIRE.createBlockData();
    public static final BlockData WATER = Material.WATER.createBlockData();
    public static final BlockData LAVA = Material.LAVA.createBlockData();
    public static final BlockData WET_SPONGE = Material.WET_SPONGE.createBlockData();

    public static boolean ShouldDoEvent(BlockEvent event) {
        return ShouldDoEvent(event.getBlock().getLocation());
    }

    public static boolean ShouldDoEvent(Location location) {
        String serverName = Splinter.getInstance().serverName;
        Zoner zoner = Splinter.getInstance().zoner;

        return zoner.getSupposedRegion(location).server.equals(serverName);
    }

    public void publishBlockChange(Block block, BlockData blockData) {
        BlockChangeMessage msg = new BlockChangeMessage(
                block.getLocation().toVector(),
                blockData
        );

        Splinter.getInstance().netHandler.publish("block.change", msg);
    }

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
        publishBlockChange(event.getBlock(), bd);
    }

    @EventHandler
    public void onStructureGrow(StructureGrowEvent event) {
        if (!ShouldDoEvent(event.getLocation())) {
            event.setCancelled(true);
            return;
        }

        List<BlockState> blockStates = event.getBlocks();
        for (BlockState bs : blockStates)
            publishBlockChange(bs.getBlock(), bs.getBlockData());
    }

    @EventHandler
    public void onBlockBurn(BlockBurnEvent event) {
        if (!ShouldDoEvent(event)) {
            event.setCancelled(true);
            return;
        }

        Block block = event.getBlock();
        publishBlockChange(block, AIR);
    }

    @EventHandler
    public void onBlockSpread(BlockSpreadEvent event) {
        if (!ShouldDoEvent(event)) {
            event.setCancelled(true);
            return;
        }

        Block block = event.getBlock();
        publishBlockChange(block, event.getNewState().getBlockData());
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        publishBlockChange(block, AIR);
    }

    @EventHandler
    public void onBlockFade(BlockFadeEvent event) {
        if (!ShouldDoEvent(event)) {
            event.setCancelled(true);
            return;
        }

        Block block = event.getBlock();
        publishBlockChange(block, event.getNewState().getBlockData());
    }

    @EventHandler
    public void onBlockExplode(BlockExplodeEvent event, List<Block> blocks, float yield) {
        publishBlockChange(event.getBlock(), AIR);

        for (int i = 0; i < blocks.size(); i++) {
            publishBlockChange(blocks.get(i), AIR);
        }
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        for (int i = 0; i < event.blockList().size(); i++) {
            publishBlockChange(event.blockList().get(i), AIR);
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
            publishBlockChange(bs.getBlock(), bs.getBlockData());
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
        publishBlockChange(block, event.getNewState().getBlockData());
    }

    @EventHandler
    public void onBlockIgnite(BlockIgniteEvent event) {
        if (!ShouldDoEvent(event)) {
            event.setCancelled(true);
            return;
        }

        Block block = event.getBlock();
        publishBlockChange(block, FIRE);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Block block = event.getBlock();
        publishBlockChange(block, block.getBlockData());
    }

    @EventHandler
    public void onCauldronChange(CauldronLevelChangeEvent event) {
        Block block = event.getBlock();
        publishBlockChange(block, block.getBlockData());
    }

    @EventHandler
    public void onLeavesDecay(LeavesDecayEvent event) {
        if (!ShouldDoEvent(event)) {
            event.setCancelled(true);
            return;
        }

        Block block = event.getBlock();
        publishBlockChange(block, AIR);
    }

    @EventHandler
    public void onMoistureChange(MoistureChangeEvent event) {
    }

    @EventHandler
    public void onSignChange(SignChangeEvent event) {
        Vector loc = event.getBlock().getLocation().toVector();
        SignChangeMessage msg = new SignChangeMessage(loc, event.getLines());

        Splinter.getInstance().netHandler.publish("block.sign", msg);
    }

    @EventHandler
    public void onSpongeAbsorb(SpongeAbsorbEvent event) {
        if (!ShouldDoEvent(event.getBlock().getLocation())) {
            event.setCancelled(true);
            return;
        }

        publishBlockChange(event.getBlock(), WET_SPONGE);

        List<BlockState> blockStates = event.getBlocks();
        for (BlockState bs : blockStates)
            publishBlockChange(bs.getBlock(), bs.getBlockData());
    }
}