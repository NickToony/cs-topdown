package com.nick.ant.towerdefense.rooms;

import box2dLight.RayHandler;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.nick.ant.towerdefense.components.CharacterManager;
import com.nick.ant.towerdefense.components.LightManager;
import com.nick.ant.towerdefense.components.weapons.WeaponManager;
import com.nick.ant.towerdefense.networking.client.CSClient;
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
public class RoomGame extends Room {
    private Map map;
    private World world;
    private float mouseX = 0f;
    private float mouseY = 0f;
    private SpriteBatch spriteBatch = new SpriteBatch();
    private HUD hud;
    private RayHandlerWrapper rayHandlerWrapper;
    private CSClient client;

    public RoomGame(CSClient client) {
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
    }

    @Override
    public void render() {
        map.step();
        getSpriteBatch().setProjectionMatrix(map.getCamera().combined);
        map.render();

        super.render();

        rayHandlerWrapper.render(getSpriteBatch());

        spriteBatch.begin();
        hud.render(spriteBatch);
        spriteBatch.end();
    }

    public void step()  {
        world.step(1, 6, 2);

        mouseX = Gdx.input.getX() + map.getCameraX() - Gdx.graphics.getWidth()/2;
        mouseY = Gdx.graphics.getHeight() - Gdx.input.getY() + map.getCameraY() - Gdx.graphics.getHeight()/2;

        hud.step();

        super.step();
    }

    @Override
    public void dispose() {
        super.dispose();

        CharacterManager.getInstance().dispose();
        WeaponManager.getInstance().dispose();
        rayHandlerWrapper.dispose();
        spriteBatch.dispose();
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

    public Map getMap() {
        return map;
    }


    public void addEntity(Entity entity, World world) {
        entity.setWorld(world);
        addEntity(entity);
    }

    public void addEntity(Entity entity) {
        addRenderable(entity);
        entity.setGameRoom(this);
    }

    public void sendPacket(Packet packet) {
        if (client != null) {
            client.sendPacket(packet);
        }
    }

    public Player createUserPlayer() {
        // Define a player object
        Player player = new UserPlayer();
        map.setEntitySnap(player);
        hud.setPlayer(player);
        return setupPlayer(player);
    }

    public Player createPlayer() {
        // Define a player object
        Player player = new Player();
        return setupPlayer(player);
    }

    private Player setupPlayer(Player player) {
        player.setTorch(LightManager.defineTorch(rayHandlerWrapper.getHandler()));
        player.setGlow(LightManager.definePlayerGlow(rayHandlerWrapper.getHandler()));
        addEntity(player, world);
        return player;
    }
}
