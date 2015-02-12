package com.nick.ant.towerdefense.networking.server;

import com.badlogic.gdx.net.Socket;

/**
 * Created by Nick on 07/02/2015.
 */
public class ServerClient {
    public interface Listener {

    }

    private Socket socket;
    private Listener listener;

    public ServerClient(Socket socket) {
        this.socket = socket;
    }

    public ServerClient(Listener listener) {
        this.listener = listener;
    }

    public void sendMessage() {


        // Decide how to send the message
        if (socket != null) {

        } else {

        }
    }
}
