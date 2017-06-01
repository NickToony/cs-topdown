package com.nicktoony.engine.services.weapons;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.nicktoony.engine.services.SoundManager;
import com.nicktoony.engine.services.weapons.config.WeaponCategoryConfig;
import com.nicktoony.engine.services.weapons.config.WeaponCategoryConfigWrapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Nick on 25/09/2014.
 */
public class WeaponManager {

    public enum SoundType {
        SHOOT,
        EQUIP,
        DEQUIP,
        EJECT,
        INSERT,
        COCK,
        EMPTY
    }

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
    private Map<Weapon, Map<SoundType, Sound>> loadedSounds = new HashMap<Weapon, Map<SoundType, Sound>>();

    public WeaponManager() {
        FileHandle file = Gdx.files.internal("weapons/weapons.json");
        findCategories(file);
    }

    private void findCategories(FileHandle fileHandle) {
        Json json = new Json();
        WeaponCategoryConfigWrapper config = json.fromJson(WeaponCategoryConfigWrapper.class, fileHandle);

        for (WeaponCategoryConfig weaponCategoryConfig :  config.config) {
            WeaponCategory weaponCategory = new WeaponCategory(weaponCategoryConfig.name);
            weaponCategories.add(weaponCategory);
//            weaponCategory.getWeapons().addAll(findWeapons(subDirectory));
            for (String weaponName : weaponCategoryConfig.weapons) {
                Weapon weapon = json.fromJson(Weapon.class,
                        Gdx.files.internal("weapons/" + weaponCategory.getName()
                                + "/" + weaponName + "/definition.json"));
                weapons.put(weaponName, weapon);
                loadedSounds.put(weapon, new HashMap<SoundType, Sound>());

                // assign category
                weapon.setCategory(weaponCategory.getName());
                weapon.setKey(weaponName);

                weaponCategory.getWeapons().add(weapon);
            }
        }

    }

    public void preloadSounds() {
        for (Weapon weapon : weapons.values()) {
            playSound(weapon, SoundType.SHOOT, 0);
            playSound(weapon, SoundType.EQUIP, 0);
            playSound(weapon, SoundType.DEQUIP, 0);
            playSound(weapon, SoundType.EJECT, 0);
            playSound(weapon, SoundType.INSERT, 0);
            playSound(weapon, SoundType.COCK, 0);
            playSound(weapon, SoundType.EMPTY, 0);
        }
    }

    public Sound loadSound(Weapon weapon, SoundType soundType) {
        if (loadedSounds.get(weapon) == null) {
            return null;
        }

        // If already loaded
        if (loadedSounds.get(weapon).containsKey(soundType)) {
            // Just return that
            return loadedSounds.get(weapon).get(soundType);
        } else {
            String fileName;
            switch (soundType) {
                case SHOOT:
                    fileName = weapon.getSounds().shoot;
                    break;
                case EQUIP:
                    fileName = weapon.getSounds().equip;
                    break;
                case DEQUIP:
                    fileName = weapon.getSounds().dequip;
                    break;
                case EJECT:
                    fileName = weapon.getSounds().eject;
                    break;
                case INSERT:
                    fileName = weapon.getSounds().insert;
                    break;
                case COCK:
                    fileName = weapon.getSounds().cock;
                    break;
                case EMPTY:
                    fileName = weapon.getSounds().empty;
                    break;
                default:
                    fileName = "";
            }

            // If it's undefined, we have nothing to load
            if (fileName.isEmpty()) {
                loadedSounds.get(weapon).put(soundType, null);
                return null;
            }

            // construct the file path
            FileHandle fileHandle = Gdx.files.internal("weapons/" + weapon.getCategory()
                    + "/" + weapon.getKey() + "/" + fileName);

            // File doesn't exist? we can't load it
            if (!fileHandle.exists()) {
                loadedSounds.get(weapon).put(soundType, null);
                return null;
            }

            Sound sound = SoundManager.getSound(fileHandle);
            if (sound != null) {
                loadedSounds.get(weapon).put(soundType, sound);
                return sound;
            } else {
                return null;
            }
        }
    }

    public void playSound(Weapon weapon, SoundType soundType, float volume) {
        if (loadSound(weapon, soundType) != null) {
            loadSound(weapon, soundType).play(volume);
        }
    }

    public void dispose() {
        weaponCategories.clear();
        instance = null;
    }

    public Weapon getWeapon(String string) {
        return weapons.get(string);
    }
}
