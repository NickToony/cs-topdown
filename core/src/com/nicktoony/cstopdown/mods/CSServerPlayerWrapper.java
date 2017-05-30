package com.nicktoony.cstopdown.mods;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.nicktoony.cstopdown.mods.gamemode.PlayerModInterface;
import com.nicktoony.cstopdown.networking.packets.helpers.WeaponWrapper;
import com.nicktoony.cstopdown.networking.server.CSServer;
import com.nicktoony.cstopdown.networking.server.CSServerClientHandler;
import com.nicktoony.cstopdown.rooms.game.entities.players.Player;
import com.nicktoony.cstopdown.rooms.game.entities.objectives.Spawn;
import com.nicktoony.engine.components.PhysicsEntity;
import com.nicktoony.engine.components.PlayerListener;
import com.nicktoony.engine.services.weapons.WeaponManager;

import java.util.Random;

/**
 * Created by Nick on 24/03/2016.
 */
public abstract class CSServerPlayerWrapper implements PlayerModInterface, PlayerListener {

    protected int team = TEAM_SPECTATE;
    protected Player player;
    protected CSServer server;
    protected CSServerClientHandler client;
    private Player entityHit;
    private Vector2 pointHit;
    private Random random = new Random();

    private RayCastCallback shootCallback = new RayCastCallback() {
        @Override
        public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
            if (fixture.getBody().getUserData() != null) {
                PhysicsEntity entity = (PhysicsEntity) fixture.getBody().getUserData();
                if (entity == player) {
                    return -1;
                } else if (entity instanceof Player) {
                    entityHit = (Player) entity;
                    pointHit = point;
                    return fraction;
                }
            }
            entityHit = null;
            pointHit = null;
            return fraction;
        }
    };

    public CSServerPlayerWrapper(CSServer server, CSServerClientHandler client) {
        this.server = server;
        this.client = client;
    }

    @Override
    public int getHealth() {
        return player.getHealth();
    }

    @Override
    public int getMaxHealth() {
        return 100;
    }

    @Override
    public float getX() {
        return player.getX();
    }

    @Override
    public float getY() {
        return player.getY();
    }

    @Override
    public boolean isAlive() {
        return player != null;
    }

    @Override
    public String getName() {
        return "Player " + this;
    }

    @Override
    public boolean spawn() {
        if (team != TEAM_SPECTATE) {
            int index = server.getRoom().getMap().spawnIndex[getTeam()];
            Spawn spawn = server.getRoom().getMap().getSpawns(team).get(index);
            createPlayer(spawn.x, spawn.y);
            index ++;
            if (index >= server.getRoom().getMap().getSpawns(team).size()) {
                index = 0;
            }
            server.getRoom().getMap().spawnIndex[getTeam()] = index;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void slay(boolean notify) {
        if (player != null) {
            destroyPlayer(notify);
        }
    }

    @Override
    public void message(String message) {

    }

    public void destroyPlayer(boolean notify) {
        // Remove the player from the room (which also disposes the object)
        server.getRoom().deleteRenderable(player);
        // No player
        player = null;
        // Notify all
        if (notify) {
            server.notifyModPlayerDestroyed(this);
        }

        client.destroyPlayer();
    }

    public void createPlayer(float x, float y) {
        // Destroy current player
        slay(false);

        // Spawn a new one
        player = server.getGame().createPlayer(getID(), x, y, isBot());
        player.setWeapons(new WeaponWrapper[]{
                new WeaponWrapper(WeaponManager.getInstance().getWeapon("shotgun_spas")),
                new WeaponWrapper(WeaponManager.getInstance().getWeapon("rifle_m4a1")),
                new WeaponWrapper(WeaponManager.getInstance().getWeapon("rifle_ak47")),
                new WeaponWrapper(WeaponManager.getInstance().getWeapon("rifle_awp")),
                new WeaponWrapper(WeaponManager.getInstance().getWeapon("pistol_pistol")),

        });
        player.setNextWeapon(0);
        player.setHealth(getMaxHealth());
        player.setListener(this);

        client.createPlayer();
    }

    @Override
    public void joinTeam(int team) {
        this.team = team;

        server.notifyModPlayerJoinedTeam(this);
    }

    @Override
    public int getTeam() {
        return team;
    }

    public Player getPlayer() {
        return player;
    }

    public void update() {
        if (isAlive() && getHealth() <= 0) {
            slay(true);
            server.notifyModPlayerKilled(this, this);
        }
    }

    @Override
    public void shoot(WeaponWrapper weapon) {
        for (int i = 0; i < player.getCurrentWeaponObject().weapon.getBullets(); i ++) {
            // Calculate visual spread
            float weaponSpread = player.getCurrentWeaponObject().weapon.getSpread();
            float spread = 0;
            if (weaponSpread > 0) {
                spread = random.nextInt((int) weaponSpread * 2) - weaponSpread;
            }

            // Figure out where
            double radians = Math.toRadians(player.getActualDrection()+90+spread);
            Vector2 vecTo = new Vector2((float)Math.cos(radians), (float)Math.sin(radians)).scl(100).add(player.getBody().getPosition());
            Vector2 vecFrom = player.getBody().getPosition();

            // Perform a raycast
            entityHit = null;
            server.getRoom().getWorld().rayCast(shootCallback, vecFrom, vecTo);

            if (entityHit != null) {
//                System.out.println(pointHit.dst(player.getBody().getPosition()));

                // Kill them
                int currentHealth = entityHit.getHealth();
                entityHit.setHealth(currentHealth - player.getCurrentWeaponObject().weapon.getDamage().medium);
            }
        }



    }
}
