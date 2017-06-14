package com.nicktoony.engine.services;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.esotericsoftware.spine.Skeleton;
import com.esotericsoftware.spine.SkeletonData;
import com.esotericsoftware.spine.SkeletonJson;
import com.esotericsoftware.spine.attachments.AtlasAttachmentLoader;
import com.esotericsoftware.spine.attachments.RegionAttachment;

/**
 * Created by Nick on 21/09/2014.
 */
public class CharacterSkin  {
    private String asset;
    private String folder;
    private String name;
    private SkeletonData data;
    private TextureAtlas atlas;

    public CharacterSkin(String asset, String folder, String name)  {
        this.asset = asset;
        this.folder = folder;
        this.name = name;
    }

//    public TextureRegion getTexture() {
//        Texture texture = TextureManager.getTexture(asset + "/head.png");
//        TextureRegion textureRegion = new TextureRegion(texture);
//        textureRegion.setRegion(0, 0, 32, 32);
//
//        return textureRegion;
//    }

    public Skeleton getSkeleton()   {

        loadAtlas();

        if (data == null) {
            AtlasAttachmentLoader attachmentLoader = new AtlasAttachmentLoader(atlas);
            SkeletonJson json = new SkeletonJson(attachmentLoader);
            data = json.readSkeletonData(Gdx.files.internal(asset + "/skeleton.json"));
        }

        return new Skeleton(data);
    }

    public void applySkin(Skeleton skeleton) {

        loadAtlas();

        applySkinPart(skeleton, atlas, "torso");
        applySkinPart(skeleton, atlas, "left_hand");
        applySkinPart(skeleton, atlas, "left_shoulder");
        applySkinPart(skeleton, atlas, "right_hand");
        applySkinPart(skeleton, atlas, "right_shoulder");
        applySkinPart(skeleton, atlas, "head");


    }

    private void applySkinPart(Skeleton skeleton, TextureAtlas atlas, String name) {
        // get existing attachment
        RegionAttachment attach = (RegionAttachment) skeleton.findSlot(name).getAttachment();
        attach.setRegion(atlas.findRegion(name));
    }

    private void loadAtlas() {
        if (this.atlas == null) {
            FileHandle packedFile = Gdx.files.internal(asset + "/" + name + ".png");
            FileHandle atlasFile = Gdx.files.internal(asset + "/" + name + ".atlas");
            TextureAtlas atlas = new TextureAtlas(atlasFile.path());
            for (Texture texture : atlas.getTextures()) {
                texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            }
            this.atlas = atlas;
        }
    }

    public void dispose() {
        if (atlas != null) {
            atlas.dispose();
        }
    }
}