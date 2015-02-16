package com.nick.ant.towerdefense.networking.server;

import com.esotericsoftware.kryonet.Connection;
import com.nick.ant.towerdefense.networking.packets.Packet;
import com.nick.ant.towerdefense.networking.packets.PlayerCreatePacket;
import com.nick.ant.towerdefense.networking.packets.PlayerMovePacket;
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

    public ServerClient(int id, CSTDServer server, Connection socket) {
        this.id = id;
        this.socket = socket;
        this.server = server;
    }

    public void handleReceivedMessage(Object object) {
        if (object instanceof PlayerMovePacket) {
            PlayerMovePacket movePacket = (PlayerMovePacket) object;
            System.out.println("Received move packet");
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
            socket.sendTCP(new PlayerCreatePacket(true, player.getX(), player.getY()));
        }
    }

    public int getId() {
        return id;
    }

    private void sendToOthers(Packet packet) {
        server.sendToOthers(packet, this);
    }
}
