package com.nicktoony.cstopdown.rooms.game;

import box2dLight.RayHandler;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.nicktoony.cstopdown.components.Room;
import com.nicktoony.cstopdown.networking.client.SBSocket;
import com.nicktoony.cstopdown.rooms.game.entities.lights.RayHandlerWrapper;
import com.nicktoony.cstopdown.rooms.game.entities.players.Player;
import com.nicktoony.cstopdown.rooms.game.entities.players.UserPlayer;
import com.nicktoony.cstopdown.rooms.game.entities.world.Map;
import com.nicktoony.cstopdown.rooms.game.entities.world.TexturelessMap;
import com.nicktoony.cstopdown.services.CharacterManager;
import com.nicktoony.cstopdown.services.LightManager;
import com.nicktoony.cstopdown.services.TextureManager;
import com.nicktoony.cstopdown.services.weapons.WeaponManager;

/**
 * Created by Nick on 08/09/2014.
 */
public class RoomGame extends Room {
    protected Map map;
    protected World world;
    private RayHandlerWrapper rayHandlerWrapper;
    private SBSocket socket;
    private GameManager gameManager;
    private float accumulator = 0;

    public RoomGame(SBSocket socket) {
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
            map = new TexturelessMap("de_dust2"); // TODO: CHANGE BACK EVENTUALLY
        } else {
            map = new Map("de_dust2");
        }

        world = new World(new Vector2(0, 0), true);
        map.addCollisionObjects(world);

        if (render) {
//            // Setup lighting engine
            RayHandler rayHandler = new RayHandler(world);
            rayHandler.setShadows(true);
            rayHandler.setAmbientLight(map.getAmbientColour());
            rayHandlerWrapper = new RayHandlerWrapper(rayHandler, map);
            // Add map lights
            map.addLightObjects(rayHandler);

            // Preload all sounds
            WeaponManager.getInstance().preloadSounds();
//
//            // Add hud
//            hud = new HUD(this);
        }
    }

    public void step(float delta)  {
        super.step(delta);

        socket.pushNotifications();

        accumulator += delta;

        while (accumulator >= 1) {
            world.step(1, 1, 1);
            accumulator -= 1;
        }
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

    public Player createPlayer(int id, float x, float y) {
        // Define a player object
        Player player;
        if (socket != null && id == socket.getId()) {
            player = new UserPlayer();
            map.setEntitySnap(player);
        } else {
            player = new Player();
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
        map.step();
        spriteBatch.setProjectionMatrix(map.getCamera().combined);
        map.render();

        super.render(spriteBatch);

        rayHandlerWrapper.render(spriteBatch);
//
//        if (foregroundSpriteBatch == null) {
//            foregroundSpriteBatch = new SpriteBatch();
//        }
//
//        foregroundSpriteBatch.begin();
//        hud.render(foregroundSpriteBatch);
//        foregroundSpriteBatch.end();
    }

    public SBSocket getSocket() {
        return socket;
    }

    public GameManager getGameManager() {
        return gameManager;
    }
}
