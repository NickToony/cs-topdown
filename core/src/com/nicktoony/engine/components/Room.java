package com.nicktoony.engine.components;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.nicktoony.engine.MyGame;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by nick on 15/07/15.
 */
public class Room extends Renderable {

    private List<Renderable> renderables;
    private MyGame game;
    private boolean render;
    private boolean created = false;
    private List<Renderable> deletedRenderables;
    private List<Renderable> addedRenderables;

    @Override
    public void create(boolean render) {
        renderables = new ArrayList<Renderable>();
        deletedRenderables = new ArrayList<Renderable>();
        addedRenderables = new ArrayList<Renderable>();
        this.render = render;
        this.created = true;
    }

    @Override
    public void step(float delta) {
        // For all added renderables
        for (Renderable toAdd : addedRenderables) {
            // Remove them from the queue
            renderables.add(toAdd);
        }
        // Clear the list
        addedRenderables.clear();

        // For all deleted renderables
        for (Renderable toDelete : deletedRenderables) {
            // Remove them from the queue
            if (renderables.remove(toDelete)) {
                toDelete.dispose(render);
            }
        }
        // Clear the list
        deletedRenderables.clear();

        // Now only non-deleted renderables should run
        for (Renderable renderable : renderables) {
            renderable.step(delta);
        }
    }

    @Override
    public void render(SpriteBatch spriteBatch) {
        spriteBatch.begin();
        for (Renderable renderable : renderables) {
                renderable.render(spriteBatch);
        }
        spriteBatch.end();
    }

    @Override
    public void dispose(boolean render) {
        for (Renderable renderable : renderables) {
            renderable.dispose(render);
        }
    }

    public  Renderable addRenderable(Renderable renderable) {
        addedRenderables.add(renderable);
        renderable.create(render);
        return renderable;
    }

    public Renderable addEntity(Entity entity) {
        entity.setRoom(this);
        addRenderable(entity);
        return entity;
    }

    public Entity addSelfManagedEntity(Entity entity) {
        entity.setRoom(this);
        entity.create(render);
        return entity;
    }

    public boolean bringRenderableToFront(Renderable renderable) {
        int pos = renderables.indexOf(renderable);
        if (pos == -1) {
            return false;
        }

        Collections.swap(renderables, 0, pos);
        return true;
    }

    public boolean bringRenderableToBack(Renderable renderable) {
        int pos = renderables.indexOf(renderable);
        if (pos == -1) {
            return false;
        }

        Collections.swap(renderables, renderables.size()-1, pos);
        return true;
    }

    public void deleteRenderable(Renderable renderable) {
//        if (renderables.contains(renderable)) {
            deletedRenderables.add(renderable);
//        }
    }

    public void resize(int width, int height) {

    }

    public MyGame getGame() {
        return game;
    }

    public <T> T getAsset (String fileName, Class<T> type) {
        return getGame().getAsset(fileName, type);
    }

    public AssetManager getAssetManager() {
        return getGame().getAssetManager();
    }

    public void setGame(MyGame game) {
        this.game = game;
    }

    public boolean isRender() {
        return render;
    }

    public boolean isCreated() {
        return created;
    }
}
