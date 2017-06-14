package com.nicktoony.engine;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.nicktoony.cstopdown.networking.server.CSServer;
import com.nicktoony.cstopdown.rooms.mainmenu.RoomMainMenu;
import com.nicktoony.engine.components.Room;
import com.nicktoony.engine.config.GameConfig;
import com.nicktoony.engine.config.ServerConfig;
import com.nicktoony.engine.networking.client.ClientSocket;
import com.nicktoony.engine.networking.server.Server;
import com.nicktoony.engine.services.Logger;

public class MyGame extends ApplicationAdapter implements Server.LoopManager {

    // Hardcoded ticks per second for game simulation
    public static final float GAME_FPS = 60;

    public PlatformProvider getPlatformProvider() {
        return platformProvider;
    }

    public interface PlatformProvider {
        ClientSocket getWebSocket(String ip, int port);
        CSServer getLocalServer(Logger logger, ServerConfig config);
        Server.LoopManager getLoopManager();
        boolean canHost();
        int[][] imageToPixels(String file);
    }

    private Logger logger;
    private Room room;
    private GameConfig gameConfig;
    private PlatformProvider platformProvider;
    private SpriteBatch spriteBatch;
    private Server linearLoop;
    private FPSLogger fpsLogger;
    private Room nextRoom;


    private AssetManager assetManager;

    public MyGame(PlatformProvider platformProvider) {
        this.platformProvider = platformProvider;
    }
	
	@Override
	public void create () {
        configure();
        preloadAssets();

        this.spriteBatch = new SpriteBatch();

        createRoom(new RoomMainMenu());

        fpsLogger = new FPSLogger();
	}

	@Override
	public void render () {
//        fpsLogger.log();

        if (nextRoom != null) {
            createRoom(nextRoom);
            nextRoom = null;
        }

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

    public void setNextRoom(Room nextRoom) {
        this.nextRoom = nextRoom;
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

    /*
    Fetches the asset from AssetManager, forcing immediate load if it isn't there
     */
    public <T> T getAsset (String fileName, Class<T> type) {
        if (!assetManager.isLoaded(fileName)) {
           assetManager.load(fileName, type);
           assetManager.finishLoading();
        }
        return  assetManager.get(fileName, type);
    }

    private void preloadAssets() {
        clearAssetManager();
    }

    @Override
    public void dispose() {
        super.dispose();

        spriteBatch.dispose();
        assetManager.dispose();

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

        gameConfig = new GameConfig();
        gameConfig.load();

        reconfigure();
    }

    public void reconfigure() {
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
//        Gdx.graphics.setDisplayMode(gameConfig.resolution_x, gameConfig.resolution_y, gameConfig.fullscreen);
        if (gameConfig.fullscreen) {
            Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
        } else {
            Gdx.graphics.setWindowedMode(gameConfig.resolution_x, gameConfig.resolution_y);
        }
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


    public AssetManager getAssetManager() {
        return assetManager;
    }

    private void clearAssetManager() {
        if (this.assetManager != null) {
            this.assetManager.dispose();
        }
        this.assetManager = new AssetManager();
    }

}
