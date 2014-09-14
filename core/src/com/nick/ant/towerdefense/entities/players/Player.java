package com.nick.ant.towerdefense.entities.players;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.nick.ant.towerdefense.entities.Entity;
import com.nick.ant.towerdefense.services.TextureManager;

/**
 * Created by Nick on 08/09/2014.
 */
public class Player extends Entity {

    private Texture texture;
    private int x;
    private int y;

    private boolean moveUp;
    private boolean moveDown;
    private boolean moveLeft;
    private boolean moveRight;

    private int xAim;
    private int yAim;

    public Player(int x, int y) {
        texture = TextureManager.getTexture("player.png");

        this.x = x;
        this.y = y;

        this.moveUp = false;
        this.moveDown = false;
        this.moveLeft = false;
        this.moveRight = false;

        this.xAim = 0;
        this.yAim = 0;
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
