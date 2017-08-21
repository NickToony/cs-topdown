package com.nicktoony.engine.entities.world;

import box2dLight.RayHandler;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.TextureMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.nicktoony.cstopdown.mods.gamemode.PlayerModInterface;
import com.nicktoony.cstopdown.rooms.game.entities.players.Player;
import com.nicktoony.cstopdown.rooms.game.entities.objectives.Spawn;
import com.nicktoony.engine.EngineConfig;
import com.nicktoony.engine.MyGame;
import com.nicktoony.engine.config.ServerConfig;
import com.nicktoony.engine.packets.connection.MapPacket;
import com.nicktoony.engine.services.AdvancedTmxMapLoader;
import com.nicktoony.engine.services.LightManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Nick on 10/09/2014.
 */
public class Map {
    protected String mapName;
    private TiledMap map;
    private MapLayer collisionLayer;
    private MapLayer objectiveLayer;
    private MapLayer lightLayer;
    private OrthogonalTiledMapRenderer renderer;
    private OrthographicCamera camera;
    protected int mapWidth;
    protected int mapHeight;
    private Color ambientColour;
    protected PathfindingGraph pathfindingGraph;
    protected HashMap<Integer, List<Spawn>> spawns;
    public int[] spawnIndex = { 0, 0, 0 };
    protected ServerConfig gameConfig;


    private Player entitySnap;

    protected Map(ServerConfig gameConfig) {
        this.gameConfig = gameConfig;
    }

    public Map(ServerConfig gameConfig, String mapName, MapPacket mapWrapper) {
        this(gameConfig);

        // Load the map
        map = new AdvancedTmxMapLoader(mapWrapper).load("/"); // the location does not matter at all
        this.mapName = mapName;

        performSetup();
    }

    public Map(ServerConfig gameConfig, String mapName)    {
        this(gameConfig);

        // Load the map
        map = new TmxMapLoader().load("maps/" + mapName + "/map.tmx");
        this.mapName = mapName;

        performSetup();
    }

    private void performSetup() {
        // Calculate map size
        MapProperties mapProperties = map.getProperties();
        int tX = mapProperties.get("width", Integer.class);
        int tY = mapProperties.get("height", Integer.class);

        // Ambient colours
        ambientColour = Color.valueOf(mapProperties.get("ambientColour", String.class));
        ambientColour.a = mapProperties.get("ambientAlpha", Float.class);
        if (gameConfig.tmp_map_lighting != -1) {
            ambientColour.a = gameConfig.tmp_map_lighting;
        }

        mapWidth = EngineConfig.CELL_SIZE * tX;
        mapHeight = EngineConfig.CELL_SIZE * tY;

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
            } else if (properties.containsKey("lights")) {
                if (((String)properties.get("lights")).contentEquals("true")) {
                    lightLayer = layer;
                    System.out.println("Found light layer with " + layer.getObjects().getCount() + " objects");
                }
            }
        }

        // Create a rendered, with a px scale of 1:1
        renderer = new OrthogonalTiledMapRenderer(map, 1);

        // Setup the camera
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.zoom = mapProperties.get("zoom", Float.class);
        camera.zoom = 0.625f; // override
        renderer.setView(camera);

        // Objectivbes
        findObjectives();

        // Setup pathfinding
        pathfindingGraph = new PathfindingGraph(tX, tY);
    }

    protected void findObjectives() {
        spawns = new HashMap<Integer, List<Spawn>>();
        spawns.put(PlayerModInterface.TEAM_CT, new ArrayList<Spawn>());
        spawns.put(PlayerModInterface.TEAM_T, new ArrayList<Spawn>());
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
    public void setEntitySnap(Player player)    {
        if (entitySnap != null) {
            entitySnap.focused(false);
        }
        this.entitySnap = player;
        if (entitySnap != null) {
            entitySnap.focused(true);
        }
    }

    public Player getEntitySnap() {
        return entitySnap;
    }

    public void step() {
        if (entitySnap != null) {
//            float offsetValue = Math.max(15, Math.min(100, entitySnap.getMouseDistance()) * (entitySnap.getZoomKey() ? entitySnap.getCurrentWeaponObject().weapon.getZoom() : 1));
            float offsetValue = Math.max( .15f, Math.min(1, entitySnap.getMouseDistance())) * 100 * (entitySnap.getZoomKey() ? entitySnap.getCurrentWeaponObject().getWeapon(entitySnap.getRoom().getWeaponManager()).getZoom() : 1);

            Vector2 offset = new Vector2(offsetValue, 0).setAngle(entitySnap.getDirection() + 90);
            float toX = entitySnap.getX() + offset.x;
            float toY = entitySnap.getY() + offset.y;
            camera.translate(Math.round(toX - camera.position.x)/2, Math.round(toY - camera.position.y)/2);
        }

        camera.update();
    }

    public float getCameraX() {
        return camera.position.x - camera.viewportWidth/2;
    }

    public float getCameraCenterX() {
        return camera.position.x;
    }

    public float getCameraY()   {
        return camera.position.y - camera.viewportHeight/2;
    }

    public float getCameraCenterY() {
        return camera.position.y;
    }

    public void addCollisionObjects(World world) {
        addCollisionWalls(world);

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
            float x1 = EngineConfig.toMetres(mapWidth * value[0]);
            float y1 = EngineConfig.toMetres(mapHeight * value[1]);
            float x2 = EngineConfig.toMetres(mapWidth * value[2]);
            float y2 = EngineConfig.toMetres(mapHeight * value[3]);
            edgeShape.set(x1, y1, x2, y2);
            fixtureDef2.shape = edgeShape;

            Body bodyEdgeScreen = world.createBody(bodyDef2);
            bodyEdgeScreen.createFixture(fixtureDef2);
            edgeShape.dispose();
        }

        // Setup pathfinding
        pathfindingGraph.setupConnections();
    }

    protected void addCollisionWalls(World world) {
        for (MapObject object : collisionLayer.getObjects())   {
            if (object instanceof TextureMapObject) {
                // Fetch the rectangle collision box
                TextureMapObject rectangle = ((TextureMapObject) object);

                addCollisionWall(world, rectangle.getX(), rectangle.getY(),
                        (Float) rectangle.getProperties().get("width"),  (Float) rectangle.getProperties().get("height") );

//                System.out.println(rectangle.getX() + "," + rectangle.getY() + " :: " + rectangle.getProperties().get("width") + "," + rectangle.getProperties().get("height"));
            }
        }
    }

    protected void addCollisionWall(World world, float x, float y, float width, float height) {
        float physicsX = EngineConfig.toMetres(x);
        float physicsY = EngineConfig.toMetres(y);
        float physicsCellWidth = EngineConfig.toMetres(width);
        float physiceCellHeight = EngineConfig.toMetres(height);



        // Resize it to the correct size and location
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(physicsX + (physicsCellWidth /2), physicsY + (physiceCellHeight /2));
//        bodyDef.position.set(physicsX + (physicsCellSize /2), physicsY + (physicsCellSize) + (physicsCellSize/2));

        Body body = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(physicsCellWidth /2, physiceCellHeight /2);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1f;

        body.createFixture(fixtureDef);
        shape.dispose();

        // Pathfinding
        for (float pathX = x; pathX < x + width; pathX += EngineConfig.CELL_SIZE) {
            for (float pathY = y; pathY < y + height; pathY += EngineConfig.CELL_SIZE) {
//                System.out.println(x + "," + y);
                pathfindingGraph.getNodeByWorld(pathX, pathY).setSolid(true);
            }
        }

    }

    public void addLightObjects(RayHandler rayHandler) {
        if (lightLayer == null) {
            return;
        }

        for (MapObject object : lightLayer.getObjects())   {
            if (object instanceof TextureMapObject) {
                MapProperties mapProperties = object.getProperties();

                // Fetch the rectangle collision box
                TextureMapObject rectangle = ((TextureMapObject) object);

                float physicsX = EngineConfig.toMetres(rectangle.getX() + (Float) rectangle.getProperties().get("width"));
                float physicsY = EngineConfig.toMetres(rectangle.getY() + (Float) rectangle.getProperties().get("height"));
                float physicsCellSize = EngineConfig.toMetres(EngineConfig.CELL_SIZE);
                LightManager.definePointLight(rayHandler, mapProperties,
                         physicsX,
                         physicsY - (physicsCellSize / 2));
            }
        }

        rayHandler.update();
    }

    public int getMapHeight() {
        return mapHeight;
    }

    public int getMapWidth() {
        return mapWidth;
    }

    public Color getAmbientColour() {
        return ambientColour;
    }

    public PathfindingGraph getPathfindingGraph() {
        return pathfindingGraph;
    }

    public List<Spawn> getSpawns(int team) {
        return spawns.get(team);
    }

    @Override
    public String toString() {
        return Gdx.files.internal("maps/" + mapName + "/map.tmx").readString();
    }

    protected void findTilesetNames() {
        // unnecessary on Map.java
    }

    public int[][][] getTilesetImages(MyGame.PlatformProvider platformProvider) {
        return null; // unnecessary on Map.java
    }

    public List<String> getTilesetNames() {
        return null; // unnecessary on Map.java
    }

    public void resize(int width, int height) {
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    public float getCameraZoom() {
        return camera.zoom;
    }
}
