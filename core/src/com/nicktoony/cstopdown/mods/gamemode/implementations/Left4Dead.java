package com.nicktoony.cstopdown.mods.gamemode.implementations;

import com.nicktoony.cstopdown.mods.gamemode.GameModeMod;
import com.nicktoony.cstopdown.mods.gamemode.PlayerModInterface;
import com.nicktoony.cstopdown.rooms.game.entities.players.BotPlayer;

import java.util.Random;

/**
 * Created by Nick on 24/03/2016.
 */
public class Left4Dead extends GameModeMod {

    private long time = System.currentTimeMillis();
    private int ZOMBIE_HEALTH = 100;

    @Override
    public void evInit() {
        getServerConfig().tmp_map_lighting = 0.02f;
//        getServerConfig().sv_bot_prefix = "Zombie ";
        getServerConfig().mp_friendly_fire = false;
    }

    @Override
    public void evRoundStart() {
        // First: spawn all humans as CT
        int humans = 0;
        for (PlayerModInterface player : getAllPlayers()) {
            // human player?
            if (!player.isBot()) {
                // Set them to CT
                humans ++;
                player.joinTeam(PlayerModInterface.TEAM_CT);
            } else {
                // They're a bot - go zombie!
                player.joinTeam(PlayerModInterface.TEAM_T);
            }
        }

        // While we don't have 4 humans...
        while (humans < Math.min(4, getActivePlayers().size())) {
            // Find a bot!
            for (PlayerModInterface player : getAllPlayers()) {
                // Is this bot not on the CT side?
                if (player.getTeam() != PlayerModInterface.TEAM_CT) {
                    // Let's bring him over
                    player.joinTeam(PlayerModInterface.TEAM_CT);
                    humans++;
                    break;
                }
            }
        }

        // Now teams are sorted - spawn everyone
        for (PlayerModInterface player : getAllPlayers()) {
            player.spawn();
        }
    }

    @Override
    public void evRoundEnd() {

    }

    @Override
    public void evPlayerShot(PlayerModInterface shooter, PlayerModInterface shot, int damage, boolean valid) {
        if (shooter.getTeam() == PlayerModInterface.TEAM_T) {
            shot.setHealth(shot.getHealth() + (damage - 5)); // zombies only do 4 damage
        }
    }

    @Override
    public void evPlayerKilled(PlayerModInterface playerKilled, PlayerModInterface playerKiller) {
        if (playerKilled.getTeam() == PlayerModInterface.TEAM_T) {
//            playerKilled.spawn(3);
//            playerKilled.message("[WHITE]You will respawn as a zombie in 3 seconds.");
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
    public void evPlayerJoinedTeam(PlayerModInterface player, boolean forced) {
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
        if (time + (10000) < System.currentTimeMillis() && isRoundActive()) {
            for (PlayerModInterface player : getDeadPlayers(PlayerModInterface.TEAM_T)) {
                player.spawn();
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
                player.setTraits(new BotPlayer.BotTraits(100, 0, 0, 0));
            }
        } else {
            player.setMaxHealth(ZOMBIE_HEALTH);
            player.setHealth(ZOMBIE_HEALTH);
            if (player.isBot()) {
                player.setTraits(new BotPlayer.BotTraits(0, 50, 0, 40));
            }
        }
    }
}
