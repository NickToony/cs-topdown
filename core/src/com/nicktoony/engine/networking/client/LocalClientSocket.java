package com.nicktoony.engine.networking.client;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;
import com.nicktoony.engine.networking.server.Server;
import com.nicktoony.engine.networking.server.ServerClientHandler;
import com.nicktoony.engine.packets.Packet;

/**
 * Created by nick on 19/07/15.
 */
public abstract class LocalClientSocket<T extends Server> extends ClientSocket {

    protected T server;
    private ServerClientHandler client;
    private static Json json;

    public LocalClientSocket(T server) {
        super("", 0); // we don't care about port/ip

        this.server = server;
        this.client = createClientHandler();
    }

    protected abstract ServerClientHandler createClientHandler();

    @Override
    public boolean open() {
        notifyOpen(LocalClientSocket.this);

        server.notifyClientConnected(client);
        return true;
    }

    @Override
    public boolean close() {
        notifyClose(LocalClientSocket.this);

        server.notifyClientDisconnected(client);
        return true;
    }

    @Override
    protected boolean sendPacket(Packet packet) {
//        packet.prepareMessageId();
//        server.notifyClientMessage(client, (Packet) getJson()
//                .fromJson((Class) PacketDefinitions.PACKET_DEFITIONS
//                        .get(packet.getMessage_id()), getJson().toJson(packet)));
        server.notifyClientMessage(client, packet);
        return true;
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
}
