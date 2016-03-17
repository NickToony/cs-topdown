package com.nicktoony.cstopdown.networking.packets.player;

import com.nicktoony.cstopdown.networking.packets.Packet;
import com.nicktoony.cstopdown.networking.packets.TimestampedPacket;

/**
 * Created by Nick on 14/03/2016.
 *
 * Sends the players key presses to server
 */
public class PlayerInputPacket extends TimestampedPacket {
    public boolean moveLeft = false;
    public boolean moveRight = false;
    public boolean moveUp = false;
    public boolean moveDown = false;
    public float direction = 0;
    public float x = 0;
    public float y = 0;

    public PlayerInputPacket() {
    }
}
