package com.nicktoony.cstopdown.mods.gamemode;

/**
 * Created by Nick on 24/03/2016.
 */
public interface PlayerModInterface {
    int TEAM_SPECTATE = 0;
    int TEAM_CT = 1;
    int TEAM_T = 2;

    int getHealth();
    int getMaxHealth();
    float getX();
    float getY();
    boolean isAlive();
    String getName();
    int getID();
    int getTeam();
    boolean isBot();

    // Actions
    boolean spawn();
    void slay(boolean notify);
    void message(String message);
    void joinTeam(int team);

}
