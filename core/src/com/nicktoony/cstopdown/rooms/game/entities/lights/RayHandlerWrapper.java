package com.nicktoony.cstopdown.rooms.game.entities.lights;

import box2dLight.RayHandler;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.nicktoony.cstopdown.rooms.game.entities.world.Map;
import com.nicktoony.engine.components.Renderable;

/**
 * Created by Nick on 14/02/2015.
 */
public class RayHandlerWrapper extends Renderable {
    private RayHandler handler;
    private Map map;
    private OrthographicCamera boxLightCamera;

    public RayHandlerWrapper(RayHandler handler, Map map) {
        this.handler = handler;
        this.map = map;

        boxLightCamera = new OrthographicCamera();
        float boxLightViewportWidth = map.getCamera().viewportWidth / 32;
        float boxLightViewportHeight = map.getCamera().viewportHeight / 32;
        boxLightCamera.setToOrtho(false, boxLightViewportWidth, boxLightViewportHeight);
        boxLightCamera.zoom = map.getCamera().zoom;
        boxLightCamera.update(true);
    }


    @Override
    public void render(SpriteBatch spriteBatch) {
        boxLightCamera.position.set(map.getCamera().position.x / 32, map.getCamera().position.y / 32, 0);
        boxLightCamera.update();

        // Render the light over everything
        handler.setCombinedMatrix(boxLightCamera.combined,
                boxLightCamera.position.x, boxLightCamera.position.y,
                boxLightCamera.viewportWidth * boxLightCamera.zoom,
                boxLightCamera.viewportHeight * boxLightCamera.zoom);
        handler.updateAndRender();
    }

    @Override
    public void create(boolean render) {

    }

    @Override
    public void step(float delta) {

    }

    @Override
    public void dispose(boolean render) {
        handler.dispose();
    }

    public RayHandler getHandler() {
        return handler;
    }
}
