package com.nicktoony.cstopdown.desktop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.nicktoony.cstopdown.MyGame;
import com.nicktoony.cstopdown.config.GameConfig;
import com.nicktoony.cstopdown.networking.client.SBSocket;
import com.nicktoony.cstopdown.networking.server.SBServer;
import com.nicktoony.cstopdown.config.ServerConfig;
import com.nicktoony.cstopdown.server.NativeLoopManager;
import com.nicktoony.cstopdown.server.SBWebServer;
import com.nicktoony.cstopdown.server.ServerUI;
import com.nicktoony.cstopdown.services.Logger;

import java.io.*;

public class DesktopLauncher {
	public static void main (String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		new LwjglApplication(new MyGame(new MyGame.PlatformProvider() {
            @Override
            public SBSocket getWebSocket(String ip, int port) {
                return new DesktopSBSocket(ip, port);
            }

            @Override
            public MyGame.GameConfigLoader getGameConfigLoader() {
                return new MyGame.GameConfigLoader() {
                    @Override
                    public GameConfig getGameConfig(Logger logger) {
                        return findConfig(logger);
                    }
                };
            }

            @Override
            public SBServer getLocalServer(Logger logger, ServerConfig config) {
                if (logger == null) { logger = new ServerUI(new ServerUI.UIListener() {
                    @Override
                    public void onClose() {

                    }
                }); }
                return new SBWebServer(logger, config, getLoopManager());
            }

            @Override
            public SBServer.LoopManager getLoopManager() {
                return new NativeLoopManager();
            }

            @Override
            public boolean canHost() {
                return true;
            }


        }), config);
	}

        public static GameConfig findConfig(Logger logger) {
        Gson gson = getGson();
        File configFile = Gdx.files.local("config.json").file();
        if (!configFile.exists()) {
            logger.log("Config file does not exist");
            logger.log("Creating new default config");
            logger.log(configFile.getAbsolutePath());
            try {
                configFile.createNewFile();
                FileWriter fileWriter = new FileWriter(configFile);
                gson.toJson(new GameConfig(), fileWriter);
                fileWriter.flush();
                fileWriter.close();
            } catch (IOException e) {
                logger.log(e);
                logger.log("Failed to copy config. Exiting.");
                return new GameConfig();
            }
        }

        try {
            return gson
                    .fromJson(new FileReader(configFile), GameConfig.class);
        } catch (FileNotFoundException e) {
            logger.log(e);
            logger.log("Failed to load config file. Exiting.");
            return new GameConfig();
        } catch (JsonSyntaxException e) {
            logger.log(e);
            logger.log("Failed to parse config file. Exiting.");
            return new GameConfig();
        }
    }

    private static Gson getGson() {
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        return builder.create();
    }
}