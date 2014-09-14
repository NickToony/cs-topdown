package com.nick.ant.towerdefense.renderables.rooms;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.nick.ant.towerdefense.renderables.entities.players.Player;
import com.nick.ant.towerdefense.renderables.entities.players.UserPlayer;
import com.nick.ant.towerdefense.renderables.entities.world.World;

/**
 * Created by Nick on 08/09/2014.
 */
public class RoomGame extends Room {
    private World world;
    private SpriteBatch spriteBatch;

    public RoomGame()   {
        spriteBatch = new SpriteBatch();
        world = new World("de_dust2");

        Player player = new UserPlayer(16,16);
        addEntity(player);
        world.setEntitySnap(player);
    }

    public SpriteBatch getSpriteBatch() {

        world.step();
        spriteBatch.setProjectionMatrix(world.getCamera().combined);
        world.render();
        return spriteBatch;
    }
}
