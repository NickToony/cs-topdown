package com.nick.ant.towerdefense.renderables.entities.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.nick.ant.towerdefense.renderables.entities.Entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nick on 10/09/2014.
 */
public class Map {
    private final int cellSize = 32;
    private String mapName;
    private TiledMap map;
    private MapLayer collisionLayer;
    private MapLayer objectiveLayer;
    private OrthogonalTiledMapRenderer renderer;
    private OrthographicCamera camera;
    private int mapWidth;
    private int mapHeight;

    private Entity entitySnap;

    public Map(String mapName)    {
        // Load the map
        map = new TmxMapLoader().load("maps/" + mapName + "/map.tmx");

        // Calculate map size
        MapProperties mapProperties = map.getProperties();
        int tX = mapProperties.get("width", Integer.class);
        int tY = mapProperties.get("height", Integer.class);
        mapWidth = cellSize * tX;
        mapHeight = cellSize * tY;

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

    public void addCollisionObjects(World world) {
        for (MapObject object : collisionLayer.getObjects())   {
            if (object instanceof RectangleMapObject) {
                // Fetch the rectangle collision box
                Rectangle rectangle = ((RectangleMapObject) object).getRectangle();

                // Resize it to the correct size and location
                BodyDef bodyDef = new BodyDef();
                bodyDef.type = BodyDef.BodyType.StaticBody;
                bodyDef.position.set(rectangle.getX() + (cellSize/2), rectangle.getY() + (cellSize/2));

                Body body = world.createBody(bodyDef);

                PolygonShape shape = new PolygonShape();
                shape.setAsBox(cellSize/2, cellSize/2);

                FixtureDef fixtureDef = new FixtureDef();
                fixtureDef.shape = shape;
                fixtureDef.density = 1f;

                body.createFixture(fixtureDef);
                shape.dispose();
            }
        }

        int[][] values =  {
                { 0, 0,     1, 0 },
                { 1, 0,     1, 1 },
                { 1, 1,     0, 1},
                { 0, 1,     0, 0}
        };

        for (int[] value : values) {
            BodyDef bodyDef2 = new BodyDef();
            bodyDef2.type = BodyDef.BodyType.StaticBody;
            bodyDef2.position.set(0,0);
            FixtureDef fixtureDef2 = new FixtureDef();
            EdgeShape edgeShape = new EdgeShape();
            edgeShape.set(mapWidth * value[0], mapHeight * value[1], mapWidth * value[2], mapHeight * value[3]);
            fixtureDef2.shape = edgeShape;

            Body bodyEdgeScreen = world.createBody(bodyDef2);
            bodyEdgeScreen.createFixture(fixtureDef2);
            edgeShape.dispose();
        }


    }

}