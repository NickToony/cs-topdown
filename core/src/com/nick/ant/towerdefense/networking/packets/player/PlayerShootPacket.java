package com.nick.ant.towerdefense.networking.packets.player;

import com.nick.ant.towerdefense.networking.packets.Packet;

/**
 * Created by Nick on 17/02/2015.
 */
public class PlayerShootPacket extends Packet{
    public boolean shoot;
    public int id;

    public PlayerShootPacket(boolean shoot) {
        this.shoot = shoot;
    }

    public PlayerShootPacket(boolean shoot, int id) {
        this.shoot = shoot;
        this.id = id;
    }

    public PlayerShootPacket() {

    }
}
