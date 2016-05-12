package com.nicktoony.cstopdown.networking.client;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;
import com.nicktoony.cstopdown.networking.packets.Packet;
import com.nicktoony.cstopdown.networking.packets.PacketDefinitions;
import com.nicktoony.cstopdown.networking.server.SBClient;
import com.nicktoony.cstopdown.networking.server.SBServer;

/**
 * Created by nick on 19/07/15.
 */
public class SBLocalSocket extends SBSocket {

    private SBServer server;
    private SBClient client;
    private static Json json;

    public SBLocalSocket(SBServer server) {
        super("", 0); // we don't care about port/ip

        this.server = server;
        this.client = new SBClient(server) {
            @Override
            public void sendPacket(Packet packet) {
                packet.prepareMessageId();
                notifyMessage(SBLocalSocket.this, (Packet) getJson()
                        .fromJson((Class) PacketDefinitions.PACKET_DEFITIONS
                                .get(packet.getMessage_id()), getJson().toJson(packet)));
            }

            @Override
            public void close() {
                notifyClose(SBLocalSocket.this);
            }
        };
    }

    @Override
    public boolean open() {
        notifyOpen(SBLocalSocket.this);

        server.notifyClientConnected(client);
        return true;
    }

    @Override
    public boolean close() {
        notifyClose(SBLocalSocket.this);

        server.notifyClientDisconnected(client);
        return true;
    }

    @Override
    protected boolean sendPacket(Packet packet) {
//        packet.prepareMessageId();
//        server.notifyClientMessage(client, (Packet) getJson()
//                .fromJson((Class) PacketDefinitions.PACKET_DEFITIONS
//                        .get(packet.getMessage_id()), getJson().toJson(packet)));
        server.notifyClientMessage(client, packet);
        return true;
    }

    private Json getJson() {
        if (json == null) {
            json = new Json();
            json.setTypeName(null);
            json.setUsePrototypes(false);
            json.setIgnoreUnknownFields(true);
            json.setOutputType(JsonWriter.OutputType.json);
        }
        return json;
    }

    public SBServer getServer() {
        return server;
    }
}
