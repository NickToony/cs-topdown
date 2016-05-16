package com.nicktoony.engine.networking.server;

/**
 * Created by nick on 13/07/15.
 */

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;
import com.nicktoony.cstopdown.MyGame;
import com.nicktoony.engine.networking.client.FakeClientSocket;
import com.nicktoony.engine.config.ServerConfig;
import com.nicktoony.engine.config.ServerlistConfig;
import com.nicktoony.cstopdown.mods.gamemode.GameModeMod;
import com.nicktoony.cstopdown.mods.gamemode.PlayerModInterface;
import com.nicktoony.cstopdown.mods.gamemode.implementations.TeamDeathMatch;
import com.nicktoony.engine.packets.Packet;
import com.nicktoony.engine.packets.PacketDefinitions;
import com.nicktoony.engine.packets.connection.LoadedPacket;
import com.nicktoony.cstopdown.rooms.game.RoomGame;
import com.nicktoony.engine.services.Logger;
import com.nicktoony.gameserver.service.GameserverConfig;
import com.nicktoony.gameserver.service.host.Host;

import java.util.ArrayList;
import java.util.List;

public abstract class Server<T extends ServerClientHandler> {

    public interface LoopManager {
        void startServerLoop(Server server);
        boolean isServerLoopRunning();
        void endServerLoop();
    }

    private class ReceivedPacket {
        public T client;
        public Packet packet;

        public ReceivedPacket(T client, Packet packet) {
            this.client = client;
            this.packet = packet;
        }
    }




    protected ServerConfig config;
    protected Logger logger;
    private Host host;
    private Json json;
    private LoopManager loopManager;
    protected boolean publicServerList = true;
    private List<T> clients = new ArrayList<T>();

    private long lastFPSCount = System.currentTimeMillis();
    private int fpsFrames = 0;

    private List<T> connectedQueue = new ArrayList<T>();
    private List<T> disconnectedQueue = new ArrayList<T>();
    private List<ReceivedPacket> messageQueue = new ArrayList<ReceivedPacket>();

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
    public Server(Logger logger, ServerConfig config, LoopManager loopManager) {
        this.logger = logger;
        this.config = config;
        this.loopManager = loopManager;

        logger.log("Setting up");
        GameserverConfig.setConfig(new ServerlistConfig());
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
        for (ServerClientHandler client : clients) {
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
        if (host != null) {
            host.stop();
        }
    }

    protected void handleClientConnected(T conn) {
        logger.log("Client Connected");
        int testId = 0;
        boolean clash = true;
        while (clash) {
            testId ++;
            clash = false;
            for (ServerClientHandler client : clients) {
                if (client.getId() == testId) {
                    clash = true;
                }
            }
        }
        conn.setId(testId);
        clients.add(conn);
        conn.setState(ServerClientHandler.STATE.CONNECTING);
    }

    private void handleClientDisconnected(ServerClientHandler conn) {
        logger.log("Client Disconnected");
        // Have the client tidy up
        conn.handleDisconnect();
        // Then remove it
        clients.remove(conn);
    }

    private void handleReceivedMessage(ServerClientHandler conn, Packet packet) {
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


    public void sendToAll(Packet packet) {
        for (ServerClientHandler client : clients) {
            if (client.getState() == ServerClientHandler.STATE.INGAME) {
                client.sendPacket(packet);
            }
        }
    }

    public void sendToOthers(Packet packet, ServerClientHandler self) {
        for (ServerClientHandler client : clients) {
            if (client != self && client.getState() == ServerClientHandler.STATE.INGAME) {
                client.sendPacket(packet);
            }
        }
    }

    public synchronized void notifyClientConnected(T conn) {
        connectedQueue.add(conn);
    }

    public synchronized void notifyClientDisconnected(T conn) {
        disconnectedQueue.add(conn);
    }

    public synchronized void notifyClientMessage(T conn, Packet packet) {
        messageQueue.add(new ReceivedPacket(conn, packet));
    }

    public synchronized void pushNotifications() {
        for (T client : connectedQueue) {
            handleClientConnected(client);
        }

        for (ReceivedPacket receivedPacket : messageQueue) {
            handleReceivedMessage(receivedPacket.client, receivedPacket.packet);
        }

        for (T client : disconnectedQueue) {
            handleClientDisconnected(client);
        }

        connectedQueue.clear();
        disconnectedQueue.clear();
        messageQueue.clear();
    }

    public List<T> getClients() {
        return clients;
    }


}

