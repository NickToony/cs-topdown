package com.nick.ant.towerdefense.networking.client;

import com.esotericsoftware.kryonet.Client;
import com.nick.ant.towerdefense.networking.packets.Packet;
import com.nick.ant.towerdefense.networking.packets.PacketDefinition;
import com.nick.ant.towerdefense.networking.server.CSTDServer;

import java.io.IOException;

/**
 * Created by Nick on 11/02/2015.
 */
public class CSClient {

    private String ip;
    private int port;
    private Client client;

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

    public boolean connect() {
        try {
            client.connect(5000, ip, port);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }
}
