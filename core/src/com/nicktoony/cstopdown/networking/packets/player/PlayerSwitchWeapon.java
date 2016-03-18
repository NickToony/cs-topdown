package com.nicktoony.cstopdown.networking.packets.player;

import com.nicktoony.cstopdown.networking.packets.TimestampedPacket;

/**
 * Created by Nick on 18/03/2016.
 */
public class PlayerSwitchWeapon extends TimestampedPacket {
    public int id;
    public int slot;
}
