package com.nick.ant.towerdefense;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.nick.ant.towerdefense.components.CharacterManager;
import com.nick.ant.towerdefense.components.weapons.WeaponManager;
import com.nick.ant.towerdefense.rooms.Room;
import com.nick.ant.towerdefense.rooms.RoomGame;
import com.nick.ant.towerdefense.renderables.ui.UIComponent;

public class Game extends ApplicationAdapter {
    private Room currentRoom;
    private UIComponent fpsCounter;
    private UIComponent HUD;
	
	@Override
	public void create () {
        currentRoom = new RoomGame();

        // Force it to load the instances
        CharacterManager characterManager = CharacterManager.getInstance();
        WeaponManager weaponManager = WeaponManager.getInstance();
	}

	@Override
	public void render () {
        currentRoom.step();

		Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        currentRoom.render();
	}
}
