package com.nicktoony.cstopdown.networking;

import com.nicktoony.cstopdown.networking.server.CSServer;
import com.nicktoony.cstopdown.networking.server.CSServerClientHandler;
import com.nicktoony.engine.networking.client.LocalClientSocket;
import com.nicktoony.engine.networking.server.ServerClientHandler;
import com.nicktoony.engine.packets.Packet;
import com.nicktoony.engine.packets.PacketDefinitions;

/**
 * Created by Nick on 16/05/2016.
 */
public class CSLocalClientSocket extends LocalClientSocket<CSServer> {

    public CSLocalClientSocket(CSServer server) {
        super(server);
    }

    @Override
    protected ServerClientHandler createClientHandler() {
        return new CSServerClientHandler(server) {
            @Override
            public void sendPacket(Packet packet) {
                packet.prepareMessageId();
                notifyMessage(CSLocalClientSocket.this, (Packet) getJson()
                        .fromJson((Class) PacketDefinitions.PACKET_DEFITIONS
                                .get(packet.getMessage_id()), getJson().toJson(packet)));
            }

            @Override
            public void close() {
                notifyClose(CSLocalClientSocket.this);
            }
        };
    }
}
