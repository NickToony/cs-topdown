package com.nicktoony.cstopdown.services.weapons;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.nicktoony.cstopdown.services.weapons.config.WeaponCategoryConfig;
import com.nicktoony.cstopdown.services.weapons.config.WeaponCategoryConfigWrapper;

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
        FileHandle file = Gdx.files.internal("weapons/weapons.json");
        findCategories(file);
    }

    private void findCategories(FileHandle fileHandle) {
        Json json = new Json();
//        WeaponCategoryConfigWrapper config = json.fromJson(WeaponCategoryConfigWrapper.class, fileHandle);
        WeaponCategoryConfigWrapper config = new WeaponCategoryConfigWrapper();
        config.config = new WeaponCategoryConfig[1];
        config.config[0] = new WeaponCategoryConfig();
        config.config[0].name="Rifles";
        config.config[0].weapons = new String[1];
        config.config[0].weapons[0] = "rifle_m4a1";


        for (WeaponCategoryConfig weaponCategoryConfig :  config.config) {
            WeaponCategory weaponCategory = new WeaponCategory(weaponCategoryConfig.name);
            weaponCategories.add(weaponCategory);
//            weaponCategory.getWeapons().addAll(findWeapons(subDirectory));
            for (String weaponName : weaponCategoryConfig.weapons) {
                Weapon weapon = json.fromJson(Weapon.class,
                        Gdx.files.internal("weapons/" + weaponCategory.getName()
                                + "/" + weaponName + "/definition.json"));
                weapons.put(weaponName, weapon);

                // assign category
                weapon.setCategory(weaponCategory.getName());
                weapon.setKey(weaponName);

                weaponCategory.getWeapons().add(weapon);
            }
        }

    }

//    private void findCategories(FileHandle file)  {
//        for (FileHandle subDirectory : file.list()) {
////            FileHandle sub = new FileHandle(file.path() + "/" + subDirectory);
//            if (subDirectory.isDirectory()) {
//                WeaponCategory weaponCategory = new WeaponCategory(subDirectory.name());
//                weaponCategories.add(weaponCategory);
//                weaponCategory.getWeapons().addAll(findWeapons(subDirectory));
//            }
//        }
//    }
//
//    private List<Weapon> findWeapons(FileHandle file) {
//        List<Weapon> weaponsToReturn = new ArrayList<Weapon>();
//        Json json = new Json();
//        for (FileHandle subDirectory : file.list()) {
////            FileHandle sub = new FileHandle(file.path() + "/" + subDirectory);
//            if (subDirectory.isDirectory()) {
//                Weapon weapon = json.fromJson(Weapon.class, Gdx.files.internal(subDirectory + "/definition.json"));
//                weapons.put(subDirectory.name(), weapon);
//                weaponsToReturn.add(weapon);
//
//                // assign category
//                weapon.setCategory(file.name());
//                weapon.setKey(subDirectory.name());
//            }
//        }
//        return weaponsToReturn;
//    }

    public void dispose() {
        weaponCategories.clear();
        instance = null;
    }

    public Weapon getWeapon(String string) {
        return weapons.get(string);
    }
}
