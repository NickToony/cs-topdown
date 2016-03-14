package com.nicktoony.cstopdown.networking.packets.connection;

import com.nicktoony.cstopdown.config.ServerConfig;
import com.nicktoony.cstopdown.networking.packets.Packet;

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
