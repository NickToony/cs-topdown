package com.nicktoony.cstopdown.android;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.nicktoony.engine.MyGame;
import com.nicktoony.engine.networking.client.ClientSocket;
import com.nicktoony.engine.config.GameConfig;
import com.nicktoony.engine.networking.server.Server;
import com.nicktoony.engine.config.ServerConfig;
import com.nicktoony.engine.services.Logger;

public class AndroidLauncher extends AndroidApplication {
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		initialize(new MyGame(new MyGame.PlatformProvider() {
            @Override
            public ClientSocket getWebSocket(String ip, int port) {
                return new AndroidClientSocket(ip, port);
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
        }), config);
	}


}
