package com.nicktoony.cstopdown;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;
import com.nicktoony.cstopdown.networking.server.CSServerClientHandler;
import com.nicktoony.engine.packets.Packet;
import org.java_websocket.WebSocket;

/**
 * Created by nick on 19/07/15.
 */
public class ServerWebClientHandler extends CSServerClientHandler {

    private WebSocket socket;
    private Json json;

    public ServerWebClientHandler(ServerSocket server, WebSocket socket) {
        super(server);
        this.socket = socket;
    }

    protected Json getJson() {
        if (json == null) {
            json = new Json();
            json.setTypeName(null);
            json.setUsePrototypes(false);
            json.setIgnoreUnknownFields(true);
            json.setOutputType(JsonWriter.OutputType.json);
        }
        return json;
    }

    protected String packetToString(Packet packet) {
        packet.prepareMessageId();
        packet.setTimestamp(getTimestamp());
        return getJson().toJson(packet);
    }

    @Override
    public void sendPacket(Packet packet) {
        if (socket.isOpen()) {
            socket.send(packetToString(packet));
        }
    }

    @Override
    public void close() {
        socket.close();
    }
}
