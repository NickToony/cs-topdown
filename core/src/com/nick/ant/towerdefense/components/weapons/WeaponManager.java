package com.nick.ant.towerdefense.components.weapons;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.XmlReader;

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


    private Map<String, Weapon> weapons;
    private List<WeaponCategory> weaponsCategories;

    public WeaponManager()  {
        weapons = new HashMap<String, Weapon>();
        weaponsCategories = new ArrayList<WeaponCategory>();

        XmlReader xmlReader = new XmlReader();
        XmlReader.Element rootElement;
        try {
            rootElement = xmlReader.parse(Gdx.files.internal("weapons/weapon_definitions.xml"));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        for (XmlReader.Element categoryElement : rootElement.getChildrenByName("category")) {
            String categoryKey = categoryElement.getAttribute("key");
            String categoryName = categoryElement.getAttribute("name");
            WeaponCategory weaponCategory = new WeaponCategory(categoryName);
            weaponsCategories.add(weaponCategory);

            for (XmlReader.Element weaponElement : categoryElement.getChildrenByName("weapon")) {
                String weaponKey = categoryKey + "_" + weaponElement.getAttribute("key");
                String weaponName = categoryElement.getAttribute("name");

                Weapon weapon = new Weapon(weaponKey, weaponName, weaponElement);

                weaponCategory.add(weapon);
                weapons.put(weaponKey, weapon);
            }
        }
    }

    public List<WeaponCategory> getWeaponsCategories() {
        return weaponsCategories;
    }

    public Weapon getWeapon(String weaponKey)   {
        return weapons.get(weaponKey);
    }
}
