package com.nick.ant.towerdefense.renderables.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.esotericsoftware.spine.Skeleton;
import com.nick.ant.towerdefense.renderables.Renderable;
import com.nick.ant.towerdefense.rooms.Room;

/**
 * Created by Nick on 08/09/2014.
 */
public abstract class Entity extends Renderable {
    protected float x = 0;
    protected float y = 0;
    protected float direction = 0;
    protected SkeletonWrapper skeletonWrapper;

    public SkeletonWrapper getSkeletonWrapper() {
        if (skeletonWrapper == null) {
            skeletonWrapper = new SkeletonWrapper(this);
        }
        return skeletonWrapper;
    }

    @Override
    public void step() {
        if (skeletonWrapper != null) {
            skeletonWrapper.step();
        }
    }

    @Override
    public void render(SpriteBatch spriteBatch) {
        if (skeletonWrapper != null) {
            skeletonWrapper.render(spriteBatch);
        }
    }

    @Override
    public void dispose() {
        if (skeletonWrapper != null) {
            skeletonWrapper.dispose();
        }
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getDirection() {
        return direction;
    }
}
