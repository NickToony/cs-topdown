package com.nicktoony.engine.components;

import com.badlogic.gdx.math.Vector2;

/**
 * Created by nick on 15/07/15.
 */
public abstract class Entity<R extends Room> extends Renderable {

    private Room room;
    protected float x;
    protected float y;
    protected float direction;
    protected int id;

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

    public Vector2 getPosition() {
        return new Vector2(x, y);
    }

    public void setPosition(float x, float y) {
        setX(x);
        setY(y);
    }

    public float getDirection() {
        return direction;
    }

    public void setDirection(float direction) {
        this.direction = direction;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void focused(boolean focused) {

    }
}
