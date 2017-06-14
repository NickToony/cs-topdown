package com.nicktoony.engine.config;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

/**
 * Created by nick on 14/07/15.
 */
public class GameConfig {
    public int resolution_x = 1280;
    public int resolution_y = 768;
    public boolean fullscreen = false;
    public String name = "";

    public void save() {
        Preferences prefs = Gdx.app.getPreferences("GameConfig");

        prefs.putInteger("resolution_x", resolution_x);
        prefs.putInteger("resolution_y", resolution_y);
        prefs.putBoolean("fullscreen", fullscreen);
        prefs.putString("name", name);

        prefs.flush();
    }

    public void load() {
        Preferences prefs = Gdx.app.getPreferences("GameConfig");

        resolution_x = prefs.getInteger("resolution_x", resolution_x);
        resolution_y = prefs.getInteger("resolution_y", resolution_y);
        fullscreen = prefs.getBoolean("fullscreen", fullscreen);
        name = prefs.getString("name", name);
    }
}
