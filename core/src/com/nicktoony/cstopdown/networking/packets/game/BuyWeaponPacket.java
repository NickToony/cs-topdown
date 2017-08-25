package com.nicktoony.cstopdown.networking.packets.game;

import com.nicktoony.engine.packets.Packet;
import com.nicktoony.engine.services.weapons.Weapon;

public class BuyWeaponPacket extends Packet {
    public BuyWeaponPacket() {
    }

    public BuyWeaponPacket(Weapon weapon) {
        this.weapon = weapon.getKey();
    }

    public BuyWeaponPacket(String weapon) {
        this.weapon = weapon;
    }

    private String weapon;

    public String getWeapon() {
        return weapon;
    }

    public void setWeapon(String weapon) {
        this.weapon = weapon;
    }
}
