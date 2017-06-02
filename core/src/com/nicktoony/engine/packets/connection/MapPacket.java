package com.nicktoony.engine.packets.connection;

import com.nicktoony.engine.packets.Packet;

/**
 * Created by Nick on 03/01/2016.
 */
public class MapPacket extends Packet {
    public String map;
    public String tilesets[];
    public int[][][] pixels;
}
