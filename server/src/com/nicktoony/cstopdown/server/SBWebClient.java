package com.nicktoony.cstopdown.server;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;
import com.nicktoony.cstopdown.networking.packets.Packet;
import com.nicktoony.cstopdown.networking.server.SBClient;
import org.java_websocket.WebSocket;

/**
 * Created by nick on 19/07/15.
 */
public class SBWebClient extends SBClient {

    private WebSocket socket;
    private Json json;

    public SBWebClient(SBWebServer server, WebSocket socket) {
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
        return getJson().toJson(packet);
    }

    @Override
    public void sendPacket(Packet packet) {
        socket.send(packetToString(packet));
    }

    @Override
    public void close() {
        socket.close();
    }
}
