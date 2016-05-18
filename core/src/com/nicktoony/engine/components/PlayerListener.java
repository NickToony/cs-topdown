package com.nicktoony.engine.components;

import com.nicktoony.cstopdown.networking.packets.helpers.WeaponWrapper;

/**
 * Created by Nick on 18/05/2016.
 */
public interface PlayerListener {
    void shoot(WeaponWrapper weapon);
}
