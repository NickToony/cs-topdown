package com.nicktoony.cstopdown.mods.gamemode.implementations;

import com.nicktoony.cstopdown.mods.gamemode.GameModeMod;
import com.nicktoony.cstopdown.mods.gamemode.PlayerModInterface;
import com.nicktoony.cstopdown.rooms.game.entities.players.BotPlayer;

import java.util.Random;

/**
 * Created by Nick on 24/03/2016.
 */
public class Zombies extends GameModeMod {

    private long time = System.currentTimeMillis();
    private Random random = new Random();
    private int ZOMBIES = 2;
    private int ZOMBIE_HEALTH = 300;

    @Override
    public void evInit() {
        getServerConfig().mp_friendly_fire = false;
    }

    @Override
    public void evRoundStart() {

        for (PlayerModInterface player : getActivePlayers()) {
            player.joinTeam(PlayerModInterface.TEAM_CT);
            player.spawn();
        }

        if (getActivePlayers().size() > 2) {
            while (getAlivePlayers(PlayerModInterface.TEAM_T).size() < Math.max(Math.min(ZOMBIES, getActivePlayers().size()-1), 1)) {
                PlayerModInterface randomPlayer =
                        getActivePlayers().get(random.nextInt(getActivePlayers().size() - 1));

                for (PlayerModInterface player : getActivePlayers()) {
                    if (player == randomPlayer) {
                        player.joinTeam(PlayerModInterface.TEAM_T);
                        player.spawn();
                    }
                }
            }
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
        playerKilled.joinTeam(PlayerModInterface.TEAM_T);

        playerKilled.spawn(3);
        playerKilled.message("[WHITE]You will respawn as a zombie in 3 seconds.");
    }

    @Override
    public void evPlayerConnected(PlayerModInterface player) {
        player.joinTeam(PlayerModInterface.TEAM_CT);
    }

    @Override
    public void evPlayerDisconnected(PlayerModInterface player) {

    }

    @Override
    public void evPlayerMessage(PlayerModInterface player) {

    }

    @Override
    public void evPlayerJoinedTeam(PlayerModInterface player, boolean forced) {
        // If it was the only player
        if (!forced) {
            if (getActivePlayers(PlayerModInterface.TEAM_CT).size() == 0
                    || getActivePlayers(PlayerModInterface.TEAM_T).size() == 0) {
                    endRound();
            } else {
                player.spawn();
            }
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

            if (player.isBot()) {
                player.setTraits(new BotPlayer.BotTraits(30, 10, 60, 0));
            }
        } else {
            player.setMaxHealth(ZOMBIE_HEALTH);
            player.setHealth(ZOMBIE_HEALTH);

            if (player.isBot()) {
                player.setTraits(new BotPlayer.BotTraits(0, 100, 0, 10));
            }
        }
    }
}
