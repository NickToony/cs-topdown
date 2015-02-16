package com.nick.ant.towerdefense.networking.client;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.nick.ant.towerdefense.networking.packets.Packet;
import com.nick.ant.towerdefense.networking.packets.PacketDefinition;
import com.nick.ant.towerdefense.networking.packets.PlayerCreatePacket;
import com.nick.ant.towerdefense.networking.packets.PlayerMovePacket;
import com.nick.ant.towerdefense.networking.server.CSTDServer;
import com.nick.ant.towerdefense.renderables.entities.players.Player;
import com.nick.ant.towerdefense.rooms.RoomGameRender;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nick on 11/02/2015.
 */
public class CSClient {

    private String ip;
    private int port;
    private Client client;
    private RoomGameRender roomGame;
    private List<PlayerWrapper> players = new ArrayList();

    private class PlayerWrapper {
        public Player player;
        public int id;

        public PlayerWrapper(Player player, int id) {
            this.player = player;
            this.id = id;
        }
    }

    public CSClient(CSTDServer server) {
        this.ip = "127.0.0.1";
        this.port = server.getConfig().getPort();
        setup();
    }

    public CSClient(String ip, int port) {
        this.ip = ip;
        this.port = port;
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

    public boolean connect(RoomGameRender roomGame) {
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
            } else {
                Player player = roomGame.createPlayer();
                player.setX(packet.x);
                player.setY(packet.y);
                player.setMultiplayer(false);
                players.add(new PlayerWrapper(player, packet.id));
            }
            return;
        }

        if (object instanceof PlayerMovePacket) {
            PlayerMovePacket packet = (PlayerMovePacket) object;
            Player player = findPlayer(packet.id);
            if (player != null) {
                player.setMovement(packet.moveUp, packet.moveRight, packet.moveDown, packet.moveLeft);
            }
        }
    }

    private Player findPlayer(int id) {
        for (PlayerWrapper wrapper : players) {
            if (wrapper.id == id) {
                return wrapper.player;
            }
        }
        return null;
    }
}
