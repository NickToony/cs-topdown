package com.nick.ant.towerdefense.renderables.rooms;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.nick.ant.towerdefense.renderables.Renderable;
import com.nick.ant.towerdefense.renderables.entities.Entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nick on 08/09/2014.
 */
public abstract class Room extends Renderable {
    private List<Renderable> entityList = new ArrayList<Renderable>();

    public void addEntity(Entity entity) {
        entityList.add(entity);
    }

    public void addRenderable(Renderable renderable)    {
        entityList.add(renderable);
    }

    public void render(SpriteBatch spriteBatch) {
        for (Renderable renderable : entityList)    {
            renderable.render(spriteBatch);
        }
    }

    public void step()  {
        for (Renderable renderable : entityList)    {
            renderable.step();
        }
    }
}
