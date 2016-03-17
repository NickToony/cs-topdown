package com.nicktoony.cstopdown.services;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Nick on 08/09/2014.
 */
public class SoundManager {
    private static Map<String, Sound> loadedSounds = new HashMap<String, Sound>();

    public static Sound getSound(FileHandle fileHandle) {
        if (!loadedSounds.containsKey(fileHandle.toString()))    {
            loadedSounds.put(fileHandle.toString(), Gdx.audio.newSound(fileHandle));
        }

        return loadedSounds.get(fileHandle.toString());
    }

    public static void dispose() {
        for (Map.Entry<String, Sound> texture : loadedSounds.entrySet()) {
            texture.getValue().dispose();
        }
        loadedSounds.clear();
    }
}
