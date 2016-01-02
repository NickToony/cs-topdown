package com.nicktoony.cstopdown.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import com.nicktoony.cstopdown.MyGame;
import com.nicktoony.cstopdown.config.GameConfig;
import com.nicktoony.cstopdown.networking.client.SBSocket;
import com.nicktoony.cstopdown.networking.server.SBServer;
import com.nicktoony.cstopdown.networking.server.ServerConfig;
import com.nicktoony.cstopdown.services.Logger;

public class HtmlLauncher extends GwtApplication {

        @Override
        public GwtApplicationConfiguration getConfig () {
            return new GwtApplicationConfiguration(1024, 728);
        }

        @Override
        public ApplicationListener getApplicationListener () {
            return new MyGame(new MyGame.PlatformProvider() {
                @Override
                public SBSocket getWebSocket(String ip, int port) {
                    return new HtmlSBSocket(ip, port);
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
                public SBServer getLocalServer(Logger logger, ServerConfig config) {
                    return null;
                }

                @Override
                public SBServer.LoopManager getLoopManager() {
                    return null;
                }
            });


        }
}