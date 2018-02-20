package com.nicktoony.engine.networking.client;

import com.nicktoony.engine.config.ServerConfig;
import com.nicktoony.engine.packets.Packet;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nick on 17/07/15.
 */
public abstract class ClientSocket {

    public interface SBSocketListener {
        void onOpen(ClientSocket socket);
        void onClose(ClientSocket socket);
        void onMessage(ClientSocket socket, Packet packet);
        void onError(ClientSocket socket, Exception exception);
    }

    private class ReceivedPacket {
        public ClientSocket socket;
        public Packet packet;

        public ReceivedPacket(ClientSocket socket, Packet packet) {
            this.socket = socket;
            this.packet = packet;
        }
    }

    private class ReceivedError {
        public ClientSocket socket;
        public Exception exception;

        public ReceivedError(ClientSocket socket, Exception exception) {
            this.socket = socket;
            this.exception = exception;
        }
    }

    protected String ip;
    protected int port;
    private List<SBSocketListener> listeners;
    private ServerConfig serverConfig;
    private int id;
    private List<ClientSocket> openQueue;
    private List<ClientSocket> closeQueue;
    private List<ReceivedPacket> messageQueue;
    private List<ReceivedError> errorQueue;
    private long initialTimestamp;

    public ClientSocket(String ip, int port) {
        this.ip = ip;
        this.port = port;
        this.listeners = new ArrayList<SBSocketListener>();

        this.openQueue = new ArrayList<ClientSocket>();
        this.closeQueue = new ArrayList<ClientSocket>();
        this.messageQueue = new ArrayList<ReceivedPacket>();
        this.errorQueue = new ArrayList<ReceivedError>();
    }

    public void prepareTimestamp() {
        initialTimestamp = System.currentTimeMillis();
        System.out.println(getTimestamp());
    }

    public long getTimestamp() {
        return (System.currentTimeMillis() - initialTimestamp);
    }

    public abstract boolean open();

    public abstract boolean close();

    public boolean sendMessage(Packet packet) {
        packet.prepareMessageId();
        packet.setTimestamp(getTimestamp());
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



    public void notifyOpen(ClientSocket socket) {
        openQueue.add(socket);
    }

    public void notifyClose(ClientSocket socket) {
        closeQueue.add(socket);
    }

    public synchronized void notifyMessage(ClientSocket socket, Packet packet) {
        messageQueue.add(new ReceivedPacket(socket, packet));
    }

    public void notifyException(ClientSocket socket, Exception exception) {
        errorQueue.add(new ReceivedError(socket, exception));
    }

    public synchronized void pushNotifications() {
        // Open queue
        for (ClientSocket socket : openQueue) {
            for (SBSocketListener listener : listeners) {
                listener.onOpen(socket);
            }
        }

        for (ReceivedPacket packet : messageQueue) {
            for (SBSocketListener listener : listeners) {
                listener.onMessage(packet.socket, packet.packet);
            }
        }

        for (ClientSocket socket : closeQueue) {
            for (SBSocketListener listener : listeners) {
                listener.onClose(socket);
            }
        }

        for (ReceivedError receivedError : errorQueue) {
            for (SBSocketListener listener : listeners) {
                listener.onError(receivedError.socket, receivedError.exception);
            }
        }

        openQueue.clear();
        messageQueue.clear();
        closeQueue.clear();
        errorQueue.clear();
    }

}
