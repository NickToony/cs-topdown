package com.nicktoony.engine.components;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.nicktoony.cstopdown.rooms.game.RoomGame;

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
        x = body.getPosition().x * getRoom().getSocket().getServerConfig().sv_pixels_per_metre;
        y = body.getPosition().y * getRoom().getSocket().getServerConfig().sv_pixels_per_metre;
    }

    public void updatePhysicsPosition() {
        body.setTransform(getX() / getRoom().getSocket().getServerConfig().sv_pixels_per_metre,
                getY() / getRoom().getSocket().getServerConfig().sv_pixels_per_metre, 0);
        changedPhysicsPosition = false;
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
    }

    @Override
    public void setY(float y) {
        super.setY(y);
        changedPhysicsPosition = true;
    }

    public boolean collisionEntity(Contact contact, Entity other) {
        return false;
    };

    public void collisionOther(Contact contact) {

    };
}
