package com.nicktoony.cstopdown.client;

import com.nicktoony.cstopdown.networking.server.SBServer;

/**
 * Created by Nick on 03/01/2016.
 */
public class HtmlLoopManager implements SBServer.LoopManager {
    private boolean running = false;

    @Override
    public void startServerLoop(SBServer server) {
        running = true;
    }

    @Override
    public boolean isServerLoopRunning() {
        return running;
    }

    @Override
    public void endServerLoop() {
        running = false;
    }
}
