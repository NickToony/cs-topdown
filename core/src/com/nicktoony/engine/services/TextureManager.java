package com.nicktoony.engine.services;

import com.badlogic.gdx.graphics.Texture;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Nick on 08/09/2014.
 */
public class TextureManager {
    private static Map<String, Texture> loadedTextures = new HashMap<String, Texture>();

    public static Texture getTexture(String texture) {
        if (!loadedTextures.containsKey(texture))    {
            loadedTextures.put(texture, new Texture(texture));
        }

        return loadedTextures.get(texture);
    }

    public static void dispose() {
        for (Map.Entry<String, Texture> texture : loadedTextures.entrySet()) {
            texture.getValue().dispose();
        }
        loadedTextures.clear();
    }
}
