package com.nick.ant.towerdefense.networking.server;

import com.esotericsoftware.kryonet.Server;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.nick.ant.towerdefense.networking.packets.PacketDefinition;
import com.nick.ant.towerdefense.serverlist.ServerlistConfig;
import com.nicktoony.gameserver.service.GameserverConfig;
import com.nicktoony.gameserver.service.host.Host;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class CSTDServer {
    private final int FPS = 1000/60;

    private Host host;
    private ServerConfig config;
    private boolean timerIsRunning = false;
    private Server serverSocket;
    private Timer timer;
    private List<ServerClient> serverClientList = new ArrayList<ServerClient>();
    private Logger logger;

    public interface Logger {
        public void log(String string);
        public void log(Exception exception);
    }

    public CSTDServer(Logger logger, ServerConfig config) {
        this.logger = logger;
        this.config = config;

        logger.log("Setting up");
        GameserverConfig.setConfig(new ServerlistConfig());

        setup();
    }

    public CSTDServer(Logger logger) {
        this.logger = logger;

        logger.log("Setting up");
        GameserverConfig.setConfig(new ServerlistConfig());

        if (getConfig()) {
            setup();
        }
    }

    public boolean getConfig() {
        File configFile = new File("server/config.json");
        if (!configFile.exists()) {
            logger.log("Config file does not exist");
            logger.log("Copying default config to location");
            try {
                FileUtils.copyFile(new File("server/default.json"), configFile);
            } catch (IOException e) {
                logger.log(e);
                logger.log("Failed to copy config. Exiting.");
                return false;
            }
        }

        try {
            config = new GsonBuilder()
                    .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                    .create()
                    .fromJson(new FileReader(configFile), ServerConfig.class);
        } catch (FileNotFoundException e) {
            logger.log(e);
            logger.log("Failed to load config file. Exiting.");
            return false;
        } catch (JsonSyntaxException e) {
            logger.log(e);
            logger.log("Failed to parse config file. Exiting.");
            return false;
        }

        return true;
    }

    private void setup() {
        // Server list
        host = new Host("A Game Server", 0, 16);

        logger.log("Server started up");

        // Open a socket
        serverSocket = new Server();
        serverSocket.start();
        try {
            serverSocket.bind(config.getPort());
        } catch (IOException e) {
            logger.log("Could not bind port. Is it already in use?");
            //e.printStackTrace();
            return;
        }

        PacketDefinition.registerClasses(serverSocket.getKryo());

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
                logger.log("Some sort of sleep error occured.. ignoring.");
            }
        }
    }

    private void step() {

    }

    public void dispose() {
        if (host != null) {
            //host.stop();
        }
        if (timer != null) {
            timer.cancel();
        }
        if (serverSocket != null) {
            serverSocket.close();
        }

        timerIsRunning = false;
    }
}