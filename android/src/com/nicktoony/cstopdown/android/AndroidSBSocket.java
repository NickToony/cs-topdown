package com.nicktoony.cstopdown.android;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;
import com.nicktoony.cstopdown.networking.client.SBSocket;
import com.nicktoony.cstopdown.networking.packets.Packet;
import com.nicktoony.cstopdown.networking.packets.PacketDefinitions;
import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

/**
 * Created by nick on 17/07/15.
 */
public class AndroidSBSocket extends SBSocket {

    private WebSocketClient socket;
    private Json json;

    public AndroidSBSocket(String ip, int port) {
        super(ip, port);

        socket = new WebSocketClient(URI.create("ws://"
                + ip + ":"
                + port + "/")) {
            @Override
            public void onOpen(ServerHandshake handshakedata) {
                notifyOpen(AndroidSBSocket.this);
            }

            @Override
            public void onMessage(String message) {
                Packet packet = getJson().fromJson(Packet.class, message);
                packet = (Packet) getJson()
                        .fromJson((Class) PacketDefinitions.PACKET_DEFITIONS.get(packet.getMessage_id()), message);

                notifyMessage(AndroidSBSocket.this, packet);
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                notifyClose(AndroidSBSocket.this);
            }

            @Override
            public void onError(Exception ex) {
                notifyException(AndroidSBSocket.this, ex);
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
        String string = getJson().toJson(packet);
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
