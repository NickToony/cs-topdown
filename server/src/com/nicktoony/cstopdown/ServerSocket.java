package com.nicktoony.cstopdown;

import com.nicktoony.cstopdown.networking.server.CSServer;
import com.nicktoony.engine.config.ServerConfig;
import com.nicktoony.engine.services.Logger;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.*;

/**
 * Created by nick on 19/07/15.
 */
public class ServerSocket extends CSServer {

    private WebSocketServer serverSocket;
    private Map<WebSocket, ServerWebClientHandler> clientSockets = new HashMap<WebSocket, ServerWebClientHandler>();

    public ServerSocket(Logger logger, ServerConfig config, LoopManager loopManager) {
        super(logger, config, loopManager);
    }

    @Override
    protected void startServerSocket(int port) {
        serverSocket = new WebSocketServer(new InetSocketAddress(port)) {
            @Override
            public void onOpen(WebSocket conn, ClientHandshake handshake) {
                clientSockets.put(conn, new ServerWebClientHandler(ServerSocket.this, conn));
                notifyClientConnected(clientSockets.get(conn));
            }

            @Override
            public void onClose(WebSocket conn, int code, String reason, boolean remote) {
                notifyClientDisconnected(clientSockets.get(conn));

                clientSockets.remove(conn);
            }

            @Override
            public void onMessage(WebSocket conn, String message) {
                notifyClientMessage(clientSockets.get(conn), stringToPacket(message));
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
