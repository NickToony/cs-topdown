package com.nicktoony.cstopdown.components;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.nicktoony.cstopdown.MyGame;
import com.nicktoony.cstopdown.services.SoundManager;
import com.nicktoony.cstopdown.services.TextureManager;

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

    @Override
    public void create(boolean render) {
        renderables = new ArrayList<Renderable>();
        deletedRenderables = new ArrayList<Renderable>();
        this.render = render;
        this.created = true;
    }

    @Override
    public void step(float delta) {
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
        TextureManager.dispose();
        SoundManager.dispose();
    }

    public synchronized Renderable addRenderable(Renderable renderable) {
        renderables.add(renderable);
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
        if (renderables.contains(renderable)) {
            deletedRenderables.add(renderable);
        }
    }

    public MyGame getGame() {
        return game;
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
