package com.nick.ant.towerdefense.networking.server;

import com.badlogic.gdx.Gdx;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.nick.ant.towerdefense.networking.packets.Packet;
import com.nick.ant.towerdefense.networking.packets.PacketDefinition;
import com.nick.ant.towerdefense.rooms.RoomGame;
import com.nick.ant.towerdefense.rooms.RoomGameRender;
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
    private RoomGame roomGame;

    public Logger getLogger() {
        return logger;
    }

    public void sendToOthers(Packet packet, ServerClient myClient) {
        for (ServerClient serverClient : serverClientList) {
            if (serverClient != myClient) {
                serverClient.getSocket().sendTCP(packet);
            }
        }
    }

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

        if (findConfig()) {
            setup();
        }
    }

    private boolean findConfig() {
        File configFile = Gdx.files.local("server/config.json").file();
        if (!configFile.exists()) {
            logger.log("Config file does not exist");
            logger.log("Copying default config to location");
            logger.log(configFile.getAbsolutePath());
            try {
                FileUtils.copyFile(Gdx.files.local("server/default.json").file(), configFile);
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
        host = new Host(config.getName(), 0, config.getMaxPlayers());
        host.addMeta("map", config.getMap());
        host.addMeta("ip", config.getIP());
        host.addMeta("port", Integer.toString(config.getPort()));
        host.create();

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
        serverSocket.addListener(new Listener() {
            @Override
            public void received(Connection connection, Object object) {
                handleReceivedMessage(connection, object);
            }

            @Override
            public void connected(Connection connection) {
                handleClientConnected(connection);
            }
        });

        PacketDefinition.registerClasses(serverSocket.getKryo());


        roomGame = new RoomGame();
        roomGame.create();

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                step();
            }
        }, FPS, FPS);
        timerIsRunning = true;
    }

    private void handleClientConnected(Connection connection) {
        int highest = 0;
        for (ServerClient serverClient : serverClientList) {
            if (serverClient.getId() > highest) {
                highest = serverClient.getId();
            }
        }
        serverClientList.add(new ServerClient(highest + 1, this, connection));
    }

    private void handleReceivedMessage(Connection connection, Object object) {
        for (ServerClient client : serverClientList) {
            if (client.getSocket() == connection) {
                client.handleReceivedMessage(object);
            }
        }
    }

    private void step() {
        roomGame.step();

        for (ServerClient serverClient : serverClientList) {
            serverClient.step();
        }
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

    public boolean isTimerIsRunning() {
        return timerIsRunning;
    }

    public ServerConfig getConfig() {
        return config;
    }


    public RoomGame getRoomGame() {
        return roomGame;
    }
}