package com.nicktoony.cstopdown.networking.server;

/**
 * Created by nick on 13/07/15.
 */

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;
import com.nicktoony.cstopdown.MyGame;
import com.nicktoony.cstopdown.config.ServerConfig;
import com.nicktoony.cstopdown.config.ServerlistConfig;
import com.nicktoony.cstopdown.mods.gamemode.GameModeMod;
import com.nicktoony.cstopdown.mods.gamemode.PlayerModInterface;
import com.nicktoony.cstopdown.mods.gamemode.implementations.LastTeamStanding;
import com.nicktoony.cstopdown.networking.packets.Packet;
import com.nicktoony.cstopdown.networking.packets.PacketDefinitions;
import com.nicktoony.cstopdown.networking.packets.connection.LoadedPacket;
import com.nicktoony.cstopdown.rooms.game.RoomGame;
import com.nicktoony.cstopdown.services.Logger;
import com.nicktoony.gameserver.service.GameserverConfig;
import com.nicktoony.gameserver.service.host.Host;

import java.util.ArrayList;
import java.util.List;

public abstract class SBServer {

    public interface LoopManager {
        void startServerLoop(SBServer server);
        boolean isServerLoopRunning();
        void endServerLoop();
    }

    private class ReceivedPacket {
        public SBClient client;
        public Packet packet;

        public ReceivedPacket(SBClient client, Packet packet) {
            this.client = client;
            this.packet = packet;
        }
    }

    enum STATE {
        ROUND_START,
        ROUND,
        ROUND_END
    }
    private STATE state;

    private ServerConfig config;
    private Logger logger;
    private Host host;
    private RoomGame roomGame;
    private Json json;
    private LoopManager loopManager;
    protected boolean publicServerList = true;
    private List<SBClient> clients = new ArrayList<>();
    private long lastTime;
    private final double MS_PER_TICK = 1000 / MyGame.GAME_FPS;
    private float delta;
    private long roundTimer = 0;

    private long lastFPSCount = System.currentTimeMillis();
    private int fpsFrames = 0;

    private List<SBClient> connectedQueue = new ArrayList<>();
    private List<SBClient> disconnectedQueue = new ArrayList<>();
    private List<ReceivedPacket> messageQueue = new ArrayList<>();

    private List<GameModeMod> mods = new ArrayList<>();

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
        roomGame = new RoomGame(new SBFakeSocket());
        roomGame.create(false);

        // begin server
        startServerSocket(config.sv_port);

        // A Java timer currently manages the game loop.. not ideal.
        loopManager.startServerLoop(this);

        this.lastTime = System.currentTimeMillis();

        LastTeamStanding lastTeamStanding = new LastTeamStanding();
        lastTeamStanding.setup(this);
        mods.add(lastTeamStanding);

        notifyModInit();

        // Begin the game
        startRound();

        for (int i = 0; i < config.sv_bots; i ++) {
            SBClient client = new SBBotClient(this);
            handleClientConnected(client);
            client.setState(SBClient.STATE.LOADING);
            notifyClientMessage(client, new LoadedPacket());
        }
    }

    public void step() {
        if (lastFPSCount < System.currentTimeMillis() - 1000) {
//            System.out.println("Server FPS: " + fpsFrames);
            fpsFrames = 0;
            lastFPSCount = System.currentTimeMillis();
        }
        fpsFrames += 1;


        // Server list
        if (publicServerList) {
            if (host == null) {
                host = new Host(config.sv_name + " " + System.currentTimeMillis(), 0, config.sv_max_players);
                host.addMeta("ip", config.sv_ip);
                host.addMeta("port", Integer.toString(config.sv_port));
                host.create();
            }
            host.setCurrentPlayers(clients.size());
            host.step();
        }

        // Push the queues
        pushNotifications();

        // Perform a step animationEvent on all client objects
        for (SBClient client : clients) {
            client.update();
        }

        long now = System.currentTimeMillis();
        delta = (float) ((now - lastTime) / MS_PER_TICK);
        lastTime = now;
        // Update world
        roomGame.step(delta);

        // Manages round time
        roundStep();
    }

    private void roundStep() {
        switch (state) {
            case ROUND_START:
                if ((config.mp_freeze_time*1000) + roundTimer < System.currentTimeMillis()) {
                    state = STATE.ROUND;
                    roundTimer = System.currentTimeMillis();
                    notifyModFreezeTime();
                }
                break;

            case ROUND:
                if ((config.mp_round_time*1000) + roundTimer < System.currentTimeMillis()) {
                    endRound();
                }
                break;

            case ROUND_END:
                if ((config.mp_victory_time*1000) + roundTimer < System.currentTimeMillis()) {
                    startRound();
                }
                break;
        }
    }

    public boolean isTimerIsRunning() {
        return loopManager.isServerLoopRunning();
    }

    public void dispose() {
        logger.log("Server closed.");
        stopServerSocket();
        loopManager.endServerLoop();
        if (host != null) {
            host.stop();
        }
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
        // Have the client tidy up
        conn.handleDisconnect();
        // Then remove it
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


    public void sendToAll(Packet packet) {
        for (SBClient client : clients) {
            if (client.getState() != SBClient.STATE.DISCONNECTING) {
                client.sendPacket(packet);
            }
        }
    }

    public void sendToOthers(Packet packet, SBClient self) {
        for (SBClient client : clients) {
            if (client != self && client.getState() != SBClient.STATE.DISCONNECTING) {
                client.sendPacket(packet);
            }
        }
    }

    public synchronized void notifyClientConnected(SBClient conn) {
        connectedQueue.add(conn);
    }

    public synchronized void notifyClientDisconnected(SBClient conn) {
        disconnectedQueue.add(conn);
    }

    public synchronized void notifyClientMessage(SBClient conn, Packet packet) {
        messageQueue.add(new ReceivedPacket(conn, packet));
    }

    public synchronized void pushNotifications() {
        for (SBClient client : connectedQueue) {
            handleClientConnected(client);
        }

        for (ReceivedPacket receivedPacket : messageQueue) {
            handleReceivedMessage(receivedPacket.client, receivedPacket.packet);
        }

        for (SBClient client : disconnectedQueue) {
            handleClientDisconnected(client);
        }

        connectedQueue.clear();
        disconnectedQueue.clear();
        messageQueue.clear();
    }

    public void startRound() {
        state = STATE.ROUND_START;
        roundTimer = System.currentTimeMillis();

        notifyModRoundStart();
    }

    public void endRound() {
        state = STATE.ROUND_END;
        roundTimer = System.currentTimeMillis();

        notifyModRoundEnd();
    }

    public List<SBClient> getClients() {
        return clients;
    }

    public float getDelta() {
        return delta;
    }

    public RoomGame getRoom() {
        return roomGame;
    }

    public void notifyModInit() {
        for (GameModeMod mod : mods) {
            mod.evInit();
        }
    }

    public void notifyModRoundStart() {
        for (GameModeMod mod : mods) {
            mod.evRoundStart();
        }
    }

    public void notifyModFreezeTime() {
        for (GameModeMod mod : mods) {
            mod.evFreezeTimeEnd();
        }
    }

    public void notifyModRoundEnd() {
        for (GameModeMod mod : mods) {
            mod.evRoundEnd();
        }
    }

    public void notifyModPlayerConnected(PlayerModInterface player) {
        for (GameModeMod mod : mods) {
            mod.evPlayerConnected(player);
        }
    }

    public void notifyModPlayerJoinedTeam(PlayerModInterface player) {
        for (GameModeMod mod : mods) {
            mod.evPlayerJoinedTeam(player);
        }
    }
}