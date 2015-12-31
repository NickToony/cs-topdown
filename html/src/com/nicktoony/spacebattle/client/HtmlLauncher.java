package com.nicktoony.spacebattle.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import com.nicktoony.spacebattle.MyGame;
import com.nicktoony.spacebattle.config.GameConfig;
import com.nicktoony.spacebattle.networking.client.SBSocket;
import com.nicktoony.spacebattle.networking.server.SBServer;
import com.nicktoony.spacebattle.networking.server.ServerConfig;
import com.nicktoony.spacebattle.services.Logger;

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