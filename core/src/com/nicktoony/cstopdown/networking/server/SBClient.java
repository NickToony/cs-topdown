package com.nicktoony.cstopdown.networking.server;

import com.nicktoony.cstopdown.networking.packets.Packet;
import com.nicktoony.cstopdown.networking.packets.connection.AcceptPacket;
import com.nicktoony.cstopdown.networking.packets.connection.ConnectPacket;
import com.nicktoony.cstopdown.networking.packets.connection.RejectPacket;
import com.nicktoony.cstopdown.networking.packets.game.CreatePlayer;
import com.nicktoony.cstopdown.rooms.game.entities.players.Player;

/**
 * Created by nick on 19/07/15.
 */
public abstract class SBClient {

    public enum STATE {
        INIT,
        CONNECTING,
        SPECTATE,
        ALIVE
    }

    private STATE state = STATE.INIT;
    private SBServer server;
    private Player player;
    private int tempCountdown = 200;
    private int id;

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

        }
    }

    private void handleConnectingMessages(Packet packet) {
        if (packet instanceof ConnectPacket) {
            // TODO: connect request validation

            // And if successful..
            if (true) {
                this.sendPacket(new AcceptPacket(server.getConfig(), this.id));
                this.state = STATE.SPECTATE;
            } else {
                this.sendPacket(new RejectPacket());
                this.close();
            }
        }
    }

    public void update() {
        if (state == STATE.SPECTATE) {
            if (tempCountdown > 0) {
                tempCountdown -= 1;
            } else {
                state = STATE.ALIVE;
                player = server.getGame().createPlayer(this.id, 50, 50);

                CreatePlayer createPlayer = new CreatePlayer();
                createPlayer.x = player.getX();
                createPlayer.y = player.getY();
                createPlayer.id = this.id;
                server.sendToAll(createPlayer);
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
}
