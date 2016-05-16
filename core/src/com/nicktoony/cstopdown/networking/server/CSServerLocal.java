package com.nicktoony.cstopdown.networking.server;

import com.nicktoony.engine.config.ServerConfig;
import com.nicktoony.engine.services.Logger;

/**
 * Created by nick on 19/07/15.
 */
public class CSServerLocal extends CSServer {

    public CSServerLocal(Logger logger, ServerConfig config, LoopManager loopManager) {
        super(logger, config, loopManager);
        publicServerList = false;
    }

    @Override
    protected void startServerSocket(int port) {

    }

    @Override
    protected void stopServerSocket() {

    }
}
