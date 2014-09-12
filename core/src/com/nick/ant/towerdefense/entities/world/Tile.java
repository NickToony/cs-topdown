package com.nick.ant.towerdefense.entities.world;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.nick.ant.towerdefense.entities.Entity;
import com.nick.ant.towerdefense.services.TextureManager;

/**
 * Created by Nick on 10/09/2014.
 */
public class Tile extends Entity {
    private Texture texture;
    private World world;
    private int x;
    private int y;

    public Tile(String texture, World world, int x, int y) {
        this.texture = TextureManager.getTexture(texture);
        this.world = world;
        this.x = x;
        this.y = y;
    }

    @Override
    public void render(SpriteBatch spriteBatch) {
        spriteBatch.draw(texture, world.getTileX(x), world.getTileY(y), world.getCellSize(), world.getCellSize());
    }

    @Override
    public void step() {
        // does nothing
    }

    public void setTexture(String texture) {
        this.texture = TextureManager.getTexture(texture);
    }
}
