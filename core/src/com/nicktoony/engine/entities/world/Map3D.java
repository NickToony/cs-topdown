package com.nicktoony.engine.entities.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.nicktoony.engine.EngineConfig;

import java.util.LinkedList;
import java.util.List;

public class Map3D {
    private Map map;
    private PerspectiveCamera camera;
    private ModelBatch modelBatch;
    private ModelBuilder modelBuilder;
    private List<ModelInstance> modelInstances = new LinkedList<ModelInstance>();
    private float cameraHeight = EngineConfig.DEFAULT_CAMERA_HEIGHT;
    private float wallHeight = 22;

    Map3D(Map map) {
        this.map = map;

        // 3D camera
        camera = new PerspectiveCamera(90, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(0f, 0f, 400f);
        camera.lookAt(0f,0f,0);
        camera.near = 1f;
        camera.far = EngineConfig.ZOOM_MAX + 1;
        camera.update();

        modelBatch = new ModelBatch();
        modelBuilder = new ModelBuilder();
    }

    public void render() {
        modelBatch.begin(camera);
        for (ModelInstance modelInstance : modelInstances) {
//            modelInstance.transform.rotate(Vector3.Y, 1);
//            modelInstance.transform.rotate(Vector3.X, 1);
            modelBatch.render(modelInstance);
        }
        modelBatch.end();
    }

    public void update() {
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) cameraHeight += 2;
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) cameraHeight -= 2;
        cameraHeight = Math.max(EngineConfig.ZOOM_MIN, Math.min(EngineConfig.ZOOM_MAX, cameraHeight));

        camera.position.set(map.getCameraCenterX(), map.getCameraCenterY(), cameraHeight);
        camera.lookAt(map.getCameraCenterX(), map.getCameraCenterY(),0f);
        camera.update();

    }

    public void dispose() {
        modelBatch.dispose();
        for (ModelInstance modelInstance : modelInstances) {
            modelInstance.model.dispose();
        }
    }

    public void addWall(float x, float y, float width, float height, float depth, int depthRepeat) {
//        Model model  = modelBuilder.createBox(width , height, 1f,
//                new Material(ColorAttribute.createDiffuse(Color.GREEN)),
//                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);

        TiledMapTileLayer layer = map.getTiledMap().getLayers().getByType(TiledMapTileLayer.class).first();
        TiledMapTile tile = layer.getCell((int) Math.floor(x / 32), (int) Math.floor(y / 32))
                .getTile();
        TextureRegion textureRegion = new TextureRegion(tile.getTextureRegion());
//        textureRegion.setRegion(Math.round(textureRegion.getV() * textureRegion.getTexture().getWidth()),
//                Math.round(textureRegion.getU() * textureRegion.getTexture().getHeight()), 32, 32);


//        Pixmap pixmap = new Pixmap((int) Math.ceil(height), (int) Math.ceil(width),
//                textureRegion.getTexture().getTextureData().getFormat());
//        for (int xx = 0; xx < width; xx += 32) {
//            for (int yy = 0; yy < height; yy += 32) {
//                TiledMapTile tileTile = layer.getCell(
//                        (int) Math.floor((x + xx) / 32),
//                        (int) Math.floor((y + yy) / 32))
//                        .getTile();
//                Pixmap p = tileTile.getTextureRegion().getTexture()
//                        .getTextureData().consumePixmap();
//                pixmap.drawPixmap(p,
//                        tileTile.getTextureRegion().getRegionX(), // srcx
//                        tileTile.getTextureRegion().getRegionY(),
//                        tileTile.getTextureRegion().getRegionWidth(),
//                        tileTile.getTextureRegion().getRegionHeight(),
//                        yy,
//                        xx,
//                        32,
//                        32
//                        );
////                p.dispose();
//
//            }
//        }

//        FrameBuffer frameBuffer = new FrameBuffer(pixmap);
//
//        Texture texture = new Texture(pixmap);
//        pixmap.dispose();

//        assetManager.load("dust.png", Texture.class);
//        assetManager.finishLoading();
//        textureRegion = new TextureRegion(texture);
//        textureRegion = new TextureRegion(assetManager.get("dust.png", Texture.class));
//        textureRegion.setRegion(textureRegion.getRegionX(), textureRegion.getRegionY(), 32, 32);
//        System.out.println(textureRegion.getRegionX());
//        System.out.println(textureRegion.getRegionY());
        textureRegion.getTexture().setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);




//        Model model  = modelBuilder.createBox(1 , 1, 1f,
//                new Material(TextureAttribute.createDiffuse(textureRegion)),
//                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates);

        int attr = VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates;
        float boxSize = 1f;
        float textureWidthUV = 32 / width ;
        float textureHeightUV = 32 / height;
        modelBuilder.begin();

        MeshPartBuilder meshPartBuilder = modelBuilder.part("box", GL20.GL_TRIANGLES, attr, new Material(TextureAttribute.createDiffuse(textureRegion.getTexture())));

        boolean smartTexture = false;
        TextureRegion lastRegion = null;
        for (int xx = 0; xx < width; xx += 32) {
            for (int yy = 0; yy < height; yy += 32) {
                TiledMapTile tileTile = layer.getCell(
                        (int) Math.floor((x + xx) / 32),
                        (int) Math.floor((y + yy) / 32))
                        .getTile();
                TextureRegion innerRegion = tileTile.getTextureRegion();
                if (lastRegion != null && innerRegion != lastRegion) {
                    smartTexture = true;
                }
                lastRegion = innerRegion;
            }
        }

        if (depthRepeat > 0) {
            smartTexture = true;
        }

        System.out.println("Smart texture: " + smartTexture);

        for (int xx = 0; xx < width; xx += 32) {
            for (int yy = 0; yy < height; yy += 32) {
                TiledMapTile tileTile = layer.getCell(
                        (int) Math.floor((x + xx) / 32),
                        (int) Math.floor((y + yy) / 32))
                        .getTile();
                TextureRegion innerRegion = tileTile.getTextureRegion();
                meshPartBuilder.setUVRange(innerRegion);

                float xZero = ((xx/32f)*textureWidthUV);
                float xFull = ((xx/32f)*textureWidthUV) + textureWidthUV;
                float yZero = ((yy/32f)*textureHeightUV);
                float yFull = ((yy/32f)*textureHeightUV) + textureHeightUV;
                // Top
                meshPartBuilder.rect(
                        xZero, yZero, boxSize, // 0, 0
                        xFull, yZero, boxSize, // 1, 0
                        xFull, yFull,boxSize, // 1, 1
                        xZero, yFull,boxSize, // 0, 1
                        0, 0, 0);
                // Bottom
                meshPartBuilder.rect(
                        xZero, yZero, 0, // 0, 0
                        xFull, yZero, 0, // 1, 0
                        xFull, yFull,0, // 1, 1
                        xZero, yFull,0, // 0, 1
                        0, 0, 0);

               // Left
                meshPartBuilder.rect(
                        xZero, 0, smartTexture ? yZero : 0, // 0, 0
                        xFull, 0, smartTexture ? yZero : 0, // 1, 0
                        xFull, 0, smartTexture ? yFull : 1, // 1, 1
                        xZero, 0, smartTexture ? yFull : 1, // 0, 1
                        0, 0, 1);
                System.out.println("Here: " + yZero + "," + yFull);

                innerRegion.flip(true, false);
                meshPartBuilder.setUVRange(innerRegion);

                meshPartBuilder.rect(
                        xFull, 1, smartTexture ? yZero : 0, // 0, 0
                        xZero, 1, smartTexture ? yZero : 0, // 1, 0
                        xZero, 1, smartTexture ? yFull : 1, // 1, 1
                        xFull, 1, smartTexture ? yFull : 1, // 0, 1
                        1, 1, 1);

                innerRegion.flip(true, false);
                meshPartBuilder.setUVRange(innerRegion);

                meshPartBuilder.rect(
                        0, yZero, smartTexture ? xZero : 0, // 0, 0
                        0, yZero, smartTexture ? xFull : 1, // 1, 0
                        0, yFull, smartTexture ? xFull : 1, // 1, 1
                        0, yFull, smartTexture ? xZero : 0, // 0, 1
                        0, 0, 0);
//                innerRegion.flip(false, true);

                innerRegion.flip(false, true);
                meshPartBuilder.setUVRange(innerRegion);

                meshPartBuilder.rect(
                        1, yFull, smartTexture ? xZero : 0, // 0, 0
                        1, yFull, smartTexture ? xFull : 1, // 1, 0
                        1, yZero, smartTexture ? xFull : 1, // 1, 1
                        1, yZero, smartTexture ? xZero : 0, // 0, 1
                        0, 0, 0);

                innerRegion.flip(false, true);
                meshPartBuilder.setUVRange(innerRegion);


//                meshPartBuilder.rect(
//                        1, yFull, smartTexture ? xFull : 1, // 0, 0
//                        1, yZero, smartTexture ? xFull : 1, // 1, 0
//                        1, yZero, smartTexture ? xZero : 0, // 1, 1
//                        1, yFull, smartTexture ? xZero : 0, // 0, 1
//                        0, 0, 0);
////
//                meshPartBuilder.rect(
//                        1, yZero, xZero, // 0, 0
//                        1, yFull, xZero, // 1, 0
//                        1, yFull, xFull, // 1, 1
//                        1, yZero, xFull, // 0, 1
//                        0, 0, 0);

            }
        }

        Model model = modelBuilder.end();



//        textureRegion.setU(0);
//        textureRegion.setU2(1);
//        textureRegion.setV(0);
//        textureRegion.setV2(1);
//        textureRegion = new TextureRegion(textureRegion.getTexture());

//        Model model = createTexturesAndModels(textureRegion);
        ModelInstance instance = new ModelInstance(model);
//        physicsX + (physicsCellWidth /2), physicsY + (physiceCellHeight /2)

        instance.transform.setTranslation(x , y , 0);
        instance.transform.scale(width, height, depth * wallHeight);

        // Texture repeat! :)
        Matrix3 mat = new Matrix3();
        mat.scl(new Vector2(height/32, width/32));
//        mat.scl(new Vector2(1, 1));
//        model.meshes.get(0).transformUV(mat);
        modelInstances.add(instance);
    }

    public Model createTexturesAndModels(TextureRegion texture) {
//            texture.setWrap(Texture.TextureWrap.MirroredRepeat, Texture.TextureWrap.MirroredRepeat);
            TextureRegion textureRegionTop = texture;

            int attr = VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates;
            float boxSize = 0.5f;
            int boxOffset = 1;
            modelBuilder.begin();

            MeshPartBuilder meshPartBuilder = modelBuilder.part("box", GL20.GL_TRIANGLES, attr, new Material(TextureAttribute.createDiffuse(textureRegionTop)));
            meshPartBuilder.setUVRange(textureRegionTop);
            meshPartBuilder.rect(-boxSize,-boxSize,-boxSize, -boxSize, boxSize, -boxSize, boxSize,boxSize,-boxSize, boxSize,-boxSize,-boxSize, 0, 0, -boxOffset);
            meshPartBuilder.setUVRange(textureRegionTop);
            meshPartBuilder.rect(-boxSize,boxSize,boxSize, -boxSize,-boxSize,boxSize,  boxSize,-boxSize,boxSize, boxSize,boxSize,boxSize, 0,0,boxOffset);
            meshPartBuilder.setUVRange(textureRegionTop);
            meshPartBuilder.rect(-boxSize,-boxSize,boxSize, -boxSize,-boxSize,-boxSize,  boxSize,-boxSize,-boxSize, boxSize,-boxSize,boxSize, 0,-boxOffset,0);
            meshPartBuilder.setUVRange(textureRegionTop);
            meshPartBuilder.rect(-boxSize,boxSize,-boxSize, -boxSize,boxSize,boxSize,  boxSize,boxSize,boxSize, boxSize,boxSize,-boxSize, 0,boxOffset,0);
            meshPartBuilder.setUVRange(textureRegionTop);
            meshPartBuilder.rect(-boxSize,-boxSize,boxSize, -boxSize,boxSize,boxSize,  -boxSize,boxSize,-boxSize, -boxSize,-boxSize,-boxSize, -boxOffset,0,0);
            meshPartBuilder.setUVRange(textureRegionTop);
            meshPartBuilder.rect(boxSize,-boxSize,-boxSize, boxSize,boxSize,-boxSize,  boxSize,boxSize,boxSize, boxSize,-boxSize,boxSize, boxOffset,0,0);

            return modelBuilder.end();
    }

    public void resize() {
        camera.viewportWidth = Gdx.graphics.getWidth();
        camera.viewportHeight = Gdx.graphics.getHeight();
        camera.update();
    }

    public PerspectiveCamera getCamera() {
        return camera;
    }

    public float getCameraHeight() {
        return cameraHeight;
    }
}
