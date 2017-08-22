package com.nicktoony.cstopdown.rooms.game.entities.players;

import box2dLight.ConeLight;
import box2dLight.Light;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.esotericsoftware.spine.Bone;
import com.esotericsoftware.spine.Event;
import com.nicktoony.cstopdown.mods.gamemode.PlayerModInterface;
import com.nicktoony.cstopdown.networking.packets.helpers.WeaponWrapper;
import com.nicktoony.cstopdown.networking.packets.player.PlayerInputPacket;
import com.nicktoony.engine.EngineConfig;
import com.nicktoony.engine.components.Entity;
import com.nicktoony.engine.components.PhysicsEntity;
import com.nicktoony.engine.components.PlayerListener;
import com.nicktoony.engine.entities.Bullet;
import com.nicktoony.engine.entities.SkeletonWrapper;
import com.nicktoony.engine.services.CharacterSkin;
import com.nicktoony.engine.services.LightManager;
import com.nicktoony.engine.services.weapons.Weapon;
import com.nicktoony.engine.services.weapons.WeaponManager;

import java.util.Collections;
import java.util.Random;

/**
 * Created by Nick on 08/09/2014.
 */
public class Player extends PhysicsEntity implements SkeletonWrapper.AnimationEventListener {

    private final float PLAYER_RADIUS = EngineConfig.toMetres(14);
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

    private int lastMove = 0;
    private boolean lastShoot = false;
    private boolean lastReload = false;
    private long lastUpdate = 0;
    private boolean lastZoom = false;

    boolean cannotSee = false;
    Body targetBody = null;
    private RayCastCallback callback = new RayCastCallback() {
        @Override
        public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
            // IF the body is not us, or the target
            if (!body.getFixtureList().contains(fixture, false)
                    && !targetBody.getFixtureList().contains(fixture, false)) {

                // If it has data
                if (fixture.getBody().getUserData() != null) {
                    PhysicsEntity entity = (PhysicsEntity) fixture.getBody().getUserData();
                    // IF it's another player
                    if (entity instanceof Player) {
                        Player player = (Player) entity;
                        if (player.team == getTeam() && getRoom().getConfig().mp_friendly_fire) {
                            cannotSee = true;
                            return 0;
                        }
                    } else {
                        cannotSee = true;
                        return 0;
                    }
                } else {
                    cannotSee = true;
                    return 0;
                }
            } else {
//                cannotSee = false;
                return -1;
            }

            return 1;
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
            charSkin = getRoom().getCharacterManager().getCharacterCategories(0).getSkins().get(0);
            getSkeletonWrapper().setSkeleton(charSkin.getSkeleton());

            leftHand = getSkeletonWrapper().getSkeleton().findBone("left_gun");
            rightHand = getSkeletonWrapper().getSkeleton().findBone("right_gun");

            shadowSprite = new Sprite(getAsset("shadow.png", Texture.class));

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
        fixtureDef.density = 50f;
        fixtureDef.restitution = 0f;
        fixtureDef.filter.categoryBits = 0x0002;
//        fixtureDef.filter.maskBits = 0x0001;

        body.createFixture(fixtureDef);
        shape.dispose();

        return body;
    }

    @Override
    public boolean presolveEntity(Contact contact, Entity other) {
        if (other instanceof Player) {
            contact.setEnabled(false);

            if (!getRoom().getConfig().mp_player_collisions){
                return true;
            } else {
                Vector2 move = new Vector2(0, .2f);
                move.setAngle(EngineConfig.angleBetweenPoints(other.getPosition(), getPosition()));
                Vector2 pos = body.getPosition();
                this.body.applyLinearImpulse(move.x, move.y, pos.x, pos.y, true);
                return true;
            }
        }

        return super.presolveEntity(contact, other);
    }

    public void setNextWeapon(int slot)  {
        if (weapons.length > slot) {
            weaponNext = slot;
        }
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

    public void addWeapon(WeaponWrapper weapon) {
        WeaponWrapper weapons[] = new WeaponWrapper[this.weapons.length + 1];
        for (int i = 0; i < this.weapons.length; i ++) {
            weapons[i] = this.weapons[i];
        }
        weapons[this.weapons.length] = weapon;
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
            charSkin = getRoom().getCharacterManager().getCharacterCategories(0).getSkins().get(0);
            charSkinChanged = true;

        } else if (team == PlayerModInterface.TEAM_T) {
//            skeletonWrapper.getSkeleton().getColor().set(Color.RED);
            charSkin = getRoom().getCharacterManager().getCharacterCategories(1).getSkins().get(0);
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
                gunTexture = getAsset(weapon.getWeapon(getRoom().getWeaponManager()).getTexture(), Texture.class);
                gunTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            }

            // If player has a gun in the left hand
            if (weapon.getWeapon(getRoom().getWeaponManager()).getGraphics().left) {
                if (leftHandSprite == null || leftHandSprite.getTexture() != gunTexture) {
                    leftHandSprite = new Sprite(gunTexture);
                    leftHandSprite.setOrigin(weapons[weaponCurrent].getWeapon(getRoom().getWeaponManager()).getGraphics().x_offset,
                            weapons[weaponCurrent].getWeapon(getRoom().getWeaponManager()).getGraphics().y_offset);
                }

                leftHandSprite.setX(leftHand.getWorldX() + x - weapons[weaponCurrent].getWeapon(getRoom().getWeaponManager()).getGraphics().x_offset);
                leftHandSprite.setY(leftHand.getWorldY() + y - weapons[weaponCurrent].getWeapon(getRoom().getWeaponManager()).getGraphics().y_offset);
                leftHandSprite.setRotation(leftHand.getWorldRotation() - 90);

                leftHandSprite.draw(spriteBatch);
            }
            // If player has a gun in the right hand
            if (weapon.getWeapon(getRoom().getWeaponManager()).getGraphics().right) {
                if (rightHandSprite == null || rightHandSprite.getTexture() != gunTexture) {
                    rightHandSprite = new Sprite(gunTexture);
                    rightHandSprite.setOrigin(weapons[weaponCurrent].getWeapon(getRoom().getWeaponManager()).getGraphics().x_offset,
                            weapons[weaponCurrent].getWeapon(getRoom().getWeaponManager()).getGraphics().y_offset);
                }

                rightHandSprite.setX(rightHand.getWorldX() + x - weapons[weaponCurrent].getWeapon(getRoom().getWeaponManager()).getGraphics().x_offset);
                rightHandSprite.setY(rightHand.getWorldY() + y - weapons[weaponCurrent].getWeapon(getRoom().getWeaponManager()).getGraphics().y_offset);
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
                    getSkeletonWrapper().startAnimation(weapons[weaponCurrent].getWeapon(getRoom().getWeaponManager()).getAnimations().shoot, stateTimer/60f, false);
                    break;
                case STATE_RELOADING:
                    getSkeletonWrapper().startAnimation(weapons[weaponCurrent].getWeapon(getRoom().getWeaponManager()).getAnimations().reload, stateTimer / 60f, false);
                    break;
                case STATE_COCK:
                    getSkeletonWrapper().startAnimation(weapons[weaponCurrent].getWeapon(getRoom().getWeaponManager()).getAnimations().cock, stateTimer / 60f, false);
                    break;
                case STATE_EQUIP:
                    getSkeletonWrapper().startAnimation(weapons[weaponCurrent].getWeapon(getRoom().getWeaponManager()).getAnimations().equip, stateTimer / 60f, false);
                    break;
                case STATE_DEQUIP:
                    getSkeletonWrapper().startAnimation(weapons[weaponCurrent].getWeapon(getRoom().getWeaponManager()).getAnimations().unequip, stateTimer / 60f, false);
                    break;
            }
        }


        // Update glow
        glow.setPosition(EngineConfig.toMetres(x), EngineConfig.toMetres(y));
        glow.setActive(glowActive);

        // Create a bullet
        if (makeBullet) {
            float range = weapons[weaponCurrent].getWeapon(getRoom().getWeaponManager()).getRange();
            if (range == -1) {
                range = EngineConfig.toPixels(100);
            }

            for (int i = 0; i < weapons[weaponCurrent].getWeapon(getRoom().getWeaponManager()).getBullets(); i ++) {
                // Calculate visual spread
                float weaponSpread = weapons[weaponCurrent].getWeapon(getRoom().getWeaponManager()).getSpread();
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
//        if (Gdx.input != null) {
//            moveSpeed += (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) ? .5f : 0);
//        }
//        float moveSpeed = 3f;
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
                if (reloadKey && weapons[weaponCurrent].bulletsIn < weapons[weaponCurrent].getWeapon(getRoom().getWeaponManager()).getClipSize()) {
                    stateTimer = Math.round(weapons[weaponCurrent].getWeapon(getRoom().getWeaponManager()).getReloadDuration() * 60);
                    state = STATE_RELOADING;
                } else if (shootKey && weaponCurrent != -1) { // otherwise is shoot key pressed?
                    stateTimer = Math.max(0, weapons[weaponCurrent].getWeapon(getRoom().getWeaponManager()).getRateOfFire());
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
                        stateTimer = Math.round(weapons[weaponCurrent].getWeapon(getRoom().getWeaponManager()).getEquipDuration() * 60);
                    } else {
                        stateTimer = 0;
                    }
                }
                break;

            case STATE_RELOADING:
                if (stateTimer <= 0) {
                    if (weapons[weaponCurrent].getWeapon(getRoom().getWeaponManager()).getReloadType() == Weapon.RELOAD_FULL_COCK) {
                        state = STATE_COCK;
                        stateTimer = Math.round(weapons[weaponCurrent].getWeapon(getRoom().getWeaponManager()).getCockDuration() * 60);
                        weapons[weaponCurrent].bulletsIn = weapons[weaponCurrent].getWeapon(getRoom().getWeaponManager()).getClipSize();
                    } else if (weapons[weaponCurrent].getWeapon(getRoom().getWeaponManager()).getReloadType() == Weapon.RELOAD_SHOTGUN) {
                        // Add a shell
                        weapons[weaponCurrent].bulletsIn ++;
                        // If full
                        if (weapons[weaponCurrent].bulletsIn >= weapons[weaponCurrent].getWeapon(getRoom().getWeaponManager()).getClipSize()) {
                            // Cock the gun
                            state = STATE_COCK;
                            stateTimer = Math.round(weapons[weaponCurrent].getWeapon(getRoom().getWeaponManager()).getCockDuration() * 60);
                            weapons[weaponCurrent].bulletsIn = weapons[weaponCurrent].getWeapon(getRoom().getWeaponManager()).getClipSize();
                        } else {
                            // Load another shell
                            stateTimer = Math.round(weapons[weaponCurrent].getWeapon(getRoom().getWeaponManager()).getReloadDuration() * 60);
                            state = STATE_RELOADING;
                            stateChange = true;
                        }
                    } else {
                        state = STATE_IDLE;
                        weapons[weaponCurrent].bulletsIn = weapons[weaponCurrent].getWeapon(getRoom().getWeaponManager()).getClipSize();
                    }
                }
                break;

            case STATE_SHOOTING:
                if (stateTimer <= 0) {
                    if (weapons[weaponCurrent].getWeapon(getRoom().getWeaponManager()).getRateOfFire() == -1) {
                        state = STATE_COCK;
                        stateTimer = Math.round(weapons[weaponCurrent].getWeapon(getRoom().getWeaponManager()).getCockDuration() * 60);
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
                    getSkeletonWrapper().setIdleAnimation(weapons[weaponCurrent].getWeapon(getRoom().getWeaponManager()).getAnimations().idle, 2);
                    getSkeletonWrapper().startIdle();
                    state = STATE_EQUIP;
                    stateTimer = Math.round(weapons[weaponCurrent].getWeapon(getRoom().getWeaponManager()).getEquipDuration() * 60);

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
        int soundRange = getRoom().getConfig().mp_sound_range;
        float volume = Math.max((soundRange -
                new Vector2(x, y)
                .dst(getRoom().getMap().getCameraCenterX(),
                        getRoom().getMap().getCameraCenterY())
        ) / soundRange, 0) * SOUND_MODIFIER;

        if (event.getData().getName().contentEquals("ev_shoot")) {
            if (weapons[weaponCurrent].bulletsIn >= 0) {
               getRoom().getWeaponManager()
                        .playSound(weapons[weaponCurrent].getWeapon(getRoom().getWeaponManager()), WeaponManager.SoundType.SHOOT, volume, getRoom().getGame());
            } else {
                getRoom().getWeaponManager()
                        .playSound(weapons[weaponCurrent].getWeapon(getRoom().getWeaponManager()), WeaponManager.SoundType.EMPTY, volume, getRoom().getGame());
            }
        } else if (event.getData().getName().contentEquals("ev_equip")) {
            getRoom().getWeaponManager()
                    .playSound(weapons[weaponCurrent].getWeapon(getRoom().getWeaponManager()), WeaponManager.SoundType.EQUIP, volume, getRoom().getGame());
        } else if (event.getData().getName().contentEquals("ev_dequip")) {
            getRoom().getWeaponManager()
                    .playSound(weapons[weaponCurrent].getWeapon(getRoom().getWeaponManager()), WeaponManager.SoundType.DEQUIP, volume, getRoom().getGame());
        } else if (event.getData().getName().contentEquals("ev_eject")) {
            getRoom().getWeaponManager()
                    .playSound(weapons[weaponCurrent].getWeapon(getRoom().getWeaponManager()), WeaponManager.SoundType.EJECT, volume, getRoom().getGame());
        } else if (event.getData().getName().contentEquals("ev_insert")) {
            getRoom().getWeaponManager()
                    .playSound(weapons[weaponCurrent].getWeapon(getRoom().getWeaponManager()), WeaponManager.SoundType.INSERT, volume, getRoom().getGame());
        } else if (event.getData().getName().contentEquals("ev_cock")) {
            getRoom().getWeaponManager()
                    .playSound(weapons[weaponCurrent].getWeapon(getRoom().getWeaponManager()), WeaponManager.SoundType.COCK, volume, getRoom().getGame());
        } else if (event.getData().getName().contentEquals("ev_empty")) {
            getRoom().getWeaponManager()
                    .playSound(weapons[weaponCurrent].getWeapon(getRoom().getWeaponManager()), WeaponManager.SoundType.EMPTY, volume, getRoom().getGame());
        }
    }

    public void setReloading(boolean reload) {
        this.reloadKey = reload;
    }

    public boolean getReloading() {
        return this.reloadKey;
    }

    public boolean canSeePlayer(Player player) {
        cannotSee = false;
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

    public void setZoom(boolean zoom) {
        this.zoomKey = zoom;
    }

    public boolean isPlayerChanged() {
        int newMove = ((moveLeft ? 1 : 0) * 1000)
                + ((moveRight ? 1 : 0) * 100)
                + ((moveUp ? 1 : 0) * 10)
                + ((moveDown ? 1 : 0));
        if (newMove != lastMove
                || lastShoot != shootKey
                || lastReload != reloadKey
                || lastZoom != zoomKey
                || lastUpdate <= getRoom().getGameManager().getTimestamp()) {
//        if (lastUpdate <= getRoom().getGameManager().getTimestamp()) {

            lastUpdate = getRoom().getGameManager().getTimestamp() + 1000/getRoom().getSocket().getServerConfig().cl_tickrate;
            lastMove = newMove;
            lastShoot = shootKey;
            lastReload = reloadKey;
            lastZoom = zoomKey;

            return true;
        }
        return false;
    }

    public PlayerInputPacket constructUpdatePacket() {
        // Send move packet
        PlayerInputPacket playerMovePacket = new PlayerInputPacket();
        playerMovePacket.moveLeft = moveLeft;
        playerMovePacket.moveRight = moveRight;
        playerMovePacket.moveUp = moveUp;
        playerMovePacket.moveDown = moveDown;
        playerMovePacket.direction = getDirection();
        playerMovePacket.x = x;
        playerMovePacket.y = y;
        playerMovePacket.reload = reloadKey;
        playerMovePacket.shoot = shootKey;
        playerMovePacket.zoom = zoomKey;
        playerMovePacket.number = getRoom().getGameManager().getInputNumber(x, y);

        return playerMovePacket;
    }
}

