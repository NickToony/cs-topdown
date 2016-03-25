package com.nicktoony.cstopdown.mods.gamemode.implementations;

import com.nicktoony.cstopdown.mods.gamemode.GameModeMod;
import com.nicktoony.cstopdown.mods.gamemode.PlayerModInterface;

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
            player.spawn();
        }
    }

    @Override
    public void evRoundEnd() {

    }

    @Override
    public void evPlayerKilled(PlayerModInterface playerKilled, PlayerModInterface playerKiller) {

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
    public void evPlayerJoinedTeam(PlayerModInterface player) {
        // If it was the only player
//        if (getActivePlayers().size() == 1) {
            restartGame();
//        }
    }

    @Override
    public void evFreezeTimeEnd() {

    }
}
