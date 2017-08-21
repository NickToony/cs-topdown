package com.nicktoony.engine.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.nicktoony.cstopdown.rooms.game.entities.players.Player;
import com.nicktoony.engine.components.Entity;
import com.nicktoony.engine.components.PhysicsEntity;

/**
 * Created by Nick on 17/05/2016.
 */
public class Bullet extends PhysicsEntity {

    private Texture texture;
    private Sprite sprite;
    private Player owner;
    private Vector2 initialPosition;
    private float range;


    public Bullet(float x, float y, float direction, Player owner, float range) {
        setX(x);
        setY(y);
        setDirection(direction);
        this.owner = owner;
        this.initialPosition = getPosition();
        this.range = range;
    }

    @Override
    protected void create(boolean render) {
        super.create(render);

        if (render) {
            texture = getAsset("weapons/bullet.png", Texture.class);
            sprite = new Sprite(texture);
        }
    }

    @Override
    public void step(float delta) {
        super.step(delta);

        if (initialPosition.dst(getPosition()) >= range) {
            this.getRoom().deleteRenderable(this);
        }
    }

    @Override
    public void render(SpriteBatch spriteBatch) {
        sprite.setOriginCenter();
        sprite.setPosition(x-sprite.getWidth()/2, y-sprite.getHeight()/2);
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
        shape.setAsBox(0.5f, 0.5f);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 20f;
        fixtureDef.restitution = 0f;
        fixtureDef.filter.categoryBits = 0x0004;
        fixtureDef.isSensor = true;

        body.createFixture(fixtureDef);
        double radians = Math.toRadians(direction+90);
        body.setLinearVelocity(new Vector2((float)Math.cos(radians), (float)Math.sin(radians)).limit(4f)); //speed
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
