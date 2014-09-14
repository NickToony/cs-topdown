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

    protected float direction;
    private int moveSpeed;

    protected boolean moveUp;
    protected boolean moveDown;
    protected boolean moveLeft;
    protected boolean moveRight;

    public Player(int x, int y) {

        texture = TextureManager.getTexture("player.png");
        sprite = new Sprite(texture);

        this.x = x;
        this.y = y;

        this.direction = 0.0f;
        this.moveSpeed = 2;

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
        sprite.setOrigin(sprite.getWidth()/2, sprite.getHeight()/2);
        sprite.draw(spriteBatch);
    }

    @Override
    public void step() {
        if(moveUp){
            y += moveSpeed;
        }
        if(moveDown){
            y -= moveSpeed;
        }
        if(moveLeft){
            x -= moveSpeed;
        }
        if(moveRight){
            x += moveSpeed;
        }
//        direction = calculateDirection(Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()/2);
    }

    protected float calculateDirection(int aimX, int aimY){
        return (float) ((Math.atan2((aimX - x), -(aimY - y)) * 180.0f / Math.PI) + 180f);
    }
}
