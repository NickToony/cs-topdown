package com.nick.ant.towerdefense.renderables.entities.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.nick.ant.towerdefense.renderables.entities.Entity;
import com.nick.ant.towerdefense.renderables.entities.players.Player;

/**
 * Created by Nick on 10/09/2014.
 */
public class World  {
    private final int cellSize = 32;
    private String mapName;
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;
    private OrthographicCamera camera;

    private Entity entitySnap;

    public World(String mapName)    {
        // Load the map
        map = new TmxMapLoader().load("maps/" + mapName + "/map.tmx");

        // Create a rendered, with a px scale of 1:1
        renderer = new OrthogonalTiledMapRenderer(map, 1);

        // Setup the camera
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        renderer.setView(camera);
    }

    public void render() {
        if (entitySnap != null) {
            camera.translate(entitySnap.getX() - camera.position.x, entitySnap.getY() - camera.position.y);
        }

        camera.update();
        renderer.setView(camera);
        renderer.render();
    }

    public OrthographicCamera getCamera() {
        return camera;
    }

    public void setEntitySnap(Player player)    {
        this.entitySnap = player;
    }
}
