package com.nick.ant.towerdefense.components.weapons;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.XmlReader;
import com.nick.ant.towerdefense.Game;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Nick on 25/09/2014.
 */
public class WeaponManager {

    // SINGULAR BEGIN
    private static WeaponManager instance;

    public static WeaponManager getInstance()   {
        if (instance == null)   {
            instance = new WeaponManager();
        }
        return instance;
    }
    // SINGULAR END


    private List<WeaponCategory> weaponCategories = new ArrayList<WeaponCategory>();
    private Map<String, Weapon> weapons = new HashMap<String, Weapon>();

    public WeaponManager() {
        File file = Gdx.files.internal("weapons").file();
        try {
            findCategories(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void findCategories(File file) throws FileNotFoundException {
        for (String subDirectory : file.list()) {
            File sub = new File(file.getPath() + "/" + subDirectory);
            if (sub.isDirectory()) {
                WeaponCategory weaponCategory = new WeaponCategory(sub.getName());
                weaponCategories.add(weaponCategory);
                weaponCategory.getWeapons().addAll(findWeapons(sub));
            }
        }
    }

    private List<Weapon> findWeapons(File file) throws FileNotFoundException {
        List<Weapon> weaponsToReturn = new ArrayList<Weapon>();
        for (String subDirectory : file.list()) {
            File sub = new File(file.getPath() + "/" + subDirectory);
            if (sub.isDirectory()) {
                Weapon weapon = Game
                        .getGson()
                        .fromJson(new FileReader(new File(sub + "/definition.json")),
                                Weapon.class);
                weapons.put(sub.getName(), weapon);
                weaponsToReturn.add(weapon);

                // assign category
                weapon.setCategory(file.getName());
                weapon.setKey(sub.getName());
            }
        }
        return weaponsToReturn;
    }

    public void dispose() {
        weaponCategories.clear();
        instance = null;
    }

    public Weapon getWeapon(String string) {
        return weapons.get(string);
    }
}
