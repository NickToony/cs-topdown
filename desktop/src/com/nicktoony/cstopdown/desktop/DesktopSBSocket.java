package com.nicktoony.cstopdown.desktop;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;
import com.nicktoony.engine.networking.client.ClientSocket;
import com.nicktoony.engine.packets.Packet;
import com.nicktoony.engine.packets.PacketDefinitions;
import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

/**
 * Created by nick on 17/07/15.
 */
public class DesktopSBSocket extends ClientSocket {

    private WebSocketClient socket;
    private Json json;

    public DesktopSBSocket(String ip, int port) {
        super(ip, port);

        socket = new WebSocketClient(URI.create("wss://"
                + ip + ":"
                + port + "/")) {
            @Override
            public void onOpen(ServerHandshake handshakedata) {
                notifyOpen(DesktopSBSocket.this);
            }

            @Override
            public void onMessage(String message) {
                Packet packet = getJson().fromJson(Packet.class, message);
                packet = (Packet) getJson()
                        .fromJson((Class) PacketDefinitions.PACKET_DEFITIONS.get(packet.getMessage_id()), message);

                notifyMessage(DesktopSBSocket.this, packet);
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                notifyClose(DesktopSBSocket.this);
            }

            @Override
            public void onError(Exception ex) {
                notifyException(DesktopSBSocket.this, ex);
            }
        };
    }

    @Override
    public boolean open() {
        socket.connect();

        return true;
    }

    @Override
    public boolean close() {
        socket.close();

        return true;
    }

    @Override
    public boolean sendPacket(Packet packet) {
        if (socket.getReadyState() != WebSocket.READYSTATE.OPEN) {
            return false;
        }

        socket.send(getJson().toJson(packet));
        return true;
    }

    private Json getJson() {
        if (json == null) {
            json = new Json();
            json.setTypeName(null);
            json.setUsePrototypes(false);
            json.setIgnoreUnknownFields(true);
            json.setOutputType(JsonWriter.OutputType.json);
        }
        return json;
    }
}
