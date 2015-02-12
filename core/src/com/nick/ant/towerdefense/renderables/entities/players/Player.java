package com.nick.ant.towerdefense.renderables.entities.players;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.esotericsoftware.spine.Bone;
import com.nick.ant.towerdefense.components.CharacterManager;
import com.nick.ant.towerdefense.components.TextureManager;
import com.nick.ant.towerdefense.components.weapons.Weapon;
import com.nick.ant.towerdefense.components.weapons.WeaponManager;
import com.nick.ant.towerdefense.renderables.entities.Entity;
import com.nick.ant.towerdefense.rooms.RoomGame;

/**
 * Created by Nick on 08/09/2014.
 */
public class Player extends RoomGame.WorldEntity {

    private final int PLAYER_RADIUS = 15;
    private final int PLAYER_MOVE_SPEED = 2;
    private final int WEAPON_X_OFFSET = 15;
    private final int WEAPON_Y_OFFSET = 64 - 46;

    protected boolean moveUp;
    protected boolean moveDown;
    protected boolean moveLeft;
    protected boolean moveRight;

    private Weapon weaponPrimary;
    private Bone leftHand;
    private Bone rightHand;
    private Sprite leftHandSprite;
    private Sprite rightHandSprite;
    private Sprite shadowSprite;

    private Texture gunTexture;

    public Player(int x, int y) {

        getSkeletonWrapper().setSkeleton(CharacterManager.getInstance().getCharacterCategories(0).getSkins().get(0).getSkeleton());
        leftHand = getSkeletonWrapper().getSkeleton().findBone("left_gun");
        rightHand = getSkeletonWrapper().getSkeleton().findBone("right_gun");

        setGun(WeaponManager.getInstance().getWeapon("rifle_m4a1"));

        this.x = x;
        this.y = y;
        this.direction = 0.0f;

        this.moveUp = false;
        this.moveDown = false;
        this.moveLeft = false;
        this.moveRight = false;

        if (weaponPrimary != null)  {
            getSkeletonWrapper().startAnimation(weaponPrimary.getAnimationIdle(), 2, true);
        }   else    {
            System.out.println("NO ANIMATIONS");
        }
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
        shadowSprite = new Sprite(TextureManager.getTexture("shadow.png"));
        shadowSprite.setX(x - shadowSprite.getWidth()/2);
        shadowSprite.setY(y - shadowSprite.getHeight()/2);
        shadowSprite.draw(spriteBatch);

        super.render(spriteBatch);
        Weapon weapon = weaponPrimary;

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
    }

    @Override
    public void dispose() {
        shadowSprite.getTexture().dispose();
        leftHandSprite.getTexture().dispose();
        rightHandSprite.getTexture().dispose();
    }

    @Override
    public void create() {

    }

    protected float calculateDirection(int aimX, int aimY){
        return (float) ((Math.atan2((aimX - x), -(aimY - y)) * 180.0f / Math.PI) + 180f);
    }

}
