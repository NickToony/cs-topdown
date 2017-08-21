package com.nicktoony.cstopdown.mods.gamemode.implementations;

import com.nicktoony.cstopdown.mods.gamemode.GameModeMod;
import com.nicktoony.cstopdown.mods.gamemode.PlayerModInterface;

import java.util.Random;

/**
 * Created by Nick on 24/03/2016.
 */
public class Left4Dead extends GameModeMod {

    private long time = System.currentTimeMillis();
    private int ZOMBIE_HEALTH = 100;

    @Override
    public void evInit() {

    }

    @Override
    public void evRoundStart() {
        for (PlayerModInterface player : getAllPlayers()) {
            player.spawn();
        }
    }

    @Override
    public void evRoundEnd() {

    }

    @Override
    public void evPlayerShot(PlayerModInterface shooter, PlayerModInterface shot, int damage, boolean valid) {

    }

    @Override
    public void evPlayerKilled(PlayerModInterface playerKilled, PlayerModInterface playerKiller) {
        if (playerKilled.getTeam() == PlayerModInterface.TEAM_T) {
            playerKilled.spawn(3);
            playerKilled.message("[WHITE]You will respawn as a zombie in 3 seconds.");
        }
    }

    @Override
    public void evPlayerConnected(PlayerModInterface player) {
        if (player.isBot()) {
            player.joinTeam(PlayerModInterface.TEAM_T);
        } else {
            player.joinTeam(PlayerModInterface.TEAM_CT);
        }
    }

    @Override
    public void evPlayerDisconnected(PlayerModInterface player) {

    }

    @Override
    public void evPlayerMessage(PlayerModInterface player) {

    }

    @Override
    public void evPlayerJoinedTeam(PlayerModInterface player) {
        // If it was the only player
        if (getAlivePlayers(PlayerModInterface.TEAM_CT).size() == 0 && isRoundActive()) {
            message("No humans alive. Ending round.");
            endRound();
        } else {
            player.spawn();
        }
    }

    @Override
    public void evFreezeTimeEnd() {

    }

    @Override
    public void evPlayerDestroyed(PlayerModInterface player) {
        if (getAlivePlayers(PlayerModInterface.TEAM_CT).size() == 0) {
            endRound();
        }
    }

    @Override
    public void evStep() {
        if (time + (1000) < System.currentTimeMillis()) {
            for (PlayerModInterface player : getAlivePlayers(PlayerModInterface.TEAM_T)) {
                player.setHealth(player.getHealth() + 5);
            }
            time = System.currentTimeMillis();
        }
    }

    @Override
    public void evPlayerSpawned(PlayerModInterface player) {
        if (player.getTeam() == PlayerModInterface.TEAM_CT) {
            String weapons[] = new String[] {
                    "rifle_m4a1", "rifle_ak47",
                    "rifle_awp", "shotgun_spas", "pistol_pistol"
            };
            player.setWeapon(weapons[0]);
            for (int i = 1; i < weapons.length; i++) {
                player.giveWeapon(weapons[i]);
            }
            player.setMaxHealth(100);
        } else {
            player.setMaxHealth(ZOMBIE_HEALTH);
            player.setHealth(ZOMBIE_HEALTH);
        }
    }
}
