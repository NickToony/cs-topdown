package com.nicktoony.cstopdown.networking.client;

import com.nicktoony.cstopdown.networking.packets.Packet;
import com.nicktoony.cstopdown.networking.server.SBClient;
import com.nicktoony.cstopdown.networking.server.SBServer;

/**
 * Created by nick on 19/07/15.
 */
public class SBLocalSocket extends SBSocket {

    private SBServer server;
    private SBClient client;

    public SBLocalSocket(SBServer server) {
        super("", 0); // we don't care about port/ip

        this.server = server;
        this.client = new SBClient() {
            @Override
            public void sendPacket(Packet packet) {
                for (SBSocketListener listener : listeners) {
                    listener.onMessage(SBLocalSocket.this, packet);
                }
            }

            @Override
            public void close() {
                for (SBSocketListener listener : listeners) {
                    listener.onClose(SBLocalSocket.this);
                }
            }
        };
    }

    @Override
    public boolean open() {
        for (SBSocketListener listener : listeners) {
            listener.onOpen(SBLocalSocket.this);
        }

        server.handleClientConnected(client);
        return true;
    }

    @Override
    public boolean close() {
        for (SBSocketListener listener : listeners) {
            listener.onClose(SBLocalSocket.this);
        }

        server.handleClientDisconnected(client);
        return true;
    }

    @Override
    protected boolean sendPacket(Packet packet) {
        server.handleReceivedMessage(client, packet);
        return true;
    }
}
