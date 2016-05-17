package com.nicktoony.engine.networking.server;

import com.nicktoony.engine.packets.Packet;
import com.nicktoony.engine.packets.TimestampedPacket;
import com.nicktoony.engine.packets.connection.*;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by nick on 19/07/15.
 */
public abstract class ServerClientHandler{

    public enum STATE {
        INIT,
        CONNECTING,
        LOADING,
        INGAME,
        DISCONNECTING
    }

    private final long PING_TIMER = 3000;

    protected STATE state = STATE.INIT;
    protected int id;
    private long initialTimestamp; // only sync'd on loaded!
    private List<TimestampedPacket> inputQueue = new ArrayList<TimestampedPacket>();
    private float leniency = 0;
    private long[] ping;
    private int pingIndex = 0;
    private long lastPing = 0;
    private long pingAverage = 0;
    private Server server;

    public abstract void sendPacket(Packet packet);
    public abstract void close();

    public ServerClientHandler(Server server) {
        this.server = server;
    }

    public void handleReceivedMessage(Packet packet) {
        switch (state) {
            case CONNECTING:
                handleConnectingMessages(packet);
                break;

            case LOADING:
                handleLoadingMessages(packet);
                break;

            case INGAME:
                handleGameMessages(packet);
                break;

        }
    }

    private void handleConnectingMessages(Packet packet) {
        if (packet instanceof ConnectPacket) {
            // TODO: connect request validation

            // And if successful..
            if (true) {
                this.sendPacket(new AcceptPacket(server.getConfig(), this.id));
                this.state = STATE.LOADING;
            } else {
                this.sendPacket(new RejectPacket());
                this.close();
            }
        }
    }

    protected void handleLoadingMessages(Packet packet) {
        if (packet instanceof LoadedPacket) {
            // State is now in game
            this.state = STATE.INGAME;
            // Sync timers
            this.initialTimestamp = System.currentTimeMillis();
            this.lastPing = getTimestamp();
        }
    }

    private void handleGameMessages(Packet packet) {
        if (packet instanceof PingPacket) {
            if (ping == null) {
                ping = new long[10];
                for (int i = 0; i < ping.length; i ++) {
                    ping[i] =  getTimestamp() - (long) ((PingPacket) packet).timestamp;
                }
            }

            ping[pingIndex] = getTimestamp() - (long) ((PingPacket) packet).timestamp;
            pingIndex += 1;
            if (pingIndex >= ping.length) {
                pingIndex = 0;
            }

            // calculate average
            pingAverage = 0;
            for (long p : ping) {
                if (p > pingAverage) {
                    pingAverage = p;
                }
            }
            System.out.println("PING MAX: " + pingAverage);

        } else {
            insertInputQueue((TimestampedPacket) packet);
        }
    }

    public void update() {

        if (state == STATE.INGAME) {
            if (lastPing < getTimestamp() - PING_TIMER) {
                PingPacket packet = new PingPacket();
                packet.timestamp = getTimestamp();
                sendPacket(packet);
                lastPing = getTimestamp();
            }


            if (leniency > 0) leniency -= 2;
            if (leniency > 100) leniency = 100;
        }

        if (state == STATE.INGAME) {
            handleInputQueue();
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public STATE getState() {
        return state;
    }

    public void setState(STATE state) {
        this.state = state;
    }

    public long getTimestamp() {
        return (System.currentTimeMillis() - initialTimestamp);
    }

    public void insertInputQueue(TimestampedPacket packet) {
        // loop through all elements
//        for (int i = 0; i < inputQueue.size(); i++) {
//            // Skip to next one if smaller
//            if (inputQueue.get(i).timestamp < packet.timestamp) continue;
//
//            System.out.println("WE JUMPED THE QUEUE");
//
//            // Otherwise, found location
//            inputQueue.add(i, packet);
//            return;
//        }
        // Jump to end
        inputQueue.add(packet);
    }

    /**
     * Event called when client is disconnected. Hence, should clean up
     */
    public void handleDisconnect() {
        state = STATE.DISCONNECTING;
    }

    public int getID() {
        return id;
    }

    private void handleInputQueue() {
        ListIterator<TimestampedPacket> iterator = inputQueue.listIterator();
        while (iterator.hasNext()) {
            TimestampedPacket packet = iterator.next();
            // Wait until we've compensated for latency
//            if (packet.timestamp < getTimestamp()
//                    - server.getConfig().sv_min_compensate) {
            // Latency has been compensated. Process it!
            iterator.remove();

            handleInput(packet);
//            } else {
//                break;
//            }
        }
    }

    protected abstract void handleInput(TimestampedPacket packet);

    public boolean bot() {
        return false;
    }
}
