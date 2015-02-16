package com.nick.ant.towerdefense.networking.packets;

import com.esotericsoftware.kryo.Kryo;

/**
 * Created by Nick on 11/02/2015.
 */
public class PacketDefinition {

    public static void registerClasses(Kryo kryo) {
        //kryo.register()
        kryo.register(PlayerMovePacket.class);
        kryo.register(PlayerCreatePacket.class);
        kryo.register(ClientReadyPacket.class);
    }

}
