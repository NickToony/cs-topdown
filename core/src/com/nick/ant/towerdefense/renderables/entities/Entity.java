package com.nick.ant.towerdefense.renderables.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.nick.ant.towerdefense.renderables.Renderable;
import com.nick.ant.towerdefense.renderables.rooms.Room;

/**
 * Created by Nick on 08/09/2014.
 */
public abstract class Entity extends Renderable {
    protected int x = 0;
    protected int y = 0;
    protected Room room;

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setRoom(Room room)  {
        this.room = room;
    }
}
