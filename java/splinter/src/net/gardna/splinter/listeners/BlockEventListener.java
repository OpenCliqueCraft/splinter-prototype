package net.gardna.splinter.listeners;

import net.gardna.splinter.Splinter;
import net.gardna.splinter.messages.block.BlockChangeMessage;
import net.gardna.splinter.messages.block.SignChangeMessage;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
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
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.block.CauldronLevelChangeEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.block.MoistureChangeEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.block.SpongeAbsorbEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.util.Vector;

import java.util.List;

public class BlockEventListener implements Listener {
    public static final BlockData AIR = Material.AIR.createBlockData();

    public void publishBlockChange(Block block, BlockData blockData) {
        BlockChangeMessage msg = new BlockChangeMessage(
                block.getLocation().toVector(),
                blockData
        );

        Splinter.getInstance().netHandler.publish("block.change", msg);
    }

    @EventHandler
    public void onBlockBurn(BlockBurnEvent event) {
        Block block = event.getBlock();
        publishBlockChange(block, AIR);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        publishBlockChange(block, AIR);
    }

    @EventHandler
    public void onBlockFade(BlockFadeEvent event) {
    }

    @EventHandler
    public void onBlockExplode(BlockExplodeEvent event, List<Block> blocks, float yield) {
        publishBlockChange(event.getBlock(), AIR);

        System.out.println(blocks);

        for (int i = 0; i < blocks.size(); i++) {
            publishBlockChange(blocks.get(i), AIR);
        }
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        System.out.println("EXPLODE: " + event.getEntity().getName());

        for (int i = 0; i < event.blockList().size(); i++) {
            publishBlockChange(event.blockList().get(i), AIR);
        }
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
    public void onPistonExtend(BlockPistonExtendEvent event) {
    }

    @EventHandler
    public void onPistonRetract(BlockPistonRetractEvent event) {
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Block block = event.getBlock();
        publishBlockChange(block, block.getBlockData());
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
        Vector loc = event.getBlock().getLocation().toVector();
        SignChangeMessage msg = new SignChangeMessage(loc, event.getLines());

        System.out.println("SIGN CHANGED!");
        for (String line : event.getLines())
            System.out.println("line:" + line);

        Splinter.getInstance().netHandler.publish("block.sign", msg);
    }

    @EventHandler
    public void onSpongeAbsorb(SpongeAbsorbEvent event) {
    }
}