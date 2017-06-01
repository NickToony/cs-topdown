package com.nicktoony.engine;

import com.badlogic.gdx.math.Vector2;

/**
 * Created by Nick on 18/05/2016.
 */
public class EngineConfig {
    public static final float PIXELS_PER_METRE = 32;
    public static final float METRES_PER_PIXEL = 1 / PIXELS_PER_METRE;
    public static final int CELL_SIZE = 32;

    public static float toMetres(float pixels) {
        return pixels * METRES_PER_PIXEL;
    }

    public static Vector2 toMetres(Vector2 vector) {
        return new Vector2(toMetres(vector.x), toMetres(vector.y));
    }

    public static float toPixels(float metres) {
        return metres * PIXELS_PER_METRE;
    }
}
