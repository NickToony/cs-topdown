package com.nick.ant.towerdefense.rooms;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.nick.ant.towerdefense.entities.Entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nick on 08/09/2014.
 */
public abstract class Room {
    private List<Entity> entityList = new ArrayList<Entity>();

    public void addEntity(Entity entity) {
        entityList.add(entity);
    }

    public void render(SpriteBatch spriteBatch) {
        for (Entity entity : entityList)    {
            entity.render(spriteBatch);
        }
    }

    public void step() {
        for (Entity entity : entityList)    {
            entity.step();
        }
    }
}
