package com.nicktoony.cstopdown.client;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.nicktoony.engine.networking.client.ClientSocket;
import com.nicktoony.engine.packets.Packet;
import com.nicktoony.engine.packets.PacketDefinitions;
import com.sksamuel.gwt.websockets.Websocket;
import com.sksamuel.gwt.websockets.WebsocketListener;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by nick on 17/07/15.
 */
public class HtmlClientSocket extends ClientSocket {

    private Websocket socket;
    private Json json;

    public HtmlClientSocket(String ip, int port) {
        super(ip, port);

        socket = new Websocket("wss://"
                + ip + ":"
                + port + "/");
        socket.addListener(new WebsocketListener() {
            @Override
            public void onClose() {
                notifyClose(HtmlClientSocket.this);
            }

            @Override
            public void onMessage(String message) {
                Packet packet = getJson().fromJson(Packet.class, message);
                packet = (Packet) getJson()
                        .fromJson((Class) PacketDefinitions.PACKET_DEFITIONS.get(packet.getMessage_id()), message);

                notifyMessage(HtmlClientSocket.this, packet);
            }

            @Override
            public void onOpen() {
                notifyOpen(HtmlClientSocket.this);
            }
        });
    }

    @Override
    public boolean open() {
        socket.open();

        return true;
    }

    @Override
    public boolean close() {
        socket.close();

        return true;
    }

    @Override
    public boolean sendPacket(Packet packet) {
        socket.send(getJson().toJson(packet));
        return true;
    }

    private Json getJson() {
        // If it's not created yet
        if (json == null) {
            // create the new json instance
            json = new Json();
            // Set a custom serializer, as "Map" is abstract and created by the default
            json.setSerializer(Map.class, new Json.ReadOnlySerializer<Map>() {

                @Override
                public Map read(Json json, JsonValue jsonData, Class type) {
                    // Create the map (Note: it only works for <String, String[]>
                    Map<String, Object> map = new HashMap<String, Object>();
                    // for each entry
                    for (JsonValue entry = jsonData.child; entry != null; entry = entry.next) {
                        // Each entry is an arrange of strings, so we fetch that
                        try {
                            map.put(entry.name, json.readValue(entry.name, String.class, jsonData));
                        } catch (Exception e) {
                            map.put(entry.name, json.readValue(entry.name, String[].class, jsonData));
                        }
                    }
                    return map;
                }

            });

            json.setIgnoreUnknownFields(true);
        }
        return json;
    }
}
