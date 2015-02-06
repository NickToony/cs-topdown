package com.nick.ant.towerdefense;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.nick.ant.towerdefense.rooms.Room;
import com.nick.ant.towerdefense.rooms.RoomMainMenu;

public class Game extends ApplicationAdapter {
    private Room currentRoom;
	
	@Override
	public void create () {
        navigateToRoom(new RoomMainMenu());
	}

    public void navigateToRoom(Room room) {
        if (currentRoom != null) {
            currentRoom.dispose();
            currentRoom = null;
        }

        currentRoom = room;
        currentRoom.setGame(this);
        currentRoom.create();
    }

	@Override
	public void render () {
        currentRoom.step();

		Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        currentRoom.render();
	}
}
