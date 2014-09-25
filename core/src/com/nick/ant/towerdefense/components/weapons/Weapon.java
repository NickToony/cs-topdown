package com.nick.ant.towerdefense.components.weapons;

import com.badlogic.gdx.utils.XmlReader;

/**
 * Created by Nick on 25/09/2014.
 */
public class Weapon {
    public static final int RELOAD_FULL = 0;
    public static final int RELOAD_FULL_COCK = 1;
    public static final int RELOAD_SHOTGUN = 2;

    private String weaponKey;
    private String weaponName;

    private int clipSize;
    private int clipTotal;

    private float rateOfFire;
    private float accuracy;
    private float recoil;
    private float speed;

    private int cost;
    private int reward;

    private boolean silencer;
    private float zoom;
    private int reloadType;
    private float reloadDuration;

    private int damageLow;
    private int damageMedium;
    private int damageHigh;

    private String animationIdle;
    private String animationShoot;
    private String animationReload;
    private String animationEquip;
    private String animationUnequip;
    private String animationCock;

    private String soundFire;
    private String soundClipIn;
    private String soundClipOut;
    private String soundCock;
    private String soundEquip;
    private String soundUnequip;

    private boolean leftHand;
    private boolean rightHand;

    public Weapon(String weaponKey, String weaponName, XmlReader.Element weaponElement) {

        // DO NOT HOLD ON TO XML REFERENCE !!!!

        this.weaponKey = weaponKey;
        this.weaponName = weaponName;

        this.clipSize = weaponElement.getInt("clip", 1);
        this.clipTotal = weaponElement.getInt("total", 1);

        this.rateOfFire = weaponElement.getFloat("rof", 1);
        this.accuracy = weaponElement.getFloat("accuracy",1);
        this.recoil = weaponElement.getFloat("recoil", 1);
        this.speed = weaponElement.getFloat("speed", 1);

        this.cost = weaponElement.getInt("cost");
        this.reward = weaponElement.getInt("reward");

        this.silencer = weaponElement.getBoolean("silencer", false);
        this.zoom = weaponElement.getFloat("zoom", 0);


        String reloadType = weaponElement.get("reload_type", "full").toLowerCase();
        if (reloadType.contentEquals("full"))   {
            this.reloadType = RELOAD_FULL;
        }   else if (reloadType.contentEquals("full_cock"))    {
            this.reloadType = RELOAD_FULL_COCK;
        }   else if (reloadType.contentEquals("shotgun"))   {
            this.reloadType = RELOAD_SHOTGUN;
        }


        this.reloadDuration = weaponElement.getFloat("reload_duration", 1);

        this.damageLow = weaponElement.getChildByName("damage").getInt("low");
        this.damageMedium = weaponElement.getChildByName("damage").getInt("medium");
        this.damageHigh = weaponElement.getChildByName("damage").getInt("high");

        this.animationIdle = weaponElement.getChildByName("animations").get("idle", "");
        this.animationShoot = weaponElement.getChildByName("animations").get("shoot", "");
        this.animationReload = weaponElement.getChildByName("animations").get("reload", "");
        this.animationEquip = weaponElement.getChildByName("animations").get("equip", "");
        this.animationUnequip = weaponElement.getChildByName("animations").get("unequip", "");
        this.animationCock = weaponElement.getChildByName("animations").get("cock", "");

        this.soundFire = weaponElement.getChildByName("sounds").get("fire", "");
        this.soundClipIn = weaponElement.getChildByName("sounds").get("clip_in", "");
        this.soundClipOut = weaponElement.getChildByName("sounds").get("clip_out", "");
        this.soundCock = weaponElement.getChildByName("sounds").get("cock", "");
        this.soundEquip = weaponElement.getChildByName("sounds").get("equip", "");
        this.soundUnequip = weaponElement.getChildByName("sounds").get("unequip", "");

        this.leftHand = weaponElement.getChildByName("graphics").getBoolean("left", false);
        this.rightHand = weaponElement.getChildByName("graphics").getBoolean("right", true);
    }

    public String getWeaponKey() {
        return weaponKey;
    }

    public String getWeaponName() {
        return weaponName;
    }

    public int getClipSize() {
        return clipSize;
    }

    public int getClipTotal() {
        return clipTotal;
    }

    public float getRateOfFire() {
        return rateOfFire;
    }

    public float getAccuracy() {
        return accuracy;
    }

    public float getRecoil() {
        return recoil;
    }

    public float getSpeed() {
        return speed;
    }

    public int getCost() {
        return cost;
    }

    public int getReward() {
        return reward;
    }

    public boolean isSilencer() {
        return silencer;
    }

    public float getZoom() {
        return zoom;
    }

    public int getReloadType() {
        return reloadType;
    }

    public float getReloadDuration()    {
        return reloadDuration;
    }

    public int getDamageLow() {
        return damageLow;
    }

    public int getDamageMedium() {
        return damageMedium;
    }

    public int getDamageHigh() {
        return damageHigh;
    }

    public String getAnimationIdle() {
        return animationIdle;
    }

    public String getAnimationShoot() {
        return animationShoot;
    }

    public String getAnimationReload() {
        return animationReload;
    }

    public String getAnimationEquip() {
        return animationEquip;
    }

    public String getAnimationUnequip() {
        return animationUnequip;
    }

    public String getAnimationCock()    {
        return animationCock;
    }

    public String getSoundFire() {
        return soundFire;
    }

    public String getSoundClipIn() {
        return soundClipIn;
    }

    public String getSoundClipOut() {
        return soundClipOut;
    }

    public String getSoundCock() {
        return soundCock;
    }

    public String getSoundEquip() {
        return soundEquip;
    }

    public String getSoundUnequip() {
        return soundUnequip;
    }

    public boolean isLeftHand() {
        return leftHand;
    }

    public boolean isRightHand() {
        return rightHand;
    }
}
