package com.nicktoony.cstopdown.networking;

import com.nicktoony.cstopdown.networking.server.CSServer;
import com.nicktoony.cstopdown.networking.server.CSServerClientHandler;
import com.nicktoony.engine.networking.client.LocalClientSocket;
import com.nicktoony.engine.networking.server.ServerClientHandler;
import com.nicktoony.engine.packets.Packet;
import com.nicktoony.engine.packets.PacketDefinitions;
import com.nicktoony.engine.packets.TimestampedPacket;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Nick on 16/05/2016.
 */
public class CSLocalClientSocket extends LocalClientSocket<CSServer> {

    private long timer = System.currentTimeMillis();
    Map<String, Integer> packetCount = new HashMap<String, Integer>();

    public CSLocalClientSocket(CSServer server) {
        super(server);
    }

    @Override
    protected ServerClientHandler createClientHandler() {
        return new CSServerClientHandler(server) {
            @Override
            public void sendPacket(Packet packet) {
                packet.prepareMessageId();
                if (packet instanceof TimestampedPacket) {
                    ((TimestampedPacket) packet).setTimestamp(getTimestamp());
                }



                notifyMessage(CSLocalClientSocket.this, (Packet) getJson()
                        .fromJson((Class) PacketDefinitions.PACKET_DEFITIONS
                                .get(packet.getMessage_id()), getJson().toJson(packet)));

//                if (!packetCount.containsKey(packet.getClass().getCanonicalName())) {
//                    packetCount.put(packet.getClass().getCanonicalName(), 1);
//                } else {
//                    packetCount.put(packet.getClass().getCanonicalName(),
//                             packetCount.get(packet.getClass().getCanonicalName()) + 1);
//                }
//
//                if (timer + (1000 * 10) < System.currentTimeMillis()) {
//                    timer = System.currentTimeMillis();
//                    System.out.println("Past 10 seconds of packets to SERVER:");
//                    for (Map.Entry<String, Integer> entry : packetCount.entrySet()) {
//                        System.out.println(entry.getKey() + ": " + entry.getValue() + " (" + (entry.getValue()/10) + ")");
//                    }
//                    packetCount.clear();
//                }
            }

            @Override
            public void close() {
                notifyClose(CSLocalClientSocket.this);
            }
        };
    }
}
