package net.gardna.splinter.handlers;

import net.gardna.splinter.Splinter;
import net.gardna.splinter.messaging.ByteMessage;
import net.gardna.splinter.messaging.SplinterHandler;
import net.gardna.splinter.messaging.SplinterMessage;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.weather.WeatherChangeEvent;

public class WorldWeatherHandler extends SplinterHandler {
    public WorldWeatherHandler(String channel) {
        super(channel);
    }

    public void send(String world, boolean thundering, boolean raining) {
        int length = ByteMessage.STRING_SIZE(world) +
                ByteMessage.BOOL_SIZE * 2;

        SplinterMessage msg = new SplinterMessage(getServerId(), length);
        msg.putString(world);
        msg.putBool(thundering);
        msg.putBool(raining);

        publish(msg);
    }

    @Override
    public void recieve(SplinterMessage msg) {
        World world = Bukkit.getWorld(msg.getString());
        boolean t = msg.getBool();
        boolean r = msg.getBool();

        world.setThundering(t);
        world.setStorm(r);

    }

    // TODO: somehow stop this from bouncing twice (fires itself)
    @EventHandler
    public void onWeatherChange(WeatherChangeEvent event) {
        Bukkit.getScheduler().runTaskLater(Splinter.getInstance(), () -> {
            boolean thundering = Splinter.getInstance().mainWorld.isThundering();
            boolean raining = !Splinter.getInstance().mainWorld.isClearWeather();

            send(event.getWorld().getName(), thundering, raining);
        }, 5);
    }
}
