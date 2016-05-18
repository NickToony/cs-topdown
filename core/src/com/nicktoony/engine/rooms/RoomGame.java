package com.nicktoony.engine.rooms;

import box2dLight.RayHandler;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.nicktoony.cstopdown.rooms.game.GameManager;
import com.nicktoony.cstopdown.rooms.game.CSHUD;
import com.nicktoony.cstopdown.rooms.game.entities.players.BotPlayer;
import com.nicktoony.cstopdown.rooms.game.entities.players.Player;
import com.nicktoony.cstopdown.rooms.game.entities.players.UserPlayer;
import com.nicktoony.engine.components.ListenerClass;
import com.nicktoony.engine.components.Room;
import com.nicktoony.engine.entities.HUD;
import com.nicktoony.engine.entities.lights.RayHandlerWrapper;
import com.nicktoony.engine.entities.world.Map;
import com.nicktoony.engine.entities.world.TexturelessMap;
import com.nicktoony.engine.networking.client.ClientSocket;
import com.nicktoony.engine.services.CharacterManager;
import com.nicktoony.engine.services.weapons.WeaponManager;

/**
 * Created by Nick on 08/09/2014.
 */
public abstract class RoomGame extends Room {
    protected World world;
    protected Map map;
    protected RayHandlerWrapper rayHandlerWrapper;
    protected ClientSocket socket;
    protected GameManager gameManager;

    private SpriteBatch foregroundSpriteBatch;
    private HUD hud;

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
        map = defineMap(render);

        world = new World(new Vector2(0, 0), true);
        world.setContactListener(new ListenerClass());
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
            hud = (HUD) addSelfManagedEntity(defineHud());
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
    }

    @Override
    public void dispose(boolean render) {
        super.dispose(render);

        CharacterManager.getInstance().dispose();
        WeaponManager.getInstance().dispose();
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

        addEntity(player);

        return player;
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

        // Render hud
        if (hud != null) {
            foregroundSpriteBatch.begin();
            hud.render(foregroundSpriteBatch);
            foregroundSpriteBatch.end();
        }
    }

    public ClientSocket getSocket() {
        return socket;
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    public float getMouseX() {
        return Gdx.input.getX();
    }

    public float getMouseY() {
        return Gdx.input.getY();
    }

    public Map getMap() {
        return map;
    }

    public World getWorld() {
        return world;
    }

    public RayHandler getRayHandler() {
        return rayHandlerWrapper.getHandler();
    }

    public HUD getHud() {
        return hud;
    }

    protected abstract HUD defineHud();

    protected abstract Map defineMap(boolean render);
}
