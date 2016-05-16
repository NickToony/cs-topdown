package com.nicktoony.cstopdown.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import com.nicktoony.cstopdown.MyGame;
import com.nicktoony.engine.networking.client.ClientSocket;
import com.nicktoony.engine.config.GameConfig;
import com.nicktoony.engine.networking.server.Server;
import com.nicktoony.engine.config.ServerConfig;
import com.nicktoony.engine.services.Logger;

public class HtmlLauncher extends GwtApplication {

        @Override
        public GwtApplicationConfiguration getConfig () {
            return new GwtApplicationConfiguration(new GameConfig().game_resolution_x, new GameConfig().game_resolution_y);
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
                public Server getLocalServer(Logger logger, ServerConfig config) {
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
            });


        }
}