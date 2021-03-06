package com.nicktoony.engine.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.esotericsoftware.spine.*;
import com.nicktoony.engine.components.Entity;

/**
 * Created by Nick on 23/09/2014.
 */
public class SkeletonWrapper {
    public interface AnimationEventListener {
        void animationEvent(Event event);
    }

    private Skeleton skeleton;
    private AnimationState state;
    private SkeletonRenderer renderer;
    private Entity entity;
    private String idleAnimation;
    private float idleDuration;
    private AnimationEventListener eventListener;
    private boolean isIdle = false;

    public SkeletonWrapper(Entity entity, AnimationEventListener listener) {
        this.entity = entity;
        if (listener != null) {
            this.eventListener = listener;
        }
    }

    public void step(float x, float y)  {
        if (skeleton == null)   {
            return;
        }

        state.update(Gdx.graphics.getDeltaTime());
        state.apply(skeleton);

        skeleton.setX(x);
        skeleton.setY(y);
        skeleton.getRootBone().setRotation(skeleton.getRootBone().getRotation() + entity.getDirection());

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
        this.state.addListener(new AnimationState.AnimationStateListener() {
            @Override
            public void event(int trackIndex, Event event) {
                if (eventListener != null) {
                    eventListener.animationEvent(event);
                }
            }

            @Override
            public void complete(int trackIndex, int loopCount) {
                startIdle();
            }

            @Override
            public void start(int trackIndex) {

            }

            @Override
            public void end(int trackIndex) {

            }
        });

        startIdle();
    }

    public void startIdle() {
        if (idleAnimation != null && !isIdle) {
            startAnimation(idleAnimation, idleDuration, true);
            isIdle = true;
        }
    }

    public void setIdleAnimation(String idleAnimation, float idleDuration) {
        this.idleAnimation = idleAnimation;
        this.idleDuration = idleDuration;
    }

    public void startAnimation(String animation, float duration, boolean loop)   {
        if (skeleton == null) {
            return;
        }

        isIdle = false;

        this.state.setAnimation(0, animation, loop);
        this.state.setTimeScale(skeleton.getData().findAnimation(animation).getDuration() / duration);
    }

    public Skeleton getSkeleton()    {
        return skeleton;
    }

    public void dispose() {

    }
}
