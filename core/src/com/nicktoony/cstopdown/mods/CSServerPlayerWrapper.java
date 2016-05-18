package com.nicktoony.cstopdown.mods;

import com.nicktoony.cstopdown.mods.gamemode.PlayerModInterface;
import com.nicktoony.cstopdown.networking.packets.helpers.WeaponWrapper;
import com.nicktoony.cstopdown.networking.server.CSServer;
import com.nicktoony.cstopdown.networking.server.CSServerClientHandler;
import com.nicktoony.cstopdown.rooms.game.entities.players.Player;
import com.nicktoony.cstopdown.rooms.game.entities.objectives.Spawn;
import com.nicktoony.engine.services.weapons.WeaponManager;

/**
 * Created by Nick on 24/03/2016.
 */
public abstract class CSServerPlayerWrapper implements PlayerModInterface {

    protected int team = TEAM_SPECTATE;
    protected Player player;
    protected CSServer server;
    protected CSServerClientHandler client;

    public CSServerPlayerWrapper(CSServer server, CSServerClientHandler client) {
        this.server = server;
        this.client = client;
    }

    @Override
    public int getHealth() {
        return 100;
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
    public void slay() {
        if (player != null) {
            destroyPlayer();
        }
    }

    @Override
    public void message(String message) {

    }

    public void destroyPlayer() {
        // Remove the player from the room (which also disposes the object)
        server.getRoom().deleteRenderable(player);
        // No player
        player = null;
        // Notify all
        server.notifyModPlayerDestroyed(this);

        client.destroyPlayer();
    }

    public void createPlayer(float x, float y) {
        // Destroy current player
        slay();

        // Spawn a new one
        player = server.getGame().createPlayer(getID(), x, y, isBot());
        player.setWeapons(new WeaponWrapper[]{
                new WeaponWrapper(WeaponManager.getInstance().getWeapon("shotgun_spas")),
                new WeaponWrapper(WeaponManager.getInstance().getWeapon("rifle_ak47")),
                new WeaponWrapper(WeaponManager.getInstance().getWeapon("pistol_pistol"))

        });
        player.setNextWeapon(0);

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
}
