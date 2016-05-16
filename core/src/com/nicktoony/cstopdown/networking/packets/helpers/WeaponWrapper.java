package com.nicktoony.cstopdown.networking.packets.helpers;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.nicktoony.engine.services.weapons.Weapon;
import com.nicktoony.engine.services.weapons.WeaponManager;

/**
 * Created by Nick on 18/03/2016.
 */
public class WeaponWrapper implements Json.Serializable {
    public Weapon weapon;
    public int bulletsIn;
    public int bulletsOut;

    public WeaponWrapper() {
    }

    public WeaponWrapper(Weapon weapon) {
        this.weapon = weapon;
        this.bulletsIn = weapon.getClipSize();
        this.bulletsOut = weapon.getClipTotal();
    }

    @Override
    public void write(Json json) {
        json.writeValue("weapon", weapon.getKey());
        json.writeValue("bulletsIn", bulletsIn);
        json.writeValue("bulletsOut", bulletsOut);
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        String weaponKey = jsonData.getString("weapon");
        this.weapon = WeaponManager.getInstance().getWeapon(weaponKey);
        this.bulletsIn = jsonData.getInt("bulletsIn");
        this.bulletsOut = jsonData.getInt("bulletsOut");
    }
}
