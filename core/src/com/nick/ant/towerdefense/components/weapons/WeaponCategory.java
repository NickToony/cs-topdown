package com.nick.ant.towerdefense.components.weapons;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nick on 25/09/2014.
 */
public class WeaponCategory {
    private String categoryName;
    private List<Weapon> weapons = new ArrayList<Weapon>();

    public WeaponCategory(String categoryName) {
        this.categoryName = categoryName;
    }

    public void add(Weapon weapon) {
        this.weapons.add(weapon);
    }

    public String getCategoryName() {
        return categoryName;
    }

    public List<Weapon> getWeapons() {
        return weapons;
    }
}
