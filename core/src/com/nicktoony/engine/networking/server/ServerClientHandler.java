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

    protected void handleConnectingMessages(Packet packet) {
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

    protected void handleGameMessages(Packet packet) {
        if (packet instanceof PingPacket) {
            if (ping == null) {
                ping = new long[10];
                for (int i = 0; i < ping.length; i ++) {
                    ping[i] =  getTimestamp() - (long) ((PingPacket) packet).getTimestamp();
                }
            }

            ping[pingIndex] = getTimestamp() - (long) ((PingPacket) packet).getTimestamp();
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

        }
    }

    public void update() {

        if (state == STATE.INGAME) {
            if (lastPing < getTimestamp() - PING_TIMER) {
                PingPacket packet = new PingPacket();
                packet.setTimestamp(getTimestamp());
                sendPacket(packet);
                lastPing = getTimestamp();
            }
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

    /**
     * Event called when client is disconnected. Hence, should clean up
     */
    public void handleDisconnect() {
        state = STATE.DISCONNECTING;
    }

    public int getID() {
        return id;
    }

    public boolean bot() {
        return false;
    }
}
