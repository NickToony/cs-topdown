package com.nicktoony.engine.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.nicktoony.engine.components.Entity;
import com.nicktoony.engine.rooms.RoomGame;

/**
 * Created by Nick on 18/05/2016.
 */
public abstract class HUD extends Entity<RoomGame> {
    @Override
    protected void create(boolean render) {

    }

    @Override
    public void step(float delta) {

    }

    @Override
    public void render(SpriteBatch spriteBatch) {

    }

    @Override
    public void dispose(boolean render) {

    }

    public boolean getMouse() {
        return false;
    }

    public boolean getKeyboard() {
        return false;
    }
}
