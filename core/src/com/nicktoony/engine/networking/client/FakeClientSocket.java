package com.nicktoony.engine.networking.client;

import com.nicktoony.engine.config.ServerConfig;
import com.nicktoony.engine.packets.Packet;

/**
 * Created by Nick on 14/03/2016.
 */
public class FakeClientSocket extends ClientSocket {
    public FakeClientSocket(ServerConfig config) {
        super("", 0);

        setServerConfig(config);
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
