package com.nick.ant.towerdefense.rooms;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.nick.ant.towerdefense.entities.Entity;
import com.nick.ant.towerdefense.entities.players.Player;
import com.nick.ant.towerdefense.entities.world.World;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nick on 08/09/2014.
 */
public class RoomGame extends Room {
    private World world;

    public RoomGame()   {
        world = new World(32, 32, 32);
        world.setTile(5, 5, "black.png");
        world.setTile(5, 6, "black.png");
        world.setArea(10, 10, 12, 12, "black.png");
        addEntity(world);
    }


}
