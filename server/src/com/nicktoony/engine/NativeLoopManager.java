package com.nicktoony.engine;

import com.nicktoony.engine.networking.server.Server;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Nick on 30/12/2015.
 */
public class NativeLoopManager implements Server.LoopManager {

    private Timer timer;
    private Boolean timerIsRunning = false;

    private final int FPS = 1000 / MyGame.GAME_FPS;

    @Override
    public void startServerLoop(final Server server) {
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
