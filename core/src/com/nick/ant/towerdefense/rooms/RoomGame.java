package com.nick.ant.towerdefense.rooms;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.nick.ant.towerdefense.components.CharacterManager;
import com.nick.ant.towerdefense.components.LightManager;
import com.nick.ant.towerdefense.components.weapons.WeaponManager;
import com.nick.ant.towerdefense.networking.TexturelessMap;
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
    protected Map map;
    protected World world;
    protected CSClient client;

    public RoomGame() {
    }

    public RoomGame(CSClient client) {
        this.client = client;
    }

    @Override
    public void create() {
        // Force it to load the instances
        CharacterManager.getInstance();
        WeaponManager.getInstance();

        // Create the map
        map = new TexturelessMap("de_dust2"); // TODO: CHANGE BACK EVENTUALLY
        world = new World(new Vector2(0, 0), true);
        map.addCollisionObjects(world);
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


    public void addEntity(Entity entity, World world) {
        entity.setWorld(world);
        addEntity(entity);
    }

    public void addEntity(Entity entity) {
        entity.setMultiplayer(client != null);
        entity.setGameRoom(this);
        addRenderable(entity);
    }

    public void sendPacket(Packet packet) {

    }

    public Player createUserPlayer() {
        // Define a player object
        Player player = new UserPlayer();
        return setupPlayer(player);
    }

    public Player createPlayer() {
        // Define a player object
        Player player = new Player();
        return setupPlayer(player);
    }

    protected Player setupPlayer(Player player) {
        addEntity(player, world);
        return player;
    }
}
