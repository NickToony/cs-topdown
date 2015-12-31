package com.nicktoony.spacebattle.android;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.nicktoony.spacebattle.MyGame;
import com.nicktoony.spacebattle.config.GameConfig;
import com.nicktoony.spacebattle.networking.client.SBSocket;
import com.nicktoony.spacebattle.networking.server.SBServer;
import com.nicktoony.spacebattle.networking.server.ServerConfig;
import com.nicktoony.spacebattle.services.Logger;

public class AndroidLauncher extends AndroidApplication {
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		initialize(new MyGame(new MyGame.PlatformProvider() {
            @Override
            public SBSocket getWebSocket(String ip, int port) {
                return new AndroidSBSocket(ip, port);
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
        }), config);
	}


}
