package com.nick.ant.towerdefense.networking.server;

import com.badlogic.gdx.math.Vector2;
import com.esotericsoftware.kryonet.Connection;
import com.nick.ant.towerdefense.networking.packets.*;
import com.nick.ant.towerdefense.networking.packets.player.*;
import com.nick.ant.towerdefense.renderables.entities.players.Player;

/**
 * Created by Nick on 07/02/2015.
 */
public class ServerClient {
    public static final int STATE_LOADING = 0;
    public static final int STATE_INGAME = 1;

    private Connection socket;
    private CSTDServer server;
    private long timeCreated = System.currentTimeMillis();
    private Player player;
    private int id;
    private int state;

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

        if (object instanceof ClientLoadedPacket) {
            setState(STATE_INGAME);
            return;
        }

        if (object instanceof PlayerPositionPacket) {
            PlayerPositionPacket packet = (PlayerPositionPacket) object;
            if (Vector2.dst(player.getX(), player.getY(), packet.x, packet.y) < 32) {
                player.setX(packet.x);
                player.setY(packet.y);
                player.setDirection(packet.direction);
            } else {
                socket.sendTCP(new PlayerPositionPacket(id, Math.round(player.getX()), Math.round(player.getY()), player.getDirection()));
            }
            return;
        }

        if (object instanceof PlayerTorchPacket) {
            PlayerTorchPacket packet = (PlayerTorchPacket) object;
            player.setLightOn(packet.torch);
            packet.id = id;
            sendToOthers(packet);
            return;
        }

        if (object instanceof PlayerShootPacket) {
            PlayerShootPacket packet = (PlayerShootPacket) object;
            player.setShooting(packet.shoot);
            packet.id = id;
            sendToOthers(packet);
            return;
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

            sendToOthers(new PlayerPositionPacket(id, Math.round(player.getX()), Math.round(player.getY()), player.getDirection()));
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
            connection.sendTCP(new PlayerMovePacket(player.getMoveLeft(), player.getMoveRight(), player.getMoveUp(), player.getMoveDown(), id));
            connection.sendTCP(new PlayerShootPacket(player.getShooting(), id));
            connection.sendTCP(new PlayerTorchPacket(player.isLightOn(), id));
        }
    }

    public void setState(int state) {
        this.state = state;
        if (state == STATE_INGAME) {
            server.updateNewClient(socket);
        }
    }

    public int getState() {
        return state;
    }
}
