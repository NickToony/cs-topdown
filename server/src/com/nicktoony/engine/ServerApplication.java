package com.nicktoony.engine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglFiles;
import com.badlogic.gdx.backends.lwjgl.LwjglNet;
import com.nicktoony.cstopdown.ServerSocket;
import com.nicktoony.engine.networking.server.Server;
import com.nicktoony.engine.services.Logger;

import java.util.Date;

public class ServerApplication {
    public static Server server;

    public static void main(String [ ] args) {

        if (Gdx.files == null) {
            Gdx.files = new LwjglFiles();
        }

        if (Gdx.net == null) {
            Gdx.net = new LwjglNet();
        }

        if (args.length <= 0) {
            startNoUI();
        } else {
            startUI();
        }

        return;
    }

    private static void startNoUI() {
        Logger logger = new Logger() {
            @Override
            public void log(String message) {
                logMessage(message);
            }

            @Override
            public void log(Exception exception) {
                exception.printStackTrace();
            }

        };
        server = new ServerSocket(logger, ServerConfigLoader.findConfig(logger), new NativeLoopManager());

        // BETTER SOLUTION?!
        while (server.isTimerIsRunning()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                server.getLogger().log("Some sort of sleep error occured.. ignoring.");
            }
        }

        server.dispose();
    }

    public static void startUI() {
        Logger logger = new ServerUI(new ServerUI.UIListener() {
            @Override
            public void onClose() {
                System.exit(0);
            }
        });
        server = new ServerSocket(logger, ServerConfigLoader.findConfig(logger), new NativeLoopManager());

        // BETTER SOLUTION?!
        while (server.isTimerIsRunning()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                server.getLogger().log("Some sort of sleep error occured.. ignoring.");
            }
        }

        server.dispose();
    }


    public static void logMessage(String message) {
        System.out.println(new Date().toString() + " :: " + message);
    }


}