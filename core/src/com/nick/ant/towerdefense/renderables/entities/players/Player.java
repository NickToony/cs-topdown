package com.nick.ant.towerdefense.renderables.entities.players;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.esotericsoftware.spine.*;
import com.nick.ant.towerdefense.components.CharacterManager;
import com.nick.ant.towerdefense.renderables.entities.Entity;
import com.nick.ant.towerdefense.components.TextureManager;

/**
 * Created by Nick on 08/09/2014.
 */
public class Player extends Entity {

    private TextureRegion texture;
    private Sprite sprite;
    private Skeleton skeleton;
    AnimationState state;
    SkeletonRenderer renderer;

    protected float direction;
    private final int moveSpeed = 2;

    protected boolean moveUp;
    protected boolean moveDown;
    protected boolean moveLeft;
    protected boolean moveRight;

    public Player(int x, int y) {

        //texture = TextureManager.getTexture("player.png");
        texture = CharacterManager.getInstance().getCharacterCategories(0).getSkins().get(0).getTexture();
        texture.getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        skeleton = CharacterManager.getInstance().getCharacterCategories(0).getSkins().get(0).getSkeleton();
        state = new AnimationState(new AnimationStateData(skeleton.getData()));
        state.setAnimation(0, "rifle_idle", true);
        state.setTimeScale(skeleton.getData().findAnimation("rifle_idle").getDuration() / 1);
        renderer = new SkeletonRenderer();

        sprite = new Sprite(texture);

        this.x = x;
        this.y = y;

        this.direction = 0.0f;

        this.moveUp = false;
        this.moveDown = false;
        this.moveLeft = false;
        this.moveRight = false;

        setCollisionCircle(new Circle(), true);
        getCollisionCircle(0, 0).setRadius(texture.getRegionWidth()/2);
    }

    @Override
    public void render(SpriteBatch spriteBatch) {
        /*sprite.setCenterX(this.x);
        sprite.setCenterY(this.y);
        sprite.setRotation(direction);
        sprite.setOrigin(sprite.getWidth()/2, sprite.getHeight()/2);
        sprite.draw(spriteBatch);*/
        renderer.draw(spriteBatch, skeleton);
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

        state.update(Gdx.graphics.getDeltaTime());
        state.apply(skeleton);
        skeleton.setX(this.x);
        skeleton.setY(this.y);
        skeleton.getRootBone().setRotation(direction - 90f);
        skeleton.updateWorldTransform();
    }

    protected float calculateDirection(int aimX, int aimY){
        return (float) ((Math.atan2((aimX - x), -(aimY - y)) * 180.0f / Math.PI) + 180f);
    }
}
