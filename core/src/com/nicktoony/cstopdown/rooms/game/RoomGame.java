package com.nicktoony.cstopdown.rooms.game;

import box2dLight.RayHandler;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.nicktoony.cstopdown.rooms.game.entities.lights.RayHandlerWrapper;
import com.nicktoony.cstopdown.rooms.game.entities.players.BotPlayer;
import com.nicktoony.cstopdown.rooms.game.entities.players.Player;
import com.nicktoony.cstopdown.rooms.game.entities.players.UserPlayer;
import com.nicktoony.cstopdown.rooms.game.entities.world.Map;
import com.nicktoony.cstopdown.rooms.game.entities.world.TexturelessMap;
import com.nicktoony.engine.components.Room;
import com.nicktoony.engine.networking.client.ClientSocket;
import com.nicktoony.engine.services.CharacterManager;
import com.nicktoony.engine.services.LightManager;
import com.nicktoony.engine.services.weapons.WeaponManager;

/**
 * Created by Nick on 08/09/2014.
 */
public class RoomGame extends Room {
    protected Map map;
    protected World world;
    private RayHandlerWrapper rayHandlerWrapper;
    private ClientSocket socket;
    private GameManager gameManager;
    private float accumulator = 0;
    private HUD hud;
    private SpriteBatch foregroundSpriteBatch;

    public RoomGame(ClientSocket socket) {
        this.socket = socket;
        this.gameManager = new GameManager(this, socket);
        if (socket != null) {
            socket.addListener(gameManager);
        }
    }

    @Override
    public void create(boolean render) {
        super.create(render);

        // Force it to load the instances
        CharacterManager.getInstance();
        WeaponManager.getInstance();

        // Create the map
        if (!render) {
            map = new TexturelessMap(socket.getServerConfig().sv_map);
        } else {
            map = new Map(socket.getServerConfig().sv_map);
        }

        world = new World(new Vector2(0, 0), true);
        map.addCollisionObjects(world);

        if (render) {
            // Setup ui layer
            foregroundSpriteBatch = new SpriteBatch();

            // Setup lighting engine
            RayHandler rayHandler = new RayHandler(world);
            rayHandler.setShadows(true);
            rayHandler.setAmbientLight(map.getAmbientColour());
            rayHandlerWrapper = new RayHandlerWrapper(rayHandler, map);
            // Add map lights
            map.addLightObjects(rayHandler);

            // Preload all sounds
            WeaponManager.getInstance().preloadSounds();

            // Add hud
            hud = (HUD) addSelfManagedEntity(new HUD());
        }
    }

    public void step(float delta)  {
        if (hud != null) {
            hud.step(delta);
        }

        // Handles inputs first
        socket.pushNotifications();

        // Update game manager
        gameManager.update();

        // Update the world
        super.step(delta);

        // Update the physics
        world.step(delta, 1, 1);
//        accumulator += delta;
//        while (accumulator >= 1) {
//            world.step(1, 1, 1);
//            accumulator -= 1;
//        }
    }

    @Override
    public void dispose(boolean render) {
        super.dispose(render);

        CharacterManager.getInstance().dispose();
        WeaponManager.getInstance().dispose();
    }

    public Map getMap() {
        return map;
    }

    public World getWorld() {
        return world;
    }

    public Player createPlayer(int id, float x, float y, boolean bot) {
        // Define a player object
        Player player;
        if (socket != null && id == socket.getId()) {
            player = new UserPlayer();
            map.setEntitySnap(player);
        } else {
            if (!bot) {
                player = new Player();
            } else {
                player = new BotPlayer();
            }
            if (map.getEntitySnap() == null) {
                map.setEntitySnap(player);
            }
        }
        player.setId(id);
        player.setX(x);
        player.setY(y);
        return setupPlayer(player);
    }

    protected Player setupPlayer(Player player) {
        if (isRender()) {
            player.setTorch(LightManager.defineTorch(rayHandlerWrapper.getHandler()));
            player.setGlow(LightManager.definePlayerGlow(rayHandlerWrapper.getHandler()));
            player.setGunFire(LightManager.defineGunFire(rayHandlerWrapper.getHandler()));
        }
        addEntity(player);

        return player;
    }

    public float getMouseX() {
        return Gdx.input.getX();
    }

    public float getMouseY() {
        return Gdx.input.getY();
    }

    @Override
    public void render(SpriteBatch spriteBatch) {
        // Render map
        map.step();
        spriteBatch.setProjectionMatrix(map.getCamera().combined);
        map.render();

        // Render everything else
        super.render(spriteBatch);

        // Render lighting
        rayHandlerWrapper.render(spriteBatch);

        // Debug code
//        ShapeRenderer shapeRenderer = new ShapeRenderer();
//        shapeRenderer.setProjectionMatrix(getMap().getCamera().combined);
//        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
//        shapeRenderer.setColor(1, 1, 0, 1);
//        for (PathfindingNode node : getMap().getPathfindingGraph().getNodes()) {
//            for (Connection<PathfindingNode> connection : node.getConnections()) {
//
//
//                shapeRenderer.line(node.getWorldX(), node.getWorldY(), connection.getToNode().getWorldX(), connection.getToNode().getWorldY());
//
//            }
//        }
//        shapeRenderer.end();

        // Render hud
        foregroundSpriteBatch.begin();
        hud.render(foregroundSpriteBatch);
        foregroundSpriteBatch.end();


    }

    public ClientSocket getSocket() {
        return socket;
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    public HUD getHud() {
        return hud;
    }
}
