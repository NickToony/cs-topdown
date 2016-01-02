package com.nicktoony.cstopdown.components;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.nicktoony.cstopdown.MyGame;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by nick on 15/07/15.
 */
public class Room implements Renderable {

    private List<Renderable> renderables;
    private MyGame game;
    private boolean render;

    @Override
    public void create(boolean render) {
        renderables = new ArrayList<Renderable>();
        this.render = render;
    }

    @Override
    public void step() {
        for (Renderable renderable : renderables) {
            renderable.step();
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
    public void dispose() {
        for (Renderable renderable : renderables) {
            renderable.dispose();
        }
    }

    public Renderable addRenderable(Renderable renderable) {
        renderables.add(renderable);
        renderable.create(render);
        return renderable;
    }

    public Renderable addEntity(Entity entity) {
        entity.setRoom(this);
        addRenderable(entity);
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

    public MyGame getGame() {
        return game;
    }

    public void setGame(MyGame game) {
        this.game = game;
    }

    public boolean isRender() {
        return render;
    }
}
