package com.nicktoony.cstopdown.networking.server;

import com.nicktoony.cstopdown.networking.packets.Packet;
import com.nicktoony.cstopdown.networking.packets.connection.AcceptPacket;

/**
 * Created by Nick on 24/03/2016.
 */
public class SBBotClient extends SBClient {
    public SBBotClient(SBServer server) {
        super(server);
    }

    @Override
    public void sendPacket(Packet packet) {

    }

    @Override
    public void close() {

    }

    @Override
    public boolean isBot() {
        return true;
    }
}
