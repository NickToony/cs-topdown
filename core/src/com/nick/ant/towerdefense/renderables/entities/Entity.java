package com.nick.ant.towerdefense.renderables.entities;

import com.badlogic.gdx.math.Circle;
import com.nick.ant.towerdefense.renderables.Renderable;
import com.nick.ant.towerdefense.renderables.entities.collisions.CollisionManager;
import com.nick.ant.towerdefense.renderables.rooms.Room;

/**
 * Created by Nick on 08/09/2014.
 */
public abstract class Entity extends Renderable {
    protected float x = 0;
    protected float y = 0;
    protected float hSpeed = 0;
    protected float vSpeed = 0;
    protected Room room;
    protected CollisionManager collisionManager;

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setRoom(Room room)  {
        this.room = room;
    }

    public void setCollisionManager(CollisionManager collisionManager)  {
        this.collisionManager = collisionManager;
    }

    @Override
    public void step()  {
        if (vSpeed == 0 && hSpeed == 0) {
            return;
        }

        float newX = x + hSpeed;
        float newY = y + vSpeed;

        if (collisionManager == null)   {
            x = newX;
            y = newY;
            return;
        }

        Circle circle = new Circle(x, y, 16);

        if (newX != x) {
            circle.setX(newX);
            if (collisionManager.checkCollision(circle)) {
                x = newX;
            }
        }

        if (newY != y) {
            circle.setX(x);
            circle.setY(newY);
            if (collisionManager.checkCollision(circle)) {
                y = newY;
            }
        }
    }
}
