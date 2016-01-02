package com.nicktoony.cstopdown.server;

import com.nicktoony.cstopdown.networking.packets.Packet;
import com.nicktoony.cstopdown.networking.server.SBClient;
import org.java_websocket.WebSocket;

/**
 * Created by nick on 19/07/15.
 */
public class SBWebClient extends SBClient {

    private WebSocket socket;

    public SBWebClient(WebSocket socket) {
        this.socket = socket;
    }

    @Override
    public void sendPacket(Packet packet) {
        // do something
    }

    @Override
    public void close() {
        socket.close();
    }
}
