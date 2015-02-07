package com.nicktoony.cstd.server;

import com.badlogic.gdx.net.Socket;

/**
 * Created by Nick on 07/02/2015.
 */
public class Client {
    public interface Listener {

    }

    private Socket socket;
    private Listener listener;

    public Client(Socket socket) {
        this.socket = socket;
    }

    public Client(Listener listener) {
        this.listener = listener;
    }

    public void sendMessage() {


        // Decide how to send the message
        if (socket != null) {

        } else {
            
        }
    }
}
