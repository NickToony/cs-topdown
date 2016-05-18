package com.nicktoony.cstopdown.rooms.game.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.nicktoony.cstopdown.rooms.game.entities.players.Player;
import com.nicktoony.engine.components.Entity;
import com.nicktoony.engine.components.PhysicsEntity;
import com.nicktoony.engine.services.TextureManager;

/**
 * Created by Nick on 17/05/2016.
 */
public class Bullet extends PhysicsEntity {

    private Texture texture;
    private Sprite sprite;
    private Player owner;


    public Bullet(float x, float y, float direction, Player owner) {
        setX(x);
        setY(y);
        setDirection(direction);
        this.owner = owner;
    }

    @Override
    protected void create(boolean render) {
        super.create(render);

        if (render) {
            texture = TextureManager.getTexture("weapons/bullet.png");
            sprite = new Sprite(texture);
        }
    }

    @Override
    public void step(float delta) {
        super.step(delta);
    }

    @Override
    public void render(SpriteBatch spriteBatch) {
        sprite.setPosition(x, y);
        sprite.setRotation(direction+90);
        sprite.draw(spriteBatch);
    }

    @Override
    public void dispose(boolean render) {
        super.dispose(render);
    }

    protected Body setupBody() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x, y);
        bodyDef.allowSleep = false;
        bodyDef.bullet = true;
        bodyDef.angle = (float) Math.toRadians(direction+90);

        Body body = getRoom().getWorld().createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(1, 0.1f);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 20f;
        fixtureDef.restitution = 0f;
        fixtureDef.isSensor = true;

        body.createFixture(fixtureDef);
        double radians = Math.toRadians(direction+90);
        body.setLinearVelocity(new Vector2((float)Math.cos(radians), (float)Math.sin(radians)).limit(2f));
        shape.dispose();

        return body;
    }

    @Override
    public boolean collisionEntity(Contact contact, Entity other) {
        if (other == owner) {
            return true;
        } else if (other instanceof Bullet) {
            return true;
        }

        return false;
    }

    @Override
    public void collisionOther(Contact contact) {
        getRoom().deleteRenderable(this);
    }
}
