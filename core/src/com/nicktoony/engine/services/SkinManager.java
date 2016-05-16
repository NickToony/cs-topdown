package com.nicktoony.engine.services;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

/**
 * Created by Nick on 06/02/2015.
 */
public class SkinManager {
    public static Skin uiSkin;

    public static Skin getUiSkin() {
        if (uiSkin == null) {
            uiSkin = new Skin(Gdx.files.internal("skins/default/uiskin.json"));
        }
        return uiSkin;
    }

    public static void dispose() {
        if (uiSkin != null) {
            uiSkin.dispose();
        }
        uiSkin = null;
    }
}
