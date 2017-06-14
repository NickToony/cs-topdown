package com.nicktoony.engine.services.weapons;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.nicktoony.engine.MyGame;
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

    private List<WeaponCategory> weaponCategories = new ArrayList<WeaponCategory>();
    private Map<String, Weapon> weapons = new HashMap<String, Weapon>();

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

                // assign category
                weapon.setCategory(weaponCategory.getName());
                weapon.setKey(weaponName);

                weaponCategory.getWeapons().add(weapon);
            }
        }

    }

    public void preloadSounds(MyGame myGame) {
        for (Weapon weapon : weapons.values()) {
            float volume = 0.01f;
            playSound(weapon, SoundType.SHOOT, volume, myGame);
            playSound(weapon, SoundType.EQUIP, volume, myGame);
            playSound(weapon, SoundType.DEQUIP, volume, myGame);
            playSound(weapon, SoundType.EJECT, volume, myGame);
            playSound(weapon, SoundType.INSERT, volume, myGame);
            playSound(weapon, SoundType.COCK, volume, myGame);
            playSound(weapon, SoundType.EMPTY, volume, myGame);
        }
    }

    public Sound loadSound(Weapon weapon, SoundType soundType, MyGame game) {
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
                return null;
            }

            // construct the file path
            FileHandle fileHandle = Gdx.files.internal("weapons/" + weapon.getCategory()
                    + "/" + weapon.getKey() + "/" + fileName);

            // File doesn't exist? we can't load it
            return game.getAsset(fileHandle.toString(), Sound.class);
    }

    public void playSound(Weapon weapon, SoundType soundType, float volume, MyGame game) {
        if (loadSound(weapon, soundType, game) != null) {
            loadSound(weapon, soundType, game).play(volume);
        }
    }

    public void dispose() {
        weaponCategories.clear();
    }

    public Weapon getWeapon(String string) {
        return weapons.get(string);
    }
}
