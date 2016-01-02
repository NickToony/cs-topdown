package com.nicktoony.cstopdown.rooms.game.entities.players;

import box2dLight.ConeLight;
import box2dLight.Light;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.esotericsoftware.spine.Bone;
import com.nicktoony.cstopdown.components.Entity;
import com.nicktoony.cstopdown.rooms.game.RoomGame;
import com.nicktoony.cstopdown.rooms.game.entities.SkeletonWrapper;
import com.nicktoony.cstopdown.services.CharacterManager;
import com.nicktoony.cstopdown.services.TextureManager;
import com.nicktoony.cstopdown.services.weapons.Weapon;
import com.nicktoony.cstopdown.services.weapons.WeaponManager;

/**
 * Created by Nick on 08/09/2014.
 */
public class Player extends Entity<RoomGame> {

    private final int PLAYER_RADIUS = 14;
    private final int PLAYER_MOVE_SPEED = 2;
    private final int WEAPON_X_OFFSET = 15;
    private final int WEAPON_Y_OFFSET = 64 - 46;

    private final int STATE_IDLE = 0;
    private final int STATE_SHOOTING = 1;
    private final int STATE_RELOADING = 2;
    private final int STATE_COCK = 3;

    protected boolean moveUp = false;
    protected boolean moveDown = false;
    protected boolean moveLeft = false;
    protected boolean moveRight = false;
    protected int lastMove = 0;
    private boolean lightOn = false;
    private boolean lastTorch = false;


    protected boolean reloadKey = false;
    protected boolean shootKey = false;
    private boolean lastShootKey = shootKey;

    private int state = STATE_IDLE;
    private int stateTimer = 0;
    private int stateLast = STATE_IDLE;
    private boolean stateChange;


    private Weapon weaponPrimary;
    private Bone leftHand;
    private Bone rightHand;
    private Sprite leftHandSprite;
    private Sprite rightHandSprite;
    private Sprite shadowSprite;

    private Body body;

    private Texture gunTexture;
    private Light glow;
    private Light torch;
    private Light gunFire;

    // Multiplayer
    private long lastUpdate = 0;
    private final long UPDATE_RATE = 1000;
    private boolean changedPosition = false;

    private SkeletonWrapper skeletonWrapper;

    public Player() {
        skeletonWrapper = new SkeletonWrapper(this);
    }

    @Override
    public void create(boolean render) {
        setupBody();

        setGun(WeaponManager.getInstance().getWeapon("rifle_m4a1"));

        if (render) {
            getSkeletonWrapper().setSkeleton(CharacterManager.getInstance().getCharacterCategories(0).getSkins().get(0).getSkeleton());
            leftHand = getSkeletonWrapper().getSkeleton().findBone("left_gun");
            rightHand = getSkeletonWrapper().getSkeleton().findBone("right_gun");

            if (weaponPrimary != null) {
                getSkeletonWrapper().startIdle();
            } else {
                System.out.println("NO ANIMATIONS");
            }

            shadowSprite = new Sprite(TextureManager.getTexture("shadow.png"));
        }
    }

    protected SkeletonWrapper getSkeletonWrapper() {
        return skeletonWrapper;
    }

    private void setupBody() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x + PLAYER_RADIUS, y + PLAYER_RADIUS);

        body = getRoom().getWorld().createBody(bodyDef);

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

        weaponPrimary = weapon;
        getSkeletonWrapper().setIdleAnimation(weaponPrimary.getAnimations().idle, 2);
    }

    public Weapon getGun(){
        return weaponPrimary;
    }

    @Override
    public void render(SpriteBatch spriteBatch) {
        // render harry's shadow
        shadowSprite.setX(x - shadowSprite.getWidth() / 2);
        shadowSprite.setY(y - shadowSprite.getHeight() / 2);
        shadowSprite.draw(spriteBatch);

        skeletonWrapper.render(spriteBatch);

        Weapon weapon = weaponPrimary;
        if (gunTexture == null) {
            gunTexture = TextureManager.getTexture(weapon.getTexture());
            gunTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        }

        // If player has a gun in the left hand
        if (weapon.getGraphics().left && weapon != null)  {
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
        if (weapon.getGraphics().right && weapon != null) {
            if (rightHandSprite == null || rightHandSprite.getTexture() != gunTexture) {
                rightHandSprite = new Sprite(gunTexture);
                rightHandSprite.setOrigin(WEAPON_X_OFFSET, WEAPON_Y_OFFSET);
            }

            rightHandSprite.setX(rightHand.getWorldX() + x - WEAPON_X_OFFSET);
            rightHandSprite.setY(rightHand.getWorldY() + y - WEAPON_Y_OFFSET);
            rightHandSprite.setRotation(rightHand.getWorldRotation() - 90);

            rightHandSprite.draw(spriteBatch);
        }

        Vector2 vector = new Vector2(rightHand.getWorldX() + x, rightHand.getWorldY() + y);
        Vector2 gunVector = new Vector2(rightHandSprite.getHeight()/2, 0);
        gunVector.setAngle(rightHand.getWorldRotation());
        vector.add(gunVector);

        // Update shoot
        torch.setPosition(vector.x, vector.y);
        torch.setDirection(rightHand.getWorldRotation());
        torch.setActive(lightOn);
        // Update gun fire
        gunFire.setPosition(vector.x, vector.y);
        gunFire.setDirection(rightHand.getWorldRotation());
        gunFire.setActive(stateChange && state == STATE_SHOOTING);

        // Handle animations
        if (stateChange) {
            switch (state) {
                case STATE_SHOOTING:
                    getSkeletonWrapper().startAnimation(weaponPrimary.getAnimations().shoot, stateTimer/60f, false);
                    break;
                case STATE_RELOADING:
                    getSkeletonWrapper().startAnimation(weaponPrimary.getAnimations().reload, stateTimer / 60f, false);
                    break;
                case STATE_COCK:
                    getSkeletonWrapper().startAnimation(weaponPrimary.getAnimations().cock, stateTimer / 60f, false);
                    break;
            }
        }


        // Update glow
        glow.setPosition(x, y);
    }

    @Override
    public void step() {
        if (changedPosition) {
            updatePosition();
        }

        this.x = Math.round(body.getPosition().x);
        this.y = Math.round(body.getPosition().y);

        skeletonWrapper.step();

        float hSpeed = (moveLeft ? -PLAYER_MOVE_SPEED : 0) + (moveRight ? PLAYER_MOVE_SPEED : 0);
        float vSpeed = (moveUp ? PLAYER_MOVE_SPEED : 0) + (moveDown ? -PLAYER_MOVE_SPEED : 0);

        body.setLinearVelocity(hSpeed, vSpeed);

        // Check if changed movement keys
        int newMove = ((moveLeft ? 1 : 0) * 1000)
                + ((moveRight ? 1 : 0) * 100)
                + ((moveUp ? 1 : 0) * 10)
                + ((moveDown ? 1 : 0));
//        if (newMove != lastMove && isMultiplayer()) {
//            // Send move packet
//            PlayerMovePacket playerMovePacket = new PlayerMovePacket(moveLeft, moveRight, moveUp, moveDown);
//            room.sendPacket(playerMovePacket);
//        }
        lastMove = newMove;


        // Handle state variables
        if (stateTimer > 0) {
            stateTimer -= 1;
        }
        stateLast = state;
        // Now do something depending on state
        switch (state) {
            case STATE_IDLE:
                // If reload key is currently pressed
                if (reloadKey) {
                    stateTimer = Math.round(weaponPrimary.getReloadDuration() * 60);
                    state = STATE_RELOADING;
                } else if (shootKey) { // otherwise is shoot key pressed?
                    stateTimer = weaponPrimary.getRateOfFire();
                    state = STATE_SHOOTING;
                }
                break;

            case STATE_RELOADING:
                if (stateTimer <= 0) {
                    if (weaponPrimary.getReloadType() == Weapon.RELOAD_FULL_COCK) {
                        state = STATE_COCK;
                        stateTimer = Math.round(weaponPrimary.getCockDuration() * 60);
                    } else {
                        state = STATE_IDLE;
                    }
                }
                break;

            case STATE_SHOOTING:
                if (stateTimer <= 0) {
                    state = STATE_IDLE;
                }
                break;

            case STATE_COCK:
                if (stateTimer <= 0) {
                    state = STATE_IDLE;
                }
                break;
        }
        stateChange = stateLast != state;

        // Handle multiplayer update
//        if (isMultiplayer()) {
//            if (System.currentTimeMillis() > lastUpdate + UPDATE_RATE) {
//                lastUpdate = System.currentTimeMillis();
//
//                room.sendPacket(new PlayerPositionPacket(0, Math.round(getX()), Math.round(getY()), direction));
//            }
//
//            if (lastTorch != lightOn) {
//                lastTorch = lightOn;
//                room.sendPacket(new PlayerTorchPacket(lightOn));
//            }
//
//            if (lastShootKey != shootKey) {
//                lastShootKey = shootKey;
//                room.sendPacket(new PlayerShootPacket(shootKey));
//            }
//        }
    }

    @Override
    public void dispose() {

    }

    protected float calculateDirection(int aimX, int aimY){
        return (float) ((Math.atan2((aimX - getRoom().getMap().getCamera().viewportWidth/2), (aimY - getRoom().getMap().getCamera().viewportHeight/2)) * 180.0f / Math.PI) + 180f);
    }

    public void setTorch(Light torch) {
        this.torch = torch;
        torch.setActive(lightOn);
    }

    public void setLightOn(boolean lightOn) {
        this.lightOn = lightOn;
    }

    public boolean isLightOn() {
        return lightOn;
    }

    public void setGlow(Light glow) {
        this.glow = glow;
    }

    @Override
    public void setX(float x) {
        super.setX(x);
        changedPosition = true;
    }

    @Override
    public void setY(float y) {
        super.setY(y);
        changedPosition = true;
    }

    public void updatePosition() {
        body.setTransform(getX(), getY(), 0);
        changedPosition = false;
    }

    public void setMovement(boolean moveUp, boolean moveRight, boolean moveDown, boolean moveLeft) {
        this.moveUp = moveUp;
        this.moveRight = moveRight;
        this.moveDown = moveDown;
        this.moveLeft = moveLeft;
    }

    public void setGunFire(ConeLight coneLight) {
        this.gunFire = coneLight;
        gunFire.setActive(false);
    }

    public void setShooting(boolean shootKey) {
        this.shootKey = shootKey;
    }

    public boolean getShooting() {
        return shootKey;
    }

    public boolean getMoveLeft() {
        return moveLeft;
    }

    public boolean getMoveRight() {
        return moveRight;
    }

    public boolean getMoveUp() {
        return moveUp;
    }

    public boolean getMoveDown() {
        return moveDown;
    }
}
