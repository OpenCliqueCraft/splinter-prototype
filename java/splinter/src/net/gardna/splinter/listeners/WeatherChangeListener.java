package net.gardna.splinter.listeners;

import net.gardna.splinter.Splinter;
import net.gardna.splinter.messages.WeatherChangeMessage;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.WeatherChangeEvent;

public class WeatherChangeListener implements Listener {
    @EventHandler
    public void onWeatherChange(WeatherChangeEvent event) {
        Bukkit.getScheduler().runTaskLater(Splinter.getInstance(), () -> {
            boolean thundering = Splinter.getInstance().mainWorld.isThundering();
            boolean raining = !Splinter.getInstance().mainWorld.isClearWeather();

            Splinter.getInstance().netHandler.publish("world.weather", new WeatherChangeMessage(thundering, raining));
        }, 5);
    }
}
