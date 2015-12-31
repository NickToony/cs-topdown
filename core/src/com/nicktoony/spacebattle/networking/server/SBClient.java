package com.nicktoony.spacebattle.networking.server;

import com.nicktoony.spacebattle.networking.packets.Packet;

/**
 * Created by nick on 19/07/15.
 */
public abstract class SBClient {

    public abstract void sendPacket(Packet packet);

    public abstract void close();

}
