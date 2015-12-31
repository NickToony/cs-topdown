package com.nicktoony.spacebattle.server;

import com.nicktoony.spacebattle.networking.server.SBServer;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Nick on 30/12/2015.
 */
public class NativeLoopManager implements SBServer.LoopManager {

    private Timer timer;
    private Boolean timerIsRunning = false;

    private final int FPS = 1000/60;

    @Override
    public void startServerLoop(final SBServer server) {
        // A Java timer currently manages the game loop.. not ideal.
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                server.step();
            }
        }, FPS, FPS);
        timerIsRunning = true;
    }

    @Override
    public boolean isServerLoopRunning() {
        return timerIsRunning;
    }

    @Override
    public void endServerLoop() {
        timer.cancel();
    }
}
