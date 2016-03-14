package com.nicktoony.cstopdown.networking.server;

import com.nicktoony.cstopdown.networking.client.SBSocket;
import com.nicktoony.cstopdown.networking.packets.Packet;

/**
 * Created by Nick on 14/03/2016.
 */
public class SBFakeSocket extends SBSocket {
    public SBFakeSocket() {
        super("", 0);
    }

    @Override
    public boolean open() {
        return true;
    }

    @Override
    public boolean close() {
        return true;
    }

    @Override
    protected boolean sendPacket(Packet packet) {
        return true;
    }
}
