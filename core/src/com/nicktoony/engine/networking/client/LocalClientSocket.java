package com.nicktoony.engine.networking.client;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;
import com.nicktoony.engine.networking.server.Server;
import com.nicktoony.engine.networking.server.ServerClientHandler;
import com.nicktoony.engine.packets.Packet;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by nick on 19/07/15.
 */
public abstract class LocalClientSocket<T extends Server> extends ClientSocket {

    protected T server;
    private ServerClientHandler client;
    private static Json json;
    private long timer = System.currentTimeMillis();
    Map<String, Integer> packetCount = new HashMap<String, Integer>();

    public LocalClientSocket(T server) {
        super("", 0); // we don't care about port/ip

        this.server = server;
        this.client = createClientHandler();
    }

    protected abstract ServerClientHandler createClientHandler();

    @Override
    public boolean open() {
        notifyOpen(LocalClientSocket.this);

        server.notifyClientConnected(client);
        return true;
    }

    @Override
    public boolean close() {
        notifyClose(LocalClientSocket.this);

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

//        if (!packetCount.containsKey(packet.getClass().getCanonicalName())) {
//            packetCount.put(packet.getClass().getCanonicalName(), 1);
//        } else {
//            packetCount.put(packet.getClass().getCanonicalName(),
//                    packetCount.get(packet.getClass().getCanonicalName()) + 1);
//        }
//
//        if (timer + (1000 * 10) < System.currentTimeMillis()) {
//            timer = System.currentTimeMillis();
//            System.out.println("Past 10 seconds of packets from SERVER:");
//            for (Map.Entry<String, Integer> entry : packetCount.entrySet()) {
//                System.out.println(entry.getKey() + ": " + entry.getValue() + " (" + (entry.getValue()/10) + ")");
//            }
//            packetCount.clear();
//        }
        return true;
    }

    protected Json getJson() {
        if (json == null) {
            json = new Json();
            json.setTypeName(null);
            json.setUsePrototypes(false);
            json.setIgnoreUnknownFields(true);
            json.setOutputType(JsonWriter.OutputType.json);
        }
        return json;
    }
}
