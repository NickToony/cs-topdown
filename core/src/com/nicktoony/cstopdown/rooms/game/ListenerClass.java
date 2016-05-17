package com.nicktoony.cstopdown.rooms.game;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.nicktoony.engine.components.Entity;

/**
 * Created by Nick on 17/05/2016.
 */
public class ListenerClass implements ContactListener {
    @Override
    public void beginContact(Contact contact) {
        boolean handled = false;
        if (contact.getFixtureA().getBody().getUserData() != null
                && contact.getFixtureA().getBody().getUserData() != null) {
            Entity entityOne = (Entity) contact.getFixtureA().getBody().getUserData();
            Entity entityTwo = (Entity) contact.getFixtureB().getBody().getUserData();
            if (entityOne.collisionEntity(contact, entityTwo)) {
                handled = true;
            }
        }

        if (!handled) {
            if (contact.getFixtureA().getBody().getUserData() != null) {
                Entity entityOne = (Entity) contact.getFixtureA().getBody().getUserData();
                entityOne.collisionOther(contact);
            }
        }
    }

    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
        if (contact.getFixtureA().getBody().getUserData() != null) {
            Entity entityOne = (Entity) contact.getFixtureA().getBody().getUserData();
            if (entityOne.shouldGlide(contact)) {
                //contact.setEnabled(false);
            }
        }

        if (contact.getFixtureB().getBody().getUserData() != null) {
            Entity entityTwo = (Entity) contact.getFixtureB().getBody().getUserData();
            if (entityTwo.shouldGlide(contact)) {
                //contact.setEnabled(false);
            }
        }
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
