package com.nick.ant.towerdefense;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.nick.ant.towerdefense.entities.Entity;
import com.nick.ant.towerdefense.entities.players.Player;
import com.nick.ant.towerdefense.rooms.Room;
import com.nick.ant.towerdefense.rooms.RoomGame;
import com.nick.ant.towerdefense.services.TextureManager;
import com.nick.ant.towerdefense.ui.FPSCounter;
import com.nick.ant.towerdefense.ui.UIComponent;

import java.util.ArrayList;
import java.util.List;

public class Game extends ApplicationAdapter {
	private SpriteBatch batch;
    private Room currentRoom;
    private UIComponent fpsCounter;
	
	@Override
	public void create () {
        

        currentRoom = new RoomGame();
		batch = new SpriteBatch();
        fpsCounter = new FPSCounter();
	}

    public void step()  {
        currentRoom.step();
    }

	@Override
	public void render () {
        step();

		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();

        if (currentRoom != null) {
            currentRoom.render(batch);
        }

        fpsCounter.render(batch);

		batch.end();
	}
}
