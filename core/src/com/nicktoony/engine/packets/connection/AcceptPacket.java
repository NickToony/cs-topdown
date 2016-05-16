package com.nicktoony.engine.packets.connection;

import com.nicktoony.engine.config.ServerConfig;
import com.nicktoony.engine.packets.Packet;

/**
 * Created by Nick on 03/01/2016.
 */
public class AcceptPacket extends Packet {

    // Blank required for JSON reading
    public AcceptPacket() {

    }

    public AcceptPacket(ServerConfig serverConfig, int id) {
        this.serverConfig = serverConfig;
        this.id = id;
    }

    public ServerConfig serverConfig;
    public int id;
}
