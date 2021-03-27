package net.gardna.splinter.listeners;

import net.gardna.splinter.Splinter;
import net.gardna.splinter.messages.BlockChangeMessage;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFertilizeEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPistonEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.block.CauldronLevelChangeEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.block.MoistureChangeEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.block.SpongeAbsorbEvent;

public class BlockEventListener implements Listener {
    @EventHandler
    public void onBlockBurn(BlockBurnEvent event) {
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();

        BlockChangeMessage msg = new BlockChangeMessage(
                block.getLocation().toVector(),
                Material.AIR.createBlockData()
        );

        Splinter.getInstance().netHandler.publish("block.change", msg);
    }

    @EventHandler
    public void onBlockFade(BlockFadeEvent event) {
    }

    @EventHandler
    public void onBlockExplode(BlockExplodeEvent event) {
    }

    @EventHandler
    public void onBlockFertilize(BlockFertilizeEvent event) {
    }

    @EventHandler
    public void onBlockFromTo(BlockFromToEvent event) {
    }

    @EventHandler
    public void onBlockGrowEvent(BlockGrowEvent event) {
    }

    @EventHandler
    public void onBlockIgnite(BlockIgniteEvent event) {
    }

    @EventHandler
    public void onBlockPiston(BlockPistonEvent event) {
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Block block = event.getBlock();

        BlockChangeMessage msg = new BlockChangeMessage(
                block.getLocation().toVector(),
                block.getBlockData()
        );

        Splinter.getInstance().netHandler.publish("block.change", msg);
    }

    @EventHandler
    public void onBlockRedstone(BlockRedstoneEvent event) {
    }

    @EventHandler
    public void onCauldronChange(CauldronLevelChangeEvent event) {
    }

    @EventHandler
    public void onLeavesDecay(LeavesDecayEvent event) {
    }

    @EventHandler
    public void onMoistureChange(MoistureChangeEvent event) {
    }

    @EventHandler
    public void onSignChange(SignChangeEvent event) {
    }

    @EventHandler
    public void onSpongeAbsorb(SpongeAbsorbEvent event) {
    }
}