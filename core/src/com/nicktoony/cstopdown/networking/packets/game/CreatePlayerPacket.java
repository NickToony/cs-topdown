package com.nicktoony.cstopdown.networking.packets.game;

import com.nicktoony.cstopdown.networking.packets.Packet;
import com.nicktoony.cstopdown.networking.packets.WeaponWrapper;

/**
 * Created by Nick on 03/01/2016.
 */
public class CreatePlayerPacket extends Packet {
    public float x;
    public float y;
    public int id;
    public boolean light = false;
    public WeaponWrapper weapons[];
    public int currentWeapon;
}
