package com.nick.ant.towerdefense.components;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by Nick on 21/09/2014.
 */
public class CharacterSkin  {
    private String asset;
    private String folder;
    private String name;

    public CharacterSkin(String asset, String folder, String name)  {
        this.asset = asset;
        this.folder = folder;
        this.name = name;
    }

    public TextureRegion getTexture() {
        Texture texture = TextureManager.getTexture(asset + "/head.png");
        TextureRegion textureRegion = new TextureRegion(texture);
        textureRegion.setRegion(0, 0, 32, 32);
        return textureRegion;
    }
}