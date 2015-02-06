package com.nick.ant.towerdefense.serverlist;

import com.nicktoony.gameserver.service.GameserverConfig;

/**
 * Created by Nick on 04/02/2015.
 */
public class ServerlistConfig extends GameserverConfig {

    @Override
    public String getServerUrl() {
        return "http://gameservers.nick-hope.co.uk/api/";
    }

    @Override
    public String getGameAPIKey() {
        return "pMLRbeKNtlK4Ton6d9riCviNqE0h7V0CWgLXNfx7hCVUl3ReIuCHmcVBymCe";
    }

    @Override
    public void debugLog(String message) {
        System.out.println("GameserverServiceLog :: " + message);
    }

    @Override
    public long getUpdateRate() {
        return 1 * 60 * 1000;
    }

    @Override
    public long getChangedUpdateRate() {
        return 1 * 60 * 1000;
    }
}
