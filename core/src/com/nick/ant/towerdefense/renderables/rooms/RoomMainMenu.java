package com.nick.ant.towerdefense.renderables.rooms;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.nick.ant.towerdefense.renderables.entities.collisions.CollisionManager;
import com.nick.ant.towerdefense.renderables.entities.players.Player;
import com.nick.ant.towerdefense.renderables.entities.players.UserPlayer;
import com.nick.ant.towerdefense.renderables.entities.world.World;
import com.nick.ant.towerdefense.renderables.ui.TextLabel;

/**
 * Created by Nick on 08/09/2014.
 */
public class RoomMainMenu extends Room {
    private SpriteBatch spriteBatch;

    public RoomMainMenu()   {
        spriteBatch = new SpriteBatch();
        addRenderable(new TextLabel("Hello World"));

    }

    public SpriteBatch getSpriteBatch() {
        return spriteBatch;
    }

    public void step()  {
        super.step();

    }

    public float getMouseX() {
        return Gdx.input.getX();
    }

    public float getMouseY() {
        return Gdx.input.getY();
    }

    @Override
    public float getViewX() {
        return 0;
    }

    @Override
    public float getViewY() {
        return 0;
    }

    @Override
    public void dispose()   {
        super.dispose();
        spriteBatch.dispose();
    }
}
