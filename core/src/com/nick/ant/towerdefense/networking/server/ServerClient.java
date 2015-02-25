package com.nick.ant.towerdefense.networking.server;

import com.badlogic.gdx.math.Vector2;
import com.esotericsoftware.kryonet.Connection;
import com.nick.ant.towerdefense.networking.packets.*;
import com.nick.ant.towerdefense.networking.packets.player.*;
import com.nick.ant.towerdefense.renderables.entities.players.Player;

/**
 * Created by Nick on 07/02/2015.
 */
public abstract class ServerClient {
    private final long timeCreated = System.currentTimeMillis();

    private Connection socket;
    private CSTDServer server;
    private int id;

    public ServerClient(int id, CSTDServer server, Connection socket) {
        this.id = id;
        this.socket = socket;
        this.server = server;
    }

    public abstract boolean handleReceivedMessage(Object packet);

    public Connection getSocket() {
        return socket;
    }

    public abstract void step();

    protected void sendToAll(Packet packet) {
        socket.sendTCP(packet);
        sendToOthers(packet);
    }

    public int getId() {
        return id;
    }

    protected void sendToOthers(Packet packet) {
        server.sendToOthers(packet, this);
    }

    public long getTimeCreated() {
        return timeCreated;
    }

    protected CSTDServer getServer() {
        return server;
    }

    protected void sendTCP(Packet packet) {
        getSocket().sendTCP(packet);
    }
}
