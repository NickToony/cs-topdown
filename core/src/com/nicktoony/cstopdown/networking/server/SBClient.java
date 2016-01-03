package com.nicktoony.cstopdown.networking.server;

import com.nicktoony.cstopdown.networking.packets.AcceptPacket;
import com.nicktoony.cstopdown.networking.packets.ConnectPacket;
import com.nicktoony.cstopdown.networking.packets.Packet;
import com.nicktoony.cstopdown.networking.packets.RejectPacket;

/**
 * Created by nick on 19/07/15.
 */
public abstract class SBClient {

    enum STATE {
        INIT,
        CONNECTED
    }

    private STATE state = STATE.INIT;

    public abstract void sendPacket(Packet packet);
    public abstract void close();

    public void handleReceivedMessage(Packet packet) {
        switch (state) {
            case INIT:
                handleConnectingMessages(packet);
                break;

        }
    }

    private void handleConnectingMessages(Packet packet) {
        if (packet instanceof ConnectPacket) {
            // TODO: connect request validation

            // And if successful..
            if (true) {
                this.sendPacket(new AcceptPacket());
                this.state = STATE.CONNECTED;
            } else {
                this.sendPacket(new RejectPacket());
                this.close();
            }
        }
    }
}
