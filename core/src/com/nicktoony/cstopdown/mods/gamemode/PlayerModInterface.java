package com.nicktoony.cstopdown.mods.gamemode;

import com.nicktoony.cstopdown.rooms.game.entities.players.BotPlayer;

/**
 * Created by Nick on 24/03/2016.
 */
public interface PlayerModInterface {
    int TEAM_SPECTATE = 0;
    int TEAM_CT = 1;
    int TEAM_T = 2;

    int PRIMARY = 0;
    int SECONDARY = 1;
    int MELEE = 2;

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
    void setTraits(BotPlayer.BotTraits botTraits);

    // Actions
    boolean spawn();
    boolean spawn(int seconds);
    void slay(boolean notify);
    void message(String message);
    void joinTeam(int team);

}
