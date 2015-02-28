package com.nick.ant.towerdefense.renderables.lights;

import box2dLight.RayHandler;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.nick.ant.towerdefense.renderables.Renderable;
import com.nick.ant.towerdefense.renderables.entities.world.Map;

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
        OrthographicCamera camera = map.getCamera();
        handler.setCombinedMatrix(camera.combined, camera.position.x, camera.position.y, camera.viewportWidth, camera.viewportHeight);
        handler.updateAndRender();
    }

    @Override
    public void step() {

    }

    @Override
    public void dispose() {
        handler.dispose();
    }

    @Override
    public void createGL() {

    }

    public RayHandler getHandler() {
        return handler;
    }
}
