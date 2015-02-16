package com.nick.ant.towerdefense.networking.packets;

/**
 * Created by Nick on 11/02/2015.
 */
public class PlayerMovePacket extends Packet {
    public boolean moveLeft;
    public boolean moveRight;
    public boolean moveUp;
    public boolean moveDown;
    public int id;

    public PlayerMovePacket() {
    }

    public PlayerMovePacket(boolean moveLeft, boolean moveRight, boolean moveUp, boolean moveDown) {
        this.moveLeft = moveLeft;
        this.moveRight = moveRight;
        this.moveUp = moveUp;
        this.moveDown = moveDown;
    }

    public PlayerMovePacket(boolean moveLeft, boolean moveRight, boolean moveUp, boolean moveDown, int id) {
        this.moveLeft = moveLeft;
        this.moveRight = moveRight;
        this.moveUp = moveUp;
        this.moveDown = moveDown;
        this.id = id;
    }


}
