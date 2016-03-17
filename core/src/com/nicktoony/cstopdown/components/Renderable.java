package com.nicktoony.cstopdown.components;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Created by nick on 15/07/15.
 */
public abstract class Renderable {
    /**
     * The animationEvent of the renderable being created. This is always called before step or render.
     * If render is false, then don't initialise anything needed to render the renderable (such as sprites)
     */
    protected abstract void create(boolean render);

    /**
     * Basic game logic occurs here.
     */
    public abstract void step(float delta);

    /**
     * The animationEvent of being drawn.
     */
    public abstract void render(SpriteBatch spriteBatch);

    /**
     * The animationEvent of being disposed. This is where it should free up its memory.
     */
    public abstract void dispose(boolean render);
}
