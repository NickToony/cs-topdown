package com.nicktoony.cstopdown.networking.server;

import com.nicktoony.cstopdown.networking.packets.Packet;
import com.nicktoony.cstopdown.networking.packets.connection.AcceptPacket;
import com.nicktoony.cstopdown.networking.packets.connection.ConnectPacket;
import com.nicktoony.cstopdown.networking.packets.connection.LoadedPacket;
import com.nicktoony.cstopdown.networking.packets.connection.RejectPacket;
import com.nicktoony.cstopdown.networking.packets.game.CreatePlayerPacket;
import com.nicktoony.cstopdown.networking.packets.player.PlayerInputPacket;
import com.nicktoony.cstopdown.networking.packets.player.PlayerUpdatePacket;
import com.nicktoony.cstopdown.rooms.game.entities.players.Player;

/**
 * Created by nick on 19/07/15.
 */
public abstract class SBClient {

    public enum STATE {
        INIT,
        CONNECTING,
        LOADING,
        SPECTATE,
        ALIVE
    }

    private STATE state = STATE.INIT;
    private SBServer server;
    private Player player;
    private int tempCountdown = 200;
    private int id;
    private int lastUpdate = 0;

    public abstract void sendPacket(Packet packet);
    public abstract void close();

    public SBClient(SBServer server) {
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

            case ALIVE:
                handleAliveMessages(packet);
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

    private void handleLoadingMessages(Packet packet) {
        if (packet instanceof LoadedPacket) {
            this.state = STATE.SPECTATE;
            fullUpdate();
        }
    }

    private void handleAliveMessages(Packet packet) {
        if (packet instanceof PlayerInputPacket) {
            PlayerInputPacket castPacket = (PlayerInputPacket) packet;
            player.setMovement(castPacket.moveUp, castPacket.moveRight,
                    castPacket.moveDown, castPacket.moveLeft);
            player.setDirection(castPacket.direction);
            player.setX(castPacket.x);
            player.setY(castPacket.y);
        }
    }

    public void update() {
        if (state == STATE.SPECTATE) {
            if (tempCountdown > 0) {
                tempCountdown -= 1;
            } else {
                state = STATE.ALIVE;
                player = server.getGame().createPlayer(this.id, 50, 50);

                CreatePlayerPacket createPlayer = new CreatePlayerPacket();
                createPlayer.x = player.getX();
                createPlayer.y = player.getY();
                createPlayer.id = this.id;
                server.sendToAll(createPlayer);
            }
        } else if (state == STATE.ALIVE) {
            if (lastUpdate <= 0) {
                lastUpdate = 1000/server.getConfig().sv_tickrate;

                PlayerUpdatePacket packet = new PlayerUpdatePacket();
                packet.x = player.getX();
                packet.y = player.getY();
                packet.direction = player.getDirection();
                packet.id = id;
                server.sendToAll(packet);

            } else {
                lastUpdate -= 1;
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

    public Player getPlayer() {
        return player;
    }

    public void fullUpdate() {
        for (SBClient client : server.getClients()) {
            if (client != this && client.getState() == STATE.ALIVE) {
                CreatePlayerPacket packet = new CreatePlayerPacket();
                packet.id = client.getId();
                packet.x = client.getPlayer().getX();
                packet.y = client.getPlayer().getY();
                sendPacket(packet);
            }
        }
    }
}
