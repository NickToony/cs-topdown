package com.nick.ant.towerdefense.renderables.entities.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.objects.TextureMapObject;
import com.badlogic.gdx.maps.tiled.*;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.nick.ant.towerdefense.renderables.entities.Entity;
import com.nick.ant.towerdefense.renderables.entities.players.Player;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

/**
 * Created by Nick on 10/09/2014.
 */
public class World  {
    private final int cellSize = 32;
    private String mapName;
    private TiledMap map;
    private MapLayer collisionLayer;
    private MapLayer objectiveLayer;
    private OrthogonalTiledMapRenderer renderer;
    private OrthographicCamera camera;

    List<Rectangle> collisionObjects;

    private Entity entitySnap;

    public World(String mapName)    {
        // Load the map
        map = new TmxMapLoader().load("maps/" + mapName + "/map.tmx");
        // Get the collision layer
        for (MapLayer layer : map.getLayers()) {
            MapProperties properties = layer.getProperties();

            if (properties.containsKey("collision"))    {
                if (((String)properties.get("collision")).contentEquals("true"))    {
                    collisionLayer = layer;
                    System.out.println("Found collision layer with " + layer.getObjects().getCount() + " objects");
                }
            }   else if (properties.containsKey("objectives"))  {
                if (((String)properties.get("objectives")).contentEquals("true"))    {
                    objectiveLayer = layer;
                    System.out.println("Found objective layer with " + layer.getObjects().getCount() + " objects");
                }
            }
        }

        // Create a rendered, with a px scale of 1:1
        renderer = new OrthogonalTiledMapRenderer(map, 1);

        // Setup the camera
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        renderer.setView(camera);
    }

    public void render() {
        renderer.setView(camera);
        renderer.render();
    }

    public OrthographicCamera getCamera() {
        return camera;
    }

    /**
     * Set the entity that the camera should lock onto. You can set it to null to disable this feature.
     * @param player
     */
    public void setEntitySnap(Entity player)    {
        this.entitySnap = player;
    }

    public void step() {
        if (entitySnap != null) {
            camera.translate(Math.round(entitySnap.getX() - camera.position.x), Math.round(entitySnap.getY() - camera.position.y));
        }

        camera.update();
    }

    public float getCameraX() {
        return camera.position.x;
    }

    public float getCameraY()   {
        return camera.position.y;
    }

    public List<Rectangle> getCollisionObjects() {
        if (collisionObjects == null)   {
            collisionObjects = new ArrayList<Rectangle>();
            for (MapObject object : collisionLayer.getObjects())   {
                if (object instanceof RectangleMapObject) {
                    // Fetch the rectangle collision box
                    Rectangle rectangle = ((RectangleMapObject) object).getRectangle();

                    // Resize it to the correct size and location
                    rectangle.setX(rectangle.getX() - (cellSize/2));
                    rectangle.setY(rectangle.getY() - (cellSize/2));
                    rectangle.setWidth(cellSize);
                    rectangle.setHeight(cellSize);

                    // Add to list
                    collisionObjects.add(rectangle);
                }
            }
        }

        return collisionObjects;
    }

}
