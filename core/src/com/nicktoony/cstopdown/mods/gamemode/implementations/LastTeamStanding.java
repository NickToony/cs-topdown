package com.nicktoony.cstopdown.mods.gamemode.implementations;

import com.nicktoony.cstopdown.mods.gamemode.GameModeMod;
import com.nicktoony.cstopdown.mods.gamemode.PlayerModInterface;
import com.nicktoony.engine.services.weapons.Weapon;

/**
 * Created by Nick on 24/03/2016.
 */
public class LastTeamStanding extends GameModeMod {

    private int lastTeam = PlayerModInterface.TEAM_CT;

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

    }

    @Override
    public void evPlayerConnected(PlayerModInterface player) {
        boolean restart = true;
        for (PlayerModInterface existingPlayers : getAlivePlayers()) {
            if (!existingPlayers.isBot())
                restart = false;
        }

        player.joinTeam(lastTeam);
        if (lastTeam == PlayerModInterface.TEAM_CT)
            lastTeam = PlayerModInterface.TEAM_T;
        else
            lastTeam = PlayerModInterface.TEAM_CT;

        if (restart) {
            this.restartGame();
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
        if (getActivePlayers().size() <= 2) {
            restartGame();
            System.out.println("Restarting");
        }
    }

    @Override
    public void evFreezeTimeEnd() {

    }

    @Override
    public void evPlayerDestroyed(PlayerModInterface player) {
        boolean alive[] = {false, false};
        for (PlayerModInterface players : getAlivePlayers()) {
            alive[players.getTeam()-1] = true;
        }

        if (!alive[0] || !alive[1]) {
            System.out.println("All players dead");
            endRound();
        }
    }

    @Override
    public void evStep() {

    }

    @Override
    public void evPlayerSpawned(PlayerModInterface player) {
        String weapons[] = new String[] {
                "rifle_m4a1", "rifle_ak47",
                "rifle_awp", "shotgun_spas", "pistol_pistol"
        };
        player.setWeapon(weapons[0]);
        for (int i = 1; i < weapons.length; i++) {
            player.giveWeapon(weapons[i]);
        }
    }
}
