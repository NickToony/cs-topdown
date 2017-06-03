package com.nicktoony.cstopdown.rooms.game.entities.players;

import box2dLight.ConeLight;
import box2dLight.Light;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.esotericsoftware.spine.Bone;
import com.esotericsoftware.spine.Event;
import com.nicktoony.cstopdown.mods.gamemode.PlayerModInterface;
import com.nicktoony.cstopdown.networking.packets.helpers.WeaponWrapper;
import com.nicktoony.engine.components.PlayerListener;
import com.nicktoony.engine.entities.Bullet;
import com.nicktoony.engine.entities.SkeletonWrapper;
import com.nicktoony.engine.EngineConfig;
import com.nicktoony.engine.components.PhysicsEntity;
import com.nicktoony.engine.services.CharacterManager;
import com.nicktoony.engine.services.CharacterSkin;
import com.nicktoony.engine.services.LightManager;
import com.nicktoony.engine.services.TextureManager;
import com.nicktoony.engine.services.weapons.Weapon;
import com.nicktoony.engine.services.weapons.WeaponManager;

import java.util.Random;

/**
 * Created by Nick on 08/09/2014.
 */
public class Player extends PhysicsEntity implements SkeletonWrapper.AnimationEventListener {

    private final float PLAYER_RADIUS = 0.3f;
    private final float PLAYER_ANGLE_SMOOTHING = 0.1f;

    private final int STATE_IDLE = 0;
    private final int STATE_SHOOTING = 1;
    private final int STATE_RELOADING = 2;
    private final int STATE_COCK = 3;
    private final int STATE_DEQUIP = 4;
    private final int STATE_EQUIP = 5;

    private final float SOUND_MODIFIER = 0.5f;

    protected boolean moveUp = false;
    protected boolean moveDown = false;
    protected boolean moveLeft = false;
    protected boolean moveRight = false;
    private boolean lightOn = false;
    private boolean lastTorch = false;
    private boolean glowActive = false;
    protected float directionTo;


    protected boolean reloadKey = false;
    protected boolean shootKey = false;
    private boolean lastShootKey = shootKey;
    protected boolean zoomKey = false;

    private int state = STATE_IDLE;
    private int stateTimer = 0;
    private int stateLast = STATE_IDLE;
    private boolean stateChange;
    private boolean makeBullet = false;


    private int weaponCurrent = 0;
    private int weaponNext = -1;
    private WeaponWrapper weapons[];
    private Bone leftHand;
    public Bone rightHand;
    private Sprite leftHandSprite;
    private Sprite rightHandSprite;
    private Sprite shadowSprite;

    private Texture gunTexture;
    private Light glow;
    private Light torch;
    private Light gunFire;
    protected float mouseDistance = 100;

    private CharacterSkin charSkin;
    private boolean charSkinChanged = false;

    private SkeletonWrapper skeletonWrapper;
    private int health = 100;
    private PlayerListener listener = null;
    private int team;
    private Random random = new Random();

    boolean cannotSee = false;
    Body targetBody = null;
    private RayCastCallback callback = new RayCastCallback() {
        @Override
        public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
            if (!body.getFixtureList().contains(fixture, false)
                    && !targetBody.getFixtureList().contains(fixture, false)) {
                cannotSee = true;
                return 0;
            } else {
                cannotSee = false;
                return -1;
            }
        }
    };

    public Player() {
        skeletonWrapper = new SkeletonWrapper(this, this);
    }

    @Override
    public void create(boolean render) {
        super.create(render);

        if (render) {
            // Set default skin
            charSkin = CharacterManager.getInstance().getCharacterCategories(0).getSkins().get(0);
            getSkeletonWrapper().setSkeleton(charSkin.getSkeleton());

            leftHand = getSkeletonWrapper().getSkeleton().findBone("left_gun");
            rightHand = getSkeletonWrapper().getSkeleton().findBone("right_gun");

            shadowSprite = new Sprite(TextureManager.getTexture("shadow.png"));

            setTorch(LightManager.defineTorch(getRoom().getRayHandler()));
            setGlow(LightManager.definePlayerGlow(getRoom().getRayHandler()));
            setGunFire(LightManager.defineGunFire(getRoom().getRayHandler()));
        }

    }

    protected SkeletonWrapper getSkeletonWrapper() {
        return skeletonWrapper;
    }

    @Override
    protected Body setupBody() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x + PLAYER_RADIUS, y + PLAYER_RADIUS);
        bodyDef.allowSleep = false;

        Body body = getRoom().getWorld().createBody(bodyDef);

        Shape shape = new CircleShape();
//        shape.setAsBox(PLAYER_RADIUS, PLAYER_RADIUS);
        shape.setRadius(PLAYER_RADIUS);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 10f;
        fixtureDef.restitution = 0f;

        body.createFixture(fixtureDef);
        shape.dispose();

        return body;
    }

    public void setNextWeapon(int slot)  {
        weaponNext = slot;
    }

    public int getCurrentWeapon(){
        return weaponCurrent;
    }

    public WeaponWrapper getCurrentWeaponObject() {
        if (weaponCurrent != -1) return weapons[weaponCurrent];
        return null;
    }

    public int getNextWeapon() {
        return weaponNext;
    }

    public void setWeapons(WeaponWrapper weapons[]) {
        this.weapons = weapons;
    }

    public WeaponWrapper[] getWeapons() {
        return weapons;
    }

    @Override
    public void render(SpriteBatch spriteBatch) {
        // render harry's shadow
        shadowSprite.setX(x - shadowSprite.getWidth() / 2);
        shadowSprite.setY(y - shadowSprite.getHeight() / 2);
        shadowSprite.draw(spriteBatch);

        // Render colours
        if (team == PlayerModInterface.TEAM_CT) {
//            skeletonWrapper.getSkeleton().getColor().set(Color.BLUE);
            charSkin = CharacterManager.getInstance().getCharacterCategories(0).getSkins().get(0);
            charSkinChanged = true;

        } else if (team == PlayerModInterface.TEAM_T) {
//            skeletonWrapper.getSkeleton().getColor().set(Color.RED);
            charSkin = CharacterManager.getInstance().getCharacterCategories(1).getSkins().get(0);
            charSkinChanged = true;

        }


        if (charSkinChanged) {
            charSkinChanged = false;
//            getSkeletonWrapper().setSkeleton(charSkin.getSkeleton());
            charSkin.applySkin(getSkeletonWrapper().getSkeleton());
//            System.out.println("CHANGED SKIN " + x + " " + charSkin);
        }

        // Render the player entirely
        skeletonWrapper.render(spriteBatch);

        if (weaponCurrent != -1) {
            WeaponWrapper weapon = weapons[weaponCurrent];
            if (gunTexture == null) {
                gunTexture = TextureManager.getTexture(weapon.weapon.getTexture());
                gunTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            }

            // If player has a gun in the left hand
            if (weapon.weapon.getGraphics().left) {
                if (leftHandSprite == null || leftHandSprite.getTexture() != gunTexture) {
                    leftHandSprite = new Sprite(gunTexture);
                    leftHandSprite.setOrigin(weapons[weaponCurrent].weapon.getGraphics().x_offset,
                            weapons[weaponCurrent].weapon.getGraphics().y_offset);
                }

                leftHandSprite.setX(leftHand.getWorldX() + x - weapons[weaponCurrent].weapon.getGraphics().x_offset);
                leftHandSprite.setY(leftHand.getWorldY() + y - weapons[weaponCurrent].weapon.getGraphics().y_offset);
                leftHandSprite.setRotation(leftHand.getWorldRotation() - 90);

                leftHandSprite.draw(spriteBatch);
            }
            // If player has a gun in the right hand
            if (weapon.weapon.getGraphics().right) {
                if (rightHandSprite == null || rightHandSprite.getTexture() != gunTexture) {
                    rightHandSprite = new Sprite(gunTexture);
                    rightHandSprite.setOrigin(weapons[weaponCurrent].weapon.getGraphics().x_offset,
                            weapons[weaponCurrent].weapon.getGraphics().y_offset);
                }

                rightHandSprite.setX(rightHand.getWorldX() + x - weapons[weaponCurrent].weapon.getGraphics().x_offset);
                rightHandSprite.setY(rightHand.getWorldY() + y - weapons[weaponCurrent].weapon.getGraphics().y_offset);
                rightHandSprite.setRotation(rightHand.getWorldRotation() - 90);

                rightHandSprite.draw(spriteBatch);
            }
        }

        Vector2 vector = new Vector2(rightHand.getWorldX() + x, rightHand.getWorldY() + y);
        if (weaponCurrent != -1) {
            Vector2 gunVector = new Vector2(rightHandSprite.getHeight() / 2, 0);
            gunVector.setAngle(rightHand.getWorldRotation());
            vector.add(gunVector);
        }

        // Update shoot
        torch.setPosition(EngineConfig.toMetres(vector.x), EngineConfig.toMetres(vector.y));
        torch.setDirection(rightHand.getWorldRotation());
        torch.setActive(lightOn);
        // Update gun fire
        gunFire.setPosition(EngineConfig.toMetres(vector.x), EngineConfig.toMetres(vector.y));
        gunFire.setDirection(rightHand.getWorldRotation());
        gunFire.setActive(stateChange && state == STATE_SHOOTING && weapons[weaponCurrent].bulletsIn >= 0);

        // Handle animations
        if (stateChange && weaponCurrent != -1) {
            switch (state) {
                case STATE_SHOOTING:
                    getSkeletonWrapper().startAnimation(weapons[weaponCurrent].weapon.getAnimations().shoot, stateTimer/60f, false);
                    break;
                case STATE_RELOADING:
                    getSkeletonWrapper().startAnimation(weapons[weaponCurrent].weapon.getAnimations().reload, stateTimer / 60f, false);
                    break;
                case STATE_COCK:
                    getSkeletonWrapper().startAnimation(weapons[weaponCurrent].weapon.getAnimations().cock, stateTimer / 60f, false);
                    break;
                case STATE_EQUIP:
                    getSkeletonWrapper().startAnimation(weapons[weaponCurrent].weapon.getAnimations().equip, stateTimer / 60f, false);
                    break;
                case STATE_DEQUIP:
                    getSkeletonWrapper().startAnimation(weapons[weaponCurrent].weapon.getAnimations().unequip, stateTimer / 60f, false);
                    break;
            }
        }


        // Update glow
        glow.setPosition(EngineConfig.toMetres(x), EngineConfig.toMetres(y));
        glow.setActive(glowActive);

        // Create a bullet
        if (makeBullet) {
            float range = weapons[weaponCurrent].weapon.getRange();
            if (range == -1) {
                range = EngineConfig.toPixels(100);
            }

            for (int i = 0; i < weapons[weaponCurrent].weapon.getBullets(); i ++) {
                // Calculate visual spread
                float weaponSpread = weapons[weaponCurrent].weapon.getSpread();
                float spread = 0;
                if (weaponSpread > 0) {
                    spread = random.nextInt((int) weaponSpread * 2) - weaponSpread;
                }
                // Figure out end of gun
                getRoom().addEntity(new Bullet(vector.x, vector.y, direction + spread, this, range));
            }

            // Don't make another
            makeBullet = false;
        }
    }

    @Override
    public void step(float delta) {
        super.step(delta);

        skeletonWrapper.step();

        float moveSpeed = getRoom().getConfig().mp_player_move_speed;
        float hSpeed = (moveLeft ? -moveSpeed : 0) + (moveRight ? moveSpeed : 0);
        float vSpeed = (moveUp ? moveSpeed : 0) + (moveDown ? -moveSpeed : 0);

        body.setLinearVelocity(EngineConfig.toMetres(hSpeed),
                EngineConfig.toMetres(vSpeed));

        // Handle state variables
        if (stateTimer > 0) {
            stateTimer -= 1;
        }
        stateLast = state;
        stateChange = false;
        // Now do something depending on state
        switch (state) {
            case STATE_IDLE:
                // If reload key is currently pressed
                if (reloadKey && weapons[weaponCurrent].bulletsIn < weapons[weaponCurrent].weapon.getClipSize()) {
                    stateTimer = Math.round(weapons[weaponCurrent].weapon.getReloadDuration() * 60);
                    state = STATE_RELOADING;
                } else if (shootKey && weaponCurrent != -1) { // otherwise is shoot key pressed?
                    stateTimer = Math.max(0, weapons[weaponCurrent].weapon.getRateOfFire());
                    state = STATE_SHOOTING;

                    if (weapons[weaponCurrent].bulletsIn >= 0) {
                        weapons[weaponCurrent].bulletsIn -= 1;

                        // Actually shoot if there was one bullet
                        if (weapons[weaponCurrent].bulletsIn >= 0) {
                            // Visual effect
                            makeBullet = true;
                            // If a listener exists
                            if (listener != null) {
                                listener.shoot(getCurrentWeaponObject());
                            }
                        }
                    }
                } else if (weaponNext != -1) {
                    state = STATE_DEQUIP;
                    if (getCurrentWeaponObject() != null) {
                        stateTimer = Math.round(weapons[weaponCurrent].weapon.getEquipDuration() * 60);
                    } else {
                        stateTimer = 0;
                    }
                }
                break;

            case STATE_RELOADING:
                if (stateTimer <= 0) {
                    if (weapons[weaponCurrent].weapon.getReloadType() == Weapon.RELOAD_FULL_COCK) {
                        state = STATE_COCK;
                        stateTimer = Math.round(weapons[weaponCurrent].weapon.getCockDuration() * 60);
                        weapons[weaponCurrent].bulletsIn = weapons[weaponCurrent].weapon.getClipSize();
                    } else if (weapons[weaponCurrent].weapon.getReloadType() == Weapon.RELOAD_SHOTGUN) {
                        // Add a shell
                        weapons[weaponCurrent].bulletsIn ++;
                        // If full
                        if (weapons[weaponCurrent].bulletsIn >= weapons[weaponCurrent].weapon.getClipSize()) {
                            // Cock the gun
                            state = STATE_COCK;
                            stateTimer = Math.round(weapons[weaponCurrent].weapon.getCockDuration() * 60);
                            weapons[weaponCurrent].bulletsIn = weapons[weaponCurrent].weapon.getClipSize();
                        } else {
                            // Load another shell
                            stateTimer = Math.round(weapons[weaponCurrent].weapon.getReloadDuration() * 60);
                            state = STATE_RELOADING;
                            stateChange = true;
                        }
                    } else {
                        state = STATE_IDLE;
                        weapons[weaponCurrent].bulletsIn = weapons[weaponCurrent].weapon.getClipSize();
                    }
                }
                break;

            case STATE_SHOOTING:
                if (stateTimer <= 0) {
                    if (weapons[weaponCurrent].weapon.getRateOfFire() == -1) {
                        state = STATE_COCK;
                        stateTimer = Math.round(weapons[weaponCurrent].weapon.getCockDuration() * 60);
                    } else {
                        state = STATE_IDLE;
                    }
                }
                break;

            case STATE_COCK:
                if (stateTimer <= 0) {
                    state = STATE_IDLE;
                }
                break;

            case STATE_DEQUIP:
                if (stateTimer <= 0) {
                    weaponCurrent = weaponNext;
                    weaponNext = -1;
                    gunTexture = null;
                    getSkeletonWrapper().setIdleAnimation(weapons[weaponCurrent].weapon.getAnimations().idle, 2);
                    getSkeletonWrapper().startIdle();
                    state = STATE_EQUIP;
                    stateTimer = Math.round(weapons[weaponCurrent].weapon.getEquipDuration() * 60);

                }
                break;

            case STATE_EQUIP:
                if (stateTimer <= 0) {
                    state = STATE_IDLE;
                }
                break;

        }
        if (stateLast != state) {
            stateChange = true;
        }

//        direction = directionTo;
        smoothRotation();
    }

    private void smoothRotation() {
//        double angleDifference = (directionTo - direction + 180) % (360) - 180;
//
//        if (Math.abs(angleDifference) < 1) {
//            direction = directionTo;
//        } else {
//            direction += angleDifference * PLAYER_ANGLE_SMOOTHING;
//        }
//
//        direction = direction % 360;

        Vector2 angleFrom = new Vector2(0, 1).setAngle(direction);
        Vector2 angleTo = new Vector2(0, 1).setAngle(directionTo);
        Vector2 angleNew = angleFrom.lerp(angleTo, 0.1f);
        direction = angleNew.angle();
    }

    @Override
    public void dispose(boolean render) {
        super.dispose(render);

        if (render) {
            glow.setActive(false);
            torch.setActive(false);
            gunFire.setActive(false);

            glow.dispose();
            torch.dispose();
            gunFire.dispose();

            glow = null;
            torch = null;
            gunFire = null;
        }
    }

    protected float calculateDirection(int aimX, int aimY){
        return (float) ((Math.atan2((aimX - getRoom().getMap().getCamera().viewportWidth/2),
                (aimY - getRoom().getMap().getCamera().viewportHeight/2)) * 180.0f / Math.PI) + 180f);
    }

    protected float calculateDistance(int aimX, int aimY){
        return new Vector2(getRoom().getMap().getCamera().viewportWidth/2, getRoom().getMap().getCamera().viewportHeight/2).dst(aimX, aimY);
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

    @Override
    public void setDirection(float direction) {
        directionTo = direction;
    }

    @Override
    public void animationEvent(Event event) {
        int soundRange = getRoom().getGame().getGameConfig().sound_range;
        float volume = Math.max((soundRange - new Vector2(x, y)
                .dst(getRoom().getMap().getCameraCenterX(),
                        getRoom().getMap().getCameraCenterY())) / soundRange, 0) * SOUND_MODIFIER;

        if (event.getData().getName().contentEquals("ev_shoot")) {
            if (weapons[weaponCurrent].bulletsIn >= 0) {
                WeaponManager.getInstance()
                        .playSound(weapons[weaponCurrent].weapon, WeaponManager.SoundType.SHOOT, volume);
            } else {
                WeaponManager.getInstance()
                        .playSound(weapons[weaponCurrent].weapon, WeaponManager.SoundType.EMPTY, volume);
            }
        } else if (event.getData().getName().contentEquals("ev_equip")) {
            WeaponManager.getInstance()
                    .playSound(weapons[weaponCurrent].weapon, WeaponManager.SoundType.EQUIP, volume);
        } else if (event.getData().getName().contentEquals("ev_dequip")) {
            WeaponManager.getInstance()
                    .playSound(weapons[weaponCurrent].weapon, WeaponManager.SoundType.DEQUIP, volume);
        } else if (event.getData().getName().contentEquals("ev_eject")) {
            WeaponManager.getInstance()
                    .playSound(weapons[weaponCurrent].weapon, WeaponManager.SoundType.EJECT, volume);
        } else if (event.getData().getName().contentEquals("ev_insert")) {
            WeaponManager.getInstance()
                    .playSound(weapons[weaponCurrent].weapon, WeaponManager.SoundType.INSERT, volume);
        } else if (event.getData().getName().contentEquals("ev_cock")) {
            WeaponManager.getInstance()
                    .playSound(weapons[weaponCurrent].weapon, WeaponManager.SoundType.COCK, volume);
        } else if (event.getData().getName().contentEquals("ev_empty")) {
            WeaponManager.getInstance()
                    .playSound(weapons[weaponCurrent].weapon, WeaponManager.SoundType.EMPTY, volume);
        }
    }

    public void setReloading(boolean reload) {
        this.reloadKey = reload;
    }

    public boolean getReloading() {
        return this.reloadKey;
    }

    public boolean canSeePlayer(Player player) {
        cannotSee = true;
        targetBody = player.body;
        getRoom().getWorld().rayCast(callback, body.getPosition(), player.body.getPosition());
        return !cannotSee;
    }

    @Override
    public void focused(boolean focused) {
        glowActive = focused;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public void setListener(PlayerListener listener) {
        this.listener = listener;
    }

    public float getActualDrection() {
        return directionTo;
    }

    public int getTeam() {
        return team;
    }

    public void setTeam(int team) {
        this.team = team;
    }

    public boolean isMoving() {
        return moveDown || moveUp || moveRight || moveLeft;
    }

    public float getMouseDistance() {
        return mouseDistance;
    }

    public boolean getZoomKey() {
        return zoomKey;
    }
}

