package com.nick.ant.towerdefense.renderables.entities.players;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.nick.ant.towerdefense.components.CharacterManager;
import com.nick.ant.towerdefense.renderables.entities.SkeletonEntity;

/**
 * Created by Nick on 08/09/2014.
 */
public class Player extends SkeletonEntity {

    private final int PLAYER_RADIUS = 15;

    private final int moveSpeed = 2;

    protected boolean moveUp;
    protected boolean moveDown;
    protected boolean moveLeft;
    protected boolean moveRight;

    public Player(int x, int y) {

        setSkeleton(CharacterManager.getInstance().getCharacterCategories(0).getSkins().get(0).getSkeleton());

        this.x = x;
        this.y = y;
        this.direction = 0.0f;

        this.moveUp = false;
        this.moveDown = false;
        this.moveLeft = false;
        this.moveRight = false;

        setCollisionCircle(new Circle(), true);
        getCollisionCircle(0, 0).setRadius(PLAYER_RADIUS);
    }

    @Override
    public void render(SpriteBatch spriteBatch) {
        super.render(spriteBatch);
    }

    @Override
    public void step() {
        super.step();

        hSpeed = 0;
        vSpeed = 0;

        if (moveUp && !moveDown) {
            vSpeed = moveSpeed;
        }   else if (moveDown && !moveUp)    {
            vSpeed = -moveSpeed;
        }

        if (moveLeft && !moveRight) {
            hSpeed = -moveSpeed;
        }   else if (moveRight && !moveLeft)    {
            hSpeed = moveSpeed;
        }

        if (hSpeed != 0 && vSpeed != 0) {
            hSpeed *= 0.75;
            vSpeed *= 0.75;
        }
    }

    protected float calculateDirection(int aimX, int aimY){
        return (float) ((Math.atan2((aimX - x), -(aimY - y)) * 180.0f / Math.PI) + 180f);
    }
}
