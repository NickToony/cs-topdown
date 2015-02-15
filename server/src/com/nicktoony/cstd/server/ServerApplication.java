package com.nicktoony.cstd.server;

import com.nick.ant.towerdefense.networking.server.CSTDServer;
import com.nick.ant.towerdefense.networking.server.ServerUI;

import java.util.Date;

public class ServerApplication {
    public static CSTDServer server;

    public static void main(String [ ] args) {

        if (args.length <= 0) {
            startNoUI();
        } else {
            startUI();
        }

        return;
    }

    private static void startNoUI() {
        server = new CSTDServer(new CSTDServer.Logger() {
            @Override
            public void log(String message) {
                logMessage(message);
            }

            @Override
            public void log(Exception exception) {
                exception.printStackTrace();
            }

        });

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
        server = new CSTDServer(new ServerUI(new ServerUI.UIListener() {
            @Override
            public void onClose() {
                System.exit(0);
            }
        }));

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