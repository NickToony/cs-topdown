package com.nicktoony.cstopdown.networking.packets.player;

import com.nicktoony.cstopdown.networking.packets.Packet;
import com.nicktoony.cstopdown.networking.packets.TimestampedPacket;

/**
 * Created by Nick on 14/03/2016.
 */
public class PlayerUpdatePacket extends TimestampedPacket {
    public float x;
    public float y;
    public float direction;
    public int id;
}
