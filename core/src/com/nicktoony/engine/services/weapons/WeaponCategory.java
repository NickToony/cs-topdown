package com.nicktoony.engine.services.weapons;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nick on 25/09/2014.
 */
public class WeaponCategory {
    private String name;
    private String desc;
    private List<Weapon> weapons = new ArrayList<Weapon>();

    public WeaponCategory(String name, String desc) {
        this.name = name;
        this.desc = desc;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Weapon> getWeapons() {
        return weapons;
    }

    public void setWeapons(List<Weapon> weapons) {
        this.weapons = weapons;
    }

    public String getDescription() {
        return desc;
    }
}
