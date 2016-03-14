package com.nicktoony.cstopdown.rooms.game.entities.lights;

import box2dLight.RayHandler;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.nicktoony.cstopdown.components.Renderable;
import com.nicktoony.cstopdown.rooms.game.entities.world.Map;

/**
 * Created by Nick on 14/02/2015.
 */
public class RayHandlerWrapper extends Renderable {
    private RayHandler handler;
    private Map map;

    public RayHandlerWrapper(RayHandler handler, Map map) {
        this.handler = handler;
        this.map = map;
    }


    @Override
    public void render(SpriteBatch spriteBatch) {
        // Render the light over everything
        handler.setCombinedMatrix(map.getCamera().combined);
        handler.updateAndRender();
    }

    @Override
    public void create(boolean render) {

    }

    @Override
    public void step() {

    }

    @Override
    public void dispose() {
        handler.dispose();
    }

    public RayHandler getHandler() {
        return handler;
    }
}
