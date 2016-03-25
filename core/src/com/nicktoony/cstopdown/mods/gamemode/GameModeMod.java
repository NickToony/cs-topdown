package com.nicktoony.cstopdown.mods.gamemode;

import com.nicktoony.cstopdown.config.ServerConfig;
import com.nicktoony.cstopdown.networking.server.SBServer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nick on 23/03/2016.
 */
public abstract class GameModeMod {

    private SBServer server;

    /**
     * You can't override this
     */
    final public void setup(SBServer server) {
        this.server = server;
    }

    public abstract void evInit();
    public abstract void evRoundStart();
    public abstract void evRoundEnd();
    public abstract void evPlayerKilled(PlayerModInterface playerKilled, PlayerModInterface playerKiller);
    public abstract void evPlayerConnected(PlayerModInterface player);
    public abstract void evPlayerDisconnected(PlayerModInterface player);
    public abstract void evPlayerMessage(PlayerModInterface player);
    public abstract void evPlayerJoinedTeam(PlayerModInterface player);
    public abstract void evFreezeTimeEnd();

    protected ServerConfig getServerConfig() {
        return server.getConfig();
    }

    protected List<? extends PlayerModInterface> getAllPlayers() {
        return server.getClients();
    }

    protected List<PlayerModInterface> getAlivePlayers() {
        List<PlayerModInterface> list = new ArrayList<>();
        for (PlayerModInterface player : getAllPlayers()) {
            if (player.isAlive()) {
                list.add(player);
            }
        }
        return list;
    }

    protected List<PlayerModInterface> getDeadPlayers() {
        List<PlayerModInterface> list = new ArrayList<>();
        for (PlayerModInterface player : getAllPlayers()) {
            if (!player.isAlive() && player.getTeam() != PlayerModInterface.TEAM_SPECTATE) {
                list.add(player);
            }
        }
        return list;
    }

    protected List<PlayerModInterface> getActivePlayers() {
        List<PlayerModInterface> list = new ArrayList<>();
        for (PlayerModInterface player : getAllPlayers()) {
            if (player.getTeam() != PlayerModInterface.TEAM_SPECTATE) {
                list.add(player);
            }
        }
        return list;
    }

    protected void restartGame() {
        server.startRound();
    }

}
