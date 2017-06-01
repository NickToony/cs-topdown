package com.nicktoony.engine.entities.world;

import com.badlogic.gdx.ai.utils.Collision;
import com.badlogic.gdx.ai.utils.Ray;
import com.badlogic.gdx.ai.utils.RaycastCollisionDetector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.nicktoony.engine.EngineConfig;

/**
 * Created by Nick on 25/03/2016.
 */
public class PathfindingRaycastCollisionDetector implements RaycastCollisionDetector<Vector2> {
    private World world;
    private boolean hasCollided = false;
    private RayCastCallback callback = new RayCastCallback() {
        @Override
        public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
            hasCollided = true;
            return 0;
        }
    };

    public PathfindingRaycastCollisionDetector(World world) {
        this.world = world;
    }

    @Override
    public boolean collides(Ray<Vector2> ray) {
        hasCollided = false;
        Vector2 start = ray.start.scl(32);
        Vector2 end = ray.end.scl(32);
        world.rayCast(callback, EngineConfig.toMetres(start), EngineConfig.toMetres(end) );
        return hasCollided;
    }

    @Override
    public boolean findCollision(Collision<Vector2> outputCollision, Ray<Vector2> inputRay) {
        throw new UnsupportedOperationException();
    }
}
