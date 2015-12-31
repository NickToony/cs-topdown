package com.nicktoony.spacebattle.rooms.game;

import box2dLight.RayHandler;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.nicktoony.spacebattle.MyGame;
import com.nicktoony.spacebattle.components.Room;
import com.nicktoony.spacebattle.networking.packets.Packet;
import com.nicktoony.spacebattle.rooms.game.entities.lights.RayHandlerWrapper;
import com.nicktoony.spacebattle.rooms.game.entities.players.Player;
import com.nicktoony.spacebattle.rooms.game.entities.players.UserPlayer;
import com.nicktoony.spacebattle.rooms.game.entities.world.Map;
import com.nicktoony.spacebattle.rooms.game.entities.world.TexturelessMap;
import com.nicktoony.spacebattle.services.CharacterManager;
import com.nicktoony.spacebattle.services.LightManager;
import com.nicktoony.spacebattle.services.weapons.WeaponManager;

/**
 * Created by Nick on 08/09/2014.
 */
public class RoomGame extends Room {
    protected Map map;
    protected World world;
    private RayHandlerWrapper rayHandlerWrapper;
//    protected CSClient client;

//    public RoomGame(CSClient client) {
//        this.client = client;
//    }

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
//
//            // Add hud
//            hud = new HUD(this);
//
//            if (isMultiplayer()) {
//                client.sendPacket(new ClientLoadedPacket());
//            }

            // temp
            Player player = createUserPlayer();
            map.setEntitySnap(player);
        }
    }

    public void step()  {
        super.step();

        world.step(1, 6, 2);
    }

    @Override
    public void dispose() {
        super.dispose();

        CharacterManager.getInstance().dispose();
        WeaponManager.getInstance().dispose();
    }

    public Map getMap() {
        return map;
    }

    public World getWorld() {
        return world;
    }

    public Player createUserPlayer() {
        // Define a player object
        Player player = new UserPlayer();
        return setupPlayer(player);
    }
//
//    public Player createPlayer() {
//        // Define a player object
//        Player player = new Player();
//        return setupPlayer(player);
//    }
//
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
}
