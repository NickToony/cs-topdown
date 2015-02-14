package com.nick.ant.towerdefense.renderables.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.esotericsoftware.spine.*;

/**
 * Created by Nick on 23/09/2014.
 */
public class SkeletonWrapper {
    private Skeleton skeleton;
    private AnimationState state;
    private SkeletonRenderer renderer;
    private Entity entity;

    public SkeletonWrapper(Entity entity) {
        this.entity = entity;
    }

    public void step()  {
        if (skeleton == null)   {
            return;
        }

        state.update(Gdx.graphics.getDeltaTime());
        state.apply(skeleton);

        skeleton.setX(entity.x);
        skeleton.setY(entity.y);
        skeleton.getRootBone().setRotation(skeleton.getRootBone().getRotation() + entity.direction);

        skeleton.updateWorldTransform();
    }

    /**
     * Renders the skeleton. Don't forget to call super.render(batch);
     * @param spriteBatch
     */
    public void render(SpriteBatch spriteBatch) {
        renderer.draw(spriteBatch, skeleton);
    }

    public void setSkeleton(Skeleton skeleton)  {
        this.skeleton = skeleton;
        this.state = new AnimationState(new AnimationStateData(skeleton.getData()));
        this.renderer = new SkeletonRenderer();
    }

    public void startAnimation(String animation, float duration, boolean loop)   {
        this.state.setAnimation(0, animation, loop);
        this.state.setTimeScale(skeleton.getData().findAnimation(animation).getDuration() / duration);
    }

    public Skeleton getSkeleton()    {
        return skeleton;
    }

    public void dispose() {

    }
}
