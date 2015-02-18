package com.nick.ant.towerdefense.networking.server;

/**
 * Created by Nick on 04/02/2015.
 */
public class ServerConfig {
    private String name = "A brand new server";
    private int maxPlayers = 32;
    private int port = 3453;
    private String map = "de_dust2";
    private String ip = "127.0.0.1";

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

    public String getIP() {
        return ip;
    }
}
