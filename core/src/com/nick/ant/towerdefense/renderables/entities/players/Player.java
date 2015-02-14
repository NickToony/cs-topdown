package com.nick.ant.towerdefense.renderables.entities.players;

import box2dLight.Light;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.esotericsoftware.spine.Bone;
import com.nick.ant.towerdefense.components.CharacterManager;
import com.nick.ant.towerdefense.components.TextureManager;
import com.nick.ant.towerdefense.components.weapons.Weapon;
import com.nick.ant.towerdefense.components.weapons.WeaponManager;
import com.nick.ant.towerdefense.renderables.entities.Entity;

/**
 * Created by Nick on 08/09/2014.
 */
public class Player extends Entity {

    private final int PLAYER_RADIUS = 14;
    private final int PLAYER_MOVE_SPEED = 2;
    private final int WEAPON_X_OFFSET = 15;
    private final int WEAPON_Y_OFFSET = 64 - 46;

    protected boolean moveUp = false;
    protected boolean moveDown = false;
    protected boolean moveLeft = false;
    protected boolean moveRight = false;
    private boolean lightOn = true;

    private Weapon weaponPrimary;
    private Bone leftHand;
    private Bone rightHand;
    private Sprite leftHandSprite;
    private Sprite rightHandSprite;
    private Sprite shadowSprite;

    private Body body;
    private Light torch;

    private Texture gunTexture;
    private Light glow;

    public Player(int x, int y) {
        this.x = x;
        this.y = y;
        this.direction = 0.0f;
    }

    @Override
    public void create() {
        getSkeletonWrapper().setSkeleton(CharacterManager.getInstance().getCharacterCategories(0).getSkins().get(0).getSkeleton());
        leftHand = getSkeletonWrapper().getSkeleton().findBone("left_gun");
        rightHand = getSkeletonWrapper().getSkeleton().findBone("right_gun");

        setGun(WeaponManager.getInstance().getWeapon("rifle_m4a1"));

        if (weaponPrimary != null)  {
            getSkeletonWrapper().startAnimation(weaponPrimary.getAnimationIdle(), 2, true);
        }   else    {
            System.out.println("NO ANIMATIONS");
        }

        setupBody();
    }

    private void setupBody() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x + PLAYER_RADIUS, y + PLAYER_RADIUS);

        body = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(PLAYER_RADIUS, PLAYER_RADIUS);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 0.1f;
        fixtureDef.restitution = 0f;

        body.createFixture(fixtureDef);
        shape.dispose();
    }

    private void setGun(Weapon weapon)  {
        if (gunTexture != null) {
            gunTexture.dispose();
        }

        gunTexture = TextureManager.getTexture("weapons/" + weapon.getTexture()+ "/texture.png");
        gunTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        weaponPrimary = weapon;
    }

    public Weapon getGun(){
        return weaponPrimary;
    }

    @Override
    public void render(SpriteBatch spriteBatch) {
        super.render(spriteBatch);
        Weapon weapon = weaponPrimary;

        // If player has a gun in the left hand
        if (weapon.isLeftHand() && weapon != null)  {
            if (leftHandSprite == null || leftHandSprite.getTexture() != gunTexture)  {
                leftHandSprite = new Sprite(gunTexture);
                leftHandSprite.setOrigin(WEAPON_X_OFFSET, WEAPON_Y_OFFSET);
            }

            leftHandSprite.setX(leftHand.getWorldX() + x - WEAPON_X_OFFSET);
            leftHandSprite.setY(leftHand.getWorldY() + y - WEAPON_Y_OFFSET);
            leftHandSprite.setRotation(leftHand.getWorldRotation() - 90);

            leftHandSprite.draw(spriteBatch);
        }
        // If player has a gun in the right hand
        if (weapon.isRightHand() && weapon != null) {
            if (rightHandSprite == null || rightHandSprite.getTexture() != gunTexture) {
                rightHandSprite = new Sprite(gunTexture);
                rightHandSprite.setOrigin(WEAPON_X_OFFSET, WEAPON_Y_OFFSET);
            }

            rightHandSprite.setX(rightHand.getWorldX() + x - WEAPON_X_OFFSET);
            rightHandSprite.setY(rightHand.getWorldY() + y - WEAPON_Y_OFFSET);
            rightHandSprite.setRotation(rightHand.getWorldRotation() - 90);

            rightHandSprite.draw(spriteBatch);
        }

        // Update torch
        torch.setPosition(rightHand.getX() + x, rightHand.getY() + y);
        torch.setDirection(direction + 90);
        // Update glow
        glow.setPosition(x, y);
    }

    @Override
    public void step() {
        this.x = Math.round(body.getPosition().x);
        this.y = Math.round(body.getPosition().y);

        super.step();

        float hSpeed = (moveLeft ? -PLAYER_MOVE_SPEED : 0) + (moveRight ? PLAYER_MOVE_SPEED : 0);
        float vSpeed = (moveUp ? PLAYER_MOVE_SPEED : 0) + (moveDown ? -PLAYER_MOVE_SPEED : 0);

        body.setLinearVelocity(hSpeed, vSpeed);
    }

    @Override
    public void dispose() {
        shadowSprite.getTexture().dispose();
        leftHandSprite.getTexture().dispose();
        rightHandSprite.getTexture().dispose();
        torch.dispose();
    }

    protected float calculateDirection(int aimX, int aimY){
        return (float) ((Math.atan2((aimX - x), -(aimY - y)) * 180.0f / Math.PI) + 180f);
    }

    public void setTorch(Light torch) {
        this.torch = torch;
        torch.setActive(lightOn);
    }

    public void setLightOn(boolean lightOn) {
        this.lightOn = lightOn;
        torch.setActive(lightOn);
    }

    public boolean isLightOn() {
        return lightOn;
    }

    public void setGlow(Light glow) {
        this.glow = glow;
    }
}
