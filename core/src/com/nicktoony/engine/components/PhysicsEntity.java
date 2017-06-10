package com.nicktoony.engine.components;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.nicktoony.engine.rooms.RoomGame;
import com.nicktoony.engine.EngineConfig;

/**
 * Created by Nick on 17/05/2016.
 */
public abstract class PhysicsEntity extends Entity<RoomGame> {

    protected Body body;
    private boolean changedPhysicsPosition = false;

    @Override
    protected void create(boolean render) {
        body = setupBody();
        body.setUserData(this);
    }

    @Override
    public void step(float delta) {
        if (changedPhysicsPosition) {
            updatePhysicsPosition();
        }
        x = EngineConfig.toPixels(body.getPosition().x);
        y = EngineConfig.toPixels(body.getPosition().y);
    }

    public void updatePhysicsPosition() {
        if (body != null) {
            body.setTransform(EngineConfig.toMetres(getX()),
                    EngineConfig.toMetres(getY()), 0);
            changedPhysicsPosition = false;
        }
    }


    protected abstract Body setupBody();

    @Override
    public void dispose(boolean render) {
        getRoom().getWorld().destroyBody(body);
    }

    @Override
    public void setX(float x) {
        super.setX(x);
        changedPhysicsPosition = true;
        updatePhysicsPosition();
    }

    @Override
    public void setY(float y) {
        super.setY(y);
        changedPhysicsPosition = true;
        updatePhysicsPosition();
    }

    public boolean collisionEntity(Contact contact, Entity other) {
        return false;
    };

    public void collisionOther(Contact contact) {

    };

    public Body getBody() {
        return body;
    }
}
