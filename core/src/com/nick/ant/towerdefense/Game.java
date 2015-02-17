package com.nick.ant.towerdefense;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nick.ant.towerdefense.networking.ServerContainer;
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

    @Override
    public void dispose() {
        super.dispose();

        ServerContainer.dispose();
    }

    // Constants
    private static Gson gson;
    public static Gson getGson() {
        if (gson == null) {
            gson = new GsonBuilder()
                    .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                    .create();
        }
        return gson;
    }
}
