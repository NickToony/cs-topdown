package com.nicktoony.cstopdown.networking.server;

/**
 * Created by nick on 13/07/15.
 */

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;
import com.nicktoony.cstopdown.config.ServerConfig;
import com.nicktoony.cstopdown.networking.packets.game.CreatePlayer;
import com.nicktoony.gameserver.service.GameserverConfig;
import com.nicktoony.gameserver.service.host.Host;
import com.nicktoony.cstopdown.config.ServerlistConfig;
import com.nicktoony.cstopdown.networking.packets.Packet;
import com.nicktoony.cstopdown.networking.packets.PacketDefinitions;
import com.nicktoony.cstopdown.rooms.game.RoomGame;
import com.nicktoony.cstopdown.services.Logger;

import java.util.*;

public abstract class SBServer {

    public interface LoopManager {
        public void startServerLoop(SBServer server);
        public boolean isServerLoopRunning();
        public void endServerLoop();
    }

    private ServerConfig config;
    private Logger logger;
    private Host host;
    private RoomGame roomGame;
    private Json json;
    private LoopManager loopManager;
    protected boolean publicServerList = true;
    private List<SBClient> clients = new ArrayList<SBClient>();

    private List<SBClient> connectedQueue = new ArrayList<SBClient>();
    private List<SBClient> disconnectedQueue = new ArrayList<SBClient>();
    private Map<SBClient, Packet> messageQueue = new LinkedHashMap<SBClient, Packet>();

    // Get the logg
    public Logger getLogger() {
        return logger;
    }

    /**
     * Create a new CSTDServer with the given logger and config
     *
     * This is useful for defining the server setup ingame
     * @param logger
     * @param config
     */
    public SBServer(Logger logger, ServerConfig config, LoopManager loopManager) {
        this.logger = logger;
        this.config = config;
        this.loopManager = loopManager;

        logger.log("Setting up");
        GameserverConfig.setConfig(new ServerlistConfig());

        setup();
    }

    /**
     * Called after a config has been successfully set up. This creates the server, and updates
     * server list
     */
    private void setup() {
        logger.log("Server started up");
//        // Game room that loads the map, validates collisions/movement
        roomGame = new RoomGame(null);
        roomGame.create(false);

        // begin server
        startServerSocket(config.sv_port);

        // A Java timer currently manages the game loop.. not ideal.
        loopManager.startServerLoop(this);
    }

    public void step() {
        // Server list
        if (publicServerList) {
            if (host == null) {
                host = new Host(config.sv_name + " " + System.currentTimeMillis(), 0, config.sv_max_players);
                host.addMeta("ip", config.sv_ip);
                host.addMeta("port", Integer.toString(config.sv_port));
                host.create();
            }
        }

        // Push the queues
        pushNotifications();

        // Perform a step event on all client objects
        for (SBClient client : clients) {
            client.update();
        }
    }

    public boolean isTimerIsRunning() {
        return loopManager.isServerLoopRunning();
    }

    public void dispose() {
        logger.log("Server closed.");
        stopServerSocket();
        loopManager.endServerLoop();
    }

    private void handleClientConnected(SBClient conn) {
        logger.log("Client Connected");
        int testId = 0;
        boolean clash = true;
        while (clash) {
            testId ++;
            clash = false;
            for (SBClient client : clients) {
                if (client.getId() == testId) {
                    clash = true;
                }
            }
        }
        conn.setId(testId);
        clients.add(conn);
        conn.setState(SBClient.STATE.CONNECTING);
    }

    private void handleClientDisconnected(SBClient conn) {
        logger.log("Client Disconnected");
        clients.remove(conn);
    }

    private void handleReceivedMessage(SBClient conn, Packet packet) {
        conn.handleReceivedMessage(packet);
    }

    protected abstract void startServerSocket(int port);

    protected abstract void stopServerSocket();

    protected Packet stringToPacket(String message) {
        Packet packet = getJson().fromJson(Packet.class, message);
        return (Packet) getJson().fromJson((Class) PacketDefinitions.PACKET_DEFITIONS.get(packet.getMessage_id()), message);
    }

    protected Json getJson() {
        if (json == null) {
            json = new Json();
            json.setTypeName(null);
            json.setUsePrototypes(false);
            json.setIgnoreUnknownFields(true);
            json.setOutputType(JsonWriter.OutputType.json);
        }
        return json;
    }

    public ServerConfig getConfig() {
        return config;
    }

    public RoomGame getGame() {
        return roomGame;
    }


    public void sendToAll(CreatePlayer createPlayer) {
        for (SBClient client : clients) {
            client.sendPacket(createPlayer);
            System.out.println("SENT IT");
        }
    }

    public void notifyClientConnected(SBClient conn) {
        connectedQueue.add(conn);
    }

    public void notifyClientDisconnected(SBClient conn) {
        disconnectedQueue.add(conn);
    }

    public void notifyClientMessage(SBClient conn, Packet packet) {
        messageQueue.put(conn, packet);
    }

    public void pushNotifications() {
        for (SBClient client : connectedQueue) {
            handleClientConnected(client);
        }

        for (Map.Entry<SBClient, Packet> client : messageQueue.entrySet()) {
            handleReceivedMessage(client.getKey(), client.getValue());
        }

        for (SBClient client : disconnectedQueue) {
            handleClientDisconnected(client);
        }

        connectedQueue.clear();
        disconnectedQueue.clear();
        messageQueue.clear();
    }
}