package com.nick.ant.towerdefense.networking.server;

import com.badlogic.gdx.math.Vector2;
import com.esotericsoftware.kryonet.Connection;
import com.nick.ant.towerdefense.networking.packets.ClientLoadedPacket;
import com.nick.ant.towerdefense.networking.packets.Packet;
import com.nick.ant.towerdefense.networking.packets.player.*;
import com.nick.ant.towerdefense.renderables.entities.players.Player;

/**
 * Created by Nick on 07/02/2015.
 */
public class ServerClientPlayer extends ServerClient {
    public static final int STATE_LOADING = 0;
    public static final int STATE_INGAME = 1;

    private Player player;
    private int state;
    private long lastUpdate = 0;

    public ServerClientPlayer(int id, CSTDServer server, Connection socket) {
        super(id, server, socket);
    }

    public boolean handleReceivedMessage(Object packet) {
        if (packet instanceof PlayerMovePacket) {
            return handleMovePacket((PlayerMovePacket) packet);
        }

        if (packet instanceof ClientLoadedPacket) {
            return handleLoadedPacket((ClientLoadedPacket) packet);
        }

        if (packet instanceof PlayerPositionPacket) {
            return handlePositionPacket((PlayerPositionPacket) packet);
        }

        if (packet instanceof PlayerTorchPacket) {
            return handleTorchPacket((PlayerTorchPacket) packet);
        }

        if (packet instanceof PlayerShootPacket) {
            return handleShootPacket((PlayerShootPacket) packet);
        }

        return false;
    }

    private boolean handleShootPacket(PlayerShootPacket packet) {
        player.setShooting(packet.shoot);
        packet.id = getId();
        sendToOthers(packet);
        return true;
    }

    private boolean handleTorchPacket(PlayerTorchPacket packet) {
        player.setLightOn(packet.torch);
        packet.id = getId();
        sendToOthers(packet);
        return true;
    }

    private boolean handlePositionPacket(PlayerPositionPacket packet) {
        if (Vector2.dst(player.getX(), player.getY(), packet.x, packet.y) < 32) {
            player.setX(packet.x);
            player.setY(packet.y);
            player.setDirection(packet.direction);
        } else {
            sendTCP(new PlayerPositionPacket(getId(), Math.round(player.getX()), Math.round(player.getY()), player.getDirection()));
        }
        return true;
    }

    private boolean handleLoadedPacket(ClientLoadedPacket packet) {
        setState(STATE_INGAME);
        return true;
    }

    private boolean handleMovePacket(PlayerMovePacket movePacket) {
        if (player != null) {
            player.setMovement(movePacket.moveUp, movePacket.moveRight, movePacket.moveDown, movePacket.moveLeft);
        }

        movePacket.id = getId();
        sendToOthers(movePacket);

        return true;
    }

    public void step() {
        if (player == null && getTimeCreated() + (1000 * 5) <= System.currentTimeMillis()) {
            player = getServer().getRoomGame().createPlayer();
            player.setX(128);
            player.setY(128);
            // Create self
            sendTCP(new PlayerCreatePacket(true, player.getX(), player.getY(), getId()));

            // Create my player on other clients
            sendToOthers(new PlayerCreatePacket(false, player.getX(), player.getY(), getId()));
        }

        if (System.currentTimeMillis() > lastUpdate + getServer().getConfig().mp_player_update_rate && player != null) {
            lastUpdate = System.currentTimeMillis();

            sendToOthers(new PlayerPositionPacket(getId(), Math.round(player.getX()),
                    Math.round(player.getY()), player.getDirection()));
        }
    }

    public void updateNewPlayer(Connection connection) {
        if (player != null) {
            connection.sendTCP(new PlayerCreatePacket(false, player.getX(), player.getY(), getId()));
            connection.sendTCP(new PlayerMovePacket(player.getMoveLeft(), player.getMoveRight(), player.getMoveUp(), player.getMoveDown(), getId()));
            connection.sendTCP(new PlayerShootPacket(player.getShooting(), getId()));
            connection.sendTCP(new PlayerTorchPacket(player.isLightOn(), getId()));
        }
    }

    public void setState(int state) {
        this.state = state;
        if (state == STATE_INGAME) {
            getServer().updateNewClient(getSocket());
        }
    }

    public int getState() {
        return state;
    }
}
