package com.nick.ant.towerdefense.components;

import box2dLight.ConeLight;
import box2dLight.Light;
import box2dLight.PointLight;
import box2dLight.RayHandler;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.maps.MapProperties;

/**
 * Created by Nick on 14/02/2015.
 */
public class LightManager {
    public static Light defineTorch(RayHandler rayHandler) {
        return new ConeLight(rayHandler, 100, Color.WHITE, 400, 16, 16, 0, 25);
    }

    public static Light definePlayerGlow(RayHandler rayHandler) {
        PointLight pointLight = new PointLight(rayHandler, 100, new Color(0, 0, 0, 0.5f), 100, 0, 0);

        pointLight.setXray(true);

        return pointLight;
    }

    public static Light definePointLight(RayHandler rayHandler, MapProperties mapProperties, float x, float y) {
        PointLight pointLight = new PointLight(rayHandler, 100);

        // Make them not so expensive..
        pointLight.setStaticLight(true);
        // Get the colourd
        Color color = Color.valueOf(mapProperties.get("color", String.class));
        color.a = Float.parseFloat(mapProperties.get("alpha", String.class));
        pointLight.setColor(color);
        // The size
        pointLight.setDistance(Integer.parseInt(mapProperties.get("distance", String.class)));
        // Position
        pointLight.setPosition(x, y);

        return pointLight;
    }

    public static ConeLight defineGunFire(RayHandler handler) {
        ConeLight coneLight = new ConeLight(handler, 100, Color.YELLOW, 50, 16, 16, 0, 90);
        coneLight.setXray(true);
        coneLight.getColor().a = .2f;
        return coneLight;
    }
}
