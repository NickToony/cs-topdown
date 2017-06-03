package com.nicktoony.cstopdown.networking.packets.game;

import com.nicktoony.engine.packets.Packet;

/**
 * Created by Nick on 03/01/2016.
 */
public class DestroyPlayerPacket extends Packet {
    public int id;
    public int killer = -1;
}
