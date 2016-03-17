package com.nicktoony.cstopdown.networking.packets.player;

import com.nicktoony.cstopdown.networking.packets.Packet;

/**
 * Created by Nick on 14/03/2016.
 */
public class PlayerUpdatePacket extends Packet {
    public float x;
    public float y;
    public float direction;
    public int id;
    public boolean moveUp;
    public boolean moveDown;
    public boolean moveLeft;
    public boolean moveRight;
}
