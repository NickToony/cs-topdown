package com.nicktoony.cstopdown.networking.server;

import com.nicktoony.cstopdown.networking.packets.Packet;

/**
 * Created by nick on 19/07/15.
 */
public abstract class SBClient {

    public abstract void sendPacket(Packet packet);

    public abstract void close();

}
