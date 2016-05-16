package com.nicktoony.cstopdown.networking.packets.player;

import com.nicktoony.engine.packets.TimestampedPacket;

/**
 * Created by Nick on 17/03/2016.
 */
public class PlayerToggleLight extends TimestampedPacket {
    public boolean light;
    public int id;
}
