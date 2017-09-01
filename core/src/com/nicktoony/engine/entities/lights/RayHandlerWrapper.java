package com.nicktoony.engine.entities.lights;

import box2dLight.RayHandler;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.nicktoony.engine.entities.world.Map;
import com.nicktoony.engine.EngineConfig;
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
        float boxLightViewportWidth = EngineConfig.toMetres(map.getCamera().viewportWidth);
        float boxLightViewportHeight = EngineConfig.toMetres(map.getCamera().viewportHeight);
        boxLightCamera.setToOrtho(false, boxLightViewportWidth, boxLightViewportHeight);
        boxLightCamera.zoom = map.getCamera().zoom;
        boxLightCamera.update(true);
    }


    @Override
    public void render(SpriteBatch spriteBatch) {
        boxLightCamera.combined.set(map.getCamera().combined);
//        boxLightCamera.position.set(EngineConfig.toMetres(map.getCamera().position.x),
//                EngineConfig.toMetres(map.getCamera().position.y), 0);
//        boxLightCamera.zoom = EngineConfig.toMetres(map.getCamera().zoom);
//        boxLightCamera.viewportWidth = EngineConfig.toMetres(map.getCamera().viewportWidth);
//        boxLightCamera.viewportHeight = EngineConfig.toMetres(map.getCamera().viewportHeight);
        boxLightCamera.combined.scl(32);
//        boxLightCamera.update();

        // Render the light over everything
//        handler.setCombinedMatrix(map.getCamera().combined,
//                boxLightCamera.position.x, boxLightCamera.position.y,
//                boxLightCamera.viewportWidth * boxLightCamera.zoom,
//                boxLightCamera.viewportHeight * boxLightCamera.zoom);
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

    @Override
    public void resize(int x, int y) {
        float boxLightViewportWidth = EngineConfig.toMetres(map.getCamera().viewportWidth);
        float boxLightViewportHeight = EngineConfig.toMetres(map.getCamera().viewportHeight);
        boxLightCamera.setToOrtho(false, boxLightViewportWidth, boxLightViewportHeight);
    }

    public RayHandler getHandler() {
        return handler;
    }
}
