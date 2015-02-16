package com.nick.ant.towerdefense.rooms;

import box2dLight.RayHandler;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.nick.ant.towerdefense.components.CharacterManager;
import com.nick.ant.towerdefense.components.LightManager;
import com.nick.ant.towerdefense.components.weapons.WeaponManager;
import com.nick.ant.towerdefense.networking.TexturelessMap;
import com.nick.ant.towerdefense.networking.client.CSClient;
import com.nick.ant.towerdefense.networking.packets.ClientReadyPacket;
import com.nick.ant.towerdefense.networking.packets.Packet;
import com.nick.ant.towerdefense.renderables.entities.Entity;
import com.nick.ant.towerdefense.renderables.entities.players.Player;
import com.nick.ant.towerdefense.renderables.entities.players.UserPlayer;
import com.nick.ant.towerdefense.renderables.entities.world.Map;
import com.nick.ant.towerdefense.renderables.lights.RayHandlerWrapper;
import com.nick.ant.towerdefense.renderables.ui.HUD;

/**
 * Created by Nick on 08/09/2014.
 */
public class RoomGameRender extends RoomGame {
    private float mouseX = 0f;
    private float mouseY = 0f;
    private SpriteBatch foregroundSpriteBatch = new SpriteBatch();
    private HUD hud;
    private RayHandlerWrapper rayHandlerWrapper;

    public RoomGameRender() {
    }

    public RoomGameRender(CSClient client) {
        this.client = client;
    }

    @Override
    public void create() {
        // Force it to load the instances
        CharacterManager.getInstance();
        WeaponManager.getInstance();

        // Create the map
        map = new Map("de_dust2");
        world = new World(new Vector2(0, 0), true);
        map.addCollisionObjects(world);

        // Setup lighting engine
        RayHandler rayHandler = new RayHandler(world);
        rayHandler.setShadows(true);
        rayHandler.setAmbientLight(map.getAmbientColour());
        rayHandlerWrapper = new RayHandlerWrapper(rayHandler, map);
        // Add map lights
        map.addLightObjects(rayHandler);

        // Add hud
        hud = new HUD(this);

        if (isMultiplayer()) {
            client.sendPacket(new ClientReadyPacket());
        }
    }

    private boolean isMultiplayer() {
        return client != null;
    }

    @Override
    public void render() {
        map.step();
        getSpriteBatch().setProjectionMatrix(map.getCamera().combined);
        map.render();

        super.render();

        rayHandlerWrapper.render(getSpriteBatch());

        if (foregroundSpriteBatch == null) {
            foregroundSpriteBatch = new SpriteBatch();
        }

        foregroundSpriteBatch.begin();
        hud.render(foregroundSpriteBatch);
        foregroundSpriteBatch.end();
    }

    public void step()  {
        mouseX = Gdx.input.getX() + map.getCameraX() - Gdx.graphics.getWidth()/2;
        mouseY = Gdx.graphics.getHeight() - Gdx.input.getY() + map.getCameraY() - Gdx.graphics.getHeight()/2;

        hud.step();

        super.step();
    }

    @Override
    public void dispose() {
        super.dispose();

        rayHandlerWrapper.dispose();
        foregroundSpriteBatch.dispose();
    }

    public float getMouseX() {
        return mouseX;
    }

    public float getMouseY() {
        return mouseY;
    }

    @Override
    public float getViewX() {
        return map.getCameraX();
    }

    @Override
    public float getViewY() {
        return map.getCameraY();
    }

    @Override
    public void sendPacket(Packet packet) {
        if (client != null) {
            client.sendPacket(packet);
        }
    }

    @Override
    public Player createUserPlayer() {
        // Define a player object
        Player player = super.createUserPlayer();
        map.setEntitySnap(player);
        hud.setPlayer(player);
        return player;
    }

    @Override
    public Player createPlayer() {
        // Define a player object
        return super.createPlayer();
    }

    @Override
    protected Player setupPlayer(Player player) {
        player.setTorch(LightManager.defineTorch(rayHandlerWrapper.getHandler()));
        player.setGlow(LightManager.definePlayerGlow(rayHandlerWrapper.getHandler()));
        super.setupPlayer(player);
        return player;
    }
}
