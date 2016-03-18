package com.nicktoony.cstopdown.services.weapons;

/**
 * Created by Nick on 25/09/2014.
 */
public class Weapon {

    public static final int RELOAD_FULL = 0;
    public static final int RELOAD_FULL_COCK = 1;
    public static final int RELOAD_SHOTGUN = 2;

    private String name = "Gun";

    private int clipSize = 30;
    private int clipTotal = 30;

    private int rateOfFire = 10;
    private float accuracy = 0;
    private float recoil = 0;
    private float speed = 100;

    private int cost = 0;
    private int reward = 0;

    private boolean silencer = false;
    private float zoom = 1;
    private float cockDuration = 0.2f;
    private float reloadDuration = 2;
    private float equipDuration = 0.5f;
    private String reloadType = "";

    private WeaponDamage damage = new WeaponDamage();
    private WeaponAnimation animations = new WeaponAnimation();
    private WeaponSound sounds = new WeaponSound();
    private WeaponGraphic graphics = new WeaponGraphic();

    private String key;
    private String category;
    private int reloadCalculatedType = -1;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getTexture() {
        return "weapons/" + getCategory() + "/" + getKey() + "/texture.png";
    }

    public String getName() {
        return name;
    }

    public int getClipSize() {
        return clipSize;
    }

    public int getClipTotal() {
        return clipTotal;
    }

    public int getRateOfFire() {
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

    public float getReloadDuration() {
        return reloadDuration;
    }

    public int getReloadType() {
        if (reloadCalculatedType == -1) {
            // Not caculated, so calculate now
            if (reloadType == null) {
                reloadCalculatedType = RELOAD_FULL;
            } else if (reloadType.equals("full")) {
                reloadCalculatedType = RELOAD_FULL;
            } else if (reloadType.equals("full_cock")) {
                reloadCalculatedType = RELOAD_FULL_COCK;
            } else if (reloadType.equals("shotgun")) {
                reloadCalculatedType = RELOAD_SHOTGUN;
            } else {
                reloadCalculatedType = RELOAD_FULL;
            }
        }

        return reloadCalculatedType;
    }

    public WeaponDamage getDamage() {
        return damage;
    }

    public WeaponAnimation getAnimations() {
        return animations;
    }

    public WeaponSound getSounds() {
        return sounds;
    }

    public WeaponGraphic getGraphics() {
        return graphics;
    }

    public float getCockDuration() {
        return cockDuration;
    }

    public float getEquipDuration() {
        return equipDuration;
    }
}
