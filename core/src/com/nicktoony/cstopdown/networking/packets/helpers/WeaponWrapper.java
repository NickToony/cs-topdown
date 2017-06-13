package com.nicktoony.cstopdown.networking.packets.helpers;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.nicktoony.engine.services.weapons.Weapon;
import com.nicktoony.engine.services.weapons.WeaponManager;

/**
 * Created by Nick on 18/03/2016.
 */
public class WeaponWrapper implements Json.Serializable {
    private Weapon weapon = null;
    public String weaponKey;
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
        json.writeValue("weaponKey", weapon.getKey());
        json.writeValue("bulletsIn", bulletsIn);
        json.writeValue("bulletsOut", bulletsOut);
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        this.weaponKey = jsonData.getString("weaponKey");
        this.weapon = null;
        this.bulletsIn = jsonData.getInt("bulletsIn");
        this.bulletsOut = jsonData.getInt("bulletsOut");
    }

    public Weapon getWeapon(WeaponManager weaponManager) {
        if (this.weapon == null) {
            this.weapon =  weaponManager.getWeapon(this.weaponKey);
        }
        return this.weapon;
    }

    public void setWeapon(Weapon weapon) {
        this.weapon = weapon;
    }
}
