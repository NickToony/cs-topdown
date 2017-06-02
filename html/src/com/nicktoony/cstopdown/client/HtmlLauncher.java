package com.nicktoony.cstopdown.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.google.gwt.user.client.Window;
import com.nicktoony.cstopdown.networking.server.CSServer;
import com.nicktoony.engine.MyGame;
import com.nicktoony.engine.config.GameConfig;
import com.nicktoony.engine.config.ServerConfig;
import com.nicktoony.engine.networking.client.ClientSocket;
import com.nicktoony.engine.networking.server.Server;
import com.nicktoony.engine.services.Logger;

public class HtmlLauncher extends GwtApplication {

        @Override
        public GwtApplicationConfiguration getConfig () {
            return new GwtApplicationConfiguration(Window.getClientWidth(), Window.getClientHeight());
        }

        @Override
        public ApplicationListener getApplicationListener () {
            return new MyGame(new MyGame.PlatformProvider() {
                @Override
                public ClientSocket getWebSocket(String ip, int port) {
                    return new HtmlClientSocket(ip, port);
                }

                @Override
                public MyGame.GameConfigLoader getGameConfigLoader() {
                    return new MyGame.GameConfigLoader() {
                        @Override
                        public GameConfig getGameConfig(Logger logger) {
                            return new GameConfig();
                        }
                    };
                }

                @Override
                public CSServer getLocalServer(Logger logger, ServerConfig config) {
                    return null;
                }

                @Override
                public Server.LoopManager getLoopManager() {
                    return null;
                }

                @Override
                public boolean canHost() {
                    return false;
                }

                @Override
                public int[][] imageToPixels(FileHandle file) {
                    Pixmap pixmap = new Pixmap(file);
                    int pixels[][] = new int[pixmap.getWidth()][pixmap.getHeight()];
                    for (int x = 0; x < pixmap.getWidth(); x ++) {
                        for (int y = 0; y < pixmap.getHeight(); y ++) {
                            pixels[x][y] = pixmap.getPixel(x, y);
                        }
                    }
                    pixmap.dispose();
                    return pixels;
                }
            });


        }
}