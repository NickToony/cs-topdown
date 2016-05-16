package com.nicktoony.cstopdown;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.nicktoony.cstopdown.networking.server.CSServer;
import com.nicktoony.engine.networking.client.ClientSocket;
import com.nicktoony.engine.components.Room;
import com.nicktoony.engine.config.GameConfig;
import com.nicktoony.engine.config.ServerConfig;
import com.nicktoony.engine.networking.server.Server;
import com.nicktoony.cstopdown.rooms.mainmenu.RoomMainMenu;
import com.nicktoony.engine.services.Logger;

public class MyGame extends ApplicationAdapter implements Server.LoopManager {

    // Hardcoded ticks per second for game simulation
    public static final int GAME_FPS = 60;

    public PlatformProvider getPlatformProvider() {
        return platformProvider;
    }

    public interface PlatformProvider {
        ClientSocket getWebSocket(String ip, int port);
        GameConfigLoader getGameConfigLoader();
        CSServer getLocalServer(Logger logger, ServerConfig config);
        Server.LoopManager getLoopManager();
        boolean canHost();
    }

    public interface GameConfigLoader {
        GameConfig getGameConfig(Logger logger);
    }

    private Logger logger;
    private Room room;
    private GameConfig gameConfig;
    private PlatformProvider platformProvider;
    private SpriteBatch spriteBatch;
    private Server linearLoop;
    private FPSLogger fpsLogger;

    public MyGame(PlatformProvider platformProvider) {
        this.platformProvider = platformProvider;
    }
	
	@Override
	public void create () {
        configure();

        this.spriteBatch = new SpriteBatch();

        createRoom(new RoomMainMenu());

        fpsLogger = new FPSLogger();
	}

	@Override
	public void render () {
//        fpsLogger.log();

        if (room != null) {
            if (!room.isCreated()) {
                room.create(true);
                return;
            }

            room.step(Gdx.graphics.getDeltaTime() * GAME_FPS);
        }

		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (room != null && room.isCreated()) {
            room.render(spriteBatch);
        }

        if (linearLoop != null) {
            linearLoop.step();
        }
	}

    public void createRoom(Room room) {
        if (this.room != null) {
            disposeRoom();
        }

        room.setGame(this);
        this.room = room;
    }

    public void disposeRoom() {
        room.dispose(true);
        room = null;
    }

    @Override
    public void dispose() {
        super.dispose();

        spriteBatch.dispose();
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        if (room != null) {
            room.resize(width, height);
        }
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

    @Override
    public void startServerLoop(Server server) {
        linearLoop = server;
    }

    @Override
    public boolean isServerLoopRunning() {
        return linearLoop != null;
    }

    @Override
    public void endServerLoop() {
        linearLoop = null;
    }
}
