package com.nicktoony.spacebattle.networking.client;

import com.nicktoony.spacebattle.networking.packets.Packet;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nick on 17/07/15.
 */
public abstract class SBSocket {

    public interface SBSocketListener {
        public void onOpen(SBSocket socket);
        public void onClose(SBSocket socket);
        public void onMessage(SBSocket socket, Packet packet);
        public void onError(SBSocket socket, Exception exception);
    }

    protected String ip;
    protected int port;
    protected List<SBSocketListener> listeners;

    public SBSocket(String ip, int port) {
        this.ip = ip;
        this.port = port;
        this.listeners = new ArrayList<SBSocketListener>();
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
}
