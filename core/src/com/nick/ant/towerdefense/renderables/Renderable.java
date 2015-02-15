package com.nick.ant.towerdefense.renderables;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.nick.ant.towerdefense.rooms.Room;

/**
 * Created by Nick on 14/09/2014.
 */
public abstract class Renderable {
    protected Room room;
    private boolean created = false;

    public void render(SpriteBatch spriteBatch) {
        if (!created) {
            createGL();
            created = true;
        }
    };
    public abstract void step();
    public abstract void dispose();
    public abstract void createGL();
    public void createLogic() {

    };

    public void setRoom(Room room) {
        this.room = room;
    }
}
