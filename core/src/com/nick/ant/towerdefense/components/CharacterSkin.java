package com.nick.ant.towerdefense.components;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import com.esotericsoftware.spine.Skeleton;
import com.esotericsoftware.spine.SkeletonData;
import com.esotericsoftware.spine.SkeletonJson;
import com.esotericsoftware.spine.attachments.AtlasAttachmentLoader;
import com.nick.ant.towerdefense.components.TextureManager;

import java.io.File;
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

    public Skeleton getSkeleton()   {

        File packedFile = new File(asset + "/" + name + ".png");
        File atlasFile = new File(asset + "/" + name + ".atlas");

        if (!packedFile.exists() || !atlasFile.exists())   {
            System.out.println("Generating the texture for " + name);
            TexturePacker.process(asset, asset, name);
        }

        TextureAtlas atlas = new TextureAtlas(atlasFile.getPath());
        for (Texture texture : atlas.getTextures()) {
            texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        }
        AtlasAttachmentLoader attachmentLoader = new AtlasAttachmentLoader(atlas);
        SkeletonJson json = new SkeletonJson(attachmentLoader);
        SkeletonData data = json.readSkeletonData(Gdx.files.internal(asset + "/skeleton.json"));

        Skeleton skeleton = new Skeleton(data);

        return skeleton;
    }
}