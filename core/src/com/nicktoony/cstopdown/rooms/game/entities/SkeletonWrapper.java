package com.nicktoony.cstopdown.rooms.game.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.esotericsoftware.spine.*;
import com.nicktoony.cstopdown.components.Entity;

/**
 * Created by Nick on 23/09/2014.
 */
public class SkeletonWrapper {
    public interface AnimationEventListener {
        public void animationEvent(Event event);
    }

    private Skeleton skeleton;
    private AnimationState state;
    private SkeletonRenderer renderer;
    private Entity entity;
    private String idleAnimation;
    private float idleDuration;
    private AnimationEventListener eventListener;

    public SkeletonWrapper(Entity entity, AnimationEventListener listener) {
        this.entity = entity;
        if (listener != null) {
            this.eventListener = listener;
        }
    }

    public void step()  {
        if (skeleton == null)   {
            return;
        }

        state.update(Gdx.graphics.getDeltaTime());
        state.apply(skeleton);

        skeleton.setX(entity.getX());
        skeleton.setY(entity.getY());
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
        if (idleAnimation != null) {
            startAnimation(idleAnimation, idleDuration, true);
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

        this.state.setAnimation(0, animation, loop);
        this.state.setTimeScale(skeleton.getData().findAnimation(animation).getDuration() / duration);
    }

    public Skeleton getSkeleton()    {
        return skeleton;
    }

    public void dispose() {

    }
}
