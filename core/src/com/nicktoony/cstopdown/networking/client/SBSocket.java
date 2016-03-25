package com.nicktoony.cstopdown.networking.client;

import com.nicktoony.cstopdown.config.ServerConfig;
import com.nicktoony.cstopdown.networking.packets.Packet;

import java.util.ArrayList;
import java.util.List;

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

    private class ReceivedPacket {
        public SBSocket socket;
        public Packet packet;

        public ReceivedPacket(SBSocket socket, Packet packet) {
            this.socket = socket;
            this.packet = packet;
        }
    }

    private class ReceivedError {
        public SBSocket socket;
        public Exception exception;

        public ReceivedError(SBSocket socket, Exception exception) {
            this.socket = socket;
            this.exception = exception;
        }
    }

    protected String ip;
    protected int port;
    private List<SBSocketListener> listeners;
    private ServerConfig serverConfig;
    private int id;
    private List<SBSocket> openQueue;
    private List<SBSocket> closeQueue;
    private List<ReceivedPacket> messageQueue;
    private List<ReceivedError> errorQueue;

    public SBSocket(String ip, int port) {
        this.ip = ip;
        this.port = port;
        this.listeners = new ArrayList<SBSocketListener>();

        this.openQueue = new ArrayList<SBSocket>();
        this.closeQueue = new ArrayList<SBSocket>();
        this.messageQueue = new ArrayList<ReceivedPacket>();
        this.errorQueue = new ArrayList<ReceivedError>();
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

    protected synchronized void notifyMessage(SBSocket socket, Packet packet) {
        messageQueue.add(new ReceivedPacket(socket, packet));
    }

    protected void notifyException(SBSocket socket, Exception exception) {
        errorQueue.add(new ReceivedError(socket, exception));
    }

    public synchronized void pushNotifications() {
        // Open queue
        for (SBSocket socket : openQueue) {
            for (SBSocketListener listener : listeners) {
                listener.onOpen(socket);
            }
        }

        for (ReceivedPacket receivedPacket : messageQueue) {
            for (SBSocketListener listener : listeners) {
                listener.onMessage(receivedPacket.socket, receivedPacket.packet);
            }
        }

        for (SBSocket socket : closeQueue) {
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
