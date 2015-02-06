package com.nicktoony.cstd.server;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.nick.ant.towerdefense.serverlist.ServerlistConfig;
import com.nicktoony.gameserver.service.GameserverConfig;
import com.nicktoony.gameserver.service.host.Host;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import static com.nicktoony.cstd.server.Main.log;

class Server {
    private final int FPS = 1000/60;

    private Host host;
    private ServerConfig config;
    private boolean timerIsRunning = false;

    private int currentPlayers = 0;
    private Timer timer;

    public Server(ServerConfig config) {
        this.config = config;
        setup();
    }

    public Server() {
        log("Setting up");
        GameserverConfig.setConfig(new ServerlistConfig());

        File configFile = new File("server/config.json");
        if (!configFile.exists()) {
            log("Config file does not exist");
            log("Copying default config to location");
            try {
                FileUtils.copyFile(new File("server/default.json"), configFile);
            } catch (IOException e) {
                e.printStackTrace();
                log("Failed to copy config. Exiting.");
                return;
            }
        }

        try {
            config = new GsonBuilder()
                    .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                    .create()
                    .fromJson(new FileReader(configFile), ServerConfig.class);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            log("Failed to load config file. Exiting.");
            return;
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            log("Failed to parse config file. Exiting.");
            return;
        }
    }

    public void setup() {
        // Server list
        host = new Host("A Game Server", 0, 16);

        log("Server started up");

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                step();
            }
        }, FPS, FPS);
        timerIsRunning = true;

        // BETTER SOLUTION?!
        while (timerIsRunning) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                log("Some sort of sleep error occured.. ignoring.");
            }
        }
    }

    public void step() {

    }

    public void dispose() {
        if (host != null) {
            host.stop();
        }
        if (timer != null) {
            timer.cancel();
            timerIsRunning = false;
        }
    }
}