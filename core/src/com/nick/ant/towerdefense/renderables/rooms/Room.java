package com.nick.ant.towerdefense.renderables.rooms;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;
import com.nick.ant.towerdefense.renderables.Renderable;
import com.nick.ant.towerdefense.renderables.entities.Entity;
import com.nick.ant.towerdefense.renderables.entities.players.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nick on 08/09/2014.
 */
public abstract class Room {
    protected List<Renderable> entityList = new ArrayList<Renderable>();

    public void addEntity(Entity entity) {
        entityList.add(entity);
        entity.setRoom(this);
    }

    public void addRenderable(Renderable renderable)    {
        entityList.add(renderable);
    }

    public void render(SpriteBatch spriteBatch) {
        for (Renderable renderable : entityList) {
            renderable.render(spriteBatch);
        }
    }


    public void step()  {
        for (Renderable renderable : entityList)    {
            renderable.step();
        }
    }

    public abstract SpriteBatch getSpriteBatch();

    public abstract float getMouseX();
    public abstract float getMouseY();

}
