package com.nicktoony.engine.packets.connection;

import com.nicktoony.engine.packets.Packet;

/**
 * Created by nick on 17/07/15.
 */
public class ConnectPacket extends Packet {
    public String password = "LOL";
    public String name = "";

    public ConnectPacket() {
    }

    public ConnectPacket(String name) {
        this.name = name;
    }
}
