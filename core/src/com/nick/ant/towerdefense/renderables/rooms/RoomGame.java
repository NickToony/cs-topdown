package com.nick.ant.towerdefense.renderables.rooms;

import com.nick.ant.towerdefense.renderables.entities.world.World;

/**
 * Created by Nick on 08/09/2014.
 */
public class RoomGame extends Room {
    private World world;

    public RoomGame()   {
        world = new World("harry");
        addRenderable(world);
    }


}
