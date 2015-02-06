package com.nick.ant.towerdefense.rooms;

import com.badlogic.gdx.Gdx;
import com.nick.ant.towerdefense.components.CharacterManager;
import com.nick.ant.towerdefense.components.weapons.WeaponManager;
import com.nick.ant.towerdefense.renderables.entities.collisions.CollisionManager;
import com.nick.ant.towerdefense.renderables.entities.players.Player;
import com.nick.ant.towerdefense.renderables.entities.players.UserPlayer;
import com.nick.ant.towerdefense.renderables.entities.world.World;
import com.nick.ant.towerdefense.renderables.ui.HUD;

/**
 * Created by Nick on 08/09/2014.
 */
public class RoomGame extends Room {
    private World world;
    private float mouseX = 0f;
    private float mouseY = 0f;
    private CollisionManager collisionManager;
    public Player userPlayer;

    @Override
    public void create() {
        // Force it to load the instances
        CharacterManager.getInstance();
        WeaponManager.getInstance();

        world = new World("de_dust2");

        collisionManager = new CollisionManager(world);

        userPlayer = new UserPlayer(16,16);
        addEntity(userPlayer);
        userPlayer.setCollisionManager(collisionManager);
        world.setEntitySnap(userPlayer);

        HUD hud = new HUD(this);
        addRenderable(hud);
    }

    @Override
    public void render() {
        world.step();
        getSpriteBatch().setProjectionMatrix(world.getCamera().combined);
        world.render();

        super.render();
    }

    public void step()  {
        super.step();

        mouseX = Gdx.input.getX() + world.getCameraX() - Gdx.graphics.getWidth()/2;
        mouseY = Gdx.graphics.getHeight() - Gdx.input.getY() + world.getCameraY() - Gdx.graphics.getHeight()/2;
    }

    @Override
    public void dispose() {
        super.dispose();

        CharacterManager.getInstance().dispose();
        WeaponManager.getInstance().dispose();
    }

    public Player getUserPlayer() {
        return userPlayer;
    }

    public float getMouseX() {
        return mouseX;
    }

    public float getMouseY() {
        return mouseY;
    }

    @Override
    public float getViewX() {
        return world.getCameraX();
    }

    @Override
    public float getViewY() {
        return world.getCameraY();
    }

    public World getWorld() {
        return world;
    }
}
