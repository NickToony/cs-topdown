package com.nicktoony.cstopdown.networking.packets.game;

import com.nicktoony.cstopdown.networking.packets.helpers.WeaponWrapper;
import com.nicktoony.engine.packets.Packet;

/**
 * Created by Nick on 03/01/2016.
 */
public class UpdateWeaponsPacket extends Packet {
    public int id;
    public int slot;
    public WeaponWrapper weapons[];

    public UpdateWeaponsPacket(int id, int slot, WeaponWrapper[] weapons) {
        this.id = id;
        this.slot = slot;
        this.weapons = weapons;
    }

    public UpdateWeaponsPacket() {
    }
}
