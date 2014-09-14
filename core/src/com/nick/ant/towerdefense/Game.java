package com.nick.ant.towerdefense;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.nick.ant.towerdefense.renderables.rooms.Room;
import com.nick.ant.towerdefense.renderables.rooms.RoomGame;
import com.nick.ant.towerdefense.renderables.ui.FPSCounter;
import com.nick.ant.towerdefense.renderables.ui.UIComponent;

public class Game extends ApplicationAdapter {
    private Room currentRoom;
    private UIComponent fpsCounter;
	
	@Override
	public void create () {
        currentRoom = new RoomGame();
        fpsCounter = new FPSCounter();
	}

    public void step()  {
        currentRoom.step();
    }

	@Override
	public void render () {
        step();

		Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        SpriteBatch batch = currentRoom.getSpriteBatch();
		batch.begin();

        currentRoom.render(batch);

        fpsCounter.render(batch);

		batch.end();
	}
}
