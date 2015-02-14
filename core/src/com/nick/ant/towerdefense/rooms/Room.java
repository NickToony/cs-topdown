package com.nick.ant.towerdefense.rooms;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.World;
import com.nick.ant.towerdefense.Game;
import com.nick.ant.towerdefense.components.SkinManager;
import com.nick.ant.towerdefense.components.TextureManager;
import com.nick.ant.towerdefense.renderables.Renderable;
import com.nick.ant.towerdefense.renderables.entities.Entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nick on 08/09/2014.
 */
public abstract class Room {
    protected List<Renderable> entityList = new ArrayList<Renderable>();
    private SpriteBatch spriteBatch = new SpriteBatch();
    private Game game;

    public abstract void create();

    public void addEntity(Entity entity, World world) {
        entity.setWorld(world);
        addEntity(entity);
    }

    public void addEntity(Entity entity) {
        entityList.add(entity);
        entity.setRoom(this);
        entity.create();
    }

    public void addRenderable(Renderable renderable)    {
        entityList.add(renderable);
        renderable.setRoom(this);
        renderable.create();
    }

    public void render() {
        spriteBatch.begin();
        for (Renderable renderable : entityList) {
            renderable.render(spriteBatch);
        }
        spriteBatch.end();
    }


    public void step()  {
        for (Renderable renderable : entityList)    {
            renderable.step();
        }
    }

    protected SpriteBatch getSpriteBatch() {
        return this.spriteBatch;
    }

    public float getMouseX() {
        return Gdx.input.getX();
    }

    public float getMouseY() {
        return Gdx.input.getY();
    }

    public float getViewX() {
        return 0;
    }

    public float getViewY() {
        return 0;
    }

    public void dispose()   {
        for (Renderable renderable : entityList) {
            renderable.dispose();
        }
        spriteBatch.dispose();

        // dispose managers
        TextureManager.dispose();
        SkinManager.dispose();
    }

    public void makePriority(Entity entitySnap) {
        if (entityList.contains(entitySnap))    {
            entityList.remove(entitySnap);
            entityList.add(0, entitySnap);
        }
    }

    public void navigateToRoom(Room room) {
        game.navigateToRoom(room);
    }

    public void setGame(Game game) {
        this.game = game;
    }
}
