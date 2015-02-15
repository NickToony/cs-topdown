package com.nick.ant.towerdefense.networking.packets;

/**
 * Created by Nick on 15/02/2015.
 */
public class PlayerCreatePacket extends Packet {
    public boolean mine;
    public float x;
    public float y;

    public PlayerCreatePacket() {
    }

    public PlayerCreatePacket(boolean mine, float x, float y) {
        this.mine = mine;
        this.x = x;
        this.y = y;
    }
}
