package com.nicktoony.cstopdown.networking.server;

import com.nicktoony.cstopdown.config.ServerConfig;
import com.nicktoony.cstopdown.services.Logger;

import java.util.List;

/**
 * Created by nick on 19/07/15.
 */
public class SBLocalServer extends SBServer {

    public SBLocalServer(Logger logger, ServerConfig config, LoopManager loopManager) {
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
