package com.nick.ant.towerdefense.entities.players;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.nick.ant.towerdefense.entities.Entity;
import com.nick.ant.towerdefense.services.TextureManager;

/**
 * Created by Nick on 08/09/2014.
 */
public class Player extends Entity {

    private Texture texture;
    private Sprite sprite;
    private int x;
    private int y;

    private float direction;

    private boolean moveUp;
    private boolean moveDown;
    private boolean moveLeft;
    private boolean moveRight;

    public Player(int x, int y) {

        texture = TextureManager.getTexture("player.png");
        sprite = new Sprite(texture);
        sprite.setScale(0.5f);

        this.x = x;
        this.y = y;

        this.direction = 0.0f;

        this.moveUp = false;
        this.moveDown = false;
        this.moveLeft = false;
        this.moveRight = false;
    }

    @Override
    public void render(SpriteBatch spriteBatch) {
        sprite.setX(this.x);
        sprite.setY(this.y);
        sprite.setRotation(direction);
        System.out.println("Dir: " + direction);
        sprite.draw(spriteBatch);
    }

    @Override
    public void step() {
        x ++;
    }
}
