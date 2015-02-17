package com.nick.ant.towerdefense.networking.server;

import com.badlogic.gdx.math.Vector2;
import com.esotericsoftware.kryonet.Connection;
import com.nick.ant.towerdefense.networking.packets.*;
import com.nick.ant.towerdefense.renderables.entities.players.Player;

/**
 * Created by Nick on 07/02/2015.
 */
public class ServerClient {
    private Connection socket;
    private CSTDServer server;
    private long timeCreated = System.currentTimeMillis();
    private Player player;
    private int id;
    private boolean ready = false;

    private long lastUpdate = 0;
    private final long UPDATE_RATE = 1000;

    public ServerClient(int id, CSTDServer server, Connection socket) {
        this.id = id;
        this.socket = socket;
        this.server = server;
    }

    public void handleReceivedMessage(Object object) {
        if (object instanceof PlayerMovePacket) {
            PlayerMovePacket movePacket = (PlayerMovePacket) object;

            if (player != null) {
                player.setMovement(movePacket.moveUp, movePacket.moveRight, movePacket.moveDown, movePacket.moveLeft);
            }

            movePacket.id = id;
            sendToOthers(movePacket);

            return;
        }

        if (object instanceof ClientReadyPacket) {
            if (!ready) {
                setReady(true);
            }
            return;
        }

        if (object instanceof PlayerPositionPacket) {
            PlayerPositionPacket packet = (PlayerPositionPacket) object;
            if (Vector2.dst(player.getX(), player.getY(), packet.x, packet.y) < 32) {
                player.setX(packet.x);
                player.setY(packet.y);
            } else {
                socket.sendTCP(new PlayerPositionPacket(id, Math.round(player.getX()), Math.round(player.getY()), 0));
            }
        }
    }

    public Connection getSocket() {
        return socket;
    }

    public void step() {
        if (player == null && timeCreated + (1000 * 5) <= System.currentTimeMillis()) {
            player = server.getRoomGame().createPlayer();
            player.setX(128);
            player.setY(128);
            // Create self
            socket.sendTCP(new PlayerCreatePacket(true, player.getX(), player.getY(), id));

            // Create my player on other clients
            sendToOthers(new PlayerCreatePacket(false, player.getX(), player.getY(), id));
        }

        if (System.currentTimeMillis() > lastUpdate + UPDATE_RATE && player != null) {
            lastUpdate = System.currentTimeMillis();

            sendToOthers(new PlayerPositionPacket(id, Math.round(player.getX()), Math.round(player.getY()), 0));
        }
    }

    private void sendToAll(Packet packet) {
        socket.sendTCP(packet);
        sendToOthers(packet);
    }

    public int getId() {
        return id;
    }

    private void sendToOthers(Packet packet) {
        server.sendToOthers(packet, this);
    }

    public void updateNewPlayer(Connection connection) {
        if (player != null) {
            connection.sendTCP(new PlayerCreatePacket(false, player.getX(), player.getY(), id));
        }
    }

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
        if (ready) {
            server.updateNewClient(socket);
        }
    }
}
