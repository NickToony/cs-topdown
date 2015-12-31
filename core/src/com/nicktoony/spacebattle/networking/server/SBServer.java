package com.nicktoony.spacebattle.networking.server;

/**
 * Created by nick on 13/07/15.
 */

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;
import com.nicktoony.gameserver.service.GameserverConfig;
import com.nicktoony.gameserver.service.host.Host;
import com.nicktoony.spacebattle.config.ServerlistConfig;
import com.nicktoony.spacebattle.networking.packets.Packet;
import com.nicktoony.spacebattle.networking.packets.PacketDefinitions;
import com.nicktoony.spacebattle.rooms.game.RoomGame;
import com.nicktoony.spacebattle.services.Logger;

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
        // Server list
        host = new Host(config.sv_name + " " + System.currentTimeMillis(), 0, config.sv_max_players);
        host.addMeta("ip", config.sv_ip);
        host.addMeta("port", Integer.toString(config.sv_port));
        host.create();

        logger.log("Server started up");
//        // Game room that loads the map, validates collisions/movement
        roomGame = new RoomGame();
        roomGame.create(false);

        // begin server
        startServerSocket(config.sv_port);

        // A Java timer currently manages the game loop.. not ideal.
        loopManager.startServerLoop(this);
    }

    public void step() {
//        roomGame.step();
    }

    public boolean isTimerIsRunning() {
        return loopManager.isServerLoopRunning();
    }

    public void dispose() {
        logger.log("Server closed.");
        stopServerSocket();
        loopManager.endServerLoop();
    }

    public void handleClientConnected(SBClient conn) {
        logger.log("Client Connected");
    }

    public void handleClientDisconnected(SBClient conn) {
        logger.log("Client Disconnected");
    }

    public void handleReceivedMessage(SBClient conn, Packet packet) {
        logger.log("Client: " + packet.getMessage_id());
    }

    protected abstract void startServerSocket(int port);

    protected abstract void stopServerSocket();

    protected Packet stringToPacket(String message) {
        Packet packet = getJson().fromJson(Packet.class, message);
        return (Packet) getJson().fromJson((Class) PacketDefinitions.PACKET_DEFITIONS.get(packet.getMessage_id()), message);
    }

    protected String packetToString(Packet packet) {
        packet.prepareMessageId();
        return getJson().toJson(packet);
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


}