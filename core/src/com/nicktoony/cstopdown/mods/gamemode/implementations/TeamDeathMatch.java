package com.nicktoony.cstopdown.mods.gamemode.implementations;

import com.nicktoony.cstopdown.mods.gamemode.GameModeMod;
import com.nicktoony.cstopdown.mods.gamemode.PlayerModInterface;

import java.util.Random;

/**
 * Created by Nick on 24/03/2016.
 */
public class TeamDeathMatch extends GameModeMod {

    private int lastTeam = PlayerModInterface.TEAM_CT;
    private long time = System.currentTimeMillis();
    private Random random = new Random();

    @Override
    public void evInit() {

    }

    @Override
    public void evRoundStart() {
        for (PlayerModInterface player : getAllPlayers()) {
//            if (player.isBot())
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
        playerKilled.spawn(3);
        playerKilled.message("[WHITE]You will respawn in 3 seconds.");
    }

    @Override
    public void evPlayerConnected(PlayerModInterface player) {
        player.joinTeam(lastTeam);
        if (lastTeam == PlayerModInterface.TEAM_CT)
            lastTeam = PlayerModInterface.TEAM_T;
        else
            lastTeam = PlayerModInterface.TEAM_CT;
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
        if (getActivePlayers().size() == 1) {
            restartGame();
        } else {
            player.spawn(3);
        }
    }

    @Override
    public void evFreezeTimeEnd() {

    }

    @Override
    public void evPlayerDestroyed(PlayerModInterface player) {

    }

    @Override
    public void evStep() {
        if (time + (1000) < System.currentTimeMillis()) {
            for (PlayerModInterface player : getAlivePlayers()) {
                player.setHealth(player.getHealth() + 2);
            }
            time = System.currentTimeMillis();
        }
    }

    @Override
    public void evPlayerSpawned(PlayerModInterface player) {
        String weapons[] = new String[] {
          "rifle_m4a1", "rifle_ak47",
                "rifle_awp", "shotgun_spas", "pistol_pistol"
        };
        player.setWeapon(weapons[random.nextInt(weapons.length)]);
    }
}
