package com.nicktoony.cstopdown.networking.packets.player;

import com.nicktoony.engine.packets.Packet;

/**
 * Created by Nick on 14/03/2016.
 *
 * Sends the players key presses to server
 */
public class PlayerInputPacket extends Packet {
    public boolean moveLeft = false;
    public boolean moveRight = false;
    public boolean moveUp = false;
    public boolean moveDown = false;
    public float direction = 0;
    public float x = 0;
    public float y = 0;
    public boolean shoot = false;
    public boolean reload = false;
    public boolean zoom = false;
    public int id;
    public int number;

    public PlayerInputPacket() {
    }
}
