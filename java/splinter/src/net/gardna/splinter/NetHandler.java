package net.gardna.splinter;

import io.nats.client.Connection;
import io.nats.client.Dispatcher;
import io.nats.client.MessageHandler;
import io.nats.client.Nats;
import net.gardna.splinter.messaging.SplinterHandler;
import net.gardna.splinter.messaging.SplinterMessage;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeoutException;

public class NetHandler extends BukkitRunnable {
    private Connection connection;
    private SplinterHandler[] handlers;
    private String addr;

    public NetHandler(String addr, SplinterHandler[] handlers) {
        this.addr = addr;
        this.handlers = handlers;
    }

    @Override
    public void run() {
        try {
            connection = Nats.connect(addr);
            CountDownLatch latch = new CountDownLatch(1);

            for (SplinterHandler handler : handlers)
                handler.subscribe(this);

            latch.await();

            Splinter.getInstance().getLogger().info("NATS now awaiting messages");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public Dispatcher createDispatcher(MessageHandler handler) {
        return connection.createDispatcher(handler);
    }

    public void publish(String channel, SplinterMessage msg) {
        publish(channel, msg.getData());
    }

    public void publish(String channel, byte[] data) {
        try {
            connection.publish(channel, data);
            connection.flush(Duration.ZERO);
        } catch (TimeoutException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
