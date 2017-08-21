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
    String getWeaponName();
    int getID();
    int getTeam();
    boolean isBot();
    void setHealth(int health);
    void setMaxHealth(int health);
    void giveWeapon(String weaponKey);
    void setWeapon(String weaponKey);

    // Actions
    boolean spawn();
    boolean spawn(int seconds);
    void slay(boolean notify);
    void message(String message);
    void joinTeam(int team);

}
