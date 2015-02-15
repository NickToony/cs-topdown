package com.nick.ant.towerdefense.networking.client;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.nick.ant.towerdefense.networking.packets.Packet;
import com.nick.ant.towerdefense.networking.packets.PacketDefinition;
import com.nick.ant.towerdefense.networking.packets.PlayerCreatePacket;
import com.nick.ant.towerdefense.networking.server.CSTDServer;
import com.nick.ant.towerdefense.renderables.entities.players.Player;
import com.nick.ant.towerdefense.rooms.RoomGame;

import java.io.IOException;

/**
 * Created by Nick on 11/02/2015.
 */
public class CSClient {

    private String ip;
    private int port;
    private Client client;
    private RoomGame roomGame;

    public CSClient(CSTDServer server) {
        this.ip = "127.0.0.1";
        this.port = server.getConfig().getPort();
        setup();
    }

    private void setup() {
        client = new Client();
        PacketDefinition.registerClasses(client.getKryo());
        client.start();
    }

    public void sendPacket(Packet packet) {
        if (client != null && client.isConnected()) {
            client.sendTCP(packet);
        }
    }

    public boolean connect(RoomGame roomGame) {
        try {
            client.connect(5000, ip, port);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        this.roomGame = roomGame;
        client.addListener(new Listener() {
            @Override
            public void received(Connection connection, Object object) {
                handleReceivedMessage(object);
            }
        });

        return true;
    }

    private void handleReceivedMessage(Object object) {
        if (object instanceof PlayerCreatePacket) {
            PlayerCreatePacket packet = (PlayerCreatePacket) object;
            if (packet.mine) {
                Player player = roomGame.createUserPlayer();
                player.setX(packet.x);
                player.setY(packet.y);
            }
        }
    }
}
