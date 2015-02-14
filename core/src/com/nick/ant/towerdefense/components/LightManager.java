package com.nick.ant.towerdefense.components;

import box2dLight.ConeLight;
import box2dLight.Light;
import box2dLight.PointLight;
import box2dLight.RayHandler;
import com.badlogic.gdx.graphics.Color;

/**
 * Created by Nick on 14/02/2015.
 */
public class LightManager {
    public static Light defineTorch(RayHandler rayHandler) {
        return new ConeLight(rayHandler, 100, Color.WHITE, 400, 16, 16, 0, 25);
    }

    public static Light definePlayerGlow(RayHandler rayHandler) {
        return new PointLight(rayHandler, 100, new Color(0, 0, 0, 0.5f), 100, 0, 0);
    }
}
