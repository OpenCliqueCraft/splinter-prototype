package net.gardna.splinter.listeners;

import org.bukkit.plugin.Plugin;

// This is just a container for Bukkit listeners
public class ListenerManager {
    public BlockEventListener blockEventListener = new BlockEventListener();
    public PlayerJoinListener playerJoinListener = new PlayerJoinListener();
    public TimeSkipListener timeSkipListener = new TimeSkipListener();
    public WeatherChangeListener weatherChangeListener = new WeatherChangeListener();

    public void registerAll(Plugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(blockEventListener, plugin);
        plugin.getServer().getPluginManager().registerEvents(playerJoinListener, plugin);
        plugin.getServer().getPluginManager().registerEvents(timeSkipListener, plugin);
        plugin.getServer().getPluginManager().registerEvents(weatherChangeListener, plugin);
    }
}
