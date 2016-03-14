package com.nicktoony.cstopdown.networking.client;

import com.nicktoony.cstopdown.config.ServerConfig;
import com.nicktoony.cstopdown.networking.packets.Packet;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by nick on 17/07/15.
 */
public abstract class SBSocket {

    public interface SBSocketListener {
        void onOpen(SBSocket socket);
        void onClose(SBSocket socket);
        void onMessage(SBSocket socket, Packet packet);
        void onError(SBSocket socket, Exception exception);
    }

    protected String ip;
    protected int port;
    private List<SBSocketListener> listeners;
    private ServerConfig serverConfig;
    private int id;
    private List<SBSocket> openQueue;
    private List<SBSocket> closeQueue;
    private Map<SBSocket, Packet> messageQueue;
    private Map<SBSocket, Exception> errorQueue;

    public SBSocket(String ip, int port) {
        this.ip = ip;
        this.port = port;
        this.listeners = new ArrayList<SBSocketListener>();

        this.openQueue = new ArrayList<SBSocket>();
        this.closeQueue = new ArrayList<SBSocket>();
        this.messageQueue = new LinkedHashMap<SBSocket, Packet>();
        this.errorQueue = new LinkedHashMap<SBSocket, Exception>();
    }

    public abstract boolean open();

    public abstract boolean close();

    public boolean sendMessage(Packet packet) {
        packet.prepareMessageId();
        return sendPacket(packet);
    }

    protected abstract boolean sendPacket(Packet packet);

    public void addListener(SBSocketListener listener) {
        listeners.add(listener);
    }

    public void removeListener(SBSocketListener listener) {
        listeners.remove(listener);
    }

    public ServerConfig getServerConfig() {
        return serverConfig;
    }

    public void setServerConfig(ServerConfig serverConfig) {
        this.serverConfig = serverConfig;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }



    protected void notifyOpen(SBSocket socket) {
        openQueue.add(socket);
    }

    protected void notifyClose(SBSocket socket) {
        closeQueue.add(socket);
    }

    protected void notifyMessage(SBSocket socket, Packet packet) {
        messageQueue.put(socket, packet);
    }

    protected void notifyException(SBSocket socket, Exception exception) {
        errorQueue.put(socket, exception);
    }

    public void pushNotifications() {
        // Open queue
        for (SBSocket socket : openQueue) {
            for (SBSocketListener listener : listeners) {
                listener.onOpen(socket);
            }
        }

        for (Map.Entry<SBSocket, Packet> message : messageQueue.entrySet()) {
            for (SBSocketListener listener : listeners) {
                listener.onMessage(message.getKey(), message.getValue());
            }
        }

        for (SBSocket socket : closeQueue) {
            for (SBSocketListener listener : listeners) {
                listener.onClose(socket);
            }
        }

        for (Map.Entry<SBSocket, Exception> message : errorQueue.entrySet()) {
            for (SBSocketListener listener : listeners) {
                listener.onError(message.getKey(), message.getValue());
            }
        }

        openQueue.clear();
        messageQueue.clear();
        closeQueue.clear();
        errorQueue.clear();
    }

}
