package com.nick.ant.towerdefense.renderables;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.nick.ant.towerdefense.rooms.Room;

/**
 * Created by Nick on 14/09/2014.
 */
public abstract class Renderable {
    protected Room room;

    public abstract void render(SpriteBatch spriteBatch);
    public abstract void step();
    public abstract void dispose();
    public abstract void create();

    public void setRoom(Room room) {
        this.room = room;
    }
}
