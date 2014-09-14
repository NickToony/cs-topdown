package com.nick.ant.towerdefense.renderables.entities.players;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.nick.ant.towerdefense.renderables.entities.Entity;
import com.nick.ant.towerdefense.components.TextureManager;

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
        sprite.draw(spriteBatch);
    }

    @Override
    public void step() {
        x ++;
        direction = calculateDirection(Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()/2);
    }

    protected float calculateDirection(int aimX, int aimY){
        return (float) ((Math.atan2((aimX - x), -(aimY - y)) * 180.0f / Math.PI) + 180f);
    }
}
