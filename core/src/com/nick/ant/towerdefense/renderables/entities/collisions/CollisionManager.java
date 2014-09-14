package com.nick.ant.towerdefense.renderables.entities.collisions;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.TextureMapObject;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.nick.ant.towerdefense.renderables.entities.world.World;

/**
 * Created by Nick on 14/09/2014.
 */
public class CollisionManager {

    private World world;

    public CollisionManager(World world)    {
        this.world = world;
    }

    public boolean checkCollision(Circle entity) {
        for (Rectangle rectangle : world.getCollisionObjects()) {
            if (Intersector.overlaps(entity, rectangle))    {
                return false;
            }
        }

        return true;
    }
}
