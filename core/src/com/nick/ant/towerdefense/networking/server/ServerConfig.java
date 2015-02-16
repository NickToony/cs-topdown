package com.nick.ant.towerdefense.networking.server;

/**
 * Created by Nick on 04/02/2015.
 */
public class ServerConfig {
    private String name;
    private int maxPlayers;
    private int port;
    private String map;

    public String getName() {
        return name;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public int getPort() {
        return port;
    }

    public String getMap() {
        return map;
    }
}
