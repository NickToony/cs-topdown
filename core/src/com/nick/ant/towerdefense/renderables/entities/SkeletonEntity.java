package com.nick.ant.towerdefense.renderables.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.AnimationStateData;
import com.esotericsoftware.spine.Skeleton;
import com.esotericsoftware.spine.SkeletonRenderer;

/**
 * Created by Nick on 23/09/2014.
 */
public abstract class SkeletonEntity extends Entity {
    private Skeleton skeleton;
    private AnimationState state;
    private SkeletonRenderer renderer;

    @Override
    public void step()  {
        super.step();

        if (skeleton == null)   {
            return;
        }

        state.update(Gdx.graphics.getDeltaTime());
        state.apply(skeleton);

        skeleton.setX(this.x);
        skeleton.setY(this.y);
        skeleton.getRootBone().setRotation(skeleton.getRootBone().getRotation() + direction);

        skeleton.updateWorldTransform();
    }

    /**
     * Renders the skeleton. Don't forget to call super.render(batch);
     * @param spriteBatch
     */
    @Override
    public void render(SpriteBatch spriteBatch) {
        renderer.draw(spriteBatch, skeleton);
    }

    protected void setSkeleton(Skeleton skeleton)  {
        this.skeleton = skeleton;
        this.state = new AnimationState(new AnimationStateData(skeleton.getData()));
        this.state.setAnimation(0, "rifle_idle", true);
        this.state.setTimeScale(skeleton.getData().findAnimation("rifle_idle").getDuration() / 1);
        this.renderer = new SkeletonRenderer();
    }
}
