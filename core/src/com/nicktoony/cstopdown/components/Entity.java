package com.nicktoony.cstopdown.components;

/**
 * Created by nick on 15/07/15.
 */
public abstract class Entity<R extends Room> implements Renderable {

    private Room room;
    protected float x;
    protected float y;
    protected float direction;

    public void setRoom(Room room) {
        this.room = room;
    }

    public R getRoom() {
        return (R) room;
    }

    public Room getUncastedRoom() {
        return room;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getDirection() {
        return direction;
    }

    public void setDirection(float direction) {
        this.direction = direction;
    }
}
