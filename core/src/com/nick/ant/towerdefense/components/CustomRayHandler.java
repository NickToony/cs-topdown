package com.nick.ant.towerdefense.components;

import box2dLight.RayHandler;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Created by nick on 28/02/15.
 */
public class CustomRayHandler extends RayHandler {
    public CustomRayHandler(World world) {
        super(world);
    }

    public CustomRayHandler(World world, int fboWidth, int fboHeigth) {
        super(world, fboWidth, fboHeigth);
    }
}
