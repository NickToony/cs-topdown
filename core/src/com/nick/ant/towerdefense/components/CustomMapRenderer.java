package com.nick.ant.towerdefense.components;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

/**
 * Created by nick on 28/02/15.
 */
public class CustomMapRenderer extends OrthogonalTiledMapRenderer {
    public CustomMapRenderer(TiledMap map) {
        super(map);
    }

    public CustomMapRenderer(TiledMap map, Batch batch) {
        super(map, batch);
    }

    public CustomMapRenderer(TiledMap map, float unitScale) {
        super(map, unitScale);
    }

    public CustomMapRenderer(TiledMap map, float unitScale, Batch batch) {
        super(map, unitScale, batch);
    }

    @Override
    public void setView (OrthographicCamera camera) {
        batch.setProjectionMatrix(camera.combined);
        float width = camera.viewportWidth * camera.zoom;
        float height = camera.viewportHeight * camera.zoom;
        float x = camera.position.x - width / 2;
        float y = camera.position.y - camera.position.y / 2;
        viewBounds.set(0, 0, 5000, 5000);
    }
}
