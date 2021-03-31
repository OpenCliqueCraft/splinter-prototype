package net.gardna.splinter.messaging;

import io.nats.client.Dispatcher;
import net.gardna.splinter.NetHandler;
import net.gardna.splinter.Splinter;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

public abstract class SplinterHandler implements Listener {
    private String channel;
    private Dispatcher dispatcher;
    private NetHandler netHandler;

    public SplinterHandler(String channel) {
        this.channel = channel;
    }

    public void subscribe(NetHandler netHandler) {
        this.netHandler = netHandler;
        this.dispatcher = netHandler.createDispatcher((incoming) -> {
            SplinterMessage msg = new SplinterMessage(incoming.getData());

            if (msg.getSenderId() != Splinter.getInstance().serverId)
                Bukkit.getScheduler().runTask(
                        Splinter.getInstance(),
                        () -> recieve(msg)
                );
        }).subscribe(channel);

        Splinter.getInstance().getLogger().info("Subscribed to " + channel);
    }

    public long getServerId() {
        return Splinter.getInstance().serverId;
    }

    public void publish(SplinterMessage msg) {
        netHandler.publish(channel, msg);
    }

    public abstract void recieve(SplinterMessage msg);
}
