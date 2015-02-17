package com.nick.ant.towerdefense.networking.packets.player;

import com.nick.ant.towerdefense.networking.packets.Packet;

/**
 * Created by Nick on 11/02/2015.
 */
public class PlayerPositionPacket extends Packet {
    public int id;
    public float direction;
    public int x;
    public int y;

    public PlayerPositionPacket() {
    }

    public PlayerPositionPacket(int id, int x, int y, float direction) {
        this.id = id;
        this.y = y;
        this.x = x;
        this.direction = direction;
    }
}
