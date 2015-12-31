package com.nicktoony.spacebattle;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.nicktoony.spacebattle.components.Room;
import com.nicktoony.spacebattle.config.GameConfig;
import com.nicktoony.spacebattle.networking.client.SBSocket;
import com.nicktoony.spacebattle.networking.server.SBServer;
import com.nicktoony.spacebattle.networking.server.ServerConfig;
import com.nicktoony.spacebattle.rooms.mainmenu.RoomMainMenu;
import com.nicktoony.spacebattle.services.Logger;

public class MyGame extends ApplicationAdapter {

    public PlatformProvider getPlatformProvider() {
        return platformProvider;
    }

    public interface PlatformProvider {
        public SBSocket getWebSocket(String ip, int port);
        public GameConfigLoader getGameConfigLoader();
        public SBServer getLocalServer(Logger logger, ServerConfig config);
        public SBServer.LoopManager getLoopManager();
    }

    public interface GameConfigLoader {
        public GameConfig getGameConfig(Logger logger);
    }

    private Logger logger;
    private Room room;
    private GameConfig gameConfig;
    private PlatformProvider platformProvider;
    private SpriteBatch spriteBatch;

    public MyGame(PlatformProvider platformProvider) {
        this.platformProvider = platformProvider;
    }
	
	@Override
	public void create () {
        configure();

        this.spriteBatch = new SpriteBatch();

//        createRoom(new RoomGame());
//        createRoom(new RoomServerList());
        createRoom(new RoomMainMenu());
	}

	@Override
	public void render () {
        if (room != null) {
            room.step();
        }

		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (room != null) {
//            spriteBatch.begin();
            room.render(spriteBatch);
//            spriteBatch.end();
        }
	}

    public void createRoom(Room room) {
        if (this.room != null) {
            disposeRoom();
        }

        room.setGame(this);
        room.create(true);
        this.room = room;
    }

    public void disposeRoom() {
        room.dispose();
        room = null;
    }

    @Override
    public void dispose() {
        super.dispose();

        spriteBatch.dispose();
    }

    /**
     * Loads the games config file, and acts upon it depending on the OS
     */
    private void configure() {

        gameConfig = platformProvider.getGameConfigLoader().getGameConfig(getLogger());

        if (Gdx.app.getType() == Application.ApplicationType.Desktop) {
            configDesktop(gameConfig);
        }
    }

    /**
     * Execute desktop configuration changes
     * @param gameConfig
     */
    private void configDesktop(GameConfig gameConfig) {
        // Only the desktop should change its display mode
        Gdx.graphics.setDisplayMode(gameConfig.game_resolution_x, gameConfig.game_resolution_y, gameConfig.game_fullscreen);
    }

    private Logger getLogger() {
        if (logger == null) {
            logger = new Logger() {
                @Override
                public void log(String string) {
                    System.out.println(string);
                }

                @Override
                public void log(Exception exception) {
                    System.out.println(exception.getMessage());
                }
            };
        }
        return logger;
    }

    public GameConfig getGameConfig() {
        return gameConfig;
    }
}
