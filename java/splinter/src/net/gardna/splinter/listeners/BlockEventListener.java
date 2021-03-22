package net.gardna.splinter.listeners;

import net.gardna.splinter.Splinter;
import net.gardna.splinter.messages.BlockChangeMessage;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockEventListener implements Listener {
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
    public void onBlockPlace(BlockPlaceEvent event) {
        Block block = event.getBlock();

        BlockChangeMessage msg = new BlockChangeMessage(
                block.getLocation().toVector(),
                block.getBlockData()
        );

        Splinter.getInstance().netHandler.publish("block.change", msg);
    }
}