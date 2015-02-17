package com.nick.ant.towerdefense.networking.packets.player;

import com.nick.ant.towerdefense.networking.packets.Packet;

/**
 * Created by Nick on 17/02/2015.
 */
public class PlayerTorchPacket extends Packet{
    public boolean torch;
    public int id;

    public PlayerTorchPacket(boolean torch) {
        this.torch = torch;
    }

    public PlayerTorchPacket(boolean torch, int id) {
        this.torch = torch;
        this.id = id;
    }

    public PlayerTorchPacket() {

    }
}
