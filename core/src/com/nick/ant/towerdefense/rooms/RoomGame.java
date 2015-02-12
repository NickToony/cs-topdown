package com.nick.ant.towerdefense.rooms;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.nick.ant.towerdefense.components.CharacterManager;
import com.nick.ant.towerdefense.components.weapons.WeaponManager;
import com.nick.ant.towerdefense.renderables.entities.Entity;
import com.nick.ant.towerdefense.renderables.entities.players.Player;
import com.nick.ant.towerdefense.renderables.entities.players.UserPlayer;
import com.nick.ant.towerdefense.renderables.entities.world.Map;
import com.nick.ant.towerdefense.renderables.ui.HUD;

/**
 * Created by Nick on 08/09/2014.
 */
public class RoomGame extends Room {
    private Map map;
    private World world;
    private float mouseX = 0f;
    private float mouseY = 0f;
    public Player userPlayer;

    @Override
    public void create() {
        // Force it to load the instances
        CharacterManager.getInstance();
        WeaponManager.getInstance();

        map = new Map("de_dust2");
        world = new World(new Vector2(0, 0), true);

        userPlayer = new UserPlayer(16,16);
        addEntity(userPlayer, world);
        map.setEntitySnap(userPlayer);

        HUD hud = new HUD(this);
        addRenderable(hud);
    }

    @Override
    public void render() {
        map.step();
        getSpriteBatch().setProjectionMatrix(map.getCamera().combined);
        map.render();

        super.render();
    }

    public void step()  {
        super.step();

        world.step(1, 6, 2);

        mouseX = Gdx.input.getX() + map.getCameraX() - Gdx.graphics.getWidth()/2;
        mouseY = Gdx.graphics.getHeight() - Gdx.input.getY() + map.getCameraY() - Gdx.graphics.getHeight()/2;
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
        return map.getCameraX();
    }

    @Override
    public float getViewY() {
        return map.getCameraY();
    }

    public Map getMap() {
        return map;
    }
}
