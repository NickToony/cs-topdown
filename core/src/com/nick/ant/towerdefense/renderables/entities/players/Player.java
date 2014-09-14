package com.nick.ant.towerdefense.renderables.entities.players;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.nick.ant.towerdefense.renderables.entities.Entity;
import com.nick.ant.towerdefense.components.TextureManager;

/**
 * Created by Nick on 08/09/2014.
 */
public class Player extends Entity {

    private Texture texture;
    private int x;
    private int y;

    public Player(int x, int y) {
        texture = TextureManager.getTexture("player.png");

        this.x = x;
        this.y = y;
    }

    @Override
    public void render(SpriteBatch spriteBatch) {
        spriteBatch.draw(texture, x, y, 32, 32);
    }

    @Override
    public void step() {
        x ++;
    }
}
