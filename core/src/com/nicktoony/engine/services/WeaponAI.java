package com.nicktoony.engine.services;

/**
 * Created by Nick on 03/06/2017.
 */
public class WeaponAI {
    public enum STYLE {
        DEFAULT,
        SNIPER
    }

    public String style = "";
    public int engage = -1;

    public STYLE getStyle() {

        if (style.contains("sniper")) return STYLE.SNIPER;

        return STYLE.DEFAULT;
    }

    public int getEngage() {
        return engage;
    }
}
