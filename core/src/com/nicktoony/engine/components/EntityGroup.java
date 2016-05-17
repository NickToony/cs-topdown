package com.nicktoony.engine.components;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Contact;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nick on 15/07/15.
 *
 * An entity group is a collection of entities that share a sprite batch. Before its children's render methods are
 * called, the spritebatch is begun.
 */
public class EntityGroup<T extends Room> extends Entity<T> {
    private List<Entity> entities;
    private SpriteBatch spriteBatch;
    private Room room;
    private boolean render;

    @Override
    public void create(boolean render) {
        entities = new ArrayList<Entity>();
        this.render = render;
        if (render) {
            spriteBatch = new SpriteBatch();
        }
    }

    @Override
    public void step(float delta) {
        for (Entity entity : entities) {
            entity.step(delta);
        }
    }

    @Override
    public void render(SpriteBatch spriteBatch) {
        this.spriteBatch.begin();
        this.spriteBatch.setProjectionMatrix(spriteBatch.getProjectionMatrix());
        for (Entity entity : entities) {
            entity.render(this.spriteBatch);
        }
        this.spriteBatch.end();
    }

    @Override
    public void dispose(boolean render) {
        for (Entity entity : entities) {
            entity.dispose(render);
        }
        spriteBatch.dispose();
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public T getRoom() {
        return (T) room;
    }

    public Room getUncastedRoom() {
        return room;
    }

    public Renderable addEntity(Entity entity) {
        entity.setRoom(room);
        entities.add(entity);
        entity.create(render);
        return entity;
    }
}
