package com.nicktoony.cstopdown.rooms.game.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.nicktoony.cstopdown.rooms.game.RoomGame;
import com.nicktoony.cstopdown.rooms.game.entities.players.Player;
import com.nicktoony.engine.components.Entity;
import com.nicktoony.engine.services.TextureManager;

/**
 * Created by Nick on 17/05/2016.
 */
public class Bullet extends Entity<RoomGame> {

    private Body body;
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
        setupBody();

        if (render) {
            texture = TextureManager.getTexture("weapons/Rifles/rifle_ak47/texture.png");
            sprite = new Sprite(texture);
        }
    }

    @Override
    public void step(float delta) {
        x = body.getPosition().x;
        y = body.getPosition().y;
    }

    @Override
    public void render(SpriteBatch spriteBatch) {
        sprite.setPosition(x, y);
        sprite.setRotation(direction);
        sprite.draw(spriteBatch);
    }

    @Override
    public void dispose(boolean render) {
        getRoom().getWorld().destroyBody(body);
    }

    private void setupBody() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x, y);
        bodyDef.allowSleep = false;
        bodyDef.bullet = true;
        bodyDef.angle = direction;

        body = getRoom().getWorld().createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(100, 1);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 20f;
        fixtureDef.restitution = 0f;
        fixtureDef.isSensor = true;

        body.createFixture(fixtureDef);
        body.setUserData(this);
        double radians = Math.toRadians(direction+90);
        body.setLinearVelocity(new Vector2((float)Math.cos(radians), (float)Math.sin(radians)));
        shape.dispose();
    }

    @Override
    public boolean collisionEntity(Contact contact, Entity other) {
        if (other == owner) {
            return true;
        }

        return false;
    }

    @Override
    public void collisionOther(Contact contact) {
        //getRoom().deleteRenderable(this);
    }

    @Override
    public boolean shouldGlide(Contact contact) {
        return true;
    }
}
