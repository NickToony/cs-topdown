package com.nicktoony.engine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglFiles;
import com.badlogic.gdx.backends.lwjgl.LwjglNet;
import com.badlogic.gdx.files.FileHandle;
import com.nicktoony.cstopdown.ServerSocket;
import com.nicktoony.cstopdown.networking.server.CSServer;
import com.nicktoony.engine.config.GameConfig;
import com.nicktoony.engine.config.ServerConfig;
import com.nicktoony.engine.networking.client.ClientSocket;
import com.nicktoony.engine.networking.server.Server;
import com.nicktoony.engine.services.Logger;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Date;

public class ServerApplication {
    public static Server server;
    // TODO: Remove this, and use a custom class......
    public static MyGame.PlatformProvider PLATFORM_PROVIDER = new MyGame.PlatformProvider() {
        @Override
        public ClientSocket getWebSocket(String ip, int port) {
            return null;
        }

        @Override
        public MyGame.GameConfigLoader getGameConfigLoader() {
            return null;
        }

        @Override
        public CSServer getLocalServer(Logger logger, ServerConfig config) {
            return null;
        }

        @Override
        public Server.LoopManager getLoopManager() {
            return new NativeLoopManager();
        }

        @Override
        public boolean canHost() {
            return true;
        }

        @Override
        public int[][] imageToPixels(String file) {
            BufferedImage image = null;
            try {
                image = ImageIO.read(new File(file));
                int pixels[][] = new int[image.getWidth()][image.getHeight()];

                for (int x = 0; x < image.getWidth(); x ++) {
                    for (int y = 0; y < image.getHeight(); y ++) {
                        int argb = image.getRGB(x,y);
                        int rgba = (argb << 8) | (argb >>> (32-8));
                        pixels[x][y] = rgba;
                    }
                }
                return pixels;
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println(file);
            }

            return null;

        }


    };

    public static void main(String [ ] args) {

        if (Gdx.files == null) {
            Gdx.files = new LwjglFiles();
        }

        if (Gdx.net == null) {
            Gdx.net = new LwjglNet();
        }

        if (args.length <= 0) {
            startNoUI();
        } else {
            startUI();
        }

        return;
    }

    private static void startNoUI() {
        Logger logger = new Logger() {
            @Override
            public void log(String message) {
                logMessage(message);
            }

            @Override
            public void log(Exception exception) {
                exception.printStackTrace();
            }

        };
        server = new ServerSocket(logger, ServerConfigLoader.findConfig(logger),
                new NativeLoopManager(), PLATFORM_PROVIDER);

        // BETTER SOLUTION?!
        while (server.isTimerIsRunning()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                server.getLogger().log("Some sort of sleep error occured.. ignoring.");
            }
        }

        server.dispose();
    }

    public static void startUI() {
        Logger logger = new ServerUI(new ServerUI.UIListener() {
            @Override
            public void onClose() {
                System.exit(0);
            }
        });
        server = new ServerSocket(logger, ServerConfigLoader.findConfig(logger),
                new NativeLoopManager(), PLATFORM_PROVIDER);

        // BETTER SOLUTION?!
        while (server.isTimerIsRunning()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                server.getLogger().log("Some sort of sleep error occured.. ignoring.");
            }
        }

        server.dispose();
    }


    public static void logMessage(String message) {
        System.out.println(new Date().toString() + " :: " + message);
    }


}