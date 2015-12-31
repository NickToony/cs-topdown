package com.nicktoony.spacebattle.networking.server;

import com.nicktoony.spacebattle.services.Logger;

/**
 * Created by nick on 19/07/15.
 */
public class SBLocalServer extends SBServer {
    public SBLocalServer(Logger logger, ServerConfig config, LoopManager loopManager) {
        super(logger, config, loopManager);
    }

    @Override
    protected void startServerSocket(int port) {

    }

    @Override
    protected void stopServerSocket() {

    }
}
