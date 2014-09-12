package com.nick.ant.towerdefense.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.nick.ant.towerdefense.Game;

/**
 * Created by Nick on 08/09/2014.
 */
public abstract class Entity {
    public abstract void render(SpriteBatch spriteBatch);
    public abstract void step();
}
