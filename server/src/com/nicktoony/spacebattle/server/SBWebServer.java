package com.nicktoony.spacebattle.server;

import com.nicktoony.spacebattle.networking.server.SBServer;
import com.nicktoony.spacebattle.networking.server.ServerConfig;
import com.nicktoony.spacebattle.services.Logger;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by nick on 19/07/15.
 */
public class SBWebServer extends SBServer {

    private WebSocketServer serverSocket;
    private Map<WebSocket, SBWebClient> clientSockets = new HashMap<WebSocket, SBWebClient>();

    public SBWebServer(Logger logger, ServerConfig config, LoopManager loopManager) {
        super(logger, config, loopManager);
    }

    @Override
    protected void startServerSocket(int port) {
        serverSocket = new WebSocketServer(new InetSocketAddress(port)) {
            @Override
            public void onOpen(WebSocket conn, ClientHandshake handshake) {
                handleClientConnected(new SBWebClient(conn));
            }

            @Override
            public void onClose(WebSocket conn, int code, String reason, boolean remote) {
                handleClientDisconnected(clientSockets.get(conn));

                clientSockets.remove(conn);
            }

            @Override
            public void onMessage(WebSocket conn, String message) {
                handleReceivedMessage(clientSockets.get(conn), stringToPacket(message));
            }

            @Override
            public void onError(WebSocket conn, Exception ex) {
                getLogger().log(ex);
            }
        };
        serverSocket.start();
    }

    @Override
    protected void stopServerSocket() {
            try {
                serverSocket.stop();
            } catch (IOException e) {
                getLogger().log(e);
            } catch (InterruptedException e) {
                getLogger().log(e);
            }
    }
}
