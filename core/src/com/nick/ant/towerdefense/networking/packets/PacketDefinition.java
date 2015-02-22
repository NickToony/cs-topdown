package com.nick.ant.towerdefense.networking.packets;

import com.esotericsoftware.kryo.Kryo;
import com.nick.ant.towerdefense.networking.packets.player.*;
import com.nick.ant.towerdefense.networking.server.ServerConfig;

/**
 * Created by Nick on 11/02/2015.
 */
public class PacketDefinition {

    public static void registerClasses(Kryo kryo) {
        //kryo.register()
        kryo.register(PlayerMovePacket.class);
        kryo.register(PlayerCreatePacket.class);
        kryo.register(ClientReadyPacket.class);
        kryo.register(PlayerPositionPacket.class);
        kryo.register(PlayerTorchPacket.class);
        kryo.register(PlayerShootPacket.class);

        // the config file, might as well just send that as an object
        kryo.register(ServerConfig.class);
    }

}
