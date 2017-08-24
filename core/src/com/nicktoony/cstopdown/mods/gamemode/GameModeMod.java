package com.nicktoony.cstopdown.mods.gamemode;

import com.nicktoony.cstopdown.networking.server.CSServer;
import com.nicktoony.cstopdown.networking.server.CSServerClientHandler;
import com.nicktoony.engine.config.ServerConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nick on 23/03/2016.
 */
public abstract class GameModeMod {

    private CSServer server;

    /**
     * You can't override this
     */
    final public void setup(CSServer server) {
        this.server = server;
    }

    public abstract void evInit();
    public abstract void evRoundStart();
    public abstract void evRoundEnd();
    public abstract void evPlayerShot(PlayerModInterface shooter, PlayerModInterface shot, int damage, boolean valid);
    public abstract void evPlayerKilled(PlayerModInterface playerKilled, PlayerModInterface playerKiller);
    public abstract void evPlayerConnected(PlayerModInterface player);
    public abstract void evPlayerDisconnected(PlayerModInterface player);
    public abstract void evPlayerMessage(PlayerModInterface player);
    public abstract void evPlayerJoinedTeam(PlayerModInterface player, boolean forced);
    public abstract void evFreezeTimeEnd();
    public abstract void evPlayerDestroyed(PlayerModInterface player);
    public abstract void evStep();
    public abstract void evPlayerSpawned(PlayerModInterface player);

    protected ServerConfig getServerConfig() {
        return server.getConfig();
    }

    protected List<? extends PlayerModInterface> getAllPlayers() {
        List<PlayerModInterface> list = new ArrayList<PlayerModInterface>();
        for (CSServerClientHandler client : server.getClients()) {
            list.add(client.getPlayerWrapper());
        }
        return list;
    }

    protected List<PlayerModInterface> getAlivePlayers() {
        List<PlayerModInterface> list = new ArrayList<PlayerModInterface>();
        for (PlayerModInterface player : getAllPlayers()) {
            if (player.isAlive()) {
                list.add(player);
            }
        }
        return list;
    }

    protected List<PlayerModInterface> getAlivePlayers(int team) {
        List<PlayerModInterface> list = new ArrayList<PlayerModInterface>();
        for (PlayerModInterface player : getAllPlayers()) {
            if (player.isAlive() && player.getTeam() == team) {
                list.add(player);
            }
        }
        return list;
    }

    protected List<PlayerModInterface> getDeadPlayers() {
        List<PlayerModInterface> list = new ArrayList<PlayerModInterface>();
        for (PlayerModInterface player : getAllPlayers()) {
            if (!player.isAlive() && player.getTeam() != PlayerModInterface.TEAM_SPECTATE) {
                list.add(player);
            }
        }
        return list;
    }

    protected List<PlayerModInterface> getDeadPlayers(int team) {
        List<PlayerModInterface> list = new ArrayList<PlayerModInterface>();
        for (PlayerModInterface player : getAllPlayers()) {
            if (!player.isAlive() && player.getTeam() == team) {
                list.add(player);
            }
        }
        return list;
    }

    protected List<PlayerModInterface> getActivePlayers(int team) {
        List<PlayerModInterface> list = new ArrayList<PlayerModInterface>();
        for (PlayerModInterface player : getAllPlayers()) {
            if (player.getTeam() == team) {
                list.add(player);
            }
        }
        return list;
    }

    protected List<PlayerModInterface> getActivePlayers() {
        List<PlayerModInterface> list = new ArrayList<PlayerModInterface>();
        for (PlayerModInterface player : getAllPlayers()) {
            if (player.getTeam() != PlayerModInterface.TEAM_SPECTATE) {
                list.add(player);
            }
        }
        return list;
    }

    protected void message(String message) {
        for (PlayerModInterface player : getAllPlayers()) {
            player.message(message);
        }
    }

    protected void restartGame() {
        server.startRound();
    }

    protected void endRound() {
        server.endRound();
    }

    protected boolean isRoundActive() {
        return server.getRoundState() == CSServer.STATE.ROUND
                || server.getRoundState() == CSServer.STATE.ROUND_FREEZETIME;
    }

    protected long getRoundSeconds() {
        return server.getRoundSeconds();
    }
}
