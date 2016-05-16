package com.nicktoony.engine.services;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.esotericsoftware.spine.Skeleton;
import com.esotericsoftware.spine.SkeletonData;
import com.esotericsoftware.spine.SkeletonJson;
import com.esotericsoftware.spine.attachments.AtlasAttachmentLoader;

/**
 * Created by Nick on 21/09/2014.
 */
public class CharacterSkin  {
    private String asset;
    private String folder;
    private String name;
    private SkeletonData data;

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

        if (data == null) {
            FileHandle packedFile = Gdx.files.internal(asset + "/" + name + ".png");
            FileHandle atlasFile = Gdx.files.internal(asset + "/" + name + ".atlas");

//            if (!packedFile.exists() || !atlasFile.exists()) {
//                System.out.println("Generating the texture for " + name);
//                TexturePacker.process(asset, asset, name);
//            }

            TextureAtlas atlas = new TextureAtlas(atlasFile.path());
            for (Texture texture : atlas.getTextures()) {
                texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            }
            AtlasAttachmentLoader attachmentLoader = new AtlasAttachmentLoader(atlas);
            SkeletonJson json = new SkeletonJson(attachmentLoader);
            data = json.readSkeletonData(Gdx.files.internal(asset + "/skeleton.json"));
        }

        return new Skeleton(data);
    }
}