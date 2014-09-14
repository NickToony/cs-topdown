package com.nick.ant.towerdefense.renderables.entities.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.nick.ant.towerdefense.renderables.Renderable;

/**
 * Created by Nick on 10/09/2014.
 */
public class World extends Renderable {
    private final int cellSize = 32;
    private String mapName;
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;

    public World(String mapName)    {
        // Load the map
        map = new TmxMapLoader().load("maps/" + mapName + "/map.tmx");

        // Calculate the scale
        float unitScale = 1 / (float) cellSize;
        renderer = new OrthogonalTiledMapRenderer(map, unitScale);

        // Setup the camera
        OrthographicCamera camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth() / 32f, Gdx.graphics.getHeight() / 32f);
        renderer.setView(camera);
    }

    public void render(SpriteBatch spriteBatch) {
        renderer.render();
    }

    @Override
    public void step() {

    }
}
