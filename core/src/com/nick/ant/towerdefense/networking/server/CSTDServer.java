package com.nick.ant.towerdefense.networking.server;

import com.badlogic.gdx.Gdx;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.nick.ant.towerdefense.networking.packets.Packet;
import com.nick.ant.towerdefense.networking.packets.PacketDefinition;
import com.nick.ant.towerdefense.rooms.RoomGame;
import com.nick.ant.towerdefense.serverlist.ServerlistConfig;
import com.nicktoony.gameserver.service.GameserverConfig;
import com.nicktoony.gameserver.service.host.Host;

import java.io.*;
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
    private Gson gson;

    /**
     * Allows you to define the server log output in whichever way you desire
     */
    public interface Logger {
        public void log(String string);
        public void log(Exception exception);
    }

    // Get the logg
    public Logger getLogger() {
        return logger;
    }

    /**
     * Convenient helper method that sends a packet to all other clients
     * @param packet the packet to send
     * @param myClient the current client (who won't receive the packet)
     */
    public void sendToOthers(Packet packet, ServerClient myClient) {
        // For all connected clients
        for (ServerClient serverClient : serverClientList) {
            // If the client isn't the current one, and the target is ready
            if (serverClient != myClient && serverClient.isReady()) {
                // Send the packet
                serverClient.getSocket().sendTCP(packet);
            }
        }
    }

    /**
     * Create a new CSTDServer with the given logger and config
     *
     * This is useful for defining the server setup ingame
     * @param logger
     * @param config
     */
    public CSTDServer(Logger logger, ServerConfig config) {
        this.logger = logger;
        this.config = config;

        logger.log("Setting up");
        GameserverConfig.setConfig(new ServerlistConfig());

        setup();
    }

    /**
     * Create a new CSTD server with given logger, and use
     * a config from the file system.
     *
     * This is useful for dedicated servers
     * @param logger
     */
    public CSTDServer(Logger logger) {
        this.logger = logger;

        logger.log("Setting up");
        GameserverConfig.setConfig(new ServerlistConfig());

        if (findConfig()) {
            setup();
        }
    }

    /**
     * Singleton method generates the gson object for reading server config
     * @return
     */
    private Gson getGson() {
        if (gson == null) {
            gson = new GsonBuilder()
                    .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                    .setPrettyPrinting()
                    .create();
        }
        return gson;
    }

    /**
     * Attempt to find the server config in a local file (server/config.json)
     * @return true on success
     */
    private boolean findConfig() {
        File configFile = Gdx.files.local("server/config.json").file();
        if (!configFile.exists()) {
            logger.log("Config file does not exist");
            logger.log("Creating new default config");
            logger.log(configFile.getAbsolutePath());
            try {
                Gdx.files.local("server").file().mkdirs();
                configFile.createNewFile();
                FileWriter fileWriter = new FileWriter(configFile);
                getGson().toJson(new ServerConfig(), fileWriter);
                fileWriter.flush();
                fileWriter.close();
            } catch (IOException e) {
                logger.log(e);
                logger.log("Failed to copy config. Exiting.");
                return false;
            }
        }

        try {
            config = getGson()
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

    /**
     * Called after a config has been successfully set up. This creates the server, and updates
     * server list
     */
    private void setup() {
        // Server list
        host = new Host(config.sv_name, 0, config.sv_max_players);
        host.addMeta("map", config.sv_map);
        host.addMeta("ip", config.sv_ip);
        host.addMeta("port", Integer.toString(config.sv_port));
        host.create();

        logger.log("Server started up");

        // Open a socket
        serverSocket = new Server();
        serverSocket.start();
        try {
            serverSocket.bind(config.sv_port);
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

        // Game room that loads the map, validates collisions/movement
        roomGame = new RoomGame();
        roomGame.create();

        // A Java timer currently manages the game loop.. not ideal.
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                step();
            }
        }, FPS, FPS);
        timerIsRunning = true;
    }

    /**
     * Handles the event on a new client joining
     * @param connection
     */
    private void handleClientConnected(Connection connection) {
        // Find a free ID to use
        int highest = 0;
        for (ServerClient serverClient : serverClientList) {
            // always use the highest id value available
            if (serverClient.getId() > highest) {
                highest = serverClient.getId();
            }
        }
        // Add the client
        serverClientList.add(new ServerClient(highest + 1, this, connection));
    }

    private void handleReceivedMessage(Connection connection, Object object) {
        for (ServerClient client : serverClientList) {
            if (client.getSocket() == connection) {
                client.handleReceivedMessage(object);
                return;
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

    public void updateNewClient(Connection connection) {
        for (ServerClient serverClient : serverClientList) {
            serverClient.updateNewPlayer(connection);
        }
    }
}