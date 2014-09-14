package com.nick.ant.towerdefense.renderables;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Created by Nick on 14/09/2014.
 */
public abstract class Renderable {
    public abstract void render(SpriteBatch spriteBatch);
    public abstract void step();
}
