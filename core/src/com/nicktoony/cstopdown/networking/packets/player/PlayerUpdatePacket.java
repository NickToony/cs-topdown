package com.nicktoony.cstopdown.networking.packets.player;

import com.nicktoony.engine.packets.Packet;

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
    public boolean shooting;
    public boolean reloading;
    public boolean zoom;
    public int health = -1;
    public int lastProcessed;
}
