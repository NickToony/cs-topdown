package com.nick.ant.towerdefense.rooms;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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

    public void addEntity(Entity entity) {
        entityList.add(entity);
        entity.setRoom(this);
    }

    public void addRenderable(Renderable renderable)    {
        entityList.add(renderable);
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

    public abstract float getMouseX();
    public abstract float getMouseY();

    public abstract float getViewX();
    public abstract float getViewY();

    public void dispose()   {
        for (Renderable renderable : entityList) {
            renderable.dispose();
        }
        spriteBatch.dispose();
        TextureManager.dispose();
    }

    public void makePriority(Entity entitySnap) {
        if (entityList.contains(entitySnap))    {
            entityList.remove(entitySnap);
            entityList.add(0, entitySnap);
        }
    }
}
